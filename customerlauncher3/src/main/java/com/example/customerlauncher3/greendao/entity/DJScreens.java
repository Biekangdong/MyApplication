package com.example.customerlauncher3.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class DJScreens {
    @Id
    Long Id;
    /**屏幕的页数（翻页的总页数）*/
    int screenRank;
    /**修改时间*/
    Long modified;
    /**场景ID*/
    String sceneId;
    /**备用字段*/
    String string1;
    String string2;
    String string3;
    @Generated(hash = 1584345577)
    public DJScreens(Long Id, int screenRank, Long modified, String sceneId,
                     String string1, String string2, String string3) {
        this.Id = Id;
        this.screenRank = screenRank;
        this.modified = modified;
        this.sceneId = sceneId;
        this.string1 = string1;
        this.string2 = string2;
        this.string3 = string3;
    }
    @Generated(hash = 1898313705)
    public DJScreens() {
    }
    public Long getId() {
        return this.Id;
    }
    public void setId(Long Id) {
        this.Id = Id;
    }
    public int getScreenRank() {
        return this.screenRank;
    }
    public void setScreenRank(int screenRank) {
        this.screenRank = screenRank;
    }
    public Long getModified() {
        return this.modified;
    }
    public void setModified(Long modified) {
        this.modified = modified;
    }
    public String getSceneId() {
        return this.sceneId;
    }
    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
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
