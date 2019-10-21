package com.mei.tododemo.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * created by meishenbo
 * 2018/12/13
 */
public class Tools {

    public static  String currentDate(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }






}
