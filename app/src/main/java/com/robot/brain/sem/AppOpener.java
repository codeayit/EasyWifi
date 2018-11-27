package com.robot.brain.sem;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lny on 2018/1/12.
 */

public class AppOpener {

    public static String openExternalActivity(Context context,AppAction appAction){
        String packageName   = appAction.getPackage();
        String action = appAction.getAction();
        if (isAvailable(context,packageName)){
            Intent intent = new Intent(action);
            intent.putExtra("json",appAction.toJSONString());
//            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                context.getApplicationContext().startActivity(intent);
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return "该应用有新版本，请下载安装";
            }
        }else{
            return "该应用未安装";
        }
    }


    public static String openOtherApp(Context context,String packageName){
        if (isAvailable(context,packageName)){
            PackageManager packageManager = context.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            context.startActivity(intent);
            return null;
        }else{
            return "该应用未安装";
        }
    }




    /**
     * 检测是否安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAvailable(Context context, String packageName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            if (packageInfo == null) {
                return false;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }
}
