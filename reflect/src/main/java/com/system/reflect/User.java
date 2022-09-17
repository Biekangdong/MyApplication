package com.system.reflect;

import android.util.Log;

/**
 * @ClassName User
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2022/6/23 16:01
 * @Version 1.0
 * @UpdateDate 2022/6/23 16:01
 * @UpdateRemark 更新说明
 */
public class User {
    private static final String TAG = "User";
    public String name="小东西";
    public int age=10;

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void show(){
        Log.e(TAG, "show: "+name );
    }

    public void function(String name){
        Log.e(TAG, "function: "+name );
    }


    public void reutrnValue(String name,int age){
        Log.e(TAG, "reutrnValue: "+name+"--"+age);
    }


    private void hello(){
        Log.e(TAG, "hello: "+name );
    }

}
