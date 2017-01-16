package cn.somehui.baselibrary.permission;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by somehui on 17/1/11.
 */

public class PermissionManager {
    private final HashSet<String> deniedPermission;
    private List<SelfPermissionCallback> mPermissionCallbackList = new ArrayList<>();
    private Activity mActivity;
    private final int PERMISSION;

    public int getRequestCode() {
        return PERMISSION;
    }

    public PermissionManager(Activity activity, int requestCode) {
        this.mActivity = activity;
        PERMISSION = requestCode;
        deniedPermission = new HashSet<>();
    }

    public void addDeniedPermission(String permission) {
        deniedPermission.add(permission);
    }

    public void getPermission(String permission) {
        deniedPermission.remove(permission);
    }

    @TargetApi(23)
    public void checkPermission(SelfPermissionCallback callback) {
        if (callback == null) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            callback.run();
            return;
        }
        int permissionState = mActivity.checkSelfPermission(callback.getPermission());
        if (permissionState != PackageManager.PERMISSION_GRANTED) {
            if (Manifest.permission.SYSTEM_ALERT_WINDOW.equals(callback.getPermission())) {
                if (!Settings.canDrawOverlays(mActivity)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + mActivity.getPackageName() ));
                    mActivity.startActivityForResult(intent, PERMISSION);
                }
            } else {
                if (deniedPermission.contains(callback.getPermission()) && !mActivity.shouldShowRequestPermissionRationale(callback.getPermission())) {
                    callback.bust();
                    return;
                }
                mPermissionCallbackList.add(callback);
                mActivity.requestPermissions(new String[]{callback.getPermission()},
                        PERMISSION);
            }

        } else {
            callback.run();
        }
    }

    public void handle(String[] permissions, int[] grantResults) {
        if (grantResults != null && grantResults.length > 0) {
            HashMap<String, Integer> map = new HashMap<>();
            {
                int i = 0;
                for (String permission : permissions) {
                    map.put(permission, grantResults[i]);
                    i++;
                }
            }
            handle(map);
        }
    }

    public void handle(@NonNull HashMap<String, Integer> hash) {
        List<SelfPermissionCallback> toDel = new ArrayList<>();
        for (SelfPermissionCallback per : mPermissionCallbackList) {
            int state = hash.get(per.getPermission());
            if (state != PackageManager.PERMISSION_GRANTED) {
                per.deny();
                addDeniedPermission(per.getPermission());
            } else {
                per.allow();
                getPermission(per.getPermission());
            }
            if (per.done()) {
                toDel.add(per);
            }
        }
        mPermissionCallbackList.removeAll(toDel);

    }
}
