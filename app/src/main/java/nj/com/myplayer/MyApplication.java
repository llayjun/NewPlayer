package nj.com.myplayer;

import com.millet.androidlib.Base.BaseApplication;
import com.millet.androidlib.Utils.SharedPreferencesHelper;

/**
 * Created by Administrator on 2017/12/11 0011.
 */

public class MyApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesHelper.getInstance();
        SharedPreferencesHelper.getInstance();
    }

}
