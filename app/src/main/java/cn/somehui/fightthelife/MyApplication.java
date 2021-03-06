package cn.somehui.fightthelife;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import cn.somehui.baselibrary.PixelUtils;

/**
 * Created by somehui on 17/1/11.
 */

public class MyApplication extends MultiDexApplication {
    public static MyApplication THIS;
    public static Toast mToast;

    @Override
    public void onCreate() {
        THIS = this;
        super.onCreate();
        PixelUtils.init(this);
    }

    @SuppressLint("ShowToast")
    public static Toast getToast(){
        if(null == mToast){
            mToast = Toast.makeText(THIS,"",Toast.LENGTH_LONG);
        }
        return mToast;
    }

    public static boolean isDebugMode(){
        return BuildConfig.DEVELOPER_MODE;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
