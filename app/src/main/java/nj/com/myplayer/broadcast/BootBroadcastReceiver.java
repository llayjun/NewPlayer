package nj.com.myplayer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import nj.com.myplayer.MainActivity;

/**
 * Created by Administrator on 2017/12/12 0012.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_BOOT = Intent.ACTION_BOOT_COMPLETED;

    @Override
    public void onReceive(Context _context, Intent _intent) {
        if (null == _intent) return;
        String _action = _intent.getAction();
        if (_action.equals(ACTION_BOOT)) {
            Intent mainActivityIntent = new Intent(_context, MainActivity.class);  // 要启动的Activity
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(mainActivityIntent);
            Toast.makeText(_context, "应用开机自启动", Toast.LENGTH_LONG).show();
        }
    }

}
