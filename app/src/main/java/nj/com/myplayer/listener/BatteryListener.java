package nj.com.myplayer.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by llay on 2017/12/5.
 */

public class BatteryListener {

    private Context mContext;

    private PowerConnectionReceiver receiver;

    private BatteryStateListener mBatteryStateListener;

    public BatteryListener(Context context) {
        mContext = context;
        receiver = new PowerConnectionReceiver();
    }

    public void register(BatteryStateListener listener) {
        mBatteryStateListener = listener;
        if (receiver != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_BATTERY_CHANGED);//电量改变
            filter.addAction(Intent.ACTION_BATTERY_LOW);//电量低
            filter.addAction(Intent.ACTION_BATTERY_OKAY);//电已经充好
            filter.addAction(Intent.ACTION_POWER_CONNECTED);//连接电源
            filter.addAction(Intent.ACTION_POWER_DISCONNECTED);//断开电源
            mContext.registerReceiver(receiver, filter);
        }
    }

    public void unregister() {
        if (receiver != null) {
            mContext.unregisterReceiver(receiver);
        }
    }

    public class PowerConnectionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context _context, Intent _intent) {
            if (_intent != null) {
                String _action = _intent.getAction();
                switch (_action) {
                    case Intent.ACTION_BATTERY_CHANGED://电量发生改变
                        if (mBatteryStateListener != null) {
                            mBatteryStateListener.onStateChanged();
                        }
                        break;
                    case Intent.ACTION_BATTERY_LOW://电量低
                        if (mBatteryStateListener != null) {
                            mBatteryStateListener.onStateLow();
                        }
                        break;
                    case Intent.ACTION_BATTERY_OKAY://电量充满
                        if (mBatteryStateListener != null) {
                            mBatteryStateListener.onStateOkay();
                        }
                        break;
                    case Intent.ACTION_POWER_CONNECTED://接通电源
                        if (mBatteryStateListener != null) {
                            mBatteryStateListener.onStatePowerConnected();
                        }
                        break;
                    case Intent.ACTION_POWER_DISCONNECTED://拔出电源
                        if (mBatteryStateListener != null) {
                            mBatteryStateListener.onStatePowerDisconnected();
                        }
                        break;
                }
            }
        }

    }

}
