package com.robot.brain.util;

import java.io.FileInputStream;
import java.io.IOException;

public class FileUtil extends com.robot.baseapi.util.FileUtil {

    public static String readFile(String path){
        return readFile(path,1024);
    }

    public static String readFile(String path,int bufferSize){
        StringBuilder sb =  new StringBuilder();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            byte[] buffer = new byte[bufferSize];

            int len = 0;
            while ((len = fis.read(buffer))!=-1){
                sb.append(new String(buffer,0,len));
            }

        } catch (IOException e) {
            e.printStackTrace();
//            MyLog.JUN_KANG.d(" ***ERROR*** read file: " + e.getMessage());
        } finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    fis = null;
                }
            }
        }
        return sb.toString();
    }
}

