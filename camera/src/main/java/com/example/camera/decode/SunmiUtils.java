package com.example.camera.decode;


import com.sunmi.camerascan.Config;
import com.sunmi.camerascan.Image;
import com.sunmi.camerascan.ImageScanner;
import com.sunmi.camerascan.Symbol;
import com.sunmi.camerascan.SymbolSet;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @ClassName SunmiUtils
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023-10-14 17:50
 * @Version 1.0
 * @UpdateDate 2023-10-14 17:50
 * @UpdateRemark 更新说明
 */
public class SunmiUtils {
    //识别画面中多个二维码
    public static boolean IDENTIFY_MORE_CODE = false;
    private static ImageScanner mImageScanner;//声明扫描器
    private static ImageScanner initScanner(){
        mImageScanner = new ImageScanner();//创建扫描器
        mImageScanner.setConfig(Symbol.NONE, Config.ENABLE_MULTILESYMS, 0);//默认0: 只解一个
        return mImageScanner;
    }


    public static String decode(int width, int height, byte[] data){
        if(mImageScanner==null){
            mImageScanner=initScanner();
        }
        //创建解码图像，并转换为原始灰度数据，注意图片是被旋转了90度的
        Image source = new Image(width, height, "Y800");
        //图片旋转了90度，将扫描框的TOP作为left裁剪
        source.setData(data);//填充数据

        ArrayList<HashMap<String, String>> result = new ArrayList<>();
//                    //解码，返回值为0代表失败，>0表示成功
        int dataResult = mImageScanner.scanImage(source);
        if (dataResult != 0) {
            SymbolSet syms = mImageScanner.getResults();//获取解码结果
            for (Symbol sym : syms) {
                HashMap<String, String> temp = new HashMap<>();
                temp.put("TYPE", sym.getSymbolName());
                temp.put("VALUE", sym.getResult());
                result.add(temp);
                //识别画面中多个二维码
                if (IDENTIFY_MORE_CODE) {
                    break;
                }
            }
            syms.destroy();
            if (result.size() > 0) {
                return result.get(0).get("VALUE");
            } else {
                return null;
            }
        }else {
            return null;
        }
    }

    private void destroy(){
        if (mImageScanner != null) {
            mImageScanner.destroy();
        }
    }
}
