package com.example.customerlauncher3.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class DJCell {
    @Id
    Long Id;
    /**单元名*/
    String Name;
    /**单元颜色值*/
    String color;
    /**单元位置*/
    int cellX;
    /**单元位置*/
    int cellY;
    /**单元或者群组（0、1）*/
    int itemType;
    /**群组ID*/
    Long container;
    /**组内的单元排序（索引）*/
    int rank;
    /**翻页ID（索引）*/
    Long screenId;
    /**上级ID*/
    String Fid;
    /**设备ID*/
    String LightId;
    /**场景ID*/
    String sceneId;
    /**创建时间*/
    Long creatTime;
    /**当前模式*/
    String MODE;
    int connectMethod;
    int g_ledtype;
    /**
     * 连接设备地址
     */
    String Control_Address;

    /**备用字段*/
    String string1;
    String string2;
    String string3;
    @Generated(hash = 474227652)
    public DJCell(Long Id, String Name, String color, int cellX, int cellY,
            int itemType, Long container, int rank, Long screenId, String Fid,
            String LightId, String sceneId, Long creatTime, String MODE,
            int connectMethod, int g_ledtype, String Control_Address,
            String string1, String string2, String string3) {
        this.Id = Id;
        this.Name = Name;
        this.color = color;
        this.cellX = cellX;
        this.cellY = cellY;
        this.itemType = itemType;
        this.container = container;
        this.rank = rank;
        this.screenId = screenId;
        this.Fid = Fid;
        this.LightId = LightId;
        this.sceneId = sceneId;
        this.creatTime = creatTime;
        this.MODE = MODE;
        this.connectMethod = connectMethod;
        this.g_ledtype = g_ledtype;
        this.Control_Address = Control_Address;
        this.string1 = string1;
        this.string2 = string2;
        this.string3 = string3;
    }
    @Generated(hash = 1665713372)
    public DJCell() {
    }
    public Long getId() {
        return this.Id;
    }
    public void setId(Long Id) {
        this.Id = Id;
    }
    public String getName() {
        return this.Name;
    }
    public void setName(String Name) {
        this.Name = Name;
    }
    public String getColor() {
        return this.color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public int getCellX() {
        return this.cellX;
    }
    public void setCellX(int cellX) {
        this.cellX = cellX;
    }
    public int getCellY() {
        return this.cellY;
    }
    public void setCellY(int cellY) {
        this.cellY = cellY;
    }
    public int getItemType() {
        return this.itemType;
    }
    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
    public Long getContainer() {
        return this.container;
    }
    public void setContainer(Long container) {
        this.container = container;
    }
    public int getRank() {
        return this.rank;
    }
    public void setRank(int rank) {
        this.rank = rank;
    }
    public Long getScreenId() {
        return this.screenId;
    }
    public void setScreenId(Long screenId) {
        this.screenId = screenId;
    }
    public String getFid() {
        return this.Fid;
    }
    public void setFid(String Fid) {
        this.Fid = Fid;
    }
    public String getLightId() {
        return this.LightId;
    }
    public void setLightId(String LightId) {
        this.LightId = LightId;
    }
    public String getSceneId() {
        return this.sceneId;
    }
    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }
    public Long getCreatTime() {
        return this.creatTime;
    }
    public void setCreatTime(Long creatTime) {
        this.creatTime = creatTime;
    }
    public String getMODE() {
        return this.MODE;
    }
    public void setMODE(String MODE) {
        this.MODE = MODE;
    }
    public int getConnectMethod() {
        return this.connectMethod;
    }
    public void setConnectMethod(int connectMethod) {
        this.connectMethod = connectMethod;
    }
    public int getG_ledtype() {
        return this.g_ledtype;
    }
    public void setG_ledtype(int g_ledtype) {
        this.g_ledtype = g_ledtype;
    }
    public String getControl_Address() {
        return this.Control_Address;
    }
    public void setControl_Address(String Control_Address) {
        this.Control_Address = Control_Address;
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

}
