package com.juai.tang;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

/**
 * @ClassName LoginActivity
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023-09-15 17:10
 * @Version 1.0
 * @UpdateDate 2023-09-15 17:10
 * @UpdateRemark 更新说明
 */
public class LoginActivity extends Activity {
    private EditText etName;
    private EditText etPassword;
    private Button btnLogin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etName = (EditText) findViewById(R.id.et_name);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }


    private void login() {
        String username=etName.getText().toString();
        String password=etPassword.getText().toString();
        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = "http://dongfangdashu.top/api/login";
        Request<String> mRequest = NoHttp.createStringRequest(url, RequestMethod.POST);
        mRequest.add("username",username);
        mRequest.add("password",password);
        // 添加到请求队列
        RequestQueue queue = NoHttp.newRequestQueue();

        queue.add(0, mRequest, new OnResponseListener<String>() {

            @Override
            public void onSucceed(int what, Response<String> response) {
                String result = response.get();
                Log.e("AAAAAA", "onSucceed: " + result);
                if (response.responseCode() == 200) {// 请求成功。
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.e("AAAAAA", "onFailed: " + response.getException().getMessage());
            }

            @Override
            public void onStart(int what) {
                // 这里可以show()一个wait dialog。
                Log.e("AAAAAA", "onStart: " + what);
            }

            @Override
            public void onFinish(int what) {
                // 这里可以dismiss()上面show()的wait dialog。
                Log.e("AAAAAA", "onFinish: " + what);
            }
        });
    }
}
