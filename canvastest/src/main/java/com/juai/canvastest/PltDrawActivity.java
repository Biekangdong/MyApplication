package com.juai.canvastest;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.juai.canvastest.plt.DrawPLTEditView;
import com.juai.canvastest.plt.PLTPointGroup;
import com.juai.canvastest.plt.PltEditUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName PltDrawActivity
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/8/31 17:52
 * @Version 1.0
 * @UpdateDate 2023/8/31 17:52
 * @UpdateRemark 更新说明
 */
public class PltDrawActivity extends AppCompatActivity {
    private FrameLayout flContent;


    private PltEditUtils pltEditUtils;
    private List<PLTPointGroup> pltPointGroupListAll = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plt);

        flContent = (FrameLayout) findViewById(R.id.fl_content);

        String pathName = "4.plt";
        new MyReadPointAsyncTask().execute(pathName);

    }

    public void plt1(View view) {
        String pathName = "1.plt";
        new MyReadPointAsyncTask().execute(pathName);
    }

    public void plt2(View view) {
        String pathName = "2.plt";
        new MyReadPointAsyncTask().execute(pathName);
    }

    public void plt3(View view) {
        String pathName = "3.plt";
        new MyReadPointAsyncTask().execute(pathName);
    }

    public void plt4(View view) {
        String pathName = "4.plt";
        new MyReadPointAsyncTask().execute(pathName);
    }


    /**
     * 初始化原始坐标数据
     */
    public class MyReadPointAsyncTask extends AsyncTask<String, Void, List<PLTPointGroup>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pltEditUtils == null) {
                pltEditUtils = new PltEditUtils();
            }
        }

        @Override
        protected List<PLTPointGroup> doInBackground(String... strings) {
            return pltEditUtils.resetPltPoint(PltDrawActivity.this, strings[0]);
        }

        @Override
        protected void onPostExecute(List<PLTPointGroup> list) {
            super.onPostExecute(list);
            pltPointGroupListAll.clear();
            pltPointGroupListAll.addAll(list);


            DrawPLTEditView drawPLTEditView=new DrawPLTEditView(PltDrawActivity.this);
            flContent.removeAllViews();
            flContent.addView(drawPLTEditView);
            drawPLTEditView.setPltPointGroupList(pltPointGroupListAll);
        }
    }

}