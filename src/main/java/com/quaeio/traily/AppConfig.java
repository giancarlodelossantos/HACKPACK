package com.quaeio.traily;

/**
 * Created by simeon.garcia on 11/25/2017.
 */

public class AppConfig {
    // Server user login url
    public static String URL_LOGIN = "http://192.168.0.3:8080/traily/api/loginemployee.php";
    public static String URL_CHECKTRANSACTION = "http://192.168.0.3:8080/traily/api/checktransaction.php?documentno=";
    public static String URL_UPDATESTATUS = "http://192.168.0.3:8080/traily/api/updatedocumentqueue.php";
    public static String URL_GETRECIEVEDDOCS = "http://192.168.0.3:8080/traily/api/getreceivedlistbyemployeeno.php?employeeno=";
    public static String URL_GETAPPROVEDDOCS = "http://192.168.0.3:8080/traily/api/getapprovedlistbyemployeeno.php?employeeno=";
}
