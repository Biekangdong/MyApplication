package com.juai.canvastest.plt;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.juai.canvastest.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * ;:H A L0 ECN U U0,0;U4000,4000;D0,4000;D0,0;D4000,0;D4000,4000;D4000,4000;U4000,0;@;
 * 上面数据是一个100*100mm的方框，数据分解如下
 * ;:H A L0 ECN U U0,0;  是整个数据的开头命令
 * U4000,4000;   ‘U’ 代表抬刀，后面数字是点的绝对坐标，‘;’是命令结束符
 * D0,4000;   ‘D’ 代表落刀，后面数字是点的绝对坐标，‘;’是命令结束符
 *
 * @; '@'是整个数据的结束命令，‘;’是命令结束符
 * <p>
 * 分辨率是1016/inch,   值4000代表100mm  ,1 inch=25.4 mm
 */
public class DrawPLTEditView extends View {
    private static final String TAG = "DrawPLTView";
    private Context context;
    public List<PLTPointGroup> pltPointGroupList = new ArrayList<>();


    private float offerX, offerY;
    private int minX, minY;
    int maxXLength, maxYLength;
    private float ratioHeight = 1;

    private Paint paint = new Paint();

    public DrawPLTEditView(Context context) {
        this(context, null);
        this.context = context;
    }

    public DrawPLTEditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }

    public void setPltPointGroupList(List<PLTPointGroup> pltPointGroupList) {
        this.pltPointGroupList = pltPointGroupList;
        requestLayout();
        handler.sendEmptyMessageDelayed(1, 1000);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewWidth = 0, viewHeight = 0;
        float pltWidth = 0, pltHeight = 0;

        //图纸坐标宽高
        if (pltPointGroupList.size() > 0) {
            maxXLength = pltPointGroupList.get(0).maxXLength;
            maxYLength = pltPointGroupList.get(0).maxYLength;
            minX = pltPointGroupList.get(0).minX;
            minY = pltPointGroupList.get(0).minY;

            //图纸像素最大宽高 (maxXLength/40f 毫米)
            pltWidth = getApplyDimension((int) (maxXLength / 40f));
            pltHeight = getApplyDimension((int) (maxYLength / 40f));

            if (maxXLength > maxYLength) {//宽大于高
                viewWidth = ScreenUtils.getScreenWidth(context) - ScreenUtils.dip2px(context, 26) * 2 - ScreenUtils.dip2px(context, 32) * 2;
                //比例
                ratioHeight = decimalFloatDouble(viewWidth * 1f / pltWidth);
                viewHeight = (int) (pltHeight * ratioHeight);
                ratioHeight = ratioHeight - 0.01f;

            } else {
                viewHeight = ScreenUtils.getScreenHeight(context) -ScreenUtils.getStatusBarHeight(context)- ScreenUtils.dip2px(context, 50) * 2 - ScreenUtils.dip2px(context, 32) * 2;
                //比例
                ratioHeight = decimalFloatDouble(viewHeight * 1f / pltHeight);

                viewWidth = (int) (pltWidth * ratioHeight);
                ratioHeight = ratioHeight - 0.02f;


            }

            //解决移动超边缘不可见问题
            viewWidth = ScreenUtils.getScreenWidth(context);
            viewHeight = ScreenUtils.getScreenHeight(context)-ScreenUtils.getStatusBarHeight(context);


            //偏移
            offerX = (viewWidth - pltWidth * ratioHeight) / 2f;
            offerY = (viewHeight - pltHeight * ratioHeight) / 2f;

            Log.e(TAG, "onMeasure: " + ScreenUtils.px2dip(context, viewWidth) + "--" + ScreenUtils.px2dip(context, viewHeight));
            Log.e(TAG, "onMeasure2: " + ScreenUtils.px2dip(context, pltWidth) + "--" + ScreenUtils.px2dip(context, pltHeight));

            setMeasuredDimension((int) viewWidth, (int) viewHeight);

        }
    }

    public float decimalFloatDouble(double number) {
        BigDecimal bigDecimal = BigDecimal.valueOf(number).setScale(2, RoundingMode.DOWN);
        return bigDecimal.floatValue();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (pltPointGroupList.size() > 0) {
            if(groupIndex>0) {
                List<PLTPointGroup> pltPointHaveGroupList = pltPointGroupList.subList(0, groupIndex);
                for (int position = 0; position < pltPointHaveGroupList.size(); position++) {
                    PLTPointGroup pltPointGroup = pltPointHaveGroupList.get(position);

                    for (int i = 0; i < pltPointGroup.pltPointList.size() - 1; i++) {
                        PLTPoint startPltPoint = pltPointGroup.pltPointList.get(i);
                        int startX = startPltPoint.x;
                        int startY = startPltPoint.y;

                        PLTPoint stopPltPoint = pltPointGroup.pltPointList.get(i + 1);
                        int stopX = stopPltPoint.x;
                        int stopY = stopPltPoint.y;

                        float startXX = getApplyDimension((startX - minX) / 40f * ratioHeight);
                        float startYY = getApplyDimension((startY - minY) / 40f * ratioHeight);
                        float stopXX = getApplyDimension((stopX - minX) / 40f * ratioHeight);
                        float stopYY = getApplyDimension((stopY - minY) / 40f * ratioHeight);

                        canvas.drawLine(startXX + offerX,
                                startYY + offerY,
                                stopXX + offerX,
                                stopYY + offerY,
                                paint);
                    }
                }
            }

            PLTPointGroup pltPointGroup = pltPointGroupList.get(groupIndex);
            for (int i = 0; i < pltPointGroup.pltPointList.size() - 1; i++) {
                if (i <= childIndex) {
                    PLTPoint startPltPoint = pltPointGroup.pltPointList.get(i);
                    int startX = startPltPoint.x;
                    int startY = startPltPoint.y;

                    PLTPoint stopPltPoint = pltPointGroup.pltPointList.get(i + 1);
                    int stopX = stopPltPoint.x;
                    int stopY = stopPltPoint.y;

                    float startXX = getApplyDimension((startX - minX) / 40f * ratioHeight);
                    float startYY = getApplyDimension((startY - minY) / 40f * ratioHeight);
                    float stopXX = getApplyDimension((stopX - minX) / 40f * ratioHeight);
                    float stopYY = getApplyDimension((stopY - minY) / 40f * ratioHeight);

                    canvas.drawLine(startXX + offerX,
                            startYY + offerY,
                            stopXX + offerX,
                            stopYY + offerY,
                            paint);
                }
            }


        }
    }

    int groupIndex = 0;
    int childIndex = 0;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                childIndex++;
                if (childIndex >= pltPointGroupList.get(groupIndex).pltPointList.size()) {
                    groupIndex++;
                    childIndex = 0;
                }

                if (groupIndex < pltPointGroupList.size()) {
                    invalidate();
                    handler.sendEmptyMessageDelayed(1, 50);
                }
            }
        }
    };

    /**
     * 一次性画完
     *
     * @param canvas
     */
    private void drawAllPoint(Canvas canvas) {
        for (int position = 0; position < pltPointGroupList.size(); position++) {
            PLTPointGroup pltPointGroup = pltPointGroupList.get(position);

            for (int i = 0; i < pltPointGroup.pltPointList.size() - 1; i++) {
                PLTPoint startPltPoint = pltPointGroup.pltPointList.get(i);
                int startX = startPltPoint.x;
                int startY = startPltPoint.y;

                PLTPoint stopPltPoint = pltPointGroup.pltPointList.get(i + 1);
                int stopX = stopPltPoint.x;
                int stopY = stopPltPoint.y;

                float startXX = getApplyDimension((startX - minX) / 40f * ratioHeight);
                float startYY = getApplyDimension((startY - minY) / 40f * ratioHeight);
                float stopXX = getApplyDimension((stopX - minX) / 40f * ratioHeight);
                float stopYY = getApplyDimension((stopY - minY) / 40f * ratioHeight);

                canvas.drawLine(startXX + offerX,
                        startYY + offerY,
                        stopXX + offerX,
                        stopYY + offerY,
                        paint);
            }
        }
    }

    /**
     * 像素=毫米x分辨率
     * dip，像素/英寸单位，1英寸=2.54厘米=25.4毫米
     * metrics.xdpi * (1.0f/25.4f)  代表分辨率x1.0fx1英寸  就是所需的dip(25.4f毫米级表示1英寸)
     * (300f / 25.4f) 一英寸上有300像素，一毫米上有 (300f / 25.4f)像素
     * value 毫米值
     */
    private float getApplyDimension(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, value, context.getResources().getDisplayMetrics());
    }
}
