package nj.com.myplayer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;

import nj.com.myplayer.common.FileAnalyzeUtil;
import nj.com.myplayer.common.FileHandleUtil;
import nj.com.myplayer.utils.SPPlayerHelper;
import nj.com.myplayer.utils.SPRollHelper;


/**
 * Created by Administrator on 2017/12/12 0012.
 */

public class TimeBroadcastReceiver extends BroadcastReceiver {

    private File mFile = new File(Environment.getExternalStorageDirectory(), "millet");

    @Override
    public void onReceive(Context _context, Intent _intent) {
        // 调用系统广播，每一分钟回接收一次，如果service停止，在这里重启service
        if (_intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            if (hour == 0) {
                int minute = c.get(Calendar.MINUTE);
                if (minute == 0) {
                    if (mFile.exists()) {
                        SPPlayerHelper.getInstance().clear();
                        FileAnalyzeUtil.savePlayInfo2Shared(mFile.getPath());
                        SPRollHelper.getInstance().clear();
                        FileAnalyzeUtil.saveRollTextInfo2Shared(mFile.getPath());
                        FileHandleUtil.deleteLossFile(mFile.getPath());
                        Toast.makeText(_context, "零点写入成功", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

}
