package com.xixia.aiimageupload.opcv;

import android.graphics.Bitmap;
import android.graphics.Color;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import java.lang.Math;
import org.opencv.imgproc.Imgproc;


public class Photo_Function {

    Photo_Function(){}

    private int colordodge(int A, int B) {
        return  Math.min(A+(A*B)/(255-B+1),255);
    }



    //灰度化方法
    Bitmap RGB2Gray(Bitmap photo) {
        Mat RGBMat = new Mat();
        Bitmap grayBitmap = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.RGB_565);
        Utils.bitmapToMat(photo, RGBMat);//convert original bitmap to Mat, R G B.
        Imgproc.cvtColor(RGBMat, RGBMat, Imgproc.COLOR_RGB2GRAY);//rgbMat to gray grayMat
        Utils.matToBitmap(RGBMat, grayBitmap);
        return grayBitmap;
    }

    //素描滤镜
    Bitmap SuMiao(Bitmap photo){
        Mat SM = new Mat();
        Mat SM1 = new Mat();
        Bitmap sumiaoMap = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap SMB = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap SMB1 = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.bitmapToMat(photo, SM);
        //灰度化
        Imgproc.cvtColor(SM, SM, Imgproc.COLOR_RGB2GRAY);
        //颜色取反
        Core.bitwise_not(SM,SM1);
        //高斯模糊
        Imgproc.GaussianBlur(SM1,SM1,new Size(13,13),0,0);
        Utils.matToBitmap(SM, SMB);
        Utils.matToBitmap(SM1, SMB1);
        for(int i = 0;i<SMB.getWidth();i++){
            for( int j = 0;j<SMB.getHeight();j++){
                int A = SMB.getPixel(i,j);
                int B = SMB1.getPixel(i,j);
                int CR = colordodge(Color.red(A),Color.red(B));
                int CG = colordodge(Color.green(A),Color.red(B));
                int CB = colordodge(Color.blue(A),Color.blue(B));
                sumiaoMap.setPixel(i,j,Color.rgb(CR,CG,CB));
            }
        }
        return sumiaoMap;
    }

    //二值化滤镜
    Bitmap theshold(Bitmap photo){
        Mat mat = new Mat();
        Bitmap thes = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.bitmapToMat(photo, mat);
        Imgproc.cvtColor(mat,mat,Imgproc.COLOR_RGB2GRAY);
        //Imgproc.GaussianBlur(mat,mat,new Size(13,13),0,0);
        //Imgproc.Canny(mat,mat,70,210);
        Core.bitwise_not(mat,mat);
        Imgproc.threshold(mat,mat,100,255,Imgproc.THRESH_BINARY_INV);
        Utils.matToBitmap(mat,thes);
        return thes;
    }

    //轮廓
    Bitmap Lunkuo(Bitmap photo){
        Mat mat = new Mat();
        Mat Cmat = new Mat();
        Mat Bmat = new Mat();
        Bitmap cartton = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.bitmapToMat(photo, mat);
        Imgproc.Canny(mat,Cmat,50,100);
        Core.bitwise_not(Cmat,Cmat);
        Utils.matToBitmap(Cmat, cartton);
        return cartton;
    }

    //怀旧色滤镜
    Bitmap HuaiJiu(Bitmap photo){
        Bitmap huaijiu = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.ARGB_8888);
        for(int i = 0;i<photo.getWidth();i++){
            for( int j = 0;j<photo.getHeight();j++){
                int A = photo.getPixel(i,j);
                int AR =(int)(0.393*Color.red(A) + 0.769*Color.green(A) + 0.189*Color.blue(A));
                int AG =(int)(0.349*Color.red(A) + 0.686*Color.green(A) + 0.168*Color.blue(A));
                int AB =(int)(0.272*Color.red(A) + 0.534*Color.green(A) + 0.131*Color.blue(A));
                AR = AR > 255 ? 255 : AR;
                AG = AG > 255 ? 255 : AG;
                AB = AB > 255 ? 255 : AB;
                huaijiu.setPixel(i,j,Color.rgb(AR,AG,AB));
            }
        }
        return huaijiu;
    }

    //连环画滤镜
    Bitmap LianHuanHua(Bitmap photo){
        Bitmap lianhuanhua = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.ARGB_8888);
        for(int i = 0;i<photo.getWidth();i++){
            for( int j = 0;j<photo.getHeight();j++){
                int A = photo.getPixel(i,j);
                int AR =Math.abs(Color.red(A) - Color.blue(A) + Color.green(A)+ Color.green(A)  ) * Color.red(A) / 256;
                int AG =Math.abs(Color.red(A) - Color.green(A) + Color.blue(A) + Color.blue(A)) * Color.red(A) / 256;
                int AB =Math.abs(Color.red(A) - Color.blue(A) + Color.blue(A) + Color.blue(A)) * Color.green(A) / 256;
                AR = AR > 255 ? 255 : AR;
                AG = AG > 255 ? 255 : AG;
                AB = AB > 255 ? 255 : AB;
                lianhuanhua.setPixel(i,j,Color.rgb(AR,AG,AB));
            }
        }
        return lianhuanhua;
    }

    //扩散滤镜（效果不明显，不采用）
    Bitmap KuoSan(Bitmap photo){
        Bitmap kuosan = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.ARGB_8888);
        int random, randomMAX = 10;
        for(int i = 0;i<photo.getWidth()-randomMAX;i++){
            for( int j = 0;j<photo.getHeight()-randomMAX;j++){
                random = (int)Math.random()*randomMAX;
                int AR =Color.red( photo.getPixel(i+random,j+random));
                int AG =Color.green( photo.getPixel(i+random,j+random));
                int AB =Color.blue( photo.getPixel(i+random,j+random));
                kuosan.setPixel(i,j,Color.rgb(AR,AG,AB));
            }
        }
        return kuosan;
    }

    //熔铸滤镜
    Bitmap RongZhu(Bitmap photo){
        Bitmap rongzhu  = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.ARGB_8888);
        for(int i = 0;i<photo.getWidth();i++){
            for( int j = 0;j<photo.getHeight();j++){
                int A = photo.getPixel(i,j);
                int AR =Color.red(A)*128/(Color.blue(A)+Color.green(A)+1);
                int AG =Color.green(A)*128/(Color.blue(A)+Color.red(A)+1);
                int AB =Color.blue(A)*128/(Color.red(A)+Color.green(A)+1);
                AR = AR > 255 ? 255 : AR;
                AG = AG > 255 ? 255 : AG;
                AB = AB > 255 ? 255 : AB;
                rongzhu.setPixel(i,j,Color.rgb(AR,AG,AB));
            }
        }
        return rongzhu;
    }

    //冰冻滤镜
    Bitmap BingDong(Bitmap photo){
        Bitmap bingdong  = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.ARGB_8888);
        for(int i = 0;i<photo.getWidth();i++){
            for( int j = 0;j<photo.getHeight();j++){
                int A = photo.getPixel(i,j);
                int AR =(Color.red(A)-Color.blue(A)-Color.green(A))*3/2;
                int AG =(Color.green(A)-Color.blue(A)-Color.red(A))*3/2;
                int AB =(Color.blue(A)-Color.red(A)-Color.green(A))*3/2;
                AR = AR > 255 ? 255 : AR;
                AG = AG > 255 ? 255 : AG;
                AB = AB > 255 ? 255 : AB;
                bingdong.setPixel(i,j,Color.rgb(AR,AG,AB));
            }
        }
        return bingdong;
    }

    //浮雕滤镜
    Bitmap FuDiao(Bitmap photo){
        Bitmap bingdong  = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.ARGB_8888);
        for(int i = 1;i<photo.getWidth()-1;i++){
            for( int j = 1;j<photo.getHeight()-1;j++){
                int A = photo.getPixel(i-1,j-1);
                int B = photo.getPixel(i+1,j+1);
                int AR =Color.red(B)-Color.red(A)+128;
                int AG =Color.green(B)-Color.green(A)+128;
                int AB =Color.blue(B)-Color.blue(A)+128;
                AR = AR > 255 ? 255 : AR;
                AG = AG > 255 ? 255 : AG;
                AB = AB > 255 ? 255 : AB;
                bingdong.setPixel(i,j,Color.rgb(AR,AG,AB));
            }
        }
        return bingdong;
    }


}

