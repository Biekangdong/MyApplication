package com.system.jitpacklibrary;

/**
 * Created by xzh on 2020/4/2.
 */
public class WebUtil {
    /**
     * 添加头部
     *
     * @auther yuezhenfeng
     * creat at 2017/8/27 0027 20:51
     */
    public static String getHtmlData(String bodyHTML) {
        String head = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
                "<style>img{max-width: 100%; width:auto; height:auto;}" +
                "body {" +
                "margin-right:8px;" +//限定网页中的文字右边距为15px(可根据实际需要进行行管屏幕适配操作)
                "margin-left:8px;" +//限定网页中的文字左边距为15px(可根据实际需要进行行管屏幕适配操作)
                "margin-top:8px;" +//限定网页中的文字上边距为15px(可根据实际需要进行行管屏幕适配操作)
                "font-size:16px;" +//限定网页中文字的大小为40px,请务必根据各种屏幕分辨率进行适配更改
                "word-wrap:break-word;" +//允许自动换行(汉字网页应该不需要这一属性,这个用来强制英文单词换行,类似于word/wps中的西文换行)
                "}" +
                "</style>" +
                "</head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body><ml>";
    }
}
