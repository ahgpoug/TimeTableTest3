package com.ahgpoug.timetabletest3;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;

public class GlobalVariables implements Serializable
{
    public static ArrayList<ArrayList<DataInfo>> mListRed = new ArrayList<ArrayList<DataInfo>>();
    public static ArrayList<ArrayList<DataInfo>> mListGreen = new ArrayList<ArrayList<DataInfo>>();
    public static ArrayList<ScheduleInfo> scheduleList = new ArrayList<ScheduleInfo>();
    public static String days[] = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"};
    public static String weekType;
    public static String startWeek;
    public static String nWeek;
    public static int wk;
    public static int twoWeeksMode = 1;
    public static int count = 0;
    public static String id;
    public static int startNotifications = 1;

    public static BigInteger hex2decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        BigInteger val = BigInteger.valueOf(0);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            BigInteger d = BigInteger.valueOf(digits.indexOf(c));
            val = val.multiply(BigInteger.valueOf(16)).add(d);
        }
        return val;
    }

    public static String dec2any(BigInteger num) {
        String digits = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String s = "";
        do{
            s = digits.charAt(Integer.parseInt(num.remainder(BigInteger.valueOf(62)).toString())) + s;
            num = num.divide(BigInteger.valueOf(62));
        } while (!num.equals(BigInteger.valueOf(0)));
        return s;
    }
}
