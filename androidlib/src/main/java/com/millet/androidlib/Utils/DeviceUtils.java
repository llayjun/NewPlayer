package com.millet.androidlib.Utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.millet.androidlib.Base.BaseApplication;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by Administrator on 2017/5/3 0003.
 */

public class DeviceUtils {

    // 无网络
    public static final int TYPE_NONE = 0X00;

    // 移动网络类型
    public static final int TYPE_MOBILE = 0X01;

    // Wi-Fi网络类型
    public static final int TYPE_WIFI = 0X02;

    /**
     * 输出机型版本等信息
     *
     * @param _outPutStream
     * @throws PackageManager.NameNotFoundException
     */
    public static void pushPhoneInfo(OutputStream _outPutStream) throws PackageManager.NameNotFoundException, IOException {
        //获取应用的版本名称和版本号
        PackageManager _packageManager = BaseApplication.getInstance().getPackageManager();
        PackageInfo _packageInfo = _packageManager.getPackageInfo(BaseApplication.getInstance().getPackageName(), PackageManager.GET_ACTIVITIES);
        _outPutStream.write("App Version:".getBytes());
        _outPutStream.write(_packageInfo.versionName.getBytes());
        _outPutStream.write('_');
        _outPutStream.write((_packageInfo.versionCode + "\n").getBytes());

        //android版本号
        _outPutStream.write("OS Version:".getBytes());
        _outPutStream.write(Build.VERSION.RELEASE.getBytes());
        _outPutStream.write('_');
        _outPutStream.write((Build.VERSION.SDK_INT + "\n").getBytes());

        //手机制造商
        _outPutStream.write("Vendor:".getBytes());
        _outPutStream.write((Build.MANUFACTURER + "\n").getBytes());

        //手机型号
        _outPutStream.write("Model:".getBytes());
        _outPutStream.write((Build.MODEL + "\n").getBytes());

        //手机cup架构
        _outPutStream.write("CPU API:".getBytes());
        _outPutStream.write((Build.CPU_ABI + "\n").getBytes());
    }

    /**
     * 获取网络类型
     *
     * @param context
     * @return
     */
    public static int getNetworkType(Context context) {
        ConnectivityManager connMng = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connMng.getAllNetworkInfo();
        if (networkInfo != null && networkInfo.length > 0) {
            for (int i = 0; i < networkInfo.length; i++) {
                // 判断当前网络状态是否为连接状态
                if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                    LogUtils.w("DeviceHelper_getNetworkType " + networkInfo[i].getState());
                    LogUtils.w("DeviceHelper_getNetworkType " + networkInfo[i].getTypeName());
                }
            }
        }
        NetworkInfo mobile = connMng.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (null != mobile && mobile.getState() == NetworkInfo.State.CONNECTED) {
            return TYPE_MOBILE;
        }
        NetworkInfo wifi = connMng.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifi && wifi.getState() == NetworkInfo.State.CONNECTED) {
            return TYPE_WIFI;
        }
        return TYPE_NONE;
    }

    /**
     * 获取本地IP
     *
     * @return
     */
    public static String getLocalIp() {
        try {
            Enumeration<NetworkInterface> networkInfo = NetworkInterface.getNetworkInterfaces();
            for (Enumeration<NetworkInterface> en = networkInfo; en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                Enumeration<InetAddress> intfAddress = intf.getInetAddresses();
                for (Enumeration<InetAddress> enumIpAddr = intfAddress; enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
            LogUtils.catchInfo(ex.toString());
        }
        return "";
    }

    /**
     * 获取序列号
     *
     * @param context
     * @return
     */
    public static String getSimCardNumber(Context context) {
        String simNumber = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSimSerialNumber();
        return null == simNumber ? "" : simNumber;
    }

    /**
     * 获取设备号
     *
     * @param context
     * @return
     */
    public static String getDeviceNumber(Context context) {
        String deviceId = ((TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        return null == deviceId ? "" : deviceId;
    }

    /**
     * 获取mac地址
     *
     * @param context
     * @return
     */
    public static String getMacAddress(Context context) {
        WifiManager mWifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifi = mWifiMgr.getConnectionInfo();
        boolean isEnable = mWifiMgr.isWifiEnabled();
        String mac = "";
        if (isEnable && null != wifi) {
            String bssid = wifi.getBSSID();
            if (null != bssid && !"".equals(bssid)) {
                mac = wifi.getMacAddress();
            }
        }
        return mac;
    }

    /**
     * 获取设备型号
     *
     * @return
     */
    public static String getModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 判断是否联网
     *
     * @param context
     * @return
     */
    public static boolean isNetConnect(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.catchInfo(e.toString());
        }
        return false;
    }

    /**
     * 获取应用版本名字
     *
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionName = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return versionName;
    }

    /**
     * 获取应用的版本号
     *
     * @param context
     * @return
     */
    public static String getAppVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionCode = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return versionCode;
    }

    /**
     * 获取应用名字
     *
     * @param context
     * @return
     */
    public static String getAppName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            return (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.catchInfo(e.toString());
        }
        return null;
    }

}
