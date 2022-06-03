package com.system.signalr;

import android.text.TextUtils;

import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * OPT付款码算法
 *
 * 付款码的生成是在TOTP算法的基础上做一次二次封装，二次封装的实现方案参考此链接。主要实现思路是：
 * 约定的质数 > 用户总数
 * 6位动态口令 = TOTP(shared_secret)
 * 付款码 = 账户ID + 6位动态口令 * 约定的质数
 * 其中，有几点要注意：
 * 1. 使用TOTP算法，算法采用30S的时长，时间戳使用UTC格式；
 * 2. 最终生成的付款码数字必须为19位，且以66开头，使用如上算法，默认生成的付款码可能最低只有15位，最高只有17位，当只有15位时，需要在数字前补充2位0。如，生成的默认付款码为99901568113183，则最终生成的付款码为660099901568113183。
 *
 * 1.2、条形码及二维码变化规则
 * 在产生如下场景时，条形码以及二维码需要重新绘制：
 * 1. 根据付款码生成规则，条形码以及二维码必须要在30秒之后重新生成，否则生成的付款码将失效；
 * 2. 支付失败之后；
 */
public class TotpUtils {

    /**
     * 共享密钥
     */
    private static final String SECRET_KEY = "hXzAb5laJWEwererer";

    /**
     * 时间步长 单位:毫秒 作为口令变化的时间周期
     */
    private static final long STEP = 30000;

    /**
     * 转码位数 [1-8]
     */
    private static final int CODE_DIGITS = 6;

    /**
     * 初始化时间
     */
    private static final long INITIAL_TIME = 0;

    /**
     * 柔性时间回溯
     */
    private static final long FLEXIBILIT_TIME = 5000;

    /**
     * 数子量级
     */
    private static final int[] DIGITS_POWER = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};

    /**
     * 生成一次性密码
     *
     * @param code 账户
     * @param pass 密码
     * @return String
     */
    public static String generateMyTOTP(String code, String pass) {
//        if (TextUtils.isEmpty(code) || TextUtils.isEmpty(pass)) {
//            throw new RuntimeException("账户密码不许为空");
//        }
        long now = new Date().getTime();
        String time = Long.toHexString(timeFactor(now)).toUpperCase();
        return generateTOTP(code + pass + SECRET_KEY, time);
    }

    /**
     * 刚性口令验证
     *
     * @param code 账户
     * @param pass 密码
     * @param totp 待验证的口令
     * @return boolean
     */
    public static boolean verifyTOTPRigidity(String code, String pass, String totp) {
        return generateMyTOTP(code, pass).equals(totp);
    }

    /**
     * 柔性口令验证
     *
     * @param code 账户
     * @param pass 密码
     * @param totp 待验证的口令
     * @return boolean
     */
    public static boolean verifyTOTPFlexibility(String code, String pass, String totp) {
        long now = new Date().getTime();
        String time = Long.toHexString(timeFactor(now)).toUpperCase();
        String tempTotp = generateTOTP(code + pass + SECRET_KEY, time);
        if (tempTotp.equals(totp)) {
            return true;
        }
        String time2 = Long.toHexString(timeFactor(now - FLEXIBILIT_TIME)).toUpperCase();
        String tempTotp2 = generateTOTP(code + pass + SECRET_KEY, time2);
        return tempTotp2.equals(totp);
    }

    /**
     * 获取动态因子
     *
     * @param targetTime 指定时间
     * @return long
     */
    private static long timeFactor(long targetTime) {
        return (targetTime - INITIAL_TIME) / STEP;
    }

    /**
     * 哈希加密
     *
     * @param crypto   加密算法
     * @param keyBytes 密钥数组
     * @param text     加密内容
     * @return byte[]
     */
    private static byte[] hmac_sha(String crypto, byte[] keyBytes, byte[] text) {
        try {
            Mac hmac;
            hmac = Mac.getInstance(crypto);
            SecretKeySpec macKey = new SecretKeySpec(keyBytes, "AES");
            hmac.init(macKey);
            return hmac.doFinal(text);
        } catch (GeneralSecurityException gse) {
            throw new UndeclaredThrowableException(gse);
        }
    }

    private static byte[] hexStr2Bytes(String hex) {
        byte[] bArray = new BigInteger("10" + hex, 16).toByteArray();
        byte[] ret = new byte[bArray.length - 1];
        System.arraycopy(bArray, 1, ret, 0, ret.length);
        return ret;
    }

    private static String generateTOTP(String key, String time) {
        return generateTOTP(key, time, "HmacSHA1");
    }


    private static String generateTOTP256(String key, String time) {
        return generateTOTP(key, time, "HmacSHA256");
    }

    private static String generateTOTP512(String key, String time) {
        return generateTOTP(key, time, "HmacSHA512");
    }

    private static String generateTOTP(String key, String time, String crypto) {
        StringBuilder timeBuilder = new StringBuilder(time);
        while (timeBuilder.length() < 16)
            timeBuilder.insert(0, "0");
        time = timeBuilder.toString();

        byte[] msg = hexStr2Bytes(time);
        byte[] k = key.getBytes();
        byte[] hash = hmac_sha(crypto, k, msg);
        return truncate(hash);
    }

    /**
     * 截断函数
     *
     * @param target 20字节的字符串
     * @return String
     */
    private static String truncate(byte[] target) {
        StringBuilder result;
        int offset = target[target.length - 1] & 0xf;
        int binary = ((target[offset] & 0x7f) << 24)
                | ((target[offset + 1] & 0xff) << 16)
                | ((target[offset + 2] & 0xff) << 8) | (target[offset + 3] & 0xff);

        int otp = binary % DIGITS_POWER[CODE_DIGITS];
        result = new StringBuilder(Integer.toString(otp));
        while (result.length() < CODE_DIGITS) {
            result.insert(0, "0");
        }
        return result.toString();
    }
}

