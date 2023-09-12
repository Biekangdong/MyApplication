package com.xixia.aiimageupload.opcv;


import android.graphics.Bitmap;

import java.util.List;

public class Photo_class {
    private Bitmap Gray_photo;
    private Bitmap theshold_photo;
    private Bitmap Sumiao_photo;
    private Bitmap Lunkuo_photo;
    private Bitmap Huaijiu_photo;
    private Bitmap Rongzhu_photo;
    private Bitmap BingDong_photo;
    private Bitmap FuDiao_photo;
    private Bitmap LianHuanHua_photo;

    Photo_class(Bitmap photo){
        Gray_photo = null;
        theshold_photo = null;
        Sumiao_photo = null;
        Lunkuo_photo = null;
        Huaijiu_photo = null;
        Rongzhu_photo = null;
        BingDong_photo = null;
        FuDiao_photo = null;
        LianHuanHua_photo = null;
    }

    public void put_Gray_photo(Bitmap photo){
        this.Gray_photo = photo;
    }

    public void put_put_theshold_photo(Bitmap photo){
        this.theshold_photo = photo;
    }

    public void put_Sumiao_photo(Bitmap photo){
        this.Sumiao_photo = photo;
    }

    public void put_Huaijiu_photo(Bitmap photo){
        this.Huaijiu_photo = photo;
    }

    public void put_Rongzhu_photo(Bitmap photo){
        this.Rongzhu_photo = photo;
    }

    public void put_BingDong_photo(Bitmap photo){
        this.BingDong_photo = photo;
    }

    public void put_FuDiao_photo(Bitmap photo){
        this.FuDiao_photo = photo;
    }

    public void put_LianHuanHua_photo(Bitmap photo){
        this.LianHuanHua_photo = photo;
    }

    public void put_Lunkuo_photo(Bitmap photo){
        this.Lunkuo_photo = photo;
    }


    public Bitmap get_Lunkuo_photo(){
        return this.Lunkuo_photo;
    }

    public Bitmap get_Gray_photo(){
        return this.Gray_photo;
    }

    public Bitmap get_Huaijiu_photo(){
        return this.Huaijiu_photo;
    }

    public Bitmap get_Rongzhu_photo(){
        return this.Rongzhu_photo;
    }

    public Bitmap get_Sumiao_photo(){
        return this.Sumiao_photo;
    }

    public Bitmap get_theshold_photo(){
        return this.theshold_photo;
    }

    public Bitmap get_BingDong_photo(){
        return this.BingDong_photo;
    }

    public Bitmap get_LianHuanHua_photo(){
        return this.LianHuanHua_photo;
    }

    public Bitmap get_FuDiao_photo(){
        return this.FuDiao_photo;
    }
}
