package cn.somehui.fightthelife;

import android.annotation.TargetApi;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.AttributeSet;

import java.util.Calendar;

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

    @Override
    protected void setTimeText(Calendar time) {
        int hour = time.get(Calendar.HOUR_OF_DAY);
        if (DateUtils.isTranslated(time)) {
            if (hour > 21) {
                setTextColor(tTimeColor);
            } else if (hour < 5) {
                setTextColor(tTimeNight);
            }
            time.setTimeInMillis(time.getTimeInMillis() + 1000 * 60 * 60 * DateUtils.translateHour(time));
        } else {
            setTextColor(tNormalColor);
            setText(DateFormat.format(mFormat, time));
        }
        setContentDescription(DateFormat.format(mDescFormat, time));
    }
}
