package com.example.customerlauncher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Trace;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.Advanceable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.launcher3.BubbleTextView;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.FirstFrameAnimatorHelper;
import com.android.launcher3.FolderInfo;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherCallbacks;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.LogDecelerateInterpolator;
import com.android.launcher3.PagedView;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.keyboard.ViewGroupFocusHelper;
import com.android.launcher3.util.Thunk;
import com.android.launcher3.util.ViewOnDrawExecutor;
import com.example.customerlauncher3.bean.EventUpdateDJCell;
import com.example.customerlauncher3.bean.SelectMenuBean;
import com.example.customerlauncher3.dialog.ConfirmEditDialog;
import com.example.customerlauncher3.dialog.SelectMenu2Dialog;
import com.example.customerlauncher3.greendao.CommonDaoUtils;
import com.example.customerlauncher3.greendao.entity.DJCell;
import com.github.mmin18.widget.RealtimeBlurView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


/**
 * Default launcher application.
 */
public class DeviceDJMainActivity extends Activity
        implements OnClickListener, OnLongClickListener,
        LauncherModel.Callbacks {
    public static final String TAG = "Launcher";

    private LinearLayout toolbar2;
    private View viewStatues;
    private LinearLayout llBack2;
    private ImageView ivBack2;
    private LinearLayout llTitleLayout2;
    private TextView tvHeadTitle2;
    private LinearLayout llTitleRight2;
    private TextView llMenuNewCell;
    private TextView tvTitleRight2;
    private View viewTitleLine2;


    private boolean isSwitch = false;

    private Bundle bundle;
    //    private String project_id;
    private String project_name;
    private String title;
    private String LightId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LauncherAppState app = LauncherAppState.getInstance();
        mDeviceProfile = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE ?
                app.getInvariantDeviceProfile().landscapeProfile
                : app.getInvariantDeviceProfile().portraitProfile;
        mModel = app.setLauncher(this);
        mDragController = new DragController(this);
        mPaused = false;


        setContentView(R.layout.activity_device_dj_main);

        setupViews();
        mDeviceProfile.layout(this, false);
        mSavedState = savedInstanceState;
        restoreState(mSavedState);
        if (!mModel.startLoader(mWorkspace.getRestorePage())) {
            mDragLayer.setAlpha(0);
        } else {
            setWorkspaceLoading(true);
        }
        app.reloadWorkspace();
        if (mLauncherCallbacks != null) {
            mLauncherCallbacks.onCreate(savedInstanceState);
        }
        mWorkspace.removeExtraEmptyScreen(false, true);

        initView();
        initData();
    }


    public void initView() {
        toolbar2 = (LinearLayout) findViewById(R.id.toolbar2);
        viewStatues = (View) findViewById(R.id.view_statues);
        llBack2 = (LinearLayout) findViewById(R.id.ll_back2);
        ivBack2 = (ImageView) findViewById(R.id.iv_back2);
        llTitleLayout2 = (LinearLayout) findViewById(R.id.ll_title_layout2);
        tvHeadTitle2 = (TextView) findViewById(R.id.tv_head_title2);
        llTitleRight2 = (LinearLayout) findViewById(R.id.ll_title_right2);
        llMenuNewCell = (TextView) findViewById(R.id.ll_menu_new_cell);
        tvTitleRight2 = (TextView) findViewById(R.id.tv_title_right2);
        viewTitleLine2 = (View) findViewById(R.id.view_title_line2);


        project_name = getIntent().getStringExtra("project_name");
//        project_id = getIntent().getStringExtra("project_id");
//        Consts.CONTROL_DJ_PROJECT_ID = project_id;

        //改变标题栏居中方式
//        llTitleLayout2.setGravity(LinearLayout.HORIZONTAL);
//        llTitleLayout2.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
//        tvHeadTitle2.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        tvHeadTitle2.setTextColor(getResources().getColor(R.color.white));

        ivBack2.setVisibility(View.VISIBLE);

    }


    public void initData() {
        ivBack2.setOnClickListener(this);
        llMenuNewCell.setOnClickListener(this);
        tvTitleRight2.setOnClickListener(this);
    }


    //主页面相关点击事件
    private void clickMainView(View v) {
        switch (v.getId()) {
            case R.id.iv_back2://返回
                finish();
                break;
            case R.id.ll_menu_new_cell://创建单元
                ConfirmEditDialog newSceneGroupDialog = new ConfirmEditDialog(this, R.style.dialog, "创建单元", "确定");
                newSceneGroupDialog.show();
                newSceneGroupDialog.setDialogViewListener(new ConfirmEditDialog.DialogViewListener() {
                    @Override
                    public void sureClick(String content) {
                        title = content;
//                        bundle = new Bundle();
//                        bundle.putInt("fromType", Consts.CONTROL_FROM_TYPE_DJ);
//                        bundle.putString("control_dj_id", String.valueOf(LauncherModel.generateNewItemId()));
//                        startBundleActivity(DeviceTimelineDJSelectActivity.class, bundle);
                        EventUpdateDJCell eventUpdateDJCell = new EventUpdateDJCell("0","0",0,0,"#000000");
                        ShortcutInfo shortcutInfo = (ShortcutInfo) mModel.sBgItemsIdMap.get(Long.parseLong(eventUpdateDJCell.getControl_dj_id()));
                        if (shortcutInfo == null) {
                            addShortcut(eventUpdateDJCell);
                        } else {
                            ArrayList<ShortcutInfo> updatedShortcutInfos = new ArrayList<>();
                            ArrayList<ShortcutInfo> deletedShortcutInfos = new ArrayList<>();
                            shortcutInfo.MODE = eventUpdateDJCell.getMODE();
                            shortcutInfo.connectMethod = eventUpdateDJCell.getConnectMethod();
                            shortcutInfo.color = eventUpdateDJCell.getColor();
                            updatedShortcutInfos.add(shortcutInfo);
                            bindShortcutsChanged(updatedShortcutInfos, deletedShortcutInfos);
                            LauncherModel.updateItemInDatabase(DeviceDJMainActivity.this, shortcutInfo);
                        }
                    }
                });
                break;
            case R.id.tv_title_right2://完成
                //0完成 1管理 2编辑
                LauncherModel.CHANGESTATUESTYPE++;
                changeStatuesType();
                if (LauncherModel.CHANGESTATUESTYPE >= 2) {
                    LauncherModel.CHANGESTATUESTYPE = -1;
                }
                break;
        }
    }

//    @Override
//    public void onEventBusCome(Event event) {
//        super.onEventBusCome(event);
//        switch (event.getCode()) {
//            case EventCode.EVENT_DJ_LIST://更新DJ列表
//                LightId = (String) event.getData();
//
//                break;
//            case EventCode.EVENT_DJ_MODE://更新或者添加DJ模式
//                EventUpdateDJCell eventUpdateDJCell = (EventUpdateDJCell) event.getData();
//                ShortcutInfo shortcutInfo = (ShortcutInfo) mModel.sBgItemsIdMap.get(Long.parseLong(eventUpdateDJCell.getControl_dj_id()));
//                if (shortcutInfo == null) {
//                    addShortcut(eventUpdateDJCell);
//                } else {
//                    ArrayList<ShortcutInfo> updatedShortcutInfos = new ArrayList<>();
//                    ArrayList<ShortcutInfo> deletedShortcutInfos = new ArrayList<>();
//                    shortcutInfo.MODE = eventUpdateDJCell.getMODE();
//                    shortcutInfo.connectMethod = eventUpdateDJCell.getConnectMethod();
//                    shortcutInfo.color = eventUpdateDJCell.getColor();
//                    updatedShortcutInfos.add(shortcutInfo);
//                    bindShortcutsChanged(updatedShortcutInfos, deletedShortcutInfos);
//                    LauncherModel.updateItemInDatabase(this, shortcutInfo);
//                }
//                break;
//
//        }
//    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 桌面开始
     */
    private static final float BOUNCE_ANIMATION_TENSION = 1.3f;
    private static final String RUNTIME_STATE_CURRENT_SCREEN = "launcher.current_screen";
    private static final String RUNTIME_STATE = "launcher.state";

    public enum State {
        NONE, WORKSPACE, WORKSPACE_SPRING_LOADED, APPS, APPS_SPRING_LOADED,
        WIDGETS, WIDGETS_SPRING_LOADED
    }

    public State mState = State.WORKSPACE;
    public static final int APPWIDGET_HOST_ID = 1024;
    public static int NEW_APPS_PAGE_MOVE_DELAY = 500;
    public static int NEW_APPS_ANIMATION_INACTIVE_TIMEOUT_SECONDS = 5;
    public static int NEW_APPS_ANIMATION_DELAY = 500;
    public Workspace mWorkspace;
    public DragLayer mDragLayer;
    public DragController mDragController;
    public int[] mTmpAddItemCellCoordinates = new int[2];
    public Bundle mSavedState;
    public boolean mWorkspaceLoading = true;
    public boolean mPaused = true;
    public boolean mOnResumeNeedsLoad;
    public ArrayList<Runnable> mBindOnResumeCallbacks = new ArrayList<Runnable>();
    public ArrayList<Runnable> mOnResumeCallbacks = new ArrayList<Runnable>();
    public ViewOnDrawExecutor mPendingExecutor;
    public LauncherModel mModel;
    public View.OnTouchListener mHapticFeedbackTouchListener;
    public final int ADVANCE_MSG = 1;
    public static final int ADVANCE_INTERVAL = 20000;
    public static final int ADVANCE_STAGGER = 250;
    public HashMap<View, AppWidgetProviderInfo> mWidgetsToAdvance = new HashMap<>();
    public final ArrayList<Integer> mSynchronouslyBoundPages = new ArrayList<Integer>();
    public ImageView mFolderIconImageView;
    public Bitmap mFolderIconBitmap;
    public Canvas mFolderIconCanvas;
    public Rect mRectForFolderAnimation = new Rect();

    public DeviceProfile mDeviceProfile;

    public long mAutoAdvanceTimeLeft = -1;
    public boolean mAutoAdvanceRunning = false;
    public long mAutoAdvanceSentTime;
    public boolean mAttached;
    public boolean mVisible;

    public ViewGroupFocusHelper mFocusHandler;

    public LauncherCallbacks mLauncherCallbacks;
    public RealtimeBlurView blurView;

    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mLauncherCallbacks != null) {
            mLauncherCallbacks.onPostCreate(savedInstanceState);
        }
    }

    public void onInsetsChanged(Rect insets) {
        mDeviceProfile.updateInsets(insets);
        mDeviceProfile.layout(this, true);
    }


    public boolean isDraggingEnabled() {
        return !isWorkspaceLoading();
    }

    public int getViewIdForItem(ItemInfo info) {
        return (int) info.id;
    }


    @Override
    protected void onStop() {
        super.onStop();
        FirstFrameAnimatorHelper.setIsVisible(false);

        if (mLauncherCallbacks != null) {
            mLauncherCallbacks.onStop();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirstFrameAnimatorHelper.setIsVisible(true);

        if (mLauncherCallbacks != null) {
            mLauncherCallbacks.onStart();
        }

    }

    @Override
    protected void onResume() {
        if (mLauncherCallbacks != null) {
            mLauncherCallbacks.preOnResume();
        }
        super.onResume();
        mPaused = false;
        if (mOnResumeNeedsLoad) {
            setWorkspaceLoading(true);
            //mModel.startLoader(getCurrentWorkspaceScreen());
            mOnResumeNeedsLoad = false;
        }
        if (mBindOnResumeCallbacks.size() > 0) {
            for (int i = 0; i < mBindOnResumeCallbacks.size(); i++) {
                mBindOnResumeCallbacks.get(i).run();
            }
            mBindOnResumeCallbacks.clear();

        }
        if (mOnResumeCallbacks.size() > 0) {
            for (int i = 0; i < mOnResumeCallbacks.size(); i++) {
                mOnResumeCallbacks.get(i).run();
            }
            mOnResumeCallbacks.clear();
        }


        if (mLauncherCallbacks != null) {
            mLauncherCallbacks.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        blurView.setVisibility(View.GONE);
        mPaused = true;
        mDragController.cancelDrag();
        mDragController.resetLastGestureUpTime();

        if (mLauncherCallbacks != null) {
            mLauncherCallbacks.onPause();
        }
    }

    public interface LauncherOverlay {

        /**
         * Touch interaction leading to overscroll has begun
         */
        public void onScrollInteractionBegin();

        /**
         * Touch interaction related to overscroll has ended
         */
        public void onScrollInteractionEnd();

        /**
         * Scroll progress, between 0 and 100, when the user scrolls beyond the leftmost
         * screen (or in the case of RTL, the rightmost screen).
         */
        public void onScrollChange(float progress, boolean rtl);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (mLauncherCallbacks != null) {
            mLauncherCallbacks.onWindowFocusChanged(hasFocus);
        }
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (!isOnCustomContent() && !mDragController.isDragging()) {
                // Close any open folders
                closeFolder();

            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void restoreState(Bundle savedState) {
        if (savedState == null) {
            return;
        }
        int currentScreen = savedState.getInt(RUNTIME_STATE_CURRENT_SCREEN,
                PagedView.INVALID_RESTORE_PAGE);
        if (currentScreen != PagedView.INVALID_RESTORE_PAGE) {
            mWorkspace.setRestorePage(currentScreen);
        }

    }

    /**
     * biekangdong
     * Finds all the views we need and configure them properly.
     */
    private void setupViews() {
        mDragLayer = (DragLayer) findViewById(R.id.drag_layer);
        mFocusHandler = mDragLayer.getFocusIndicatorHelper();
        mWorkspace = (Workspace) mDragLayer.findViewById(R.id.workspace);

        mWorkspace.initParentViews(mDragLayer);

        // Setup the drag layer
        mDragLayer.setup(this, mDragController);
        mWorkspace.setOnLongClickListener(this);
        mWorkspace.setup(mDragController);
        // Until the workspace is bound, ensure that we keep the wallpaper offset locked to the
        // default state, otherwise we will update to the wrong offsets in RTL
        mWorkspace.bindAndInitFirstWorkspaceScreen(null /* recycled qsb */);
        mDragController.addDragListener(mWorkspace);


        // Setup the drag controller (drop targets have to be added in reverse order in priority)
        mDragController.setDragScoller(mWorkspace);
        mDragController.setScrollView(mDragLayer);
        mDragController.setMoveTarget(mWorkspace);
        mDragController.addDropTarget(mWorkspace);

        blurView = findViewById(R.id.blur_view);
        blurView.setBlurRadius(10);
        blurView.setOverlayColor(Color.TRANSPARENT);
        blurView.setVisibility(View.GONE);


    }

    long screenId = 0;

    private void addShortcut(EventUpdateDJCell eventUpdateDJCell) {
        if (screenId >= mModel.sBgWorkspaceScreens.size()) {
            screenId = mWorkspace.commitExtraEmptyScreen();
        }
        boolean isEmpty = false;
        CellLayout cellLayout = mWorkspace.getScreenWithId(screenId);
        int cellX = 0;
        int cellY = 0;
        tag2:
        for (int i = 0; i < 4; i++) {
            tag1:
            for (int j = 0; j < 4; j++) {
                cellY = i;
                cellX = j;
                if (cellLayout!=null&&cellLayout.getChildAt(cellX, cellY) == null) {
                    isEmpty = true;
                    break tag2;
                }
            }
        }
        if (isEmpty) {
            String color = eventUpdateDJCell.getColor();
            String MODE = eventUpdateDJCell.getMODE();
            int connectMethod = eventUpdateDJCell.getConnectMethod();
            int g_ledtype = eventUpdateDJCell.getG_ledtype();
            ItemInfo itemInfo = new ItemInfo();
            itemInfo.cellX = cellX;
            itemInfo.cellY = cellY;
            itemInfo.container = -100;
            itemInfo.title = title;
            itemInfo.itemType = 1;
            itemInfo.color = color;
            itemInfo.LightId = LightId+System.currentTimeMillis();
            itemInfo.MODE = MODE;
            itemInfo.connectMethod = connectMethod;
            itemInfo.g_ledtype = g_ledtype;
            if (!TextUtils.isEmpty(LightId)&&LightId.startsWith("B")) {
//                LightEquipmentGroup lightEquipmentGroup = mModel.getLightEquipmentGroup(LightId);
//                itemInfo.LightName = lightEquipmentGroup.getName();
                itemInfo.LightName = "群组";
            } else {
//                LightEquipment lightEquipment = mModel.getLightEquipment(LightId);
//                itemInfo.LightName = lightEquipment.getName();
                itemInfo.LightName = "Cell";
            }
            ShortcutInfo shortcutInfo = new ShortcutInfo(title, LightId, itemInfo.LightName, color, MODE, connectMethod, g_ledtype);
            completeAddShortcut(itemInfo.container, screenId, itemInfo.cellX, itemInfo.cellY, shortcutInfo);
        } else {
            screenId++;
            addShortcut(eventUpdateDJCell);
        }
    }


    View createShortcut(ShortcutInfo info) {
        return createShortcut((ViewGroup) mWorkspace.getChildAt(mWorkspace.getCurrentPage()), info);
    }

    public View createShortcut(ViewGroup parent, ShortcutInfo info) {
        BubbleTextView favorite = (BubbleTextView) LayoutInflater.from(this).inflate(R.layout.launcher_app_icon, null);
        favorite.applyFromShortcutInfo(info);
        favorite.setOnClickListener(this);
        favorite.setOnFocusChangeListener(mFocusHandler);
        return favorite;
    }

    private void completeAddShortcut(long container, long screenId, int cellX,
                                     int cellY, ShortcutInfo info) {
        int[] cellXY = mTmpAddItemCellCoordinates;
        CellLayout layout = getCellLayout(container, screenId);
        final View view = createShortcut(info);

        boolean foundCellSpan = false;
        // First we check if we already know the exact location where we want to add this item.
        if (cellX >= 0 && cellY >= 0) {
            cellXY[0] = cellX;
            cellXY[1] = cellY;
            foundCellSpan = true;

            // If appropriate, either create a folder or add to an existing folder
            if (mWorkspace.createUserFolderIfNecessary(view, container, layout, cellXY, 0,
                    true, null, null)) {
                return;
            }
            DragObject dragObject = new DragObject();
            dragObject.dragInfo = info;
            if (mWorkspace.addToExistingFolderIfNecessary(view, layout, cellXY, 0, dragObject,
                    true)) {
                return;
            }
        } else {
            foundCellSpan = layout.findCellForSpan(cellXY, 1, 1);
        }

        LauncherModel.addItemToDatabase(this, info, container, screenId, cellXY[0], cellXY[1]);

        mWorkspace.addInScreen(view, container, screenId, cellXY[0], cellXY[1], 1, 1,
                isWorkspaceLocked());
    }


    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mVisible = false;

        if (mAttached) {
            unregisterReceiver(mReceiver);
            mAttached = false;
        }
        updateAutoAdvanceState();

        if (mLauncherCallbacks != null) {
            mLauncherCallbacks.onDetachedFromWindow();
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                updateAutoAdvanceState();

            } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                updateAutoAdvanceState();
            }
        }
    };

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Listen for broadcasts related to user-presence
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mReceiver, filter);
        FirstFrameAnimatorHelper.initializeDrawListener(getWindow().getDecorView());
        mAttached = true;
        mVisible = true;

        if (mLauncherCallbacks != null) {
            mLauncherCallbacks.onAttachedToWindow();
        }
    }

    public void onWindowVisibilityChanged(int visibility) {
        mVisible = visibility == View.VISIBLE;
        updateAutoAdvanceState();
        if (mVisible) {
            if (!mWorkspaceLoading) {
                final ViewTreeObserver observer = mWorkspace.getViewTreeObserver();
                observer.addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
                    private boolean mStarted = false;

                    public void onDraw() {
                        if (mStarted) return;
                        mStarted = true;
                        final ViewTreeObserver.OnDrawListener listener = this;
                        mWorkspace.post(new Runnable() {
                            public void run() {
                                if (mWorkspace != null &&
                                        mWorkspace.getViewTreeObserver() != null) {
                                    mWorkspace.getViewTreeObserver().
                                            removeOnDrawListener(listener);
                                }
                            }
                        });
                        return;
                    }
                });
            }
        }
    }

    @Thunk
    void sendAdvanceMessage(long delay) {
        mHandler.removeMessages(ADVANCE_MSG);
        Message msg = mHandler.obtainMessage(ADVANCE_MSG);
        mHandler.sendMessageDelayed(msg, delay);
        mAutoAdvanceSentTime = System.currentTimeMillis();
    }

    @Thunk
    void updateAutoAdvanceState() {
        boolean autoAdvanceRunning = mVisible && !mWidgetsToAdvance.isEmpty();
        if (autoAdvanceRunning != mAutoAdvanceRunning) {
            mAutoAdvanceRunning = autoAdvanceRunning;
            if (autoAdvanceRunning) {
                long delay = mAutoAdvanceTimeLeft == -1 ? ADVANCE_INTERVAL : mAutoAdvanceTimeLeft;
                sendAdvanceMessage(delay);
            } else {
                if (!mWidgetsToAdvance.isEmpty()) {
                    mAutoAdvanceTimeLeft = Math.max(0, ADVANCE_INTERVAL -
                            (System.currentTimeMillis() - mAutoAdvanceSentTime));
                }
                mHandler.removeMessages(ADVANCE_MSG);
                mHandler.removeMessages(0); // Remove messages sent using postDelayed()
            }
        }
    }

    @Thunk
    final Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == ADVANCE_MSG) {
                int i = 0;
                for (View key : mWidgetsToAdvance.keySet()) {
                    final View v = key.findViewById(mWidgetsToAdvance.get(key).autoAdvanceViewId);
                    final int delay = ADVANCE_STAGGER * i;
                    if (v instanceof Advanceable) {
                        mHandler.postDelayed(new Runnable() {
                            public void run() {
                                ((Advanceable) v).advance();
                            }
                        }, delay);
                    }
                    i++;
                }
                sendAdvanceMessage(ADVANCE_INTERVAL);
            }
            return true;
        }
    });


    public DragLayer getDragLayer() {
        return mDragLayer;
    }

    public Workspace getWorkspace() {
        return mWorkspace;
    }


    public LauncherModel getModel() {
        return mModel;
    }

    public DeviceProfile getDeviceProfile() {
        return mDeviceProfile;
    }

    @Override
    public void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        for (int page : mSynchronouslyBoundPages) {
            mWorkspace.restoreInstanceStateForChild(page);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mWorkspace.getChildCount() > 0) {
            outState.putInt(RUNTIME_STATE_CURRENT_SCREEN,
                    mWorkspace.getCurrentPageOffsetFromCustomContent());

        }
        super.onSaveInstanceState(outState);

        outState.putInt(RUNTIME_STATE, mState.ordinal());
        closeFolder(false);
        if (mLauncherCallbacks != null) {
            mLauncherCallbacks.onSaveInstanceState(outState);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        blurView.setVisibility(View.GONE);
        LauncherModel.CHANGESTATUESTYPE = 0;
        mHandler.removeMessages(ADVANCE_MSG);
        mHandler.removeMessages(0);
        mWorkspace.removeFolderListeners();
        if (mModel.isCurrentCallbacks(this)) {
            mModel.stopLoader();
            LauncherAppState.getInstance().setLauncher(null);
        }
        mWidgetsToAdvance.clear();
        LauncherAnimUtils.onDestroyActivity();
        if (mLauncherCallbacks != null) {
            mLauncherCallbacks.onDestroy();
        }
    }

    public DragController getDragController() {
        return mDragController;
    }


    public boolean isOnCustomContent() {
        return mWorkspace.isOnOrMovingToCustomContent();
    }


    public boolean isWorkspaceLocked() {
        return mWorkspaceLoading;
    }

    public boolean isWorkspaceLoading() {
        return mWorkspaceLoading;
    }

    private void setWorkspaceLoading(boolean value) {
        boolean isLocked = isWorkspaceLocked();
        mWorkspaceLoading = value;
        if (isLocked != isWorkspaceLocked()) {
            onWorkspaceLockedChanged();
        }
    }


    protected void onWorkspaceLockedChanged() {
        if (mLauncherCallbacks != null) {
            mLauncherCallbacks.onWorkspaceLockedChanged();
        }
    }


    public FolderIcon addFolder(CellLayout layout, long container, final long screenId, int cellX,
                                int cellY) {
        final FolderInfo folderInfo = new FolderInfo();
        folderInfo.title = getText(R.string.folder_name);

        // Update the model
        LauncherModel.addItemToDatabase(DeviceDJMainActivity.this, folderInfo, container, screenId,
                cellX, cellY);

        // Create the view
        FolderIcon newFolder =
                FolderIcon.fromXml(R.layout.launcher_folder_icon, this, layout, folderInfo);
        mWorkspace.addInScreen(newFolder, container, screenId, cellX, cellY, 1, 1,
                isWorkspaceLocked());
        // Force measure the new folder icon
        CellLayout parent = mWorkspace.getParentCellLayoutForView(newFolder);
        parent.getShortcutsAndWidgets().measureChild(newFolder);
        return newFolder;
    }


    public boolean removeItem(View v, final ItemInfo itemInfo, boolean deleteFromDb) {
        if (itemInfo instanceof ShortcutInfo) {
            // Remove the shortcut from the folder before removing it from launcher
            View folderIcon = mWorkspace.getHomescreenIconByItemId(itemInfo.container);
            if (folderIcon instanceof FolderIcon) {
                ((FolderInfo) folderIcon.getTag()).remove((ShortcutInfo) itemInfo, true);
            } else {
                mWorkspace.removeWorkspaceItem(v);
            }
            if (deleteFromDb) {
                LauncherModel.deleteItemFromDatabase(this, itemInfo);
            }
        } else if (itemInfo instanceof FolderInfo) {
            final FolderInfo folderInfo = (FolderInfo) itemInfo;
            if (v instanceof FolderIcon) {
                ((FolderIcon) v).removeListeners();
            }
            mWorkspace.removeWorkspaceItem(v);
            if (deleteFromDb) {
                LauncherModel.deleteFolderAndContentsFromDatabase(folderInfo);
            }
        } else {
            return false;
        }
        mWorkspace.removeExtraEmptyScreen(true, true);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (mLauncherCallbacks != null && mLauncherCallbacks.handleBackPressed()) {
            return;
        }

        if (mDragController.isDragging()) {
            mDragController.cancelDrag();
            return;
        }

        if (mWorkspace.getOpenFolder() != null) {
            Folder openFolder = mWorkspace.getOpenFolder();
            if (openFolder.isEditingName()) {
                openFolder.dismissEditingName();
            } else {
                closeFolder();
            }
        } else {
            mWorkspace.showOutlinesTemporarily();
            finish();
        }
    }

    /**
     * Launches the intent referred by the clicked shortcut.
     *
     * @param v The view representing the clicked shortcut.
     */
    public void onClick(View v) {
        if (v.getWindowToken() == null) {
            return;
        }

        if (!mWorkspace.isFinishedSwitchingState()) {
            return;
        }


        Object tag = v.getTag();
        if (tag instanceof ShortcutInfo) {
            onClickAppShortcut(v);
        } else if (tag instanceof FolderInfo) {
            if (v instanceof FolderIcon) {
                onClickFolderIcon(v);
            }
        }

        clickMainView(v);
    }


    /**
     * bieakangdong 点击图标
     * Event handler for an app shortcut click.
     *
     * @param v The view that was clicked. Must be a tagged with a {@link ShortcutInfo}.
     */
    protected void onClickAppShortcut(final View v) {
        Object tag = v.getTag();
        if (!(tag instanceof ShortcutInfo)) {
            throw new IllegalArgumentException("Input must be a Shortcut");
        }
        final ShortcutInfo shortcut = (ShortcutInfo) tag;
        if (LauncherModel.CHANGESTATUESTYPE == 0) {//正常
            shortcut.isSelected = !shortcut.isSelected;
            ArrayList<ShortcutInfo> updatedShortcutInfos = new ArrayList<>();
            ArrayList<ShortcutInfo> deletedShortcutInfos = new ArrayList<>();
            updatedShortcutInfos.add(shortcut);
            bindShortcutsChanged(updatedShortcutInfos, deletedShortcutInfos);

            //发送指令
            if (shortcut.isSelected) {
//                LightEquipment lightEquipment = mModel.getLightEquipment(shortcut.LightId);
//                LightEquipmentGroup lightEquipmentGroup=mModel.getLightEquipmentGroup(shortcut.LightId);

            }

        } else if (LauncherModel.CHANGESTATUESTYPE == 1) {//管理-删除
            if (v instanceof BubbleTextView) {
                BubbleTextView bubbleTextView = (BubbleTextView) v;
                if (bubbleTextView.ismDeleteTargeted()) {//删除
                    View removeView = mWorkspace.getViewForTag(tag);
                    removeItem(removeView, shortcut, true);
                    //queryDJModeDataDelete(String.valueOf(shortcut.id));
                } else {//重命名
                    ConfirmEditDialog renameDialog = new ConfirmEditDialog(this, R.style.dialog);
                    renameDialog.show();
                    renameDialog.setDialogViewListener(new ConfirmEditDialog.DialogViewListener() {
                        @Override
                        public void sureClick(String content) {
                            shortcut.title = content;
                            ArrayList<ShortcutInfo> updatedShortcutInfos = new ArrayList<>();
                            ArrayList<ShortcutInfo> deletedShortcutInfos = new ArrayList<>();
                            updatedShortcutInfos.add(shortcut);
                            bindShortcutsChanged(updatedShortcutInfos, deletedShortcutInfos);
                            LauncherModel.updateItemInDatabase(DeviceDJMainActivity.this, shortcut);
                        }
                    });
                }
            }
        } else if (LauncherModel.CHANGESTATUESTYPE == 2) {//编辑模式
//            bundle = new Bundle();
//            bundle.putInt("fromType", Consts.CONTROL_FROM_TYPE_DJ);
//            bundle.putString("scene_id", Consts.CONTROL_SCENE_ID);
//            bundle.putString("control_dj_id", String.valueOf(shortcut.id));
//            bundle.putString("control_light_id", shortcut.LightId);
//            startBundleActivity(ControlMainActivity.class, bundle);
        }

    }


    /**
     * Event handler for a folder icon click.
     *
     * @param v The view that was clicked. Must be an instance of {@link FolderIcon}.
     */
    protected void onClickFolderIcon(View v) {
        if (!(v instanceof FolderIcon)) {
            throw new IllegalArgumentException("Input must be a FolderIcon");
        }

        FolderIcon folderIcon = (FolderIcon) v;
        FolderInfo folderInfo = (FolderInfo) v.getTag();

        //选中
        if (LauncherModel.CHANGESTATUESTYPE == 0) {
            folderIcon.setmState(folderIcon.getmState() == 0 ? 1 : 0);

            //发送指令
            ArrayList<ShortcutInfo> contents = folderInfo.contents;
            if (contents.size() > 0) {

            }
        } else {
            folderIcon.setmState(0);
        }

        //打开
        if (LauncherModel.CHANGESTATUESTYPE != 0 && !folderIcon.getFolderInfo().opened && !folderIcon.getFolder().isDestroyed() && !folderIcon.ismDeleteTargeted()) {
            openFolder(folderIcon);
        }

        //删除
        if (LauncherModel.CHANGESTATUESTYPE == 1 && folderIcon.ismDeleteTargeted()) {
            selectDeleteDialog(v);
        }

        //重命名
        folderIcon.getFolder().getmFolderName().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LauncherModel.CHANGESTATUESTYPE == 1) {//管理
                    String folderName = TextUtils.isEmpty(folderInfo.title.toString()) ? "New Group" : folderInfo.title.toString();
                    ConfirmEditDialog renameDialog = new ConfirmEditDialog(DeviceDJMainActivity.this, R.style.dialog, folderName, "创建");
                    renameDialog.show();
                    renameDialog.setDialogViewListener(new ConfirmEditDialog.DialogViewListener() {
                        @Override
                        public void sureClick(String content) {
                            folderIcon.getFolder().setFolderName(content);
                            folderIcon.getFolder().doneEditingFolderName(true);

                        }
                    });
                }
            }
        });
    }


    //选择删除操作弹窗
    private void selectDeleteDialog(View v) {
        List<SelectMenuBean> menuList = new ArrayList<>();
        menuList.add(new SelectMenuBean("删除该单元群组及群组中的单元", "（该群组中的单元将会被删除）", R.color.white));
        menuList.add(new SelectMenuBean("仅删除该单元群组", "（该群组中的单元将会被返回到单元列表中）", R.color.white));
        SelectMenu2Dialog selectMenu2Dialog = new SelectMenu2Dialog(this, R.style.dialog, menuList, "删除", R.color.red, R.color.white);
        selectMenu2Dialog.show();
        selectMenu2Dialog.setListener(new SelectMenu2Dialog.DialogViewListener() {
            @Override
            public void sureClick(int position) {
                switch (position) {
                    case 0:// 删除该场景群组及群组中的场景
                        FolderInfo folderInfo = (FolderInfo) v.getTag();
                        View removeView = mWorkspace.getViewForTag(v.getTag());
                        removeItem(removeView, folderInfo, true);
                        break;
                    case 1:// 仅删除场景群组
                        removeFolderOnlyGroup(v);
                        break;
                }
            }
        });
    }


    //仅删除群组，单元将会返回到列表中
    public void removeFolderOnlyGroup(View v) {
        FolderInfo folderInfo = (FolderInfo) v.getTag();
        CellLayout cellLayout = getCellLayout(folderInfo.container,
                folderInfo.screenId);

        for (int p = 0; p < folderInfo.contents.size(); p++) {
            ShortcutInfo shortcutInfo = folderInfo.contents.get(p);
            if (p == 0) {//删除群组里面的第一个单元，替换原来群组的位置
                View newIcon = createShortcut(cellLayout, shortcutInfo);
                LauncherModel.addOrMoveItemInDatabase(this, shortcutInfo, folderInfo.container,
                        folderInfo.screenId, folderInfo.cellX, folderInfo.cellY);
                if (newIcon != null) {
                    getWorkspace().addInScreenFromBind(newIcon, folderInfo.container,
                            folderInfo.screenId, folderInfo.cellX, folderInfo.cellY, folderInfo.spanX, folderInfo.spanY);
                    newIcon.requestFocus();
                }
            } else {//删除群组里面的单元
                addNewIconShortcut(folderInfo, shortcutInfo);
            }
        }

        LauncherModel.deleteItemFromDatabase(this, folderInfo);
        getWorkspace().removeWorkspaceItem(v);
    }


    //删除群组，替换原来群组的位置
    long newIconScreenIndex = 0;

    public void addNewIconShortcut(FolderInfo folderInfo, ShortcutInfo shortcutInfo) {
        if (newIconScreenIndex >= mModel.sBgWorkspaceScreens.size()) {
            newIconScreenIndex = mWorkspace.commitExtraEmptyScreen();
        }
        CellLayout cellLayout = getCellLayout(folderInfo.container,
                newIconScreenIndex);
        View newIcon = createShortcut(cellLayout, shortcutInfo);
        long container = LauncherSettings.CONTAINER_DESKTOP;
        boolean isEmpty = false;
        int cellX = 0;
        int cellY = 0;
        tag2:
        for (int i = 0; i < 4; i++) {
            tag1:
            for (int j = 0; j < 4; j++) {
                cellY = i;
                cellX = j;
                if (cellLayout.getChildAt(cellX, cellY) == null) {
                    isEmpty = true;
                    break tag2;
                }
            }
        }

        if (isEmpty) {
            LauncherModel.addOrMoveItemInDatabase(this, shortcutInfo, container, newIconScreenIndex, cellX, cellY);
            if (newIcon != null) {
                getWorkspace().addInScreenFromBind(newIcon, container,
                        newIconScreenIndex, cellX, cellY, 1, 1);
                newIcon.requestFocus();
            }
        } else {
            newIconScreenIndex++;
            addNewIconShortcut(folderInfo, shortcutInfo);
        }
    }

    public View.OnTouchListener getHapticFeedbackTouchListener() {
        if (mHapticFeedbackTouchListener == null) {
            mHapticFeedbackTouchListener = new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    }
                    return false;
                }
            };
        }
        return mHapticFeedbackTouchListener;
    }


    public void onDragStarted() {
        if (isOnCustomContent()) {
            // Custom content screen doesn't participate in drag and drop. If on custom
            // content screen, move to default.
            moveWorkspaceToDefaultScreen();
        }
    }


    private void copyFolderIconToImage(FolderIcon fi) {
        final int width = fi.getMeasuredWidth();
        final int height = fi.getMeasuredHeight();

        // Lazy load ImageView, Bitmap and Canvas
        if (mFolderIconImageView == null) {
            mFolderIconImageView = new ImageView(this);
        }
        if (mFolderIconBitmap == null || mFolderIconBitmap.getWidth() != width ||
                mFolderIconBitmap.getHeight() != height) {
            mFolderIconBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mFolderIconCanvas = new Canvas(mFolderIconBitmap);
        }

        DragLayer.LayoutParams lp;
        if (mFolderIconImageView.getLayoutParams() instanceof DragLayer.LayoutParams) {
            lp = (DragLayer.LayoutParams) mFolderIconImageView.getLayoutParams();
        } else {
            lp = new DragLayer.LayoutParams(width, height);
        }

        // The layout from which the folder is being opened may be scaled, adjust the starting
        // view size by this scale factor.
        float scale = mDragLayer.getDescendantRectRelativeToSelf(fi, mRectForFolderAnimation);
        lp.customPosition = true;
        lp.x = mRectForFolderAnimation.left;
        lp.y = mRectForFolderAnimation.top;
        lp.width = (int) (scale * width);
        lp.height = (int) (scale * height);

        mFolderIconCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        fi.draw(mFolderIconCanvas);
        mFolderIconImageView.setImageBitmap(mFolderIconBitmap);
        if (fi.getFolder() != null) {
            mFolderIconImageView.setPivotX(fi.getFolder().getPivotXForIconAnimation());
            mFolderIconImageView.setPivotY(fi.getFolder().getPivotYForIconAnimation());
        }
        // Just in case this image view is still in the drag layer from a previous animation,
        // we remove it and re-add it.
        if (mDragLayer.indexOfChild(mFolderIconImageView) != -1) {
            mDragLayer.removeView(mFolderIconImageView);
        }
        mDragLayer.addView(mFolderIconImageView, lp);
        if (fi.getFolder() != null) {
            fi.getFolder().bringToFront();
        }
    }

    private void growAndFadeOutFolderIcon(FolderIcon fi) {
        if (fi == null) return;
        FolderInfo info = (FolderInfo) fi.getTag();
        // Push an ImageView copy of the FolderIcon into the DragLayer and hide the original
        copyFolderIconToImage(fi);
        fi.setVisibility(View.INVISIBLE);

        ObjectAnimator oa = LauncherAnimUtils.ofViewAlphaAndScale(
                mFolderIconImageView, 0, 1.5f, 1.5f);
        if (Utilities.ATLEAST_LOLLIPOP) {
            oa.setInterpolator(new LogDecelerateInterpolator(100, 0));
        }
        oa.setDuration(getResources().getInteger(R.integer.config_folderExpandDuration));
        oa.start();
    }

    private void shrinkAndFadeInFolderIcon(final FolderIcon fi, boolean animate) {
        if (fi == null) return;
        final CellLayout cl = (CellLayout) fi.getParent().getParent();

        // We remove and re-draw the FolderIcon in-case it has changed
        mDragLayer.removeView(mFolderIconImageView);
        copyFolderIconToImage(fi);

        if (cl != null) {
            cl.clearFolderLeaveBehind();
        }

        ObjectAnimator oa = LauncherAnimUtils.ofViewAlphaAndScale(mFolderIconImageView, 1, 1, 1);
        oa.setDuration(getResources().getInteger(R.integer.config_folderExpandDuration));
        oa.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (cl != null) {
                    // Remove the ImageView copy of the FolderIcon and make the original visible.
                    mDragLayer.removeView(mFolderIconImageView);
                    fi.setVisibility(View.VISIBLE);
                }
            }
        });
        oa.start();
        if (!animate) {
            oa.end();
        }
    }

    /**
     * Opens the user folder described by the specified tag. The opening of the folder
     * is animated relative to the specified View. If the View is null, no animation
     * is played.
     *
     * @param folderIcon The FolderIcon describing the folder to open.
     */
    public void openFolder(FolderIcon folderIcon) {
        blurView.setVisibility(View.VISIBLE);
        Folder folder = folderIcon.getFolder();
        Folder openFolder = mWorkspace != null ? mWorkspace.getOpenFolder() : null;
        if (openFolder != null && openFolder != folder) {
            // Close any open folder before opening a folder.
            closeFolder();
        }

        FolderInfo info = folder.mInfo;

        info.opened = true;

        // While the folder is open, the position of the icon cannot change.
        ((CellLayout.LayoutParams) folderIcon.getLayoutParams()).canReorder = false;

        // Just verify that the folder hasn't already been added to the DragLayer.
        // There was a one-off crash where the folder had a parent already.
        if (folder.getParent() == null) {
            mDragLayer.addView(folder);
            mDragController.addDropTarget(folder);
        }
        folder.animateOpen();

        growAndFadeOutFolderIcon(folderIcon);

        // Notify the accessibility manager that this folder "window" has appeared and occluded
        // the workspace items
        folder.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
        getDragLayer().sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED);
    }

    public void closeFolder() {
        closeFolder(true);
        blurView.setVisibility(View.GONE);
    }

    public void closeFolder(boolean animate) {
        Folder folder = mWorkspace != null ? mWorkspace.getOpenFolder() : null;
        if (folder != null) {
            if (folder.isEditingName()) {
                folder.dismissEditingName();
            }
            closeFolder(folder, animate);
        }
    }

    public void closeFolder(Folder folder, boolean animate) {
        animate &= !Utilities.isPowerSaverOn(this);

        folder.getInfo().opened = false;

        ViewGroup parent = (ViewGroup) folder.getParent().getParent();
        if (parent != null) {
            FolderIcon fi = (FolderIcon) mWorkspace.getViewForTag(folder.mInfo);
            shrinkAndFadeInFolderIcon(fi, animate);
            if (fi != null) {
                ((CellLayout.LayoutParams) fi.getLayoutParams()).canReorder = true;
            }
        }
        if (animate) {
            folder.animateClosed();
        } else {
            folder.close(false);
        }

        // Notify the accessibility manager that this folder "window" has disappeared and no
        // longer occludes the workspace items
        getDragLayer().sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
    }

    @Override
    public boolean onLongClick(View v) {
        if (!isDraggingEnabled()) return false;
        if (isWorkspaceLocked()) return false;
        if (mState != State.WORKSPACE) return false;

        CellLayout.CellInfo longClickCellInfo = null;
        View itemUnderLongClick = null;
        if (v.getTag() instanceof ItemInfo) {
            ItemInfo info = (ItemInfo) v.getTag();
            longClickCellInfo = new CellLayout.CellInfo(v, info);
            itemUnderLongClick = longClickCellInfo.cell;
        }
        if (!mDragController.isDragging()) {
            if (itemUnderLongClick == null) {
                // User long pressed on empty space
                mWorkspace.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS,
                        HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);

            } else {
                if (!(itemUnderLongClick instanceof Folder)) {
                    // User long pressed on an item
                    DragOptions dragOptions = new DragOptions();
                    if (itemUnderLongClick instanceof BubbleTextView) {
                        BubbleTextView icon = (BubbleTextView) itemUnderLongClick;
                    }
                    mWorkspace.startDrag(longClickCellInfo, dragOptions);
                }
            }
        }
        return true;
    }


    /**
     * Returns the CellLayout of the specified container at the specified screen.
     */
    public CellLayout getCellLayout(long container, long screenId) {
        return mWorkspace.getScreenWithId(screenId);
    }


    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level >= ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            // The widget preview db can result in holding onto over
            // 3MB of memory for caching which isn't necessary.
            SQLiteDatabase.releaseMemory();

            // This clears all widget bitmaps from the widget tray
            // TODO(hyunyoungs)
        }
        if (mLauncherCallbacks != null) {
            mLauncherCallbacks.onTrimMemory(level);
        }
    }


    @Override
    public boolean setLoadOnResume() {
        if (mPaused) {
            mOnResumeNeedsLoad = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getCurrentWorkspaceScreen() {
        if (mWorkspace != null) {
            return mWorkspace.getCurrentPage();
        } else {
            return 0;
        }
    }


    @Override
    public void bindScreens(ArrayList<Long> orderedScreenIds) {
        // Make sure the first screen is always at the start.
        if (FeatureFlags.QSB_ON_FIRST_SCREEN &&
                orderedScreenIds.indexOf(Workspace.FIRST_SCREEN_ID) != 0) {
            orderedScreenIds.remove(Workspace.FIRST_SCREEN_ID);
            orderedScreenIds.add(0, Workspace.FIRST_SCREEN_ID);
            mModel.updateWorkspaceScreenOrder(this, orderedScreenIds);
        } else if (!FeatureFlags.QSB_ON_FIRST_SCREEN && orderedScreenIds.isEmpty()) {
            // If there are no screens, we need to have an empty screen
            mWorkspace.addExtraEmptyScreen();
        }
        bindAddScreens(orderedScreenIds);
    }

    private void bindAddScreens(ArrayList<Long> orderedScreenIds) {
        int count = orderedScreenIds.size();
        for (int i = 0; i < count; i++) {
            long screenId = orderedScreenIds.get(i);
            if (!FeatureFlags.QSB_ON_FIRST_SCREEN || screenId != Workspace.FIRST_SCREEN_ID) {
                // No need to bind the first screen, as its always bound.
                mWorkspace.insertNewWorkspaceScreenBeforeEmptyScreen(screenId);
            }
        }
    }


    @Override
    public void bindItems(final ArrayList<ItemInfo> shortcuts, final int start, final int end,
                          final boolean forceAnimateIcons) {
        Runnable r = new Runnable() {
            public void run() {
                bindItems(shortcuts, start, end, forceAnimateIcons);
            }
        };

        final AnimatorSet anim = LauncherAnimUtils.createAnimatorSet();
        final Collection<Animator> bounceAnims = new ArrayList<Animator>();
        final boolean animateIcons = forceAnimateIcons && canRunNewAppsAnimation();
        Workspace workspace = mWorkspace;
        long newShortcutsScreenId = -1;
        for (int i = start; i < end; i++) {
            final ItemInfo item = shortcuts.get(i);

            final View view;
            switch (item.itemType) {
                case LauncherSettings.ITEM_TYPE_SHORTCUT:
                    ShortcutInfo info = (ShortcutInfo) item;
                    view = createShortcut(info);
                    break;
                case LauncherSettings.ITEM_TYPE_FOLDER:
                    view = FolderIcon.fromXml(R.layout.launcher_folder_icon, this,
                            (ViewGroup) workspace.getChildAt(workspace.getCurrentPage()),
                            (FolderInfo) item);
                    break;
                default:
                    throw new RuntimeException("Invalid Item Type");
            }

            workspace.addInScreenFromBind(view, item.container, item.screenId, item.cellX,
                    item.cellY, 1, 1);
            if (animateIcons) {
                // Animate all the applications up now
                view.setAlpha(0f);
                view.setScaleX(0f);
                view.setScaleY(0f);
                bounceAnims.add(createNewAppBounceAnimation(view, i));
                newShortcutsScreenId = item.screenId;
            }
        }

        if (animateIcons) {
            // Animate to the correct page
            if (newShortcutsScreenId > -1) {
                long currentScreenId = mWorkspace.getScreenIdForPageIndex(mWorkspace.getNextPage());
                final int newScreenIndex = mWorkspace.getPageIndexForScreenId(newShortcutsScreenId);
                final Runnable startBounceAnimRunnable = new Runnable() {
                    public void run() {
                        anim.playTogether(bounceAnims);
                        anim.start();
                    }
                };
                if (newShortcutsScreenId != currentScreenId) {
                    // We post the animation slightly delayed to prevent slowdowns
                    // when we are loading right after we return to launcher.
                    mWorkspace.postDelayed(new Runnable() {
                        public void run() {
                            if (mWorkspace != null) {
                                mWorkspace.snapToPage(newScreenIndex);
                                mWorkspace.postDelayed(startBounceAnimRunnable,
                                        NEW_APPS_ANIMATION_DELAY);
                            }
                        }
                    }, NEW_APPS_PAGE_MOVE_DELAY);
                } else {
                    mWorkspace.postDelayed(startBounceAnimRunnable, NEW_APPS_ANIMATION_DELAY);
                }
            }
        }
        workspace.requestLayout();
    }


    public void onPageBoundSynchronously(int page) {
        mSynchronouslyBoundPages.add(page);
    }

    @Override
    public void executeOnNextDraw(ViewOnDrawExecutor executor) {
        if (mPendingExecutor != null) {
            mPendingExecutor.markCompleted();
        }
        mPendingExecutor = executor;
        executor.attachTo(this);
    }

    public void clearPendingExecutor(ViewOnDrawExecutor executor) {
        if (mPendingExecutor == executor) {
            mPendingExecutor = null;
        }
    }

    @Override
    public void finishFirstPageBind(final ViewOnDrawExecutor executor) {
        Runnable r = new Runnable() {
            public void run() {
                finishFirstPageBind(executor);
            }
        };

        Runnable onComplete = new Runnable() {
            @Override
            public void run() {
                if (executor != null) {
                    executor.onLoadAnimationCompleted();
                }
            }
        };
        if (mDragLayer.getAlpha() < 1) {
            mDragLayer.animate().alpha(1).withEndAction(onComplete).start();
        } else {
            onComplete.run();
        }
    }

    public void finishBindingItems() {
        Runnable r = new Runnable() {
            public void run() {
                finishBindingItems();
            }
        };
        if (LauncherAppState.PROFILE_STARTUP) {
            Trace.beginSection("Page bind completed");
        }
        if (mSavedState != null) {
            if (!mWorkspace.hasFocus()) {
                mWorkspace.getChildAt(mWorkspace.getCurrentPage()).requestFocus();
            }

            mSavedState = null;
        }

        mWorkspace.restoreInstanceStateForRemainingPages();

        setWorkspaceLoading(false);


        if (mLauncherCallbacks != null) {
            mLauncherCallbacks.finishBindingItems(false);
        }
        if (LauncherAppState.PROFILE_STARTUP) {
            Trace.endSection();
        }
    }


    private boolean canRunNewAppsAnimation() {
        long diff = System.currentTimeMillis() - mDragController.getLastGestureUpTime();
        return diff > (NEW_APPS_ANIMATION_INACTIVE_TIMEOUT_SECONDS * 1000);
    }

    private ValueAnimator createNewAppBounceAnimation(View v, int i) {
        ValueAnimator bounceAnim = LauncherAnimUtils.ofViewAlphaAndScale(v, 1, 1, 1);
        bounceAnim.setInterpolator(new OvershootInterpolator(BOUNCE_ANIMATION_TENSION));
        return bounceAnim;
    }


    public List<String> getShortcutIdsForItem(ItemInfo info) {
        ComponentName component = info.getTargetComponent();
        if (component == null) {
            return Collections.EMPTY_LIST;
        }

        return Collections.EMPTY_LIST;
    }

    @Override
    public void bindShortcutsChanged(final ArrayList<ShortcutInfo> updated,
                                     final ArrayList<ShortcutInfo> removed) {
        Runnable r = new Runnable() {
            public void run() {
                bindShortcutsChanged(updated, removed);
            }
        };
        if (!updated.isEmpty()) {
            mWorkspace.updateShortcuts(updated);
        }
    }

    @Override
    public void bindRestoreItemsChange(final HashSet<ItemInfo> updates) {
        Runnable r = new Runnable() {
            public void run() {
                bindRestoreItemsChange(updates);
            }
        };
        mWorkspace.updateRestoreItems(updates);
    }


    protected void moveWorkspaceToDefaultScreen() {
        mWorkspace.moveToDefaultScreen(false);
    }

    public FastBitmapDrawable createIconDrawable(Bitmap icon) {
        FastBitmapDrawable d = new FastBitmapDrawable(icon);
        d.setFilterBitmap(true);
        resizeIconDrawable(d);
        return d;
    }

    public Drawable resizeIconDrawable(Drawable icon) {
        icon.setBounds(0, 0, mDeviceProfile.iconSizePx, mDeviceProfile.iconSizePx);
        return icon;
    }


    public static DeviceDJMainActivity getLauncher(Context context) {
        if (context instanceof DeviceDJMainActivity) {
            return (DeviceDJMainActivity) context;
        }
        return ((DeviceDJMainActivity) ((ContextWrapper) context).getBaseContext());
    }

    //改变模式状态0，正常 1，管理 2，编辑
    private void changeStatuesType() {
        ArrayList<ShortcutInfo> updatedShortcutInfos = new ArrayList<>();
        for (ItemInfo itemInfo : mModel.sBgItemsIdMap) {
            if (itemInfo.itemType == 1) {
                ShortcutInfo si = (ShortcutInfo) itemInfo;
                si.setStatuesType(LauncherModel.CHANGESTATUESTYPE);
                updatedShortcutInfos.add(si);
            }

        }
        mWorkspace.updateShortcuts(updatedShortcutInfos);
    }


}
