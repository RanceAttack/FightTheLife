package cn.somehui.fightthelife;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;

import android.view.Window;
import android.view.WindowManager;

public class LifeActivity extends MyBaseActivity {
    private View mStartBtn;
    private BatteryView mBatteryView;

    private void checkStatusBar(){
        Rect rectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        int contentViewTop =
                window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight= contentViewTop - statusBarHeight;

        SharedPreferences sb = ResProvider.sharePreferences("statusbar");
        sb.edit().putInt("height",titleBarHeight).apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mBatteryView = $View(R.id.batteryview);
        mStartBtn = $View(R.id.start_clock);
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(THIS,LifeService.class);
                startService(intent);
            }
        });
    }

    @Override
    public void onAttachedToWindow() {
        Window window = getWindow();
        new WindowManager.LayoutParams();
        window.addFlags(
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onAttachedToWindow();
    }

    private BroadcastReceiver mBroadcastReceiver;

    private void registChargeReceiver(){
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                    int status=intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
                    boolean isCharging = status==BatteryManager.BATTERY_STATUS_CHARGING;
                    int level = intent.getIntExtra("level", 0);
                    int total = intent.getIntExtra("scale", 100);
                    updateBattery(level, total,isCharging);
                }
            }
        };
        //创建一个过滤器
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void unregistChargeReceiver(){
        unregisterReceiver(mBroadcastReceiver);
        mBroadcastReceiver = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registChargeReceiver();
    }

    @Override
    protected void onPause() {
        unregistChargeReceiver();
        super.onPause();
    }

    private void updateBattery(int level, int total,boolean isCharging) {
        mBatteryView.setProgress((float) level / (float) total);
        mBatteryView.setCharging(isCharging);
    }
}
