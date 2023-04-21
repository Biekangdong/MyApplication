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
import android.content.Intent;

import com.example.customerlauncher3.Consts;
import com.example.customerlauncher3.greendao.entity.DJCell;


/**
 * Represents an item in the launcher.
 */
public class ItemInfo {

    /**
     * Intent extra to store the profile. Format: UserHandle
     */
    public static final String EXTRA_PROFILE = "profile";

    public static final int NO_ID = -1;

    /**
     * The id in the settings database for this item
     */
    public long id = NO_ID;


    public int itemType;


    public long container = NO_ID;

    /**
     * Iindicates the screen in which the shortcut appears.
     */
    public long screenId = -1;

    /**
     * Indicates the X position of the associated cell.
     */
    public int cellX = -1;

    /**
     * Indicates the Y position of the associated cell.
     */
    public int cellY = -1;

    /**
     * Indicates the X cell span.
     */
    public int spanX = 1;

    /**
     * Indicates the Y cell span.
     */
    public int spanY = 1;

    /**
     * Indicates the minimum X cell span.
     */
    public int minSpanX = 1;

    /**
     * Indicates the minimum Y cell span.
     */
    public int minSpanY = 1;

    /**
     * Indicates the position in an ordered list.
     */
    public int rank = 0;

    /**
     * Title of the item
     */
    public CharSequence title;

    public String Fid= Consts.CONTROL_DJ_PROJECT_ID;
    public long creatTime=System.currentTimeMillis();

    public String LightId="";
    public String LightName="";
    public String color;

    public String MODE="";
    /**0= 蓝牙 ; 1=2.4G; 2=DMX 3,wifi*/
    public int connectMethod;
    public int g_ledtype;
    public String CHShow;

    public int statuesType=0;
    public boolean isSelected=false;//是状态否选中，改变边框闪烁
    public boolean isBlack=false;//是否黑场
    public ItemInfo() {

    }

    ItemInfo(ItemInfo info) {
        copyFrom(info);
        // tempdebug:
       // LauncherModel.checkItemInfo(this);
    }

    public void copyFrom(ItemInfo info) {
        id = info.id;
        cellX = info.cellX;
        cellY = info.cellY;
        spanX = info.spanX;
        spanY = info.spanY;
        rank = info.rank;
        screenId = info.screenId;
        itemType = info.itemType;
        container = info.container;
        color=info.color;
        Fid=info.Fid;
        LightId=info.LightId;
        LightName=info.LightName;
        creatTime=info.creatTime;
        MODE=info.MODE;
        connectMethod=info.connectMethod;
        g_ledtype=info.g_ledtype;
        isSelected=info.isSelected;
        isBlack=info.isBlack;
    }

    public Intent getIntent() {
        return null;
    }

    public ComponentName getTargetComponent() {
        return getIntent() == null ? null : getIntent().getComponent();
    }

    public void writeToValues(DJCell values) {
        values.setItemType(itemType);
        values.setContainer(container);
        values.setCellX(cellX);
        values.setCellY(cellY);
        values.setRank(rank);
        values.setScreenId(screenId);
        values.setColor(color);
        values.setFid(Fid);
        values.setCreatTime(creatTime);
        values.setLightId(LightId);
        values.setMODE(MODE);
        values.setConnectMethod(connectMethod);
        values.setG_ledtype(g_ledtype);

    }


    /**
     * Write the fields of this item to the DB
     *
     * @param context A context object to use for getting UserManagerCompat
     * @param values
     */
    void onAddToDatabase(Context context, DJCell values) {
        writeToValues(values);
        if (screenId == Workspace.EXTRA_EMPTY_SCREEN_ID) {
            // We should never persist an item on the extra empty screen.
            throw new RuntimeException("Screen id should not be EXTRA_EMPTY_SCREEN_ID");
        }
    }


    @Override
    public final String toString() {
        return getClass().getSimpleName() + "(" + dumpProperties() + ")";
    }

    protected String dumpProperties() {
        return "id=" + id
                + " type=" + itemType
                + " container=" + container
                + " screen=" + screenId
                + " cellX=" + cellX
                + " cellY=" + cellY
                + " spanX=" + spanX
                + " spanY=" + spanY
                + " minSpanX=" + minSpanX
                + " minSpanY=" + minSpanY
                + " rank=" + rank
                + " color=" + color
                + " Fid=" + Fid
                + " creatTime=" + creatTime
                + " LightId=" + LightId
                + " MODE=" + MODE
                + " title=" + title;
    }

    /**
     * Whether this item is disabled.
     */
    public boolean isDisabled() {
        return false;
    }

    public int getStatuesType() {
        return statuesType;
    }

    public void setStatuesType(int statuesType) {
        this.statuesType = statuesType;
    }
}
