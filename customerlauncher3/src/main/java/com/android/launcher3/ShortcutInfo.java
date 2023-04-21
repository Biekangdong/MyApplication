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

import android.content.Context;

import com.example.customerlauncher3.greendao.entity.DJCell;


/**
 * Represents a launchable icon on the workspaces and in folders.
 */
public class ShortcutInfo extends ItemInfo {

    public static final int DEFAULT = 0;

    /**
     * The shortcut was restored from a backup and it not ready to be used. This is automatically
     * set during backup/restore
     */
    public static final int FLAG_RESTORED_ICON = 1;

    /**
     * The icon was added as an auto-install app, and is not ready to be used. This flag can't
     * be present along with {@link #FLAG_RESTORED_ICON}, and is set during default layout
     * parsing.
     */
    public static final int FLAG_AUTOINTALL_ICON = 2; //0B10;

    /**
     * The icon is being installed. If {@link #FLAG_RESTORED_ICON} or {@link #FLAG_AUTOINTALL_ICON}
     * is set, then the icon is either being installed or is in a broken state.
     */
    public static final int FLAG_INSTALL_SESSION_ACTIVE = 4; // 0B100;


    /**
     * Indicates whether we're using the default fallback icon instead of something from the
     * app.
     */
    boolean usingFallbackIcon;

    /**
     * Could be disabled, if the the app is installed but unavailable (eg. in safe mode or when
     * sd-card is not available).
     */
    int isDisabled = DEFAULT;


    int status;

    /**
     * The installation progress [0-100] of the package that this shortcut represents.
     */
    private int mInstallProgress;

    /**
     * TODO move this to {@link #status}
     */
    int flags = 0;

    public ShortcutInfo() {
        itemType = LauncherSettings.ITEM_TYPE_SHORTCUT;
    }


    public ShortcutInfo(CharSequence title,
                  String LightId,String LightName,String color,String MODE,int connectMethod,int g_ledtype) {
        this();
        this.title = Utilities.trim(title);
        this.color = color;
        this.LightId = LightId;
        this.LightName = LightName;
        this.MODE = MODE;
        this.connectMethod = connectMethod;
        this.g_ledtype = g_ledtype;
        this.CHShow = CHShow;
    }

    public ShortcutInfo(ShortcutInfo info) {
        super(info);
        title = info.title;
        color = info.color;
        flags = info.flags;
        status = info.status;
        mInstallProgress = info.mInstallProgress;
        isDisabled = info.isDisabled;
        usingFallbackIcon = info.usingFallbackIcon;
        LightId = info.LightId;
        MODE = info.MODE;
        connectMethod = info.connectMethod;
        g_ledtype = info.g_ledtype;
        LightName = info.LightName;
    }



    @Override
    void onAddToDatabase(Context context, DJCell values) {
        super.onAddToDatabase(context, values);

        String titleStr = title != null ? title.toString() : null;
        values.setName(titleStr);
    }


    public boolean hasStatusFlag(int flag) {
        return (status & flag) != 0;
    }


    public final boolean isPromise() {
        return hasStatusFlag(FLAG_RESTORED_ICON | FLAG_AUTOINTALL_ICON);
    }

    public int getInstallProgress() {
        return mInstallProgress;
    }

    public void setInstallProgress(int progress) {
        mInstallProgress = progress;
        status |= FLAG_INSTALL_SESSION_ACTIVE;
    }


    @Override
    public boolean isDisabled() {
        return isDisabled != 0;
    }
}
