package nj.com.myplayer;

import com.millet.androidlib.Base.BaseApplication;
import com.millet.androidlib.Utils.SharedPreferencesHelper;

import nj.com.myplayer.common.Constant;

/**
 * Created by Administrator on 2017/12/11 0011.
 */

public class MyApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesHelper.getInstance(Constant.PLAYER_NAME, getInstance());
        SharedPreferencesHelper.getInstance(Constant.TEXT_NAME, getInstance());
    }

}
