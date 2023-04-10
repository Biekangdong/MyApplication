package com.example.nfc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;

    private TextView tvContent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvContent = (TextView) findViewById(R.id.tv_content);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

    }

    //在onResume中开启前台调度
    @Override
    protected void onResume() {
        super.onResume();
        //设定intentfilter和tech-list。如果两个都为null就代表优先接收任何形式的TAG action。也就是说系统会主动发TAG intent。
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null); //启动        }
        }
    }


    //在onNewIntent中处理由NFC设备传递过来的intent
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e(TAG, "--------------NFC-------------");
        processIntent(intent);
    }

    //  这块的processIntent() 就是处理卡中数据的方法
    public void processIntent(Intent intent) {
        try {
            tvContent.setText("");
            // 检测卡的id
            String id = readNFCId(intent);
            Log.e(TAG, "processIntent--id: " + id);
            // NfcUtils中获取卡中数据的方法
            String result = readNFCFromTag(intent);
            Log.e(TAG, "processIntent--result: " + result);
            Toast.makeText(this, "卡ID:"+id, Toast.LENGTH_SHORT).show();

            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append("卡ID:"+id).append("\r\n");
            stringBuilder.append("信息:").append("\r\n");
            stringBuilder.append(result).append("\r\n");
            tvContent.setText(stringBuilder);
            // 往卡中写数据
//            String data = "this.is.write";
//            writeNFCToTag(data, intent);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将字节数组转换为字符串
     */
    private  String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

    /**
     * 读取nfcID
     */
    public  String readNFCId(Intent intent) throws UnsupportedEncodingException {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String id = ByteArrayToHexString(tag.getId());
        return id;
    }


    /**
     * 读取NFC的数据
     */
    public  String readNFCFromTag(Intent intent) throws UnsupportedEncodingException {
        Parcelable[] rawArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        StringBuilder stringBuilder=new StringBuilder();
        if (rawArray != null) {
            for(int i=0;i<rawArray.length;i++){
                NdefMessage mNdefMsg = (NdefMessage) rawArray[i];

                for(int j=0;j<mNdefMsg.getRecords().length;i++){
                NdefRecord mNdefRecord = mNdefMsg.getRecords()[j];

                    if (mNdefRecord != null) {
                        String readResult = new String(mNdefRecord.getPayload(), "UTF-8");
                        stringBuilder.append(readResult).append("\r\n");
                    }
                }
            }
//            NdefMessage mNdefMsg = (NdefMessage) rawArray[0];
//            NdefRecord mNdefRecord = mNdefMsg.getRecords()[0];
//            if (mNdefRecord != null) {
//                String readResult = new String(mNdefRecord.getPayload(), "UTF-8");
//                return readResult;
//            }
        }
        return stringBuilder.toString();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNfcAdapter = null;
    }
}