package com.juai.canvastest.plt;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PltEditUtils {
    private static final String TAG = "PltEditUtils";


    public List<PLTPointGroup> resetPltPoint(Context context, String filePath) {
        if (filePath.contains("/")) {
            return ReadFileToPoint(ReadTxtFile(filePath));
        } else {
            return ReadFileToPoint(getFromAssets(context, filePath));
        }
    }

    //读取Assets下的文件
    public String getFromAssets(Context context, String fileName) {
        String line = "";
        String Result = "";
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            while ((line = bufReader.readLine()) != null)
                Result += line;
            inputReader.close();
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取plt文件到字符串文本
     *
     * @param strFilePath
     * @return
     */
    public String ReadTxtFile(String strFilePath) {
        try {
            StringBuffer stringBuffer = new StringBuffer();
            //打开文件
            File file = new File(strFilePath);
            //如果path是传递过来的参数，可以做一个非目录的判断
            if (!file.exists()) {
                Log.d("TestFile", "The File doesn't not exist.");
            } else {

                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream, "utf-8");
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        stringBuffer.append(line + "\n");
                    }
                    instream.close();
                }

            }
            return stringBuffer.toString();
        } catch (Exception exception) {
            Log.e(TAG, "ReadTxtFile: " + exception.getMessage());
        }
        return null;
    }

    //原始全部数据数组
    public String[] dataArray;
    //下刀分组数据集合
    public List<PLTPointGroup> pltPointGroupList = new ArrayList<>();
    //下刀分组对象
    private PLTPointGroup pltPointGroup = new PLTPointGroup();
    private List<PLTPoint> pltPointList = new ArrayList<>();

    //后缀类型，“;”或者“ ”
    private int type;

    public List<PLTPointGroup> ReadFileToPoint(String data) {
        if (!TextUtils.isEmpty(data)) {
            Log.e(TAG, "ReadFileToPoint: "+data);

            pltPointGroupList.clear();

            dataArray = data.split(";");
            type = 0;
            if (dataArray.length <= 0) {
                dataArray = data.split(" ");
                type = 1;
            }
            if (dataArray.length <= 0) {
                return pltPointGroupList;
            }

            Log.e(TAG, "dataArray: "+dataArray.length);

            for (int i = 0; i < dataArray.length; i++) {
                String ss = dataArray[i];
                if (ss.startsWith("U")) {
                    if (pltPointList.size() > 1) {
                        pltPointGroup.pltPointList = pltPointList;
                        //添加闭合点开始
                        pltPointList.add(pltPointList.get(0));
                        //添加闭合点结束
                        pltPointGroupList.add(pltPointGroup);
                    }
                    pltPointGroup = new PLTPointGroup();
                    pltPointGroup.index = i;
                    pltPointList = new ArrayList<>();
                } else if (ss.startsWith("D")) {
                    String[] stringUD = ss.substring(ss.indexOf("D") + 1).split(",");
                    PLTPoint pltPoint = new PLTPoint(Integer.parseInt(stringUD[0]), Integer.parseInt(stringUD[1]), Integer.parseInt(stringUD[0]), Integer.parseInt(stringUD[1]));
                    pltPointList.add(pltPoint);
                }
            }

            getMinMaxPoint();
        }
        Log.e(TAG, "pltPointGroupList: "+pltPointGroupList.size());
        return pltPointGroupList;
    }

    //最大最小点
    private int iMaxX = -1;
    private int iMaxY = -1;
    public int iMinY = -1;
    public int iMinX = -1;
    public int maxYLength = 0;
    public int maxXLength = 0;

    public void getMinMaxPoint() {
        for (PLTPointGroup pltPointGroup : pltPointGroupList) {
            int groupMaxX = -1;
            int groupMaxY = -1;
            int groupMinY = -1;
            int groupMinX = -1;
            for (PLTPoint pltPoint : pltPointGroup.pltPointList) {
                int x = pltPoint.x;
                int y = pltPoint.y;

                //单个item获取最大最小
                if (groupMaxX == -1 && groupMinX == -1 && groupMinY == -1 && groupMaxY == -1) {
                    groupMinX = (int) x;
                    groupMaxX = (int) x;
                    groupMaxY = (int) y;
                    groupMinY = (int) y;
                } else {
                    groupMinX = (int) Math.min(x, groupMinX);
                    groupMinY = (int) Math.min(y, groupMinY);
                    groupMaxX = (int) Math.max(x, groupMaxX);
                    groupMaxY = (int) Math.max(y, groupMaxY);
                }

                //全局获取最大最小
                if (iMaxX == -1 && iMinX == -1 && iMinY == -1 && iMaxY == -1) {
                    iMinX = (int) x;
                    iMaxX = (int) x;
                    iMaxY = (int) y;
                    iMinY = (int) y;
                } else {
                    iMinX = (int) Math.min(x, iMinX);
                    iMinY = (int) Math.min(y, iMinY);
                    iMaxX = (int) Math.max(x, iMaxX);
                    iMaxY = (int) Math.max(y, iMaxY);
                }
            }

            //单个item最大最小
            pltPointGroup.groupMinX = groupMinX;
            pltPointGroup.groupMinY = groupMinY;
            pltPointGroup.groupMaxXLength = groupMaxX - groupMinX;
            pltPointGroup.groupMaxYLength = groupMaxY - groupMinY;
        }

        //全局最大最小
        maxXLength = iMaxX - iMinX;
        maxYLength = iMaxY - iMinY;

        if (pltPointGroupList.size() > 0) {
            pltPointGroupList.get(0).maxXLength = maxXLength;
            pltPointGroupList.get(0).maxYLength = maxYLength;
            pltPointGroupList.get(0).minX = iMinX;
            pltPointGroupList.get(0).minY = iMinY;
        }

        //计算最外层边框
        for (PLTPointGroup pltPointGroup : pltPointGroupList) {
            if (pltPointGroup.pltPointList.size() > 3 && maxXLength - pltPointGroup.groupMaxXLength < 5 * 40 && maxYLength - pltPointGroup.groupMaxYLength < 5 * 40) {
                pltPointGroup.isBorder = true;
            } else {
                pltPointGroup.isBorder = false;
            }
        }
    }

    /**
     * 保存编辑后的坐标
     */
    public String saveEditPoint() {
        StringBuilder stringBuilder = new StringBuilder();
        String splitEnd = type == 0 ? ";" : " ";
        stringBuilder.append(";:H A L0 ECN U0,0;P0;");
        for (int position = 0; position < pltPointGroupList.size(); position++) {
            PLTPointGroup pltPointGroup = pltPointGroupList.get(position);
            for (int i = 0; i < pltPointGroup.pltPointList.size(); i++) {
                PLTPoint pltPoint = pltPointGroup.pltPointList.get(i);
                if (i == 0) {
                    stringBuilder.append("U").append(pltPoint.x).append(",").append(pltPoint.y).append(splitEnd);
                }
                stringBuilder.append("D").append(pltPoint.x).append(",").append(pltPoint.y).append(splitEnd);
            }
        }

        stringBuilder.append("U0,0;@;@");

        String resultString = stringBuilder.toString();
        Log.e(TAG, "saveEditPoint: " + resultString);
        return resultString;
    }


}
