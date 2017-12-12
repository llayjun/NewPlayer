package nj.com.myplayer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2017/12/12 0012.
 */

public class TimeBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context _context, Intent _intent) {
        // 调用系统广播，每一分钟回接收一次，如果service停止，在这里重启service
        if (_intent.getAction().equals(Intent.ACTION_TIME_TICK)) {

        }
    }

}
