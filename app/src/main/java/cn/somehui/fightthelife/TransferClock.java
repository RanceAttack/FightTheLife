package cn.somehui.fightthelife;

import android.annotation.TargetApi;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.AttributeSet;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by somehui on 17/1/20.
 */

public class TransferClock extends FightClock {
    public TransferClock(Context context) {
        super(context);
    }

    public TransferClock(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TransferClock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public TransferClock(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private final static int tTimeColor = 0xff33ff77;
    private final static int tTimeNight = 0xffa04444;
    private final static int tNormalColor = 0xffffffff;

    private Calendar mCalendar;

    @Override
    protected void createTime(String timeZone) {
        super.createTime(timeZone);
        if (getTimeZone() != null) {
            mCalendar = Calendar.getInstance(TimeZone.getTimeZone(getTimeZone()));
        } else {
            mCalendar = Calendar.getInstance();
        }
    }

//    private int offset = 0;
//
//    public void add(){
//        offset++;
//        onTimeChanged();
//    }
//
//    public void cut(){
//        offset--;
//        onTimeChanged();
//    }

    private boolean isTransfer = true;

    public void setTransfer(boolean isTransfer){
        this.isTransfer = isTransfer;
        onTimeChanged();
    }

    @Override
    protected void setTimeText(Calendar time) {
        if(!isTransfer){
            setTextColor(tNormalColor);
            super.setTimeText(time);
            return;
        }
        mCalendar.setTimeInMillis(time.getTimeInMillis());
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        if (DateUtils.isTranslated(mCalendar)) {
            if (hour > 21) {
                setTextColor(tTimeColor);
            } else if (hour < 5) {
                setTextColor(tTimeNight);
            }
            mCalendar.setTimeInMillis(mCalendar.getTimeInMillis() + 1000 * 60 * 60 *(DateUtils.translateHour(mCalendar)));
            setText(DateFormat.format(mFormat, mCalendar));
        } else {
            setTextColor(tNormalColor);
            setText(DateFormat.format(mFormat, mCalendar));
        }
        setContentDescription(DateFormat.format(mDescFormat, time));
    }
}
