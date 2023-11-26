package com.skydoves.colorpickerview;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 金额数字转化为千分位  1000--1,000
 * Created by Charles on 2018/10/19.
 */
public class DecimalUtil {

    //不含小数
    public static String decimal(String number) {
        BigDecimal a = new BigDecimal(number);
        DecimalFormat df = new DecimalFormat("##0");
        return df.format(a);
    }

    //保留一位小数
    public static String decimalFloat(String number) {
        BigDecimal a = new BigDecimal(number);
        DecimalFormat df = new DecimalFormat("##0.0");
        return df.format(a);
    }

    //保留两位为位小数
    public static String decimalFloatDouble(String number) {
        BigDecimal a = new BigDecimal(number);
        DecimalFormat df = new DecimalFormat("##0.00");
        return df.format(a);
    }

    //保留两位为位小数
    public static String decimalFloatDouble(String number, String format) {
        BigDecimal a = new BigDecimal(number);
        DecimalFormat df = new DecimalFormat(format);
        return df.format(a);
    }
    /**

     * 使用java正则表达式去掉多余的.与0

     * @param s

     * @return

     */

//    public static String subZeroAndDot(String s){
//        if(s.indexOf(".") > 0){
//            s = s.replaceAll("0+?$", "");//去掉多余的0
//            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
//        }
//        return s;
//    }
}
