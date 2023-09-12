package com.xixia.aiimageupload.icc;

import android.content.Context;

import android.util.Log;


import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;

public class ICCUtils {
    private static final String TAG = "ICCUtils";

    public static String ICCJaiCreate(Context context, String filePath) {

        try {
            BufferedImage rgbImage = ImageIO.read(new File(filePath));

            BufferedImage cmykImage = null;
            ColorSpace cpace = new ICC_ColorSpace(ICC_Profile.getInstance(ICCUtils.class.getClassLoader().getResourceAsStream("/assets/ISOcoated_v2_300_eci.icc")));
            ColorConvertOp op = new ColorConvertOp(rgbImage.getColorModel().getColorSpace(), cpace, null);
            cmykImage = op.filter(rgbImage, null);

            // SD卡根目录
            File dir = context.getExternalFilesDir("lensun");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File picFile = new File(dir, System.currentTimeMillis() + ".jpg");
            //JAI.create("filestore", cmykImage, "C://Users//Lixia//Pictures//Saved Pictures//Microsoft//山水.tif", "TIFF");
            JAI.create("filestore", cmykImage, picFile.getAbsoluteFile(), "JPG");
            return picFile.getAbsolutePath();
        } catch (IOException e) {
            e.getStackTrace();
            Log.e(TAG, "ICCJaiCreate: " + e.getMessage());
        }
        return null;
    }


}
