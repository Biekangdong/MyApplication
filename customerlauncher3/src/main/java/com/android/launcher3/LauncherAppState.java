/*
 * Copyright (C) 2013 The Android Open Source Project
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

import com.example.customerlauncher3.MyApp;
import com.android.launcher3.config.ProviderConfig;
import com.android.launcher3.logging.FileLog;
import com.android.launcher3.util.Thunk;
import com.example.customerlauncher3.DeviceDJMainActivity;

public class LauncherAppState {

    public static final boolean PROFILE_STARTUP = ProviderConfig.IS_DOGFOOD_BUILD;

    @Thunk final LauncherModel mModel;

    @Thunk boolean mWallpaperChangedSinceLastCheck;

    private static Context sContext;

    private static LauncherAppState INSTANCE;

    private InvariantDeviceProfile mInvariantDeviceProfile;

    public static LauncherAppState getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LauncherAppState();
        }
        return INSTANCE;
    }

    public Context getContext() {
        return sContext;
    }


    private LauncherAppState() {
        sContext= MyApp.getInstance();
        if (sContext == null) {
            throw new IllegalStateException("LauncherAppState inited before app context set");
        }



        mInvariantDeviceProfile = new InvariantDeviceProfile(sContext);

        mModel = new LauncherModel(this);
        FileLog.setDir(sContext.getFilesDir());
    }


    /**
     * Reloads the workspace items from the DB and re-binds the workspace. This should generally
     * not be called as DB updates are automatically followed by UI update
     */
    public void reloadWorkspace() {
        mModel.resetLoadedState(false, true);
        mModel.startLoaderFromBackground();
    }

    public   LauncherModel setLauncher(DeviceDJMainActivity deviceDJMainActivity) {
        //sLauncherProvider.get().setLauncherProviderChangeListener(launcher);
        mModel.initialize(deviceDJMainActivity);
        return mModel;
    }


    public LauncherModel getModel() {
        return mModel;
    }


    public InvariantDeviceProfile getInvariantDeviceProfile() {
        return mInvariantDeviceProfile;
    }
}
