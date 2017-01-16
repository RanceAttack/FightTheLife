package cn.somehui.fightthelife;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

/**
 * Created by somehui on 17/1/10.
 */

public class TestService extends Service {

    private static View mView;
    private TextClock mTextView;
    private WindowManager mWindowManager;
    @Override
    public void onCreate() {
        super.onCreate();
        bindNotifycation();
        initView();
    }

    protected void initView(){
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if(mView==null) {
            mView = LayoutInflater.from(this).inflate(R.layout.view_test, null);
            WindowManager.LayoutParams mParams
                    = new WindowManager.LayoutParams();
            mParams.width = 300;
            mParams.height = 150;
            mParams.gravity = Gravity.TOP|Gravity.RIGHT;
            mParams.flags =
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;// |

            mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
            mParams.format = PixelFormat.RGBA_8888;
            mView.setLayoutParams(mParams);
            mWindowManager.addView(mView,mParams);
        }
        mTextView = (TextClock)mView.findViewById(R.id.timer);
        mTextView.setFormat12Hour("HH:mm:ss");
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TestService.this,"sdf",Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void bindNotifycation(){
        Notification.Builder builder = new Notification.Builder(ResProvider.application());
        Intent notificationIntent = new Intent(this, TestActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setSmallIcon(R.mipmap.ic_launcher).setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.ic_launcher));//FIXME

        builder.setContentTitle(ResProvider.string(R.string.app_name));//FIXME
        builder.setContentText(ResProvider.string(R.string.app_name));//FIXME
        builder.setContentIntent(pendingIntent);
        Notification notify;
        if(Build.VERSION.SDK_INT>=16){
            notify = builder.build();
        }else{
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
