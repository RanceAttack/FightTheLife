package cn.somehui.fightthelife;

import android.Manifest;
import android.content.Intent;
import android.icu.util.TimeZone;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DigitalClock;
import android.widget.TextClock;

import cn.somehui.baselibrary.permission.SelfPermissionCallback;
import cn.somehui.publiclibrary.BaseActivity;

public class TestActivity extends MyBaseActivity {
    private Button mStartBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mStartBtn = $View(R.id.test_start);
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndRun(new SelfPermissionCallback(Manifest.permission.SYSTEM_ALERT_WINDOW) {
                    @Override
                    protected void run() {
                        Intent intent = new Intent(THIS,TestService.class);
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
    public void onAttachedToWindow() {
//        Window window = getWindow();
//        new WindowManager.LayoutParams();
//        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//                | WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onAttachedToWindow();
    }
}
