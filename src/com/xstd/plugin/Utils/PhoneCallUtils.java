package com.xstd.plugin.Utils;

import android.content.Context;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.*;
import com.xstd.plugin.config.Config;

import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-11-3
 * Time: PM9:06
 * To change this template use File | Settings | File Templates.
 */
public class PhoneCallUtils {

    public static void encCall(Context context) {
        ITelephony iTelephony = getITelephony(context); //获取电话接口
        Config.LOGD("[[encCall]] ITelephony = " + iTelephony);
        if (iTelephony != null) {
            try {
                iTelephony.endCall(); // 挂断电话
            } catch (RemoteException e) {
                e.printStackTrace();
                Config.LOGD("EndCall Exception" , e);
            }
        }
    }

    private static ITelephony getITelephony(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Class<TelephonyManager> c = TelephonyManager.class;
        Method getITelephonyMethod = null;
        try {
            getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null); // 获取声明的方法
            getITelephonyMethod.setAccessible(true);
        } catch (SecurityException e) {
            e.printStackTrace();
            Config.LOGD("getITelephony Exception" , e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Config.LOGD("getITelephony Exception" , e);
        }

        try {
            ITelephony iTelephony = (ITelephony) getITelephonyMethod.invoke(mTelephonyManager, (Object[]) null); // 获取实例
            Config.LOGD("[[getITelephony]] return ITelephony = " + iTelephony);
            return iTelephony;
        } catch (Exception e) {
            e.printStackTrace();
            Config.LOGD("getITelephony Exception" , e);
        }
        return null;
    }

}
