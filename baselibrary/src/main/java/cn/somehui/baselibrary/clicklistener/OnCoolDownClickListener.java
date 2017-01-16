package cn.somehui.baselibrary.clicklistener;

import android.view.View;

/**
 * Created by somehui on 15/9/6.
 */
public abstract class OnCoolDownClickListener implements View.OnClickListener{
    public abstract void onClickWithCD(View v);

    private final int DURATION_MILLS;

    private long lastClickTime = Long.MIN_VALUE;

    public OnCoolDownClickListener() {
        this.DURATION_MILLS = 500;
    }

    public OnCoolDownClickListener(int DURATION_MILLS) {
        this.DURATION_MILLS = DURATION_MILLS;
    }

    @Override
    public void onClick(View v) {
        long currentTime = currentTime();
        if(currentTime-lastClickTime>DURATION_MILLS){
            onClickWithCD(v);
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
