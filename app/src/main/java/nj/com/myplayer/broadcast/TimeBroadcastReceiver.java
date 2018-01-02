package nj.com.myplayer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

import com.millet.androidlib.Utils.LogUtils;

import java.io.File;
import java.util.Calendar;

import nj.com.myplayer.common.Constant;
import nj.com.myplayer.common.FileAnalyzeUtil;
import nj.com.myplayer.common.FileHandleUtil;
import nj.com.myplayer.utils.SPPlayerHelper;
import nj.com.myplayer.utils.SPRollHelper;


/**
 * Created by Administrator on 2017/12/12 0012.
 */

public class TimeBroadcastReceiver extends BroadcastReceiver {

    private File mFile = new File(Environment.getExternalStorageDirectory(), Constant.FILE_PATH);

    @Override
    public void onReceive(Context _context, Intent _intent) {
        try {
            // 调用系统广播，每一分钟回接收一次
            if (_intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                oneMinuteReadRollFile(_context);
                zeroReadFile(_context);
            }
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    /**
     * 读取播放文件，0点读入，并且删除无用文件
     *
     * @param _context
     */
    public void zeroReadFile(Context _context) {
        try {
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            if (hour == 0) {
                int minute = c.get(Calendar.MINUTE);
                if (minute == 0) {
                    if (mFile.exists()) {
                        SPRollHelper.getInstance().clear();
                        SPPlayerHelper.getInstance().clear();
                        FileAnalyzeUtil.savePlayInfo2Shared(mFile.getPath());
                        FileHandleUtil.deleteLossFile(_context, mFile.getPath());
                        Toast.makeText(_context, "零点写入成功", Toast.LENGTH_LONG).show();
                    }
                }
            }
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    /**
     * 读取弹幕文件，每分钟读取一次
     *
     * @param _context
     */
    public void oneMinuteReadRollFile(Context _context) {
        try {
            FileAnalyzeUtil.saveRollTextInfo2Shared(_context, mFile.getPath());
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

}
