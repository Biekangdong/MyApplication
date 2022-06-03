package com.system.signalr;

public class MessageBean {

    public boolean success;
    public String msg;
    public int code;
    public DataDTO data;

    public static class DataDTO {
        public String appId;
        public String outTradeNo;
        public int userId;
        public double amount;
        public String shopName;
    }
}
