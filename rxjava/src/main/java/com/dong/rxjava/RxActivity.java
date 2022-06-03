package com.dong.rxjava;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okio.ByteString;


public class RxActivity extends AppCompatActivity {
    private static final String TAG = "RxActivity";

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_rxmain);

        /**
         * 数据缓存实例
         * 先获取本地缓存，如果网络缓和本地一样，就回调一遍。否则回调两遍
         */

        //数据1
        Observable observableLocal = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter emitter) throws Exception {
                emitter.onNext("0");
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());


        //数据2
        Observable observableLocal2 = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter emitter) throws Exception {
                emitter.onNext("0");
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());



        /**
         * 比对数据1和数据2
         * concat操作符和merge类似，把多个Observable拼接成一个可以观察的输出
         * filter只发射通过了函数过滤条件的数据项
         * distinct  : 过滤掉重复的元素
         * distinctUntilChanged: 过滤掉连续重复的元素,不连续重复的是不过滤
         */
        Observable.concat(observableLocal, observableLocal2)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(@io.reactivex.annotations.NonNull String tCacheResult) throws Exception {
                        return tCacheResult != null;
                    }
                })
                .distinctUntilChanged(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String tCacheResult) throws Exception {
                        return tCacheResult;
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String sss){
                        Log.e(TAG,"rxjava:"+sss);
                    }
                });
    }


}