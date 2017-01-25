package cn.somehui.fightthelife;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.somehui.baselibrary.clicklistener.OnIntentClickListener;
import cn.somehui.baselibrary.permission.SelfPermissionCallback;

public class LifeActivity extends MyBaseActivity {
    private View mStartBtn;
    private BatteryView mBatteryView;
    private ViewGroup mBgViewGroup;
    private TransferClock mClockView;
    private View mReaderBtn;
    private View mCenterBtn;
    private RecyclerView mRecyclerView;

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

    private class MyHolder extends RecyclerView.ViewHolder{
        public MyHolder(View itemView) {
            super(itemView);
        }

        private TextView mText;

        public TextView getText() {
            return mText;
        }

        public void setText(TextView text) {
            mText = text;
        }
    }
    private class MyAdapter extends RecyclerView.Adapter<MyHolder>{
        List<ResolveInfo> pkgAppsList = new ArrayList<>();
        public MyAdapter() {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            pkgAppsList = getPackageManager().queryIntentActivities( mainIntent, 0);
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(THIS).inflate(R.layout.item_text,parent,false);
            TextView textView = (TextView)view.findViewById(R.id.sssseeee);
            MyHolder holder = new MyHolder(view);
            holder.setText(textView);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyHolder holder, int position) {
            if(position<0){
                return;
            }
            TextView tv = holder.getText();
            ResolveInfo info = pkgAppsList.get(position);
            tv.setText(info.activityInfo==null?"":info.activityInfo.packageName);
            tv.setOnClickListener(new OnIntentClickListener() {
                @Override
                public void onClickWithGlobalCD(View v) {
                    ResolveInfo info = pkgAppsList.get(holder.getAdapterPosition());
                    String packageName = info.activityInfo==null?"":info.activityInfo.packageName;
                    if(TextUtils.isEmpty(packageName)){
                        return;
                    }
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
                    startActivity( launchIntent );
                }
            });
        }

        @Override
        public int getItemCount() {
            return pkgAppsList.size();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_test);
        mCenterBtn = $View(R.id.missyou);
        mRecyclerView = $View(R.id.packagerecy);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mCenterBtn.setOnClickListener(new OnIntentClickListener() {
            @Override
            public void onClickWithGlobalCD(View v) {
                mRecyclerView.setAdapter(new MyAdapter());
            }
        });
        mBgViewGroup = $View(R.id.myground);
        mBgViewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mReaderBtn = $View(R.id.duokan);
        mReaderBtn.setOnClickListener(new OnIntentClickListener() {
            @Override
            public void onClickWithGlobalCD(View v) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.duokan.reader");
                startActivity( launchIntent );

            }
        });
        mBatteryView = $View(R.id.batteryview);
        mClockView = $View(R.id.clock_screen);
        mStartBtn = $View(R.id.start_clock);
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndRun(new SelfPermissionCallback(Manifest.permission.SYSTEM_ALERT_WINDOW) {
                    @Override
                    protected void run() {
                        Intent intent = new Intent(THIS,LifeService.class);
                        startService(intent);
                    }

                    @Override
                    protected void bust() {

                    }
                });

            }
        });
    }

    @Override
    public void finish() {
        overridePendingTransition(0,0);
        super.finish();
        overridePendingTransition(0,0);
    }

    @Override
    public void onAttachedToWindow() {
        Window window = getWindow();
        new WindowManager.LayoutParams();
        window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

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
