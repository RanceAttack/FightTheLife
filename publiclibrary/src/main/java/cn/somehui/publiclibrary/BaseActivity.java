package cn.somehui.publiclibrary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import cn.somehui.baselibrary.permission.SelfPermissionCallback;
import cn.somehui.baselibrary.permission.PermissionManager;
import cn.somehui.publiclibrary.RequestCodeBook;

/**
 * Created by somehui on 17/1/11.
 */

public abstract class BaseActivity extends AppCompatActivity {
    public static final int PERMISSION = 999;
    public final BaseActivity THIS = this;
    public Handler mHandler = new Handler();

/** <Permission> */
    private PermissionManager mPermissionManager;

    private PermissionManager getPermissionManager(){
        if(null == mPermissionManager){
            mPermissionManager = new PermissionManager(this, RequestCodeBook.PERMISSION);
        }
        return mPermissionManager;
    }

    private boolean isPermissionRequest(int code){
        if(mPermissionManager == null){
            return false;
        }else{
            return code == mPermissionManager.getRequestCode();
        }
    }

    public void checkPermissionAndRun(SelfPermissionCallback callback){
        getPermissionManager().checkPermission(callback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(isPermissionRequest(requestCode)){
            mPermissionManager.handle(permissions,grantResults);
        }else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
/* </Permission>*/

/** <FindView */
    @SuppressWarnings("unchecked")
    protected <T extends View> T $View(@IdRes int resId) {
        return (T) super.findViewById(resId);
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T $View(@NonNull View parent, @IdRes int resId) {
        return (T) parent.findViewById(resId);
    }

    protected View $inflate(@LayoutRes int id) {
        return LayoutInflater.from(this).inflate(id, null);
    }

    protected View $inflate(@LayoutRes int id, ViewGroup parent) {
        return LayoutInflater.from(this).inflate(id, parent, false);
    }
/* /FindView> */

/** <Post */
    public void post(Runnable runnable) {
    mHandler.post(runnable);
}

    public void postDelay(Runnable runnable, int millsecond) {
        mHandler.postDelayed(runnable, millsecond);
    }
/* /Post> */
    public boolean isIntentAvailable(Intent intent) {
        return getPackageManager().resolveActivity(intent, 0) != null;
    }

    protected abstract Toast getToast();

    public final void toast(final @NonNull String string) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                getToast().setText(string);
                getToast().setDuration(Toast.LENGTH_LONG);
                getToast().show();
            }
        });
    }

}
