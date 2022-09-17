package com.system.ipscan;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName ScanNetworkUtils
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2022/6/25 11:08
 * @Version 1.0
 * @UpdateDate 2022/6/25 11:08
 * @UpdateRemark 更新说明
 */
public class ScanNetworkUtils {

    /**
     * @description: 获取设备在线状态
     * @param networkSegment 网段 port 端口
     * @return Map<String,Boolean>
     * @author panlupeng
     * @date 2022/3/22 16:19
     */
    public static Map<String,Boolean> getDeviceOnLineStatus(String networkSegment, String port){
        Map<String,String> map=new LinkedHashMap<>();
        Map<String,Boolean> resultMap=new HashMap();
        if(TextUtils.isEmpty(networkSegment)){
            try{
                networkSegment = InetAddress.getLocalHost().getHostAddress();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        int lastPoint = networkSegment.lastIndexOf('.');
        String ipHead = networkSegment.substring(0, ++lastPoint);
        for (int num = 1; num < 255; num++) {
            String ip = ipHead + String.valueOf(num);
            map.put(ip,port);
        }
        if(!map.isEmpty()){
            Iterator<Map.Entry<String, String>> entries = map.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry<String, String> entry = entries.next();
                boolean pingIpAndPort = pingIpAndPort(entry.getKey(),entry.getValue());
                System.out.println("IP为:"+entry.getKey()+",连接状态:"+pingIpAndPort);
                resultMap.put(entry.getKey(),pingIpAndPort);
            }
        }
        return resultMap;
    }

    /**
     * @description: PING IP网络
     * @param  ip
     * @return boolean
     * @author panlupeng
     * @date 2022/3/22 16:53
     */
    public static boolean pingDeviceIp(String ip){
        if(TextUtils.isEmpty(ip)){
            return false;
        }
        if (!pingIp(ip)) {
            return false;
        }
        int timeOut = 3000;
        boolean reachable =false;
        try{
            reachable = InetAddress.getByName(ip).isReachable(timeOut);
        }catch (Exception e){
            e.printStackTrace();
        }
        return reachable;
    }

    /**
     * ping ip
     *
     * @param ip
     * @return
     */
    public static boolean pingIp(String ip) {
        if (null == ip || 0 == ip.length()) {
            return false;
        }
        try {
            InetAddress.getByName(ip);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * ping ip加端口
     *
     * @param ip
     * @param port
     * @return
     */
    public static boolean pingIpAndPort(String ip, String port) {
        if (null == ip || 0 == ip.length() || null == port || 0 == port.length() || !isInt(port) || !isRangeInt(port, 1024, 65535)) {
            return false;
        }
        return pingIpAndPort(ip, port);
    }

    /**
     * 判断是否是整数
     *
     * @param str
     * @return
     */
    public static boolean isInt(String str) {
        if (!isNumeric(str)) {
            return false;
        }
        // 该正则表达式可以匹配所有的数字 包括负数
        Pattern pattern = Pattern.compile("[0-9]+");

        Matcher isNum = pattern.matcher(str); // matcher是全匹配
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 匹配是否包含数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (null == str || 0 == str.length()) {
            return false;
        }
        if (str.endsWith(".")) {
            return false;
        }
        // 该正则表达式可以匹配所有的数字 包括负数
        Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");

        Matcher isNum = pattern.matcher(str); // matcher是全匹配
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 是否在范围内
     *
     * @param str
     * @param start
     * @param end
     * @return
     */
    public static boolean isRangeInt(String str, int start, int end) {
        if (!isInt(str)) {
            return false;
        }
        int i = Integer.parseInt(str);
        return i > start && i < end;
    }



    /**
     * 获取ip地址
     *
     * @return
     */
    public static String getHostIP() {

        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            if (nis != null) {
                while (nis.hasMoreElements()) {
                    NetworkInterface ni = (NetworkInterface) nis.nextElement();
                    Enumeration<InetAddress> ias = ni.getInetAddresses();
                    while (ias.hasMoreElements()) {
                        ia = ias.nextElement();
                        if (ia instanceof Inet6Address) {
                            continue;// skip ipv6
                        }
                        String ip = ia.getHostAddress();
                        if (!"127.0.0.1".equals(ip)) {
                            hostIp = ia.getHostAddress();
                            break;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("yao", "SocketException");
            e.printStackTrace();
        }
        return hostIp;

    }
}
