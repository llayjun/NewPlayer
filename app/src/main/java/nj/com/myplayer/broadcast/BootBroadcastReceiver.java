package nj.com.myplayer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;

import nj.com.myplayer.MainActivity;
import nj.com.myplayer.common.FileAnalyzeUtil;
import nj.com.myplayer.utils.SPPlayerHelper;
import nj.com.myplayer.utils.SPRollHelper;

/**
 * Created by Administrator on 2017/12/12 0012.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_BOOT = Intent.ACTION_BOOT_COMPLETED;
    public static final String ACTION_DATE = Intent.ACTION_DATE_CHANGED;

    private File mFile = new File(Environment.getExternalStorageDirectory(), "millet");

    @Override
    public void onReceive(Context _context, Intent _intent) {
        if (null == _intent) return;
        String _action = _intent.getAction();
        if (_action.equals(ACTION_DATE)) {
            if (mFile.exists()) {
                SPPlayerHelper.getInstance().clear();
                SPRollHelper.getInstance().clear();
                FileAnalyzeUtil.savePlayInfo2Shared(mFile.getPath());
                FileAnalyzeUtil.saveRollTextInfo2Shared(mFile.getPath());
            }
        } else if (_action.equals(ACTION_BOOT)) {
            Intent mainActivityIntent = new Intent(_context, MainActivity.class);  // 要启动的Activity
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(mainActivityIntent);
            Toast.makeText(_context, "应用开机自启动", Toast.LENGTH_LONG).show();
        }
    }

}
