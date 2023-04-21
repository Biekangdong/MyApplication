/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3;


import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.os.SystemClock;
import android.os.Trace;
import android.text.TextUtils;
import android.util.Log;

import com.android.launcher3.config.ProviderConfig;
import com.android.launcher3.util.LongArrayMap;
import com.android.launcher3.util.Thunk;
import com.android.launcher3.util.ViewOnDrawExecutor;
import com.example.customerlauncher3.Consts;
import com.example.customerlauncher3.greendao.CommonDaoUtils;
import com.example.customerlauncher3.greendao.DaoUtilsStore;
import com.example.customerlauncher3.greendao.entity.DJCell;
import com.example.customerlauncher3.greendao.entity.DJCellDao;
import com.example.customerlauncher3.greendao.entity.DJScreens;
import com.example.customerlauncher3.greendao.entity.DJScreensDao;
import com.example.customerlauncher3.greendao.entity.LightEquipment;
import com.example.customerlauncher3.greendao.entity.LightEquipmentDao;
import com.example.customerlauncher3.greendao.entity.LightEquipmentGroup;


import org.greenrobot.greendao.query.QueryBuilder;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

public class LauncherModel {
    static final boolean DEBUG_LOADERS = false;

    static final String TAG = "Launcher.Model";

    private static final int ITEMS_CHUNK = 6; // batch size for the workspace icons
    private static final long INVALID_SCREEN_ID = -1L;

    LauncherAppState mApp;
    @Thunk
    final Object mLock = new Object();
    @Thunk
    DeferredHandler mHandler = new DeferredHandler();
    @Thunk
    LoaderTask mLoaderTask;
    @Thunk
    boolean mIsLoaderTaskRunning;
    @Thunk
    boolean mHasLoaderCompletedOnce;

    @Thunk
    static final HandlerThread sWorkerThread = new HandlerThread("launcher-loader");

    static {
        sWorkerThread.start();
    }

    @Thunk
    static final Handler sWorker = new Handler(sWorkerThread.getLooper());

    private boolean mWorkspaceLoaded;
    private boolean mAllAppsLoaded;
    private boolean mDeepShortcutsLoaded;

    /**
     * Set of runnables to be called on the background thread after the workspace binding
     * is complete.
     */
    static final ArrayList<Runnable> mBindCompleteRunnables = new ArrayList<Runnable>();

    @Thunk
    WeakReference<Callbacks> mCallbacks;


    static final Object sBgLock = new Object();

    // sBgItemsIdMap maps *all* the ItemInfos (shortcuts, folders, and widgets) created by
    // LauncherModel to their ids
    public static final LongArrayMap<ItemInfo> sBgItemsIdMap = new LongArrayMap<>();


    public static final ArrayList<ItemInfo> sBgWorkspaceItems = new ArrayList<ItemInfo>();

    // sBgFolders is all FolderInfos created by LauncherModel. Passed to bindFolders()
    static final LongArrayMap<FolderInfo> sBgFolders = new LongArrayMap<>();

    // sBgWorkspaceScreens is the ordered set of workspace screens.
    public static final ArrayList<Long> sBgWorkspaceScreens = new ArrayList<Long>();

    public static int CHANGESTATUESTYPE = 0;//0正常，1管理 ，2编辑
    static CommonDaoUtils<DJCell> djCellCommonDaoUtils;
    static CommonDaoUtils<DJScreens> djScreensCommonDaoUtils;
//    static CommonDaoUtils<LightEquipment> lightEquipmentCommonDaoUtils ;
//    static CommonDaoUtils<LightEquipmentGroup> lightEquipmentGroupCommonDaoUtils ;


    public interface Callbacks {
        public boolean setLoadOnResume();

        public int getCurrentWorkspaceScreen();

        public void bindItems(ArrayList<ItemInfo> shortcuts, int start, int end,
                              boolean forceAnimateIcons);

        public void bindScreens(ArrayList<Long> orderedScreenIds);

        public void finishFirstPageBind(ViewOnDrawExecutor executor);

        public void finishBindingItems();

        public void bindShortcutsChanged(ArrayList<ShortcutInfo> updated,
                                         ArrayList<ShortcutInfo> removed);

        public void bindRestoreItemsChange(HashSet<ItemInfo> updates);

        public void onPageBoundSynchronously(int page);

        public void executeOnNextDraw(ViewOnDrawExecutor executor);
    }

    public interface ItemInfoFilter {
        public boolean filterItem(ItemInfo parent, ItemInfo info, ComponentName cn);
    }

    LauncherModel(LauncherAppState app) {
        mApp = app;
        if (djCellCommonDaoUtils == null) {
            djCellCommonDaoUtils = DaoUtilsStore.getInstance().getDjCellCommonDaoUtils();
        }
        if (djScreensCommonDaoUtils == null) {
            djScreensCommonDaoUtils = DaoUtilsStore.getInstance().getDjScreensCommonDaoUtils();
        }
    }

    /**
     * Runs the specified runnable immediately if called from the main thread, otherwise it is
     * posted on the main thread handler.
     */
    private void runOnMainThread(Runnable r) {
        if (sWorkerThread.getThreadId() == Process.myTid()) {
            // If we are on the worker thread, post onto the main handler
            mHandler.post(r);
        } else {
            r.run();
        }
    }

    /**
     * Runs the specified runnable immediately if called from the worker thread, otherwise it is
     * posted on the worker thread handler.
     */
    private static void runOnWorkerThread(Runnable r) {
        if (sWorkerThread.getThreadId() == Process.myTid()) {
            r.run();
        } else {
            // If we are not on the worker thread, then post to the worker handler
            sWorker.post(r);
        }
    }


    /**
     * Adds an item to the DB if it was not created previously, or move it to a new
     * <container, screen, cellX, cellY>
     */
    public static void addOrMoveItemInDatabase(Context context, ItemInfo item, long container,
                                               long screenId, int cellX, int cellY) {
        if (item.container == ItemInfo.NO_ID) {
            // From all apps
            addItemToDatabase(context, item, container, screenId, cellX, cellY);
        } else {
            // From somewhere else
            moveItemInDatabase(context, item, container, screenId, cellX, cellY);
        }
    }

    static void checkItemInfoLocked(
            final long itemId, final ItemInfo item, StackTraceElement[] stackTrace) {
        ItemInfo modelItem = sBgItemsIdMap.get(itemId);
        if (modelItem != null && item != modelItem) {
            // check all the data is consistent
            if (modelItem instanceof ShortcutInfo && item instanceof ShortcutInfo) {
                ShortcutInfo modelShortcut = (ShortcutInfo) modelItem;
                ShortcutInfo shortcut = (ShortcutInfo) item;
                if (modelShortcut.title.toString().equals(shortcut.title.toString()) &&
                        modelShortcut.id == shortcut.id &&
                        modelShortcut.itemType == shortcut.itemType &&
                        modelShortcut.container == shortcut.container &&
                        modelShortcut.screenId == shortcut.screenId &&
                        modelShortcut.cellX == shortcut.cellX &&
                        modelShortcut.cellY == shortcut.cellY &&
                        modelShortcut.spanX == shortcut.spanX &&
                        modelShortcut.spanY == shortcut.spanY) {
                    // For all intents and purposes, this is the same object
                    return;
                }
            }

            // the modelItem needs to match up perfectly with item if our model is
            // to be consistent with the database-- for now, just require
            // modelItem == item or the equality check above
            String msg = "item: " + ((item != null) ? item.toString() : "null") +
                    "modelItem: " +
                    ((modelItem != null) ? modelItem.toString() : "null") +
                    "Error: ItemInfo passed to checkItemInfo doesn't match original";
            RuntimeException e = new RuntimeException(msg);
            if (stackTrace != null) {
                e.setStackTrace(stackTrace);
            }
            throw e;
        }
    }

    static void checkItemInfo(final ItemInfo item) {
        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        final long itemId = item.id;
        Runnable r = new Runnable() {
            public void run() {
                synchronized (sBgLock) {
                    checkItemInfoLocked(itemId, item, stackTrace);
                }
            }
        };
        runOnWorkerThread(r);
    }

    static void updateItemInDatabaseHelper(final DJCell values,
                                           final ItemInfo item) {
        final long itemId = item.id;
        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        Runnable r = new Runnable() {
            public void run() {
                djCellCommonDaoUtils.update(values);
                updateItemArrays(item, itemId, stackTrace);
            }
        };
        runOnWorkerThread(r);
    }

    static void updateItemsInDatabaseHelper(final ArrayList<DJCell> valuesList,
                                            final ArrayList<ItemInfo> items) {
        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        Runnable r = new Runnable() {
            public void run() {
                int count = items.size();
                for (int i = 0; i < count; i++) {
                    ItemInfo item = items.get(i);
                    final long itemId = item.id;
                    DJCell values = valuesList.get(i);
                    djCellCommonDaoUtils.update(values);
                    updateItemArrays(item, itemId, stackTrace);

                }
            }
        };
        runOnWorkerThread(r);
    }

    static void updateItemArrays(ItemInfo item, long itemId, StackTraceElement[] stackTrace) {
        // Lock on mBgLock *after* the db operation
        synchronized (sBgLock) {
            //checkItemInfoLocked(itemId, item, stackTrace);

            if (item.container != LauncherSettings.CONTAINER_DESKTOP) {
                // Item is in a folder, make sure this folder exists
                if (!sBgFolders.containsKey(item.container)) {
                    // An items container is being set to a that of an item which is not in
                    // the list of Folders.
                    String msg = "item: " + item + " container being set to: " +
                            item.container + ", not in the list of folders";
                    Log.e(TAG, msg);
                }
            }

            // Items are added/removed from the corresponding FolderInfo elsewhere, such
            // as in Workspace.onDrop. Here, we just add/remove them from the list of items
            // that are on the desktop, as appropriate
            ItemInfo modelItem = sBgItemsIdMap.get(itemId);
            if (modelItem != null &&
                    (modelItem.container == LauncherSettings.CONTAINER_DESKTOP)) {
                switch (modelItem.itemType) {
                    case LauncherSettings.ITEM_TYPE_SHORTCUT:
                    case LauncherSettings.ITEM_TYPE_FOLDER:
                        if (!sBgWorkspaceItems.contains(modelItem)) {
                            sBgWorkspaceItems.add(modelItem);
                        }
                        break;
                    default:
                        break;
                }
            } else {
                sBgWorkspaceItems.remove(modelItem);
            }
        }
    }

    /**
     * Move an item in the DB to a new <container, screen, cellX, cellY>
     */
    public static void moveItemInDatabase(Context context, final ItemInfo item, final long container,
                                          final long screenId, final int cellX, final int cellY) {
        item.container = container;
        item.cellX = cellX;
        item.cellY = cellY;

        item.screenId = screenId;

        DJCell values = djCellCommonDaoUtils.queryById(item.id);
        if (values == null) {
            values = new DJCell();
        }
        values.setId(item.id);
        values.setContainer(item.container);
        values.setCellX(item.cellX);
        values.setCellY(item.cellY);
        values.setRank(item.rank);
        values.setScreenId(item.screenId);
        updateItemInDatabaseHelper(values, item);
    }

    /**
     * Move items in the DB to a new <container, screen, cellX, cellY>. We assume that the
     * cellX, cellY have already been updated on the ItemInfos.
     */
    public static void moveItemsInDatabase(final ArrayList<ItemInfo> items,
                                           final long container, final int screen) {
        int count = items.size();
        ArrayList<DJCell> contentValues = new ArrayList<DJCell>();
        for (int i = 0; i < count; i++) {
            ItemInfo item = items.get(i);
            item.container = container;

            item.screenId = screen;

            DJCell values = djCellCommonDaoUtils.queryById(item.id);
            if (values == null) {
                values = new DJCell();
            }
            values.setId(item.id);
            values.setContainer(item.container);
            values.setCellX(item.cellX);
            values.setCellY(item.cellY);
            values.setRank(item.rank);
            values.setScreenId(item.screenId);

            contentValues.add(values);
        }
        updateItemsInDatabaseHelper(contentValues, items);
    }

    /**
     * Move and/or resize item in the DB to a new <container, screen, cellX, cellY, spanX, spanY>
     */
    static void modifyItemInDatabase(final ItemInfo item, final long container,
                                     final long screenId, final int cellX, final int cellY, final int spanX, final int spanY) {
        item.container = container;
        item.cellX = cellX;
        item.cellY = cellY;
        item.spanX = spanX;
        item.spanY = spanY;

        item.screenId = screenId;

        DJCell values = djCellCommonDaoUtils.queryById(item.id);
        if (values == null) {
            values = new DJCell();
        }
        values.setId(item.id);
        values.setContainer(item.container);
        values.setCellX(item.cellX);
        values.setCellY(item.cellY);
        values.setRank(item.rank);
        values.setScreenId(item.screenId);

        updateItemInDatabaseHelper(values, item);
    }

    /**
     * Update an item to the database in a specified container.
     */
    public static void updateItemInDatabase(Context context, final ItemInfo item) {
        final DJCell values = djCellCommonDaoUtils.queryById(item.id);
        item.onAddToDatabase(context, values);
        updateItemInDatabaseHelper(values, item);
    }


    /**
     * Add an item to the database in a specified container. Sets the container, screen, cellX and cellY fields of the item. Also assigns an ID to the item.
     */
    public static void addItemToDatabase(Context context, final ItemInfo item, final long container,
                                         final long screenId, final int cellX, final int cellY) {
        item.container = container;
        item.cellX = cellX;
        item.cellY = cellY;
        item.screenId = screenId;
        final DJCell values = new DJCell();
        item.onAddToDatabase(context, values);

        item.id = generateNewItemId();
        values.setId(item.id);

        Runnable r = new Runnable() {
            public void run() {
                djCellCommonDaoUtils.insert(values);
                // Lock on mBgLock *after* the db operation
                synchronized (sBgLock) {
                    //checkItemInfoLocked(item.id, item, stackTrace);
                    sBgItemsIdMap.put(item.id, item);
                    switch (item.itemType) {
                        case LauncherSettings.ITEM_TYPE_FOLDER:
                            sBgFolders.put(item.id, (FolderInfo) item);
                            // Fall through
                        case LauncherSettings.ITEM_TYPE_SHORTCUT:
                            sBgWorkspaceItems.add(item);
                            break;
                    }
                }
            }
        };
        runOnWorkerThread(r);
    }


    /**
     * Removes the specified item from the database
     */
    public static void deleteItemFromDatabase(Context context, final ItemInfo item) {
        ArrayList<ItemInfo> items = new ArrayList<ItemInfo>();
        items.add(item);
        deleteItemsFromDatabase(context, items);
    }

    /**
     * Removes the specified items from the database
     */
    static void deleteItemsFromDatabase(Context context, final ArrayList<? extends ItemInfo> items) {
        Runnable r = new Runnable() {
            public void run() {
                for (ItemInfo item : items) {
                    djCellCommonDaoUtils.delete(djCellCommonDaoUtils.queryById(item.id));

                    // Lock on mBgLock *after* the db operation
                    synchronized (sBgLock) {
                        switch (item.itemType) {
                            case LauncherSettings.ITEM_TYPE_FOLDER:
                                sBgFolders.remove(item.id);
                                for (ItemInfo info : sBgItemsIdMap) {
                                    if (info.container == item.id) {
                                        // We are deleting a folder which still contains items that
                                        // think they are contained by that folder.
                                        String msg = "deleting a folder (" + item + ") which still " +
                                                "contains items (" + info + ")";
                                        Log.e(TAG, msg);
                                    }
                                }
                                sBgWorkspaceItems.remove(item);
                                break;
                            case LauncherSettings.ITEM_TYPE_SHORTCUT:
                                sBgWorkspaceItems.remove(item);
                                break;
                        }
                        sBgItemsIdMap.remove(item.id);
                    }
                }
            }
        };
        runOnWorkerThread(r);
    }

    /**
     * Update the order of the workspace screens in the database. The array list contains
     * a list of screen ids in the order that they should appear.
     */
    public void updateWorkspaceScreenOrder(Context context, final ArrayList<Long> screens) {
        final ArrayList<Long> screensCopy = new ArrayList<Long>(screens);

        // Remove any negative screen ids -- these aren't persisted
        Iterator<Long> iter = screensCopy.iterator();
        while (iter.hasNext()) {
            long id = iter.next();
            if (id < 0) {
                iter.remove();
            }
        }

        Runnable r = new Runnable() {
            @Override
            public void run() {
                // Clear the table
                djScreensCommonDaoUtils.getDaoSession().getDatabase().execSQL("DELETE FROM " + DJScreensDao.TABLENAME);
                int count = screensCopy.size();
                Log.e(TAG, "screensCopyCount: " + count);
                for (int i = 0; i < count; i++) {
                    long screenId = screensCopy.get(i);
                    if (djScreensCommonDaoUtils.queryById(screenId) == null) {
                        djScreensCommonDaoUtils.insert(new DJScreens(screenId, i, System.currentTimeMillis(), Consts.CONTROL_SCENE_ID, "", "", ""));
                    }
                }


                synchronized (sBgLock) {
                    sBgWorkspaceScreens.clear();
                    sBgWorkspaceScreens.addAll(screensCopy);
                }
            }
        };
        runOnWorkerThread(r);
    }

    /**
     * Remove the specified folder and all its contents from the database.
     */
    public static void deleteFolderAndContentsFromDatabase(final FolderInfo info) {
        Runnable r = new Runnable() {
            public void run() {
                djCellCommonDaoUtils.delete(djCellCommonDaoUtils.queryById(info.id));
                // Lock on mBgLock *after* the db operation
                synchronized (sBgLock) {
                    sBgItemsIdMap.remove(info.id);
                    sBgFolders.remove(info.id);
                    sBgWorkspaceItems.remove(info);
                }
                QueryBuilder<DJCell> queryBuilder = djCellCommonDaoUtils.getDaoSession().queryBuilder(DJCell.class);
                queryBuilder.where(DJCellDao.Properties.Container.eq(info.id)).buildDelete().executeDeleteWithoutDetachingEntities();
                // Lock on mBgLock *after* the db operation
                synchronized (sBgLock) {
                    for (ItemInfo childInfo : info.contents) {
                        sBgItemsIdMap.remove(childInfo.id);
                    }
                }
            }
        };
        runOnWorkerThread(r);
    }

    /**
     * Set this as the current Launcher activity object for the loader.
     */
    public void initialize(Callbacks callbacks) {
        synchronized (mLock) {
            // Remove any queued UI runnables
            mHandler.cancelAll();
            mCallbacks = new WeakReference<>(callbacks);
        }
    }


    public void resetLoadedState(boolean resetAllAppsLoaded, boolean resetWorkspaceLoaded) {
        synchronized (mLock) {
            // Stop any existing loaders first, so they don't set mAllAppsLoaded or
            // mWorkspaceLoaded to true later
            stopLoaderLocked();
            if (resetAllAppsLoaded) mAllAppsLoaded = false;
            if (resetWorkspaceLoaded) mWorkspaceLoaded = false;
            // Always reset deep shortcuts loaded.
            // TODO: why?
            mDeepShortcutsLoaded = false;
        }
    }

    /**
     * When the launcher is in the background, it's possible for it to miss paired
     * configuration changes.  So whenever we trigger the loader from the background
     * tell the launcher that it needs to re-run the loader when it comes back instead
     * of doing it now.
     */
    public void startLoaderFromBackground() {
        Callbacks callbacks = getCallback();
        if (callbacks != null) {
            // Only actually run the loader if they're not paused.
            if (!callbacks.setLoadOnResume()) {
                startLoader(callbacks.getCurrentWorkspaceScreen());
            }
        }
    }

    /**
     * If there is already a loader task running, tell it to stop.
     */
    private void stopLoaderLocked() {
        LoaderTask oldTask = mLoaderTask;
        if (oldTask != null) {
            oldTask.stopLocked();
        }
    }

    public boolean isCurrentCallbacks(Callbacks callbacks) {
        return (mCallbacks != null && mCallbacks.get() == callbacks);
    }

    /**
     * Starts the loader. Tries to bind {@params synchronousBindPage} synchronously if possible.
     *
     * @return true if the page could be bound synchronously.
     */
    public boolean startLoader(int synchronousBindPage) {
        synchronized (mLock) {
            // Don't bother to start the thread if we know it's not going to do anything
            if (mCallbacks != null && mCallbacks.get() != null) {
                final Callbacks oldCallbacks = mCallbacks.get();

                // If there is already one running, tell it to stop.
                stopLoaderLocked();
                mLoaderTask = new LoaderTask(mApp.getContext(), synchronousBindPage);
                // TODO: mDeepShortcutsLoaded does not need to be true for synchronous bind.
                if (synchronousBindPage != PagedView.INVALID_RESTORE_PAGE && mAllAppsLoaded
                        && mWorkspaceLoaded && mDeepShortcutsLoaded && !mIsLoaderTaskRunning) {
                    mLoaderTask.runBindSynchronousPage(synchronousBindPage);
                    return true;
                } else {
                    sWorkerThread.setPriority(Thread.NORM_PRIORITY);
                    sWorker.post(mLoaderTask);
                }
            }
        }
        return false;
    }

    public void stopLoader() {
        synchronized (mLock) {
            if (mLoaderTask != null) {
                mLoaderTask.stopLocked();
            }
        }
    }

    /**
     * Loads the workspace screen ids in an ordered list.
     */
    public static ArrayList<Long> loadWorkspaceScreensDb(Context context) {
        ArrayList<Long> screenIds = new ArrayList<Long>();

        QueryBuilder<DJScreens> queryBuilder = djScreensCommonDaoUtils.getDaoSession().queryBuilder(DJScreens.class);
        queryBuilder.orderAsc(DJScreensDao.Properties.ScreenRank);
        List<DJScreens> list = queryBuilder.list();
        for (DJScreens djScreens : list) {
            screenIds.add(djScreens.getId());
        }
        Log.e(TAG, "loadWorkspaceScreensDb: " + screenIds);
        // Get screens ordered by rank.
        return screenIds;
    }

    /**
     * Runnable for the thread that loads the contents of the launcher:
     * - workspace icons
     * - widgets
     * - all apps icons
     * - deep shortcuts within apps
     */
    private class LoaderTask implements Runnable {
        private Context mContext;
        private int mPageToBindFirst;

        @Thunk
        boolean mIsLoadingAndBindingWorkspace;
        private boolean mStopped;
        @Thunk
        boolean mLoadAndBindStepFinished;

        LoaderTask(Context context, int pageToBindFirst) {
            mContext = context;
            mPageToBindFirst = pageToBindFirst;
        }

        private void loadAndBindWorkspace() {
            mIsLoadingAndBindingWorkspace = true;

            // Load the workspace
            if (DEBUG_LOADERS) {
                Log.d(TAG, "loadAndBindWorkspace mWorkspaceLoaded=" + mWorkspaceLoaded);
            }

            if (!mWorkspaceLoaded) {
                loadWorkspace();
                synchronized (LoaderTask.this) {
                    if (mStopped) {
                        return;
                    }
                    mWorkspaceLoaded = true;
                }
            }

            // Bind the workspace
            bindWorkspace(mPageToBindFirst);
        }

        private void waitForIdle() {
            // Wait until the either we're stopped or the other threads are done.
            // This way we don't start loading all apps until the workspace has settled
            // down.
            synchronized (LoaderTask.this) {
                final long workspaceWaitTime = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;

                mHandler.postIdle(new Runnable() {
                    public void run() {
                        synchronized (LoaderTask.this) {
                            mLoadAndBindStepFinished = true;
                            if (DEBUG_LOADERS) {
                                Log.d(TAG, "done with previous binding step");
                            }
                            LoaderTask.this.notify();
                        }
                    }
                });

                while (!mStopped && !mLoadAndBindStepFinished) {
                    try {
                        // Just in case mFlushingWorkerThread changes but we aren't woken up,
                        // wait no longer than 1sec at a time
                        this.wait(1000);
                    } catch (InterruptedException ex) {
                        // Ignore
                    }
                }
                if (DEBUG_LOADERS) {
                    Log.d(TAG, "waited "
                            + (SystemClock.uptimeMillis() - workspaceWaitTime)
                            + "ms for previous step to finish binding");
                }
            }
        }

        void runBindSynchronousPage(int synchronousBindPage) {
            if (synchronousBindPage == PagedView.INVALID_RESTORE_PAGE) {
                // Ensure that we have a valid page index to load synchronously
                throw new RuntimeException("Should not call runBindSynchronousPage() without " +
                        "valid page index");
            }
            if (!mAllAppsLoaded || !mWorkspaceLoaded) {
                // Ensure that we don't try and bind a specified page when the pages have not been
                // loaded already (we should load everything asynchronously in that case)
                throw new RuntimeException("Expecting AllApps and Workspace to be loaded");
            }
            synchronized (mLock) {
                if (mIsLoaderTaskRunning) {
                    // Ensure that we are never running the background loading at this point since
                    // we also touch the background collections
                    throw new RuntimeException("Error! Background loading is already running");
                }
            }

            // XXX: Throw an exception if we are already loading (since we touch the worker thread
            //      data structures, we can't allow any other thread to touch that data, but because
            //      this call is synchronous, we can get away with not locking).

            // The LauncherModel is static in the LauncherAppState and mHandler may have queued
            // operations from the previous activity.  We need to ensure that all queued operations
            // are executed before any synchronous binding work is done.
            mHandler.flush();

            // Divide the set of loaded items into those that we are binding synchronously, and
            // everything else that is to be bound normally (asynchronously).
            bindWorkspace(synchronousBindPage);
        }

        public void run() {
            synchronized (mLock) {
                if (mStopped) {
                    return;
                }
                mIsLoaderTaskRunning = true;
            }
            // Optimize for end-user experience: if the Launcher is up and // running with the
            // All Apps interface in the foreground, load All Apps first. Otherwise, load the
            // workspace first (default).
            keep_running:
            {
                if (DEBUG_LOADERS) Log.d(TAG, "step 1: loading workspace");
                loadAndBindWorkspace();

                if (mStopped) {
                    break keep_running;
                }

                waitForIdle();

                // second step
                if (DEBUG_LOADERS) Log.d(TAG, "step 2: loading all apps");
                loadAndBindAllApps();

                waitForIdle();

            }

            // Clear out this reference, otherwise we end up holding it until all of the
            // callback runnables are done.
            mContext = null;

            synchronized (mLock) {
                // If we are still the last one to be scheduled, remove ourselves.
                if (mLoaderTask == this) {
                    mLoaderTask = null;
                }
                mIsLoaderTaskRunning = false;
                mHasLoaderCompletedOnce = true;
            }
        }

        public void stopLocked() {
            synchronized (LoaderTask.this) {
                mStopped = true;
                this.notify();
            }
        }

        /**
         * Gets the callbacks object.  If we've been stopped, or if the launcher object
         * has somehow been garbage collected, return null instead.  Pass in the Callbacks
         * object that was around when the deferred message was scheduled, and if there's
         * a new Callbacks object around then also return null.  This will save us from
         * calling onto it with data that will be ignored.
         */
        Callbacks tryGetCallbacks(Callbacks oldCallbacks) {
            synchronized (mLock) {
                if (mStopped) {
                    return null;
                }

                if (mCallbacks == null) {
                    return null;
                }

                final Callbacks callbacks = mCallbacks.get();
                if (callbacks != oldCallbacks) {
                    return null;
                }
                if (callbacks == null) {
                    Log.w(TAG, "no mCallbacks");
                    return null;
                }

                return callbacks;
            }
        }

        // check & update map of what's occupied; used to discard overlapping/invalid items
//        private boolean checkItemPlacement(LongArrayMap<GridOccupancy> occupied, ItemInfo item,
//                                           ArrayList<Long> workspaceScreens) {
//            LauncherAppState app = LauncherAppState.getInstance();
//            InvariantDeviceProfile profile = app.getInvariantDeviceProfile();
//
//            long containerIndex = item.screenId;
//            if (item.container == LauncherSettings.CONTAINER_DESKTOP) {
//                if (!workspaceScreens.contains((Long) item.screenId)) {
//                    // The item has an invalid screen id.
//                    return false;
//                }
//            } else {
//                // Skip further checking if it is not the launcher_hotseat or workspace container
//                return true;
//            }
//
//            final int countX = profile.numColumns;
//            final int countY = profile.numRows;
//            if (item.container == LauncherSettings.CONTAINER_DESKTOP &&
//                    item.cellX < 0 || item.cellY < 0 ||
//                    item.cellX + item.spanX > countX || item.cellY + item.spanY > countY) {
//                Log.e(TAG, "Error loading shortcut " + item
//                        + " into cell (" + containerIndex + "-" + item.screenId + ":"
//                        + item.cellX + "," + item.cellY
//                        + ") out of screen bounds ( " + countX + "x" + countY + ")");
//                return false;
//            }
//
////            if (!occupied.containsKey(item.screenId)) {
////                GridOccupancy screen = new GridOccupancy(countX + 1, countY + 1);
////                if (item.screenId == Workspace.FIRST_SCREEN_ID) {
////                    // Mark the first row as occupied (if the feature is enabled)
////                    // in order to account for the QSB.
////                    screen.markCells(0, 0, countX + 1, 1, FeatureFlags.QSB_ON_FIRST_SCREEN);
////                }
////                occupied.put(item.screenId, screen);
////            }
//            final GridOccupancy occupancy = occupied.get(item.screenId);
//
//            // Check if any workspace icons overlap with each other
//            if (occupancy != null && occupancy.isRegionVacant(item.cellX, item.cellY, item.spanX, item.spanY)) {
//                occupancy.markCells(item, true);
//                return true;
//            } else {
//                Log.e(TAG, "Error loading shortcut " + item
//                        + " into cell (" + containerIndex + "-" + item.screenId + ":"
//                        + item.cellX + "," + item.cellX + "," + item.spanX + "," + item.spanY
//                        + ") already occupied");
//                return false;
//            }
//        }

        /**
         * Clears all the sBg data structures
         */
        private void clearSBgDataStructures() {
            synchronized (sBgLock) {
                sBgWorkspaceItems.clear();
                sBgFolders.clear();
                sBgItemsIdMap.clear();
                sBgWorkspaceScreens.clear();
            }
        }

        private void loadWorkspace() {
            if (LauncherAppState.PROFILE_STARTUP) {
                Trace.beginSection("Loading Workspace");
            }

            synchronized (sBgLock) {
                clearSBgDataStructures();

                sBgWorkspaceScreens.addAll(loadWorkspaceScreensDb(mContext));
                try {
                    List<DJCell> djCellList = djCellCommonDaoUtils.queryAll();
                    if (djCellList.size() > 0) {
                        for (DJCell djCell : djCellList) {
                            ShortcutInfo info = new ShortcutInfo();
                            int itemType = djCell.getItemType();
                            switch (itemType) {
                                case LauncherSettings.ITEM_TYPE_SHORTCUT:
                                    int disabledState = 0;
                                    if (info != null) {
                                        info.id = djCell.getId();
                                        info.container = djCell.getContainer();
                                        info.screenId = djCell.getScreenId();
                                        info.cellX = djCell.getCellX();
                                        info.cellY = djCell.getCellY();
                                        info.rank = djCell.getRank();
                                        info.color = djCell.getColor();
                                        info.title = djCell.getName();
                                        info.MODE = djCell.getMODE();
                                        info.connectMethod = djCell.getConnectMethod();
                                        info.LightId = djCell.getLightId();
                                        info.Fid = djCell.getFid();
                                        info.spanX = 1;
                                        info.spanY = 1;
                                        info.isDisabled |= disabledState;
                                        if (djCell != null && !TextUtils.isEmpty(djCell.getLightId()) && djCell.getLightId().startsWith("B")) {
                                            //LightEquipmentGroup lightEquipmentGroup= getLightEquipmentGroup(djCell.getLightId());
                                            info.LightName = "群组";
                                        } else {
//                                            LightEquipment lightEquipment= getLightEquipment(djCell.getLightId());
//                                            info.LightName=lightEquipment.getName();
                                            info.LightName = "Cell";
                                        }

                                        // check & update map of what's occupied
//                                        if (!checkItemPlacement(occupied, info, sBgWorkspaceScreens)) {
//                                            itemsToRemove.add(djCell.getId());
//                                            break;
//                                        }


                                        if (djCell.getContainer() == LauncherSettings.CONTAINER_DESKTOP) {
                                            sBgWorkspaceItems.add(info);
                                        } else {// Item is in a user folder
                                            FolderInfo folderInfo =
                                                    findOrMakeFolder(sBgFolders, djCell.getContainer());
                                            folderInfo.add(info, false);
                                        }
                                        sBgItemsIdMap.put(info.id, info);
                                    } else {
                                        throw new RuntimeException("Unexpected null ShortcutInfo");
                                    }
                                    break;

                                case LauncherSettings.ITEM_TYPE_FOLDER:
                                    FolderInfo folderInfo = findOrMakeFolder(sBgFolders, djCell.getId());

                                    // Do not trim the folder label, as is was set by the user.
                                    folderInfo.title = djCell.getName();
                                    folderInfo.id = djCell.getId();
                                    folderInfo.container = djCell.getContainer();
                                    folderInfo.screenId = djCell.getScreenId();
                                    folderInfo.cellX = djCell.getCellX();
                                    folderInfo.cellY = djCell.getCellY();
                                    folderInfo.spanX = 1;
                                    folderInfo.spanY = 1;
//                                    // check & update map of what's occupied
//                                    if (!checkItemPlacement(occupied, folderInfo, sBgWorkspaceScreens)) {
//                                        itemsToRemove.add(djCell.getId());
//                                        break;
//                                    }

                                    if (djCell.getContainer() == LauncherSettings.CONTAINER_DESKTOP) {
                                        sBgWorkspaceItems.add(folderInfo);
                                    }
                                    sBgItemsIdMap.put(folderInfo.id, folderInfo);
                                    sBgFolders.put(folderInfo.id, folderInfo);
                                    break;
                            }
                        }
                    }
                } finally {

                }

                // Break early if we've stopped loading
//                if (mStopped) {
//                    clearSBgDataStructures();
//                    return;
//                }

//                if (itemsToRemove.size() > 0) {
//                    for(Long itemsId:itemsToRemove){
//                        // Remove dead items
//                        QueryBuilder<DJCell> queryBuilder = djCellCommonDaoUtils.getDaoSession().queryBuilder(DJCell.class);
//                        queryBuilder.where(DJCellDao.Properties.Id.eq(index)).buildDelete().executeDeleteWithoutDetachingEntities();
//                    }
//
//                    contentResolver.delete(LauncherSettings.Favorites.CONTENT_URI,
//                            Utilities.createDbSelectionQuery(
//                                    LauncherSettings.Favorites._ID, itemsToRemove), null);
//                    // Remove any empty folder
//                    ArrayList<Long> deletedFolderIds = (ArrayList<Long>) LauncherSettings.Settings
//                            .call(contentResolver,
//                                    LauncherSettings.Settings.METHOD_DELETE_EMPTY_FOLDERS)
//                            .getSerializable(LauncherSettings.Settings.EXTRA_VALUE);
//                    for (long folderId : deletedFolderIds) {
//                        sBgWorkspaceItems.remove(sBgFolders.get(folderId));
//                        sBgFolders.remove(folderId);
//                        sBgItemsIdMap.remove(folderId);
//                    }
//                }

                // Sort all the folder items and make sure the first 3 items are high resolution.
//                for (FolderInfo folder : sBgFolders) {
//                    Collections.sort(folder.contents, Folder.ITEM_POS_COMPARATOR);
//                    int pos = 0;
//                    for (ShortcutInfo info : folder.contents) {
//                        pos++;
//                        if (pos >= FolderIcon.NUM_ITEMS_IN_PREVIEW) {
//                            break;
//                        }
//                    }
            }

            // Remove any empty screens
//                ArrayList<Long> unusedScreens = new ArrayList<Long>(sBgWorkspaceScreens);
//                for (ItemInfo item : sBgItemsIdMap) {
//                    long screenId = item.screenId;
//                    if (item.container == LauncherSettings.CONTAINER_DESKTOP &&
//                            unusedScreens.contains(screenId)) {
//                        unusedScreens.remove(screenId);
//                    }
//                }
//
//                // If there are any empty screens remove them, and update.
//                if (unusedScreens.size() != 0) {
//                    sBgWorkspaceScreens.removeAll(unusedScreens);
//                    updateWorkspaceScreenOrder(context, sBgWorkspaceScreens);
//                }
//
//                if (DEBUG_LOADERS) {
//                    Log.d(TAG, "loaded workspace in " + (SystemClock.uptimeMillis() - t) + "ms");
//                    Log.d(TAG, "workspace layout: ");
//                    int nScreens = occupied.size();
//                    for (int y = 0; y < countY; y++) {
//                        String line = "";
//
//                        for (int i = 0; i < nScreens; i++) {
//                            long screenId = occupied.keyAt(i);
//                            if (screenId > 0) {
//                                line += " | ";
//                            }
//                        }
//                        Log.d(TAG, "[ " + line + " ]");
//                    }
//                }
//            }
//            if (LauncherAppState.PROFILE_STARTUP) {
//                Trace.endSection();
//            }

        }


        /**
         * Filters the set of items who are directly or indirectly (via another container) on the
         * specified screen.
         */
        private void filterCurrentWorkspaceItems(long currentScreenId,
                                                 ArrayList<ItemInfo> allWorkspaceItems,
                                                 ArrayList<ItemInfo> currentScreenItems,
                                                 ArrayList<ItemInfo> otherScreenItems) {
            // Purge any null ItemInfos
            Iterator<ItemInfo> iter = allWorkspaceItems.iterator();
            while (iter.hasNext()) {
                ItemInfo i = iter.next();
                if (i == null) {
                    iter.remove();
                }
            }

            Set<Long> itemsOnScreen = new HashSet<Long>();
            Collections.sort(allWorkspaceItems, new Comparator<ItemInfo>() {
                @Override
                public int compare(ItemInfo lhs, ItemInfo rhs) {
                    return Utilities.longCompare(lhs.container, rhs.container);
                }
            });
            for (ItemInfo info : allWorkspaceItems) {
                if (info.container == LauncherSettings.CONTAINER_DESKTOP) {
                    if (info.screenId == currentScreenId) {
                        currentScreenItems.add(info);
                        itemsOnScreen.add(info.id);
                    } else {
                        otherScreenItems.add(info);
                    }
                } else {
                    if (itemsOnScreen.contains(info.container)) {
                        currentScreenItems.add(info);
                        itemsOnScreen.add(info.id);
                    } else {
                        otherScreenItems.add(info);
                    }
                }
            }
        }


        /**
         * Sorts the set of items by launcher_hotseat, workspace (spatially from top to bottom, left to
         * right)
         */
        private void sortWorkspaceItemsSpatially(ArrayList<ItemInfo> workspaceItems) {
            final LauncherAppState app = LauncherAppState.getInstance();
            final InvariantDeviceProfile profile = app.getInvariantDeviceProfile();
            final int screenCols = profile.numColumns;
            final int screenCellCount = profile.numColumns * profile.numRows;
            Collections.sort(workspaceItems, new Comparator<ItemInfo>() {
                @Override
                public int compare(ItemInfo lhs, ItemInfo rhs) {
                    if (lhs.container == rhs.container) {
                        // Within containers, order by their spatial position in that container
                        switch ((int) lhs.container) {
                            case LauncherSettings.CONTAINER_DESKTOP: {
                                long lr = (lhs.screenId * screenCellCount +
                                        lhs.cellY * screenCols + lhs.cellX);
                                long rr = (rhs.screenId * screenCellCount +
                                        rhs.cellY * screenCols + rhs.cellX);
                                return Utilities.longCompare(lr, rr);
                            }
                            default:
                                if (ProviderConfig.IS_DOGFOOD_BUILD) {
                                    throw new RuntimeException("Unexpected container type when " +
                                            "sorting workspace items.");
                                }
                                return 0;
                        }
                    } else {
                        // Between containers, order by launcher_hotseat, desktop
                        return Utilities.longCompare(lhs.container, rhs.container);
                    }
                }
            });
        }

        private void bindWorkspaceScreens(final Callbacks oldCallbacks,
                                          final ArrayList<Long> orderedScreens) {
            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                    if (callbacks != null) {
                        callbacks.bindScreens(orderedScreens);
                    }
                }
            };
            runOnMainThread(r);
        }

        private void bindWorkspaceItems(final Callbacks oldCallbacks,
                                        final ArrayList<ItemInfo> workspaceItems,
                                        final Executor executor) {

            // Bind the workspace items
            int N = workspaceItems.size();
            for (int i = 0; i < N; i += ITEMS_CHUNK) {
                final int start = i;
                final int chunkSize = (i + ITEMS_CHUNK <= N) ? ITEMS_CHUNK : (N - i);
                final Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                        if (callbacks != null) {
                            callbacks.bindItems(workspaceItems, start, start + chunkSize,
                                    false);
                        }
                    }
                };
                executor.execute(r);
            }

        }

        /**
         * Binds all loaded data to actual views on the main thread.
         */
        private void bindWorkspace(int synchronizeBindPage) {
            final long t = SystemClock.uptimeMillis();
            Runnable r;

            // Don't use these two variables in any of the callback runnables.
            // Otherwise we hold a reference to them.
            final Callbacks oldCallbacks = mCallbacks.get();
            if (oldCallbacks == null) {
                // This launcher has exited and nobody bothered to tell us.  Just bail.
                Log.w(TAG, "LoaderTask running with no launcher");
                return;
            }

            // Save a copy of all the bg-thread collections
            ArrayList<ItemInfo> workspaceItems = new ArrayList<>();
            ArrayList<Long> orderedScreenIds = new ArrayList<>();

            synchronized (sBgLock) {
                workspaceItems.addAll(sBgWorkspaceItems);
                orderedScreenIds.addAll(sBgWorkspaceScreens);
            }

            final int currentScreen;
            {
                int currScreen = synchronizeBindPage != PagedView.INVALID_RESTORE_PAGE
                        ? synchronizeBindPage : oldCallbacks.getCurrentWorkspaceScreen();
                if (currScreen >= orderedScreenIds.size()) {
                    // There may be no workspace screens (just launcher_hotseat items and an empty page).
                    currScreen = PagedView.INVALID_RESTORE_PAGE;
                }
                currentScreen = currScreen;
            }
            final boolean validFirstPage = currentScreen >= 0;
            final long currentScreenId =
                    validFirstPage ? orderedScreenIds.get(currentScreen) : INVALID_SCREEN_ID;

            // Separate the items that are on the current screen, and all the other remaining items
            ArrayList<ItemInfo> currentWorkspaceItems = new ArrayList<>();
            ArrayList<ItemInfo> otherWorkspaceItems = new ArrayList<>();

            filterCurrentWorkspaceItems(currentScreenId, workspaceItems, currentWorkspaceItems,
                    otherWorkspaceItems);
            sortWorkspaceItemsSpatially(currentWorkspaceItems);
            sortWorkspaceItemsSpatially(otherWorkspaceItems);


            bindWorkspaceScreens(oldCallbacks, orderedScreenIds);

            Executor mainExecutor = new DeferredMainThreadExecutor();
            // Load items on the current page.
            bindWorkspaceItems(oldCallbacks, currentWorkspaceItems, mainExecutor);

            final Executor deferredExecutor =
                    validFirstPage ? new ViewOnDrawExecutor(mHandler) : mainExecutor;

            mainExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                    if (callbacks != null) {
                        callbacks.finishFirstPageBind(
                                validFirstPage ? (ViewOnDrawExecutor) deferredExecutor : null);
                    }
                }
            });

            bindWorkspaceItems(oldCallbacks, otherWorkspaceItems, deferredExecutor);

            // Tell the workspace that we're done binding items
            r = new Runnable() {
                public void run() {
                    Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                    if (callbacks != null) {
                        callbacks.finishBindingItems();
                    }

                    mIsLoadingAndBindingWorkspace = false;

                    // Run all the bind complete runnables after workspace is bound.
                    if (!mBindCompleteRunnables.isEmpty()) {
                        synchronized (mBindCompleteRunnables) {
                            for (final Runnable r : mBindCompleteRunnables) {
                                runOnWorkerThread(r);
                            }
                            mBindCompleteRunnables.clear();
                        }
                    }

                    // If we're profiling, ensure this is the last thing in the queue.
                    if (DEBUG_LOADERS) {
                        Log.d(TAG, "bound workspace in "
                                + (SystemClock.uptimeMillis() - t) + "ms");
                    }

                }
            };
            deferredExecutor.execute(r);

            if (validFirstPage) {
                r = new Runnable() {
                    public void run() {
                        Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                        if (callbacks != null) {
                            // We are loading synchronously, which means, some of the pages will be
                            // bound after first draw. Inform the callbacks that page binding is
                            // not complete, and schedule the remaining pages.
                            if (currentScreen != PagedView.INVALID_RESTORE_PAGE) {
                                callbacks.onPageBoundSynchronously(currentScreen);
                            }
                            callbacks.executeOnNextDraw((ViewOnDrawExecutor) deferredExecutor);
                        }
                    }
                };
                runOnMainThread(r);
            }
        }

        private void loadAndBindAllApps() {
            if (DEBUG_LOADERS) {
                Log.d(TAG, "loadAndBindAllApps mAllAppsLoaded=" + mAllAppsLoaded);
            }
            if (!mAllAppsLoaded) {
                synchronized (LoaderTask.this) {
                    if (mStopped) {
                        return;
                    }
                }
                synchronized (LoaderTask.this) {
                    if (mStopped) {
                        return;
                    }
                    mAllAppsLoaded = true;
                }
            }
        }

    }

    static ArrayList<ItemInfo> filterItemInfos(Iterable<ItemInfo> infos,
                                               ItemInfoFilter f) {
        HashSet<ItemInfo> filtered = new HashSet<ItemInfo>();
        for (ItemInfo i : infos) {
            if (i instanceof ShortcutInfo) {
                ShortcutInfo info = (ShortcutInfo) i;
                ComponentName cn = info.getTargetComponent();
                if (cn != null && f.filterItem(null, info, cn)) {
                    filtered.add(info);
                }
            } else if (i instanceof FolderInfo) {
                FolderInfo info = (FolderInfo) i;
                for (ShortcutInfo s : info.contents) {
                    ComponentName cn = s.getTargetComponent();
                    if (cn != null && f.filterItem(info, s, cn)) {
                        filtered.add(s);
                    }
                }
            }
        }
        return new ArrayList<ItemInfo>(filtered);
    }


    /**
     * Return an existing FolderInfo object if we have encountered this ID previously,
     * or make a new one.
     */
    @Thunk
    static FolderInfo findOrMakeFolder(LongArrayMap<FolderInfo> folders, long id) {
        // See if a placeholder was created for us already
        FolderInfo folderInfo = folders.get(id);
        if (folderInfo == null) {
            // No placeholder -- create a new instance
            folderInfo = new FolderInfo();
            folders.put(id, folderInfo);
        }
        return folderInfo;
    }


    public Callbacks getCallback() {
        return mCallbacks != null ? mCallbacks.get() : null;
    }


    @Thunk
    class DeferredMainThreadExecutor implements Executor {

        @Override
        public void execute(Runnable command) {
            runOnMainThread(command);
        }
    }

    //获取单元的最大Id
    public static long generateNewItemId() {
        Cursor c = djCellCommonDaoUtils.getDaoSession().getDatabase().rawQuery("SELECT MAX(" + DJCellDao.Properties.Id.columnName + ") FROM " + DJCellDao.TABLENAME, null);
        // get the result
        long id = -1;
        if (c != null && c.moveToNext()) {
            id = c.getLong(0);
        }
        if (c != null) {
            c.close();
        }

        if (id == -1) {
            throw new RuntimeException("Error: could not query max id in " + DJScreensDao.TABLENAME);
        }
        long mMaxItemId = id;
        if (mMaxItemId < 0) {
            throw new RuntimeException("Error: max item id was not initialized");
        }
        mMaxItemId += 1;
        return mMaxItemId;
    }


    //获取全部单元
    public static List<DJCell> getDjCellList() {
        return djCellCommonDaoUtils.queryAll();
    }

    //获取屏幕的最大Id
    public static long generateNewScreenId() {
        Cursor c = djScreensCommonDaoUtils.getDaoSession().getDatabase().rawQuery("SELECT MAX(" + DJScreensDao.Properties.Id.columnName + ") FROM " + DJScreensDao.TABLENAME, null);
        // get the result
        long id = -1;
        if (c != null && c.moveToNext()) {
            id = c.getLong(0);
        }
        if (c != null) {
            c.close();
        }

        if (id == -1) {
            throw new RuntimeException("Error: could not query max id in " + DJScreensDao.TABLENAME);
        }
        long mMaxScreenId = id;
        if (mMaxScreenId < 0) {
            throw new RuntimeException("Error: max item id was not initialized");
        }
        mMaxScreenId += 1;
        return mMaxScreenId;
    }

//    public static LightEquipment getLightEquipment(String LightId){
//        LightEquipment lightEquipment=lightEquipmentCommonDaoUtils.queryBykey(LightId);
//        return lightEquipment;
//    }
//    public static LightEquipmentGroup getLightEquipmentGroup(String LightId){
//        LightEquipmentGroup lightEquipmentGroup=lightEquipmentGroupCommonDaoUtils.queryBykey(LightId);
//        return lightEquipmentGroup;
//    }
//
//    public static List<LightEquipment> getLightEquipmentList(String groupId){
//        List<LightEquipment> lightEquipmentList= lightEquipmentCommonDaoUtils.queryByNativeSql("where diy_id in ( select diy_id from LIGHT_EQUIPMENT_GROUP_DATA where GROUP_ID=?)"+" order by "+ LightEquipmentDao.Properties.CreatTime.columnName+" desc",new String[]{groupId});
//        return lightEquipmentList;
//    }


//    /**
//     * Deletes any empty folder from the DB.
//     * @return Ids of deleted folders.
//     */
//    private ArrayList<Long> deleteEmptyFolders() {
//        ArrayList<Long> folderIds = new ArrayList<>();
//        try {
//            // Select folders whose id do not match any container value.
//            String selection = LauncherSettings.Favorites.ITEM_TYPE + " = "
//                    + LauncherSettings.Favorites.ITEM_TYPE_FOLDER + " AND "
//                    + LauncherSettings.Favorites._ID +  " NOT IN (SELECT " +
//                    LauncherSettings.Favorites.CONTAINER + " FROM "
//                    + Favorites.TABLE_NAME + ")";
//            Cursor c = db.query(Favorites.TABLE_NAME,
//                    new String[] {LauncherSettings.Favorites._ID},
//                    selection, null, null, null, null);
//            while (c.moveToNext()) {
//                folderIds.add(c.getLong(0));
//            }
//            c.close();
//            if (!folderIds.isEmpty()) {
//                db.delete(Favorites.TABLE_NAME, Utilities.createDbSelectionQuery(
//                        LauncherSettings.Favorites._ID, folderIds), null);
//            }
//            db.setTransactionSuccessful();
//        } catch (SQLException ex) {
//            Log.e(TAG, ex.getMessage(), ex);
//            folderIds.clear();
//        } finally {
//            db.endTransaction();
//        }
//        return folderIds;
//    }

}
