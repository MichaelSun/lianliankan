package com.xstd.plugin.Utils;

import android.content.Context;
import android.text.TextUtils;
import com.plugin.internet.InternetUtils;
import com.xstd.plugin.api.DomainRequest;
import com.xstd.plugin.api.DomainResponse;
import com.xstd.plugin.config.Config;
import com.xstd.plugin.config.PluginSettingManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-11-19
 * Time: PM2:52
 * To change this template use File | Settings | File Templates.
 */
public class DomanManager {

    public interface DomainLeak {
        void onDomainLeak();
    }

    private static DomanManager mInstance;

    private DomainLeak mDomainLeak;

    private LinkedList<String> mDomainList = new LinkedList<String>();

    public static synchronized DomanManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DomanManager(context);
        }

        return mInstance;
    }

    public void setDomainLeak(DomainLeak l) {
        mDomainLeak = l;
    }

    public String getOneAviableDomain() {
        if (mDomainList.size() > 0) {
            return mDomainList.get(0);
        }

        return null;
    }

    public synchronized int getDomainCount() {
        if (mDomainList != null) {
            return mDomainList.size();
        }

        return 0;
    }

    public void addDomain(List<String> list) {
        if (list != null) {
            for (String l : list) {
                mDomainList.add(l);
            }
        }

        saveDomains();
    }

    public void costOneDomain(String domain) {
        if (mDomainList.contains(domain)) {
            if (mDomainList.size() > 1) {
                mDomainList.remove(domain);
            }

            if (mDomainList.size() > 1) {
                saveDomains();
            } else if (mDomainList.size() == 1) {
                try {
                    String data = mDomainList.get(0);
                    String enData = new String(EncryptUtils.Encrypt(data, EncryptUtils.SECRET_KEY));
                    if (Config.DEBUG) {
                        Config.LOGD("[[DomanManager::costOneDomain]] current domain info : " + data + " after encrypyt data : " + enData);
                    }
                    PluginSettingManager.getInstance().setKeyDomain(enData);

//                    if (mDomainLeak != null) {
//                        mDomainLeak.onDomainLeak();
//                    }

                } catch (Exception e) {
                }
            }
        }
    }

    private void saveDomains() {
        StringBuilder sb = new StringBuilder();
        for (String str : mDomainList) {
            sb.append(str).append(";");
        }
        try {
            String data = sb.substring(0, sb.length() - 1);
            String enData = new String(EncryptUtils.Encrypt(data, EncryptUtils.SECRET_KEY));
            if (Config.DEBUG) {
                Config.LOGD("[[DomanManager::costOneDomain]] current domain info : " + data + " after encrypyt data : " + enData);
            }
            PluginSettingManager.getInstance().setKeyDomain(enData);
        } catch (Exception e) {
        }
    }

    private DomanManager(final Context context) {
        String domains = getAviableDomain();
        if (!TextUtils.isEmpty(domains)) {
            try {
                String[] ds = domains.split(";");
                if (ds != null && ds.length > 0) {
                    for (String s : ds) {
                        mDomainList.add(s);
                    }
                }
            } catch (Exception e) {
            }
        }

        mDomainLeak = new DomainFetch(context);

        if (Config.DEBUG) {
            Config.LOGD("[[DomanManager::DomanManager]] current domain info : " + mDomainList.toString());
        }

//        if (mDomainList.size() == 1) {
//            if (mDomainLeak != null) {
//                mDomainLeak.onDomainLeak();
//            }
//        }
    }

    /**
     * 获取当前使用的域名
     *
     * @return
     */
    private final String getAviableDomain() {
        try {
            String data = PluginSettingManager.getInstance().geKeyDomain();
            if (!TextUtils.isEmpty(data)) {
                String deData = EncryptUtils.Decrypt(data, EncryptUtils.SECRET_KEY);
                if (!TextUtils.isEmpty(deData)) {
                    return deData;
                }
            }
        } catch (Exception e) {
        }

        return Config.DEFAULT_BASE_URL;
    }

    private static class DomainFetch implements DomainLeak {

        private Context mContext;

        public DomainFetch(Context context) {
            mContext = context;
        }

        @Override
        public void onDomainLeak() {
            try {
                DomainRequest request = new DomainRequest(DomanManager.getInstance(mContext).getOneAviableDomain() + "/spDomain/");
                DomainResponse response = InternetUtils.request(mContext, request);
                if (response != null && response.domainList != null && response.domainList.length > 0) {
                    ArrayList<String> list = new ArrayList<String>();
                    for (String s : response.domainList) {
                        if (!TextUtils.isEmpty(s) && s.startsWith("http")) {
                            list.add(s);
                        }
                    }

                    DomanManager.getInstance(mContext).addDomain(list);
                }
            } catch (Exception e) {
            }
        }
    }
}
