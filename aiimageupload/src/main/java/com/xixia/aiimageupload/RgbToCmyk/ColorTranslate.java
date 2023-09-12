package com.xixia.aiimageupload.RgbToCmyk;

import java.awt.Color;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;

import javax.swing.JOptionPane;

/**
 * @ClassName ColorTranslate
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/7/26 9:45
 * @Version 1.0
 * @UpdateDate 2023/7/26 9:45
 * @UpdateRemark 更新说明
 */
public class ColorTranslate {
    ICC_Profile ICC_pf;
    ICC_ColorSpace ICC_ClSpace;
    //以下变量存储CMYK颜色值，取值为0到100
    int C = 9;
    int M = 9;
    int Y = 9;
    int K = 9;

    //初始化ICC_Profile和ICC_ColorSpace类对象
    public ColorTranslate() {
        String Filename = "CMYK.pf";
        GetICCFrompfFile(Filename);
    }

    void GetICCFrompfFile(String Filename) {
        try {
            ICC_pf = ICC_Profile.getInstance(Filename);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Can''t create ICC_Profile");
        }
        ICC_ClSpace = new ICC_ColorSpace(ICC_pf);
    }

    //由RGB色彩空间变换到CMYK
    public float[] RGBtoCMYK(Color RGBColor) {
        float[] CMYKfloat = ICC_ClSpace.fromRGB
                (RGBColor.getRGBComponents(null));
        C = (int) (CMYKfloat[0] * 100);
        M = (int) (CMYKfloat[1] * 100);
        Y = (int) (CMYKfloat[2] * 100);
        K = (int) (CMYKfloat[3] * 100);
        return CMYKfloat;
    }

    //由CMYK色彩空间变换到RGB
    public Color CMYKtoRGB(float[] CMYKfloat) {
        Color RGBColor = new Color(ICC_ClSpace,
                CMYKfloat, 1.0f);
        return RGBColor;
    }

    public Color CMYKtoRGB() {
        float[] CMYKfloat = new float[4];
        CMYKfloat[0] = 0.01f * (float) C;
        CMYKfloat[1] = 0.01f * (float) M;
        CMYKfloat[2] = 0.01f * (float) Y;
        CMYKfloat[3] = 0.01f * (float) K;
        Color RGBColor = new Color(ICC_ClSpace, CMYKfloat, 1.0f);
        return RGBColor;
    }
}

