package com.system.ipscan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView tvText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvText = (TextView) findViewById(R.id.tv_text);

        tvText.setText(saleFormat((int) (9.82*1000)));

        //new ScanDeviceUtile().scan();

//        ScanNetworkUtils.getDeviceOnLineStatus(ScanNetworkUtils.getHostIP(),"8089");
    }

    /**
     * 销售量格式化
     * 销量小于万位：展示实际数量
     * 销量大于万位：取万位和千位保留一位小数 无四舍五入等规则 例如1.2+万 2.3+万
     * 销量大于10w ：直接显示10万+
     */

    public  String saleFormat(int number){
        if(number>10*10000){
            // 1w+ 格式化
            return "10万+";
        }else if(number>10000){
            return String.format(Locale.getDefault(), "%.1fw+", number / 10000f);
        }else {
            return String.valueOf(number);
        }
    }
}