package com.xixia.aiimageupload;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;

public class TestRgbToCMYK {

	public static void main(String args[]){
		try {
			rgbToCmyk();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void rgbToCmyk() throws IOException{

	    BufferedImage rgbImage = ImageIO.read(new File("C://Users//Lixia//Pictures//Saved Pictures//Microsoft//山水.png"));
	    BufferedImage cmykImage = null;
	    ColorSpace cpace = new ICC_ColorSpace(ICC_Profile.getInstance(TestRgbToCMYK.class.getClassLoader().getResourceAsStream("ISOcoated_v2_300_eci.icc")));
	    ColorConvertOp op = new ColorConvertOp(rgbImage.getColorModel().getColorSpace(), cpace, null);       
	    cmykImage = op.filter(rgbImage, null);

	    JAI.create("filestore", cmykImage, "C://Users//Lixia//Pictures//Saved Pictures//Microsoft//山水.tif", "TIFF");
	}
	
}
