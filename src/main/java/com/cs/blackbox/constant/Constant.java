package com.cs.blackbox.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Constant {


    public static HashMap<String, Thread> threadMap = new HashMap<String, Thread>();

    public static HashMap userTokenHashMap = new HashMap() {
        {
            put("token", "this is token");
        }
    };

    public static HashMap userHeaderToken = new HashMap() {
        {
            put("User-Agent", "xxxx");
        }
    };

    //cookie 使用全局替换就行
    public static String cookie = "xxx=i am cookie;";


}
