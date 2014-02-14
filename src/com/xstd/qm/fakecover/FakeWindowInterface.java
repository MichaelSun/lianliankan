package com.xstd.qm.fakecover;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-11-27
 * Time: PM12:02
 * To change this template use File | Settings | File Templates.
 */
public interface FakeWindowInterface {

    void updateTimerCount();

    void dismiss();

    void show(boolean full);

    void setCountDown(int countDown);

}
