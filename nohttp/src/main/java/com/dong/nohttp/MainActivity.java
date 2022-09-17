package com.dong.nohttp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONObject;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static String IP = "http://app.lensunstore.com/public/api/mobile/";//正式


    private Button btnAdd;
    private Button btnAdd2;

    private RequestQueue requestQueue;//请求的队列
    private  BlockingQueue<Request<?>> mFailQueue = new LinkedBlockingDeque<>();//失败的队列
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue=NoHttp.newRequestQueue();


        btnAdd = (Button) findViewById(R.id.btn_add);
        btnAdd2 = (Button) findViewById(R.id.btn_add2);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFailQueue.clear();
                addRequest1();
                addRequest2();
            }
        });

        btnAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int what=0;
                Log.e(TAG, "mFailQueue: "+mFailQueue.size());
                for(Request mRequest:mFailQueue){
                    what++;
                    requestQueue.add(what, mRequest, new OnResponseListener<String>() {
                        @Override
                        public void onStart(int what) {
                            Log.e(TAG, "onStart: "+what);
                        }

                        @Override
                        public void onSucceed(int what, Response<String> response) {
                            try {
                                JSONObject   object = new JSONObject(response.get());
                                if (object.getInt("msgcode") == 1) {
                                    mFailQueue.remove(mRequest);
                                    Log.e(TAG, "onSucceed: "+response.get());
                                }else {
                                    Log.e(TAG, "onSucceed: "+"失败");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "onSucceed: "+e.getMessage());
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailed(int what, Response<String> response) {
                            Log.e(TAG, "onFailed: "+response.get());
                        }

                        @Override
                        public void onFinish(int what) {
                            Log.e(TAG, "onFinish: "+what);
                        }
                    });
                }
            }
        });
    }


    private void addRequest1(){
        Request mRequest = NoHttp.createStringRequest(IP + "public/login", RequestMethod.POST);
        mRequest.add("user_name", "123@qq.com");
        mRequest.add("user_pass", "1234567");
        mFailQueue.add(mRequest);
        requestQueue.add(0, mRequest, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                Log.e(TAG, "onStart: "+what);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                try {
                    JSONObject   object = new JSONObject(response.get());
                    if (object.getInt("msgcode") == 1) {
                        mFailQueue.remove(mRequest);
                        Log.e(TAG, "onSucceed: "+response.get());
                    }else {
                        Log.e(TAG, "onSucceed: "+"失败");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "onSucceed: "+e.getMessage());
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.e(TAG, "onFailed: "+response.get());
            }

            @Override
            public void onFinish(int what) {
                Log.e(TAG, "onFinish: "+what);
            }
        });
    }

    private void addRequest2(){
        Request mRequest = NoHttp.createStringRequest(IP + "user/get_userInfo_second", RequestMethod.POST);
        mRequest.add("uid", "80000");
        mFailQueue.add(mRequest);
        requestQueue.add(1, mRequest, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                try {
                    JSONObject   object = new JSONObject(response.get());
                    if (object.getInt("msgcode") == 1) {
                        mFailQueue.remove(mRequest);
                        Log.e(TAG, "onSucceed: "+response.get());
                    }else {
                        Log.e(TAG, "onSucceed: "+"失败");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "onSucceed: "+e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.e(TAG, "onFailed: "+response.get());
            }

            @Override
            public void onFinish(int what) {
                Log.e(TAG, "onFinish: "+what);
            }
        });
    }
}