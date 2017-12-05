package nj.com.myplayer.listener;

/**
 * Created by llay on 2017/12/5.
 */

public interface BatteryStateListener {

    public void onStateChanged();

    public void onStateLow();

    public void onStateOkay();

    public void onStatePowerConnected();

    public void onStatePowerDisconnected();
}
