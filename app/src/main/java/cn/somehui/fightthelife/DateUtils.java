package cn.somehui.fightthelife;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by somehui on 17/1/20.
 */

public class DateUtils {

    public static boolean isTranslated(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return (hour >= 22 || hour <= 4);
    }

    public static int translateHour(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour == 22) {
            return 1;
        } else if (hour == 23) {
            return 1;
        } else if (hour == 0) {
            return 2;
        } else if (hour == 1) {
            return 2;
        } else if (hour == 2) {
            return 4;
        } else if (hour == 3) {
            return 1;
        } else if (hour == 4) {
            return 1;
        }else {
            return 0;
        }
    }
}
