package cn.somehui.baselibrary.clicklistener;

import android.view.View;

/**
 * Created by somehui on 15/9/6.
 * share click cooldown global
 */
public abstract class OnIntentClickListener implements View.OnClickListener{
    public abstract void onClickWithGlobalCD(View v);

    private static final int DURATION_MILLS = 500;

    private static long lastClickTime = Long.MIN_VALUE;

    public OnIntentClickListener() {
    }

    @Override
    public void onClick(View v) {
        long currentTime = currentTime();
        if(currentTime-lastClickTime>DURATION_MILLS){
            onClickWithGlobalCD(v);
            lastClickTime = currentTime;
        }

    }
    private long currentTime(){
        long nowTime = System.currentTimeMillis();
        if(lastClickTime>nowTime){//if user changing time at runtime
            lastClickTime = Long.MIN_VALUE;
        }
        return nowTime;
    }
}
