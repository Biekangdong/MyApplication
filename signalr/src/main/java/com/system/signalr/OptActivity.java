package com.system.signalr;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;

public class OptActivity extends AppCompatActivity {
    private static final String TAG = "OptActivity";
    private Button btnStart;
    private Button btnStop;
    private TextView tvCode;


    private CountDownTimer timer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opt);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnStop = (Button) findViewById(R.id.btn_stop);
        tvCode = (TextView) findViewById(R.id.tv_code);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDown();
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timer!=null)
                timer.cancel();
            }
        });
    }

    private void startDown(){
        timer = new CountDownTimer(Long.valueOf(30*1000), 1000) {
            public void onTick(long millisUntilFinished) {
                tvCode.setText(payCode());
            }

            public void onFinish() {
                timer.start();
            }
        };
        timer.start();
    }

    private String payCode(){
        /**
         * 约定的质数 > 用户总数
         * 6位动态口令 = TOTP(shared_secret)
         * 付款码 = 账户ID + 6位动态口令 * 约定的质数
         */
        String userId="615788";//账户ID
        String prime="1000001773";//质数

        //6位动态口令
        String totp = TotpUtils.generateMyTOTP("", "");
        //6位动态口令 * 约定的质数
        long conum = (BigDecimal.valueOf(Long.parseLong(totp)).multiply(BigDecimal.valueOf(Long.parseLong(prime)))).longValue();
        // 付款码 = 账户ID + 6位动态口令 * 约定的质数
        conum = (BigDecimal.valueOf(Long.parseLong(userId)).add(BigDecimal.valueOf(conum))).longValue();

        //1. 使用TOTP算法，算法采用30S的时长，时间戳使用UTC格式；
        //2. 最终生成的付款码数字必须为19位，且以66开头，使用如上算法，默认生成的付款码可能最低只有15位，最高只有17位，当只有15位时，需要在数字前补充2位0。如，生成的默认付款码为99901568113183，则最终生成的付款码为660099901568113183。
        String lastcode=String.valueOf(conum);
        if(lastcode.length() == 17 ){
            lastcode = "66" + lastcode;
        }else{
            int laNum = 17 - lastcode.length();
            for(int i = 0; i < laNum; i++){
                lastcode = "0" + lastcode;
            }
            lastcode = "66" + lastcode;
        }
        return lastcode;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer!=null)
            timer.cancel();
    }
}
