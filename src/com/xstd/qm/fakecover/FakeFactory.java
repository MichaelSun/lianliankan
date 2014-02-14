package com.xstd.qm.fakecover;

import android.content.Context;
import com.xstd.qm.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-11-27
 * Time: PM1:35
 * To change this template use File | Settings | File Templates.
 */
public class FakeFactory {

    public static FakeWindowInterface fakeWindowFactory(Context context) {
        FakeWindowInterface ret = null;
        if (!Utils.isVersionBeyondGB()) {
            ret = new FakeInstallWindowForGB(context);
        } else {
            ret = new FakeWindowWithSingleArrow(context);
        }

        return ret;
    }

}
