package com.example.customerlauncher3.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

@Entity
public class LightEquipmentGroup {
    @Id
    /**设备群组id*/
    String diy_id;
    /**设备群组名称*/
    String name;
    /**	场景id*/
    String sceneId;
    /**创建时间*/
    long creatTime;
    /**最后记录时间*/
    long editTime;
    /**创建人*/
    String uid;
    /**地址码开始值 /固定值*/
    String CH_Min;
    /**显示的地址码*/
    String CH_Show;
    /**0= 蓝牙 ; 1=2.4G; 2=DMX 3,wifi*/
    int connectMethod;

    /**
     * 亮度开关
     */
    boolean isLightClose=false;
    /**备用字段*/
    String string1;
    String string2;
    String string3;

    @Transient
    boolean isChecked=false;
    @Generated(hash = 1164598321)
    public LightEquipmentGroup(String diy_id, String name, String sceneId,
                               long creatTime, long editTime, String uid, String CH_Min,
                               String CH_Show, int connectMethod, boolean isLightClose, String string1,
                               String string2, String string3) {
        this.diy_id = diy_id;
        this.name = name;
        this.sceneId = sceneId;
        this.creatTime = creatTime;
        this.editTime = editTime;
        this.uid = uid;
        this.CH_Min = CH_Min;
        this.CH_Show = CH_Show;
        this.connectMethod = connectMethod;
        this.isLightClose = isLightClose;
        this.string1 = string1;
        this.string2 = string2;
        this.string3 = string3;
    }
    @Generated(hash = 958182091)
    public LightEquipmentGroup() {
    }
    public String getDiy_id() {
        return this.diy_id;
    }
    public void setDiy_id(String diy_id) {
        this.diy_id = diy_id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSceneId() {
        return this.sceneId;
    }
    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }
    public long getCreatTime() {
        return this.creatTime;
    }
    public void setCreatTime(long creatTime) {
        this.creatTime = creatTime;
    }
    public long getEditTime() {
        return this.editTime;
    }
    public void setEditTime(long editTime) {
        this.editTime = editTime;
    }
    public String getUid() {
        return this.uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getCH_Min() {
        return this.CH_Min;
    }
    public void setCH_Min(String CH_Min) {
        this.CH_Min = CH_Min;
    }
    public String getCH_Show() {
        return this.CH_Show;
    }
    public void setCH_Show(String CH_Show) {
        this.CH_Show = CH_Show;
    }
    public int getConnectMethod() {
        return this.connectMethod;
    }
    public void setConnectMethod(int connectMethod) {
        this.connectMethod = connectMethod;
    }
    public boolean getIsLightClose() {
        return this.isLightClose;
    }
    public void setIsLightClose(boolean isLightClose) {
        this.isLightClose = isLightClose;
    }
    public String getString1() {
        return this.string1;
    }
    public void setString1(String string1) {
        this.string1 = string1;
    }
    public String getString2() {
        return this.string2;
    }
    public void setString2(String string2) {
        this.string2 = string2;
    }
    public String getString3() {
        return this.string3;
    }
    public void setString3(String string3) {
        this.string3 = string3;
    }

    public boolean isLightClose() {
        return isLightClose;
    }

    public void setLightClose(boolean lightClose) {
        isLightClose = lightClose;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
