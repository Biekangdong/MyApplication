package com.example.customerlauncher3.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

@Entity
public class LightEquipment {
    @Id
    /**设备ID*/
    String diy_id;
    /**
     * 场景id
     */
    String sceneId;
    /**
     * 设备名称
     */
    String name;
    /**
     * 设备原始名称
     */
    String originalName;
    /**
     * 连接设备地址
     */
    String Control_Address;
    /**
     * 地址码开始值 /固定值
     */
    String CH_Min;
    /**
     * 地址码结束值
     */
    String Ch_Max;
    /**
     * 0= 蓝牙 ; 1=2.4G; 2=DMX
     */
    int connectMethod;
    /**
     *2.4G类型 0=单色温 1= 双色温 2= 全彩
     */
    int G_LedType;
    /**
     *2.4G连接方式 0=蓝牙 1= wifi 2= 蓝牙和wifi
     */
    int G_ContentType;
    /**
     * Dmx 品牌
     */
    String Dmx_brand;
    /**
     * Dmx 型号
     */
    String Dmx_type;
    /**
     * Dmx 模式
     */
    String Dmx_model;
    /**
     * 创建时间
     */
    long creatTime;
    /**
     * 最后记录时间
     */
    long editTime;
    /**
     * 创建人
     */
    String uid;
    /**
     * 型号ID
     */
    String MODE_ID;
    /**
     * 灯版本
     */
    String AGREEMENT_VER;
    /**
     * 展示的地址码
     */
    String CH_Show;

    /**
     * 亮度开关
     */
    boolean isLightClose = false;
    /**
     * 备用字段
     */
    String string1;
    String string2;
    String string3;

    @Transient
    boolean isChecked = false;

    @Transient
    int position;

    /**
     * 亮度
     */
    @Transient
    int dimProgress = 0;

    @Transient
    int dimStep = 1;
    @Transient
    int dimMin = 0;
    @Transient
    int dimMax = 100;

    @Generated(hash = 1588614598)
    public LightEquipment(String diy_id, String sceneId, String name,
                          String originalName, String Control_Address, String CH_Min,
                          String Ch_Max, int connectMethod, int G_LedType, int G_ContentType,
                          String Dmx_brand, String Dmx_type, String Dmx_model, long creatTime,
                          long editTime, String uid, String MODE_ID, String AGREEMENT_VER,
                          String CH_Show, boolean isLightClose, String string1, String string2,
                          String string3) {
        this.diy_id = diy_id;
        this.sceneId = sceneId;
        this.name = name;
        this.originalName = originalName;
        this.Control_Address = Control_Address;
        this.CH_Min = CH_Min;
        this.Ch_Max = Ch_Max;
        this.connectMethod = connectMethod;
        this.G_LedType = G_LedType;
        this.G_ContentType = G_ContentType;
        this.Dmx_brand = Dmx_brand;
        this.Dmx_type = Dmx_type;
        this.Dmx_model = Dmx_model;
        this.creatTime = creatTime;
        this.editTime = editTime;
        this.uid = uid;
        this.MODE_ID = MODE_ID;
        this.AGREEMENT_VER = AGREEMENT_VER;
        this.CH_Show = CH_Show;
        this.isLightClose = isLightClose;
        this.string1 = string1;
        this.string2 = string2;
        this.string3 = string3;
    }

    @Generated(hash = 1130189569)
    public LightEquipment() {
    }

    public String getDiy_id() {
        return this.diy_id;
    }

    public void setDiy_id(String diy_id) {
        this.diy_id = diy_id;
    }

    public String getSceneId() {
        return this.sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalName() {
        return this.originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getControl_Address() {
        return this.Control_Address;
    }

    public void setControl_Address(String Control_Address) {
        this.Control_Address = Control_Address;
    }

    public String getCH_Min() {
        return this.CH_Min;
    }

    public void setCH_Min(String CH_Min) {
        this.CH_Min = CH_Min;
    }

    public String getCh_Max() {
        return this.Ch_Max;
    }

    public void setCh_Max(String Ch_Max) {
        this.Ch_Max = Ch_Max;
    }

    public int getConnectMethod() {
        return this.connectMethod;
    }

    public void setConnectMethod(int connectMethod) {
        this.connectMethod = connectMethod;
    }

    public int getG_LedType() {
        return this.G_LedType;
    }

    public void setG_LedType(int G_LedType) {
        this.G_LedType = G_LedType;
    }

    public int getG_ContentType() {
        return this.G_ContentType;
    }

    public void setG_ContentType(int G_ContentType) {
        this.G_ContentType = G_ContentType;
    }

    public String getDmx_brand() {
        return this.Dmx_brand;
    }

    public void setDmx_brand(String Dmx_brand) {
        this.Dmx_brand = Dmx_brand;
    }

    public String getDmx_type() {
        return this.Dmx_type;
    }

    public void setDmx_type(String Dmx_type) {
        this.Dmx_type = Dmx_type;
    }

    public String getDmx_model() {
        return this.Dmx_model;
    }

    public void setDmx_model(String Dmx_model) {
        this.Dmx_model = Dmx_model;
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

    public String getMODE_ID() {
        return this.MODE_ID;
    }

    public void setMODE_ID(String MODE_ID) {
        this.MODE_ID = MODE_ID;
    }

    public String getAGREEMENT_VER() {
        return this.AGREEMENT_VER;
    }

    public void setAGREEMENT_VER(String AGREEMENT_VER) {
        this.AGREEMENT_VER = AGREEMENT_VER;
    }

    public String getCH_Show() {
        return this.CH_Show;
    }

    public void setCH_Show(String CH_Show) {
        this.CH_Show = CH_Show;
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getDimProgress() {
        return dimProgress;
    }

    public void setDimProgress(int dimProgress) {
        this.dimProgress = dimProgress;
    }

    public int getDimStep() {
        return dimStep;
    }

    public void setDimStep(int dimStep) {
        this.dimStep = dimStep;
    }

    public int getDimMin() {
        return dimMin;
    }

    public void setDimMin(int dimMin) {
        this.dimMin = dimMin;
    }

    public int getDimMax() {
        return dimMax;
    }

    public void setDimMax(int dimMax) {
        this.dimMax = dimMax;
    }
}
