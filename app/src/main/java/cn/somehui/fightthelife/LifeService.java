package cn.somehui.fightthelife;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by somehui on 17/1/10.
 */

public class LifeService extends Service {

    private static View mView;
    private FightClock mTextView;
    private WindowManager mWindowManager;

    private long lastTime = -7000000;

    @Override
    public void onCreate() {
        super.onCreate();
        bindNotifycation();
        initView();
    }

    private void stateRecever(boolean isStartRecever) {
        if (isStartRecever) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            registerReceiver(mLockscreenReceiver, filter);
        } else {
            if (null != mLockscreenReceiver) {
                unregisterReceiver(mLockscreenReceiver);
            }
        }
    }

    public boolean isStandardKeyguardState() {
        boolean isStandardKeyguqrd = false;
        KeyguardManager keyManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (null != keyManager) {
            if (android.os.Build.VERSION.SDK_INT > 15) {
                isStandardKeyguqrd = keyManager.isKeyguardSecure();
            } else {
                try {
                    Class<?> clazz = Class.forName("com.android.internal.widget.LockPatternUtils");
                    Constructor<?> constructor = clazz.getConstructor(Context.class);
                    constructor.setAccessible(true);
                    Object utils = constructor.newInstance(this);
                    Method method = clazz.getMethod("isSecure");
                    isStandardKeyguqrd = (Boolean) method.invoke(utils);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return isStandardKeyguqrd;
    }

    private BroadcastReceiver mLockscreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != context) {
                boolean isLockEnable = false;
                if (!isStandardKeyguardState()) {
                    isLockEnable = false;
                } else {
                    isLockEnable = true;
                }

                if ((intent.getAction().equals(Intent.ACTION_SCREEN_OFF) && !isLockEnable)
                        || intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                    startLockscreenActivity();
                }
            }
        }
    };

    private void startLockscreenActivity() {
        if (lastTime < -700) {
            lastTime = SystemClock.elapsedRealtime();
        } else if (lastTime + 1000 > SystemClock.elapsedRealtime()) {
            return;
        } else {
            lastTime = SystemClock.elapsedRealtime();
        }
        Intent intent = new Intent(this, LifeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stateRecever(true);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stateRecever(false);
        super.onDestroy();
    }

    protected void initView() {
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (mView == null) {
            int statusBarHeight = (int)(24* getApplicationContext().getResources().getDisplayMetrics().density);
            if (statusBarHeight <= 0) {
                statusBarHeight = WindowManager.LayoutParams.WRAP_CONTENT;
            }
            mView = LayoutInflater.from(this).inflate(R.layout.view_test, null);
            WindowManager.LayoutParams mParams
                    = new WindowManager.LayoutParams();
            mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            mParams.height = statusBarHeight;
            mParams.gravity = Gravity.TOP | Gravity.RIGHT;
            mParams.flags =
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;// |

            mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
            mParams.format = PixelFormat.RGBA_8888;
            mView.setLayoutParams(mParams);
            mWindowManager.addView(mView, mParams);
        }
        mTextView = (FightClock) mView.findViewById(R.id.timer);
        mTextView.setFormat12Hour("HH:mm:ss");
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LifeService.this, "sdf", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void bindNotifycation() {
        Notification.Builder builder = new Notification.Builder(ResProvider.application());
        Intent notificationIntent = new Intent(this, LifeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setSmallIcon(R.mipmap.ic_launcher).setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.ic_launcher));//FIXME

        builder.setContentTitle(ResProvider.string(R.string.app_name));//FIXME
        builder.setContentText(ResProvider.string(R.string.app_name));//FIXME
        builder.setContentIntent(pendingIntent);
        Notification notify;
        if (Build.VERSION.SDK_INT >= 16) {
            notify = builder.build();
        } else {
            notify = builder.getNotification();
        }
        startForeground(338527, notify);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
