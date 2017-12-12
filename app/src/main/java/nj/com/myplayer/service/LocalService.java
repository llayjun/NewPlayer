package nj.com.myplayer.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import nj.com.myplayer.IMyAidlInterface;

/**
 * Created by Administrator on 2017/12/12 0012.
 */

public class LocalService extends Service {

    private MyBinder mMyBinder;

    private MyServiceConnection mMyServiceConnection;

//    private TimeBroadcastReceiver mBroadcastReceiver = new TimeBroadcastReceiver();

    @Override
    public void onCreate() {
        super.onCreate();
        if (null == mMyBinder) {
            mMyBinder = new MyBinder();
        }
        mMyServiceConnection = new MyServiceConnection();
//        registBroadcast();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent _intent = new Intent(this, RemoteService.class);
        this.bindService(_intent, mMyServiceConnection, Context.BIND_IMPORTANT);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent _intent) {
        return mMyBinder;
    }

    class MyBinder extends IMyAidlInterface.Stub {

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public String getProName() throws RemoteException {
            return "LocalService Millet";
        }
    }

    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName _componentName, IBinder _iBinder) {

        }

        @Override
        public void onServiceDisconnected(ComponentName _componentName) {
            // 启动RemoteService
            Intent _intent = new Intent(LocalService.this, RemoteService.class);
            startService(_intent);
            Intent _intent1 = new Intent(LocalService.this, RemoteService.class);
            bindService(_intent1, mMyServiceConnection, Context.BIND_IMPORTANT);
        }
    }

    @Override
    public void onDestroy() {
//        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

//    public void registBroadcast() {
//        IntentFilter filter = new IntentFilter();
//        filter.setPriority(1000);
//        filter.addAction(Intent.ACTION_TIME_TICK);
//        registerReceiver(mBroadcastReceiver, filter);
//    }

}
