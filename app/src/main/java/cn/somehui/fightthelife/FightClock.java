package cn.somehui.fightthelife;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewDebug;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by somehui on 17/1/16.
 * a copy of TextClock
 */

public class FightClock extends TextView {
    /**
     * The default formatting pattern in 12-hour mode. This pattern is used
     * if {@link #setFormat12Hour(CharSequence)} is called with a null pattern
     * or if no pattern was specified when creating an instance of this class.
     *
     * This default pattern shows only the time, hours and minutes, and an am/pm
     * indicator.
     *
     * @see #setFormat12Hour(CharSequence)
     * @see #getFormat12Hour()
     *
     * @deprecated Let the system use locale-appropriate defaults instead.
     */
    public static final CharSequence DEFAULT_FORMAT_12_HOUR = "h:mm a";

    /**
     * The default formatting pattern in 24-hour mode. This pattern is used
     * if {@link #setFormat24Hour(CharSequence)} is called with a null pattern
     * or if no pattern was specified when creating an instance of this class.
     *
     * This default pattern shows only the time, hours and minutes.
     *
     * @see #setFormat24Hour(CharSequence)
     * @see #getFormat24Hour()
     *
     * @deprecated Let the system use locale-appropriate defaults instead.
     */
    public static final CharSequence DEFAULT_FORMAT_24_HOUR = "H:mm";

    private CharSequence mFormat12;
    private CharSequence mFormat24;
    private CharSequence mDescFormat12;
    private CharSequence mDescFormat24;

    @ViewDebug.ExportedProperty
    protected CharSequence mFormat;
    @ViewDebug.ExportedProperty
    private boolean mHasSeconds;

    protected CharSequence mDescFormat;

    private boolean mAttached;

    private Calendar mTime;
    private String mTimeZone;

    private boolean mShowCurrentUserTime;

    private ContentObserver mFormatChangeObserver;
    private class FormatChangeObserver extends ContentObserver {

        public FormatChangeObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            chooseFormat();
            onTimeChanged();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            chooseFormat();
            onTimeChanged();
        }
    };

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mTimeZone == null && Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
                final String timeZone = intent.getStringExtra("time-zone");
                createTime(timeZone);
            }
            onTimeChanged();
        }
    };

    private final Runnable mTicker = new Runnable() {
        public void run() {
            onTimeChanged();

            long now = SystemClock.uptimeMillis();
            long next = now + (1000 - now % 1000);

            getHandler().postAtTime(mTicker, next);
        }
    };

    /**
     * Creates a new clock using the default patterns for the current locale.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc.
     */
    @SuppressWarnings("UnusedDeclaration")
    public FightClock(Context context) {
        super(context);
        init();
    }

    /**
     * Creates a new clock inflated from XML. This object's properties are
     * intialized from the attributes specified in XML.
     *
     * This constructor uses a default style of 0, so the only attribute values
     * applied are those in the Context's Theme and the given AttributeSet.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view
     */
    @SuppressWarnings("UnusedDeclaration")
    public FightClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Creates a new clock inflated from XML. This object's properties are
     * intialized from the attributes specified in XML.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view
     * @param defStyleAttr An attribute in the current theme that contains a
     *        reference to a style resource that supplies default values for
     *        the view. Can be 0 to not look for defaults.
     */
    public FightClock(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public FightClock(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

//        final TypedArray a = context.obtainStyledAttributes(
//                attrs, R.styleable.FightClock, defStyleAttr, defStyleRes);
//        try {
//            mFormat12 = a.getText(R.styleable.FightClock_format12Hour);
//            mFormat24 = a.getText(R.styleable.FightClock_format24Hour);
//            mTimeZone = a.getString(R.styleable.FightClock_timeZone);
//        } finally {
//            a.recycle();
//        }

        init();
    }

    private void init() {
        if (mFormat12 == null || mFormat24 == null) {
            if (mFormat12 == null) {
                mFormat12 = "hh:mm";
            }
            if (mFormat24 == null) {
                mFormat24 = "HH:mm";
            }
        }

        createTime(mTimeZone);
        // Wait until onAttachedToWindow() to handle the ticker
        chooseFormat(false);
    }

    protected void createTime(String timeZone) {
        if (timeZone != null) {
            mTime = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
        } else {
            mTime = Calendar.getInstance();
        }
    }

    /**
     * Returns the formatting pattern used to display the date and/or time
     * in 12-hour mode. The formatting pattern syntax is described in
     * {@link DateFormat}.
     *
     * @return A {@link CharSequence} or null.
     *
     * @see #setFormat12Hour(CharSequence)
     * @see #is24HourModeEnabled()
     */
    @ViewDebug.ExportedProperty
    public CharSequence getFormat12Hour() {
        return mFormat12;
    }

    /**
     * <p>Specifies the formatting pattern used to display the date and/or time
     * in 12-hour mode. The formatting pattern syntax is described in
     * {@link DateFormat}.</p>
     *
     * <p>If this pattern is set to null, {@link #getFormat24Hour()} will be used
     * even in 12-hour mode. If both 24-hour and 12-hour formatting patterns
     * are set to null, the default pattern for the current locale will be used
     * instead.</p>
     *
     * <p><strong>Note:</strong> if styling is not needed, it is highly recommended
     * you supply a format string generated by
     * {@link DateFormat#getBestDateTimePattern(java.util.Locale, String)}. This method
     * takes care of generating a format string adapted to the desired locale.</p>
     *
     *
     * @param format A date/time formatting pattern as described in {@link DateFormat}
     *
     * @see #getFormat12Hour()
     * @see #is24HourModeEnabled()
     * @see DateFormat#getBestDateTimePattern(java.util.Locale, String)
     * @see DateFormat
     *
     * @attr ref android.R.styleable#FightClock_format12Hour
     */
    public void setFormat12Hour(CharSequence format) {
        mFormat12 = format;

        chooseFormat();
        onTimeChanged();
    }

    /**
     * Like setFormat12Hour, but for the content description.
     * @hide
     */
    public void setContentDescriptionFormat12Hour(CharSequence format) {
        mDescFormat12 = format;

        chooseFormat();
        onTimeChanged();
    }

    /**
     * Returns the formatting pattern used to display the date and/or time
     * in 24-hour mode. The formatting pattern syntax is described in
     * {@link DateFormat}.
     *
     * @return A {@link CharSequence} or null.
     *
     * @see #setFormat24Hour(CharSequence)
     * @see #is24HourModeEnabled()
     */
    @ViewDebug.ExportedProperty
    public CharSequence getFormat24Hour() {
        return mFormat24;
    }

    /**
     * <p>Specifies the formatting pattern used to display the date and/or time
     * in 24-hour mode. The formatting pattern syntax is described in
     * {@link DateFormat}.</p>
     *
     * <p>If this pattern is set to null, {@link #getFormat24Hour()} will be used
     * even in 12-hour mode. If both 24-hour and 12-hour formatting patterns
     * are set to null, the default pattern for the current locale will be used
     * instead.</p>
     *
     * <p><strong>Note:</strong> if styling is not needed, it is highly recommended
     * you supply a format string generated by
     * {@link DateFormat#getBestDateTimePattern(java.util.Locale, String)}. This method
     * takes care of generating a format string adapted to the desired locale.</p>
     *
     * @param format A date/time formatting pattern as described in {@link DateFormat}
     *
     * @see #getFormat24Hour()
     * @see #is24HourModeEnabled()
     * @see DateFormat#getBestDateTimePattern(java.util.Locale, String)
     * @see DateFormat
     *
     * @attr ref android.R.styleable#FightClock_format24Hour
     */
    public void setFormat24Hour(CharSequence format) {
        mFormat24 = format;

        chooseFormat();
        onTimeChanged();
    }

    /**
     * Like setFormat24Hour, but for the content description.
     * @hide
     */
    public void setContentDescriptionFormat24Hour(CharSequence format) {
        mDescFormat24 = format;

        chooseFormat();
        onTimeChanged();
    }

    /**
     * Sets whether this clock should always track the current user and not the user of the
     * current process. This is used for single instance processes like the systemUI who need
     * to display time for different users.
     *
     * @hide
     */
    public void setShowCurrentUserTime(boolean showCurrentUserTime) {
        mShowCurrentUserTime = showCurrentUserTime;

        chooseFormat();
        onTimeChanged();
        unregisterObserver();
        registerObserver();
    }

    /**
     * Indicates whether the system is currently using the 24-hour mode.
     *
     * When the system is in 24-hour mode, this view will use the pattern
     * returned by {@link #getFormat24Hour()}. In 12-hour mode, the pattern
     * returned by {@link #getFormat12Hour()} is used instead.
     *
     * If either one of the formats is null, the other format is used. If
     * both formats are null, the default formats for the current locale are used.
     *
     * @return true if time should be displayed in 24-hour format, false if it
     *         should be displayed in 12-hour format.
     *
     * @see #setFormat12Hour(CharSequence)
     * @see #getFormat12Hour()
     * @see #setFormat24Hour(CharSequence)
     * @see #getFormat24Hour()
     */
    public boolean is24HourModeEnabled() {
//        if (mShowCurrentUserTime) {
//            return DateFormat.is24HourFormat(getContext(), ActivityManager.getCurrentUser());
//        } else {
            return DateFormat.is24HourFormat(getContext());
//        }
    }

    /**
     * Indicates which time zone is currently used by this view.
     *
     * @return The ID of the current time zone or null if the default time zone,
     *         as set by the user, must be used
     *
     * @see TimeZone
     * @see java.util.TimeZone#getAvailableIDs()
     * @see #setTimeZone(String)
     */
    public String getTimeZone() {
        return mTimeZone;
    }

    /**
     * Sets the specified time zone to use in this clock. When the time zone
     * is set through this method, system time zone changes (when the user
     * sets the time zone in settings for instance) will be ignored.
     *
     * @param timeZone The desired time zone's ID as specified in {@link TimeZone}
     *                 or null to user the time zone specified by the user
     *                 (system time zone)
     *
     * @see #getTimeZone()
     * @see java.util.TimeZone#getAvailableIDs()
     * @see TimeZone#getTimeZone(String)
     *
     * @attr ref android.R.styleable#FightClock_timeZone
     */
    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;

        createTime(timeZone);
        onTimeChanged();
    }

    /**
     * Selects either one of {@link #getFormat12Hour()} or {@link #getFormat24Hour()}
     * depending on whether the user has selected 24-hour format.
     *
     * Calling this method does not schedule or unschedule the time ticker.
     */
    private void chooseFormat() {
        chooseFormat(true);
    }

    /**
     * Returns the current format string. Always valid after constructor has
     * finished, and will never be {@code null}.
     *
     * @hide
     */
    public CharSequence getFormat() {
        return mFormat;
    }

    /**
     * Selects either one of {@link #getFormat12Hour()} or {@link #getFormat24Hour()}
     * depending on whether the user has selected 24-hour format.
     *
     * param handleTicker true if calling this method should schedule/unschedule the
     *                     time ticker, false otherwise
     */

    public static boolean hasSeconds(CharSequence inFormat) {
        return hasDesignator(inFormat, SECONDS);
    }

    /**
     * Test if a format string contains the given designator. Always returns
     * {@code false} if the input format is {@code null}.
     *
     * @hide
     */
    public static boolean hasDesignator(CharSequence inFormat, char designator) {
        if (inFormat == null) return false;

        final int length = inFormat.length();

        int c;
        int count;

        for (int i = 0; i < length; i += count) {
            count = 1;
            c = inFormat.charAt(i);

            if (c == QUOTE) {
                count = skipQuotedText(inFormat, i, length);
            } else if (c == designator) {
                return true;
            }
        }

        return false;
    }

    private static int skipQuotedText(CharSequence s, int i, int len) {
        if (i + 1 < len && s.charAt(i + 1) == QUOTE) {
            return 2;
        }

        int count = 1;
        // skip leading quote
        i++;

        while (i < len) {
            char c = s.charAt(i);

            if (c == QUOTE) {
                count++;
                //  QUOTEQUOTE -> QUOTE
                if (i + 1 < len && s.charAt(i + 1) == QUOTE) {
                    i++;
                } else {
                    break;
                }
            } else {
                i++;
                count++;
            }
        }

        return count;
    }

    public  static final char    QUOTE                  =    '\'';
    public  static final char    SECONDS                =    's';

    private void chooseFormat(boolean handleTicker) {
        final boolean format24Requested = is24HourModeEnabled();

//        LocaleData ld = LocaleData.get(getContext().getResources().getConfiguration().locale);

        if (format24Requested) {
            mFormat = abc(mFormat24, mFormat12, "HH:mm");
            mDescFormat = abc(mDescFormat24, mDescFormat12, mFormat);
        } else {
            mFormat = abc(mFormat12, mFormat24, "hh:mm");
            mDescFormat = abc(mDescFormat12, mDescFormat24, mFormat);
        }

        boolean hadSeconds = mHasSeconds;
        mHasSeconds = hasSeconds(mFormat);

        if (handleTicker && mAttached && hadSeconds != mHasSeconds) {
            if (hadSeconds) getHandler().removeCallbacks(mTicker);
            else mTicker.run();
        }
    }

    /**
     * Returns a if not null, else return b if not null, else return c.
     */
    private static CharSequence abc(CharSequence a, CharSequence b, CharSequence c) {
        return a == null ? (b == null ? c : b) : a;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (!mAttached) {
            mAttached = true;

            registerReceiver();
            registerObserver();

            createTime(mTimeZone);

            if (mHasSeconds) {
                mTicker.run();
            } else {
                onTimeChanged();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mAttached) {
            unregisterReceiver();
            unregisterObserver();

            getHandler().removeCallbacks(mTicker);

            mAttached = false;
        }
    }

    private void registerReceiver() {
        final IntentFilter filter = new IntentFilter();

        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

        // OK, this is gross but needed. This class is supported by the
        // remote views mechanism and as a part of that the remote views
        // can be inflated by a context for another user without the app
        // having interact users permission - just for loading resources.
        // For example, when adding widgets from a managed profile to the
        // home screen. Therefore, we register the receiver as the user
        // the app is running as not the one the context is for.
        getContext().registerReceiver(mIntentReceiver,
                filter, null, getHandler());
    }



    private void registerObserver() {
        if (mAttached) {
            if (mFormatChangeObserver == null) {
                mFormatChangeObserver = new FormatChangeObserver(getHandler());
            }
            final ContentResolver resolver = getContext().getContentResolver();
            if (mShowCurrentUserTime) {
                resolver.registerContentObserver(Settings.System.CONTENT_URI, true,
                        mFormatChangeObserver );
            } else {
                resolver.registerContentObserver(Settings.System.CONTENT_URI, true,
                        mFormatChangeObserver);
            }
        }
    }

    private void unregisterReceiver() {
        getContext().unregisterReceiver(mIntentReceiver);
    }

    private void unregisterObserver() {
        if (mFormatChangeObserver != null) {
            final ContentResolver resolver = getContext().getContentResolver();
            resolver.unregisterContentObserver(mFormatChangeObserver);
        }
    }



    protected final void onTimeChanged() {
        mTime.setTimeInMillis(System.currentTimeMillis());
        setTimeText(mTime);
    }

    protected void setTimeText(Calendar time){
        CharSequence str = DateFormat.format(mFormat, time);
        setContentDescription(DateFormat.format(mDescFormat, time));
    }

//    @Override
//    protected void encodeProperties(@NonNull ViewHierarchyEncoder stream) {
//        super.encodeProperties(stream);
//
//        CharSequence s = getFormat12Hour();
//        stream.addProperty("format12Hour", s == null ? null : s.toString());
//
//        s = getFormat24Hour();
//        stream.addProperty("format24Hour", s == null ? null : s.toString());
//        stream.addProperty("format", mFormat == null ? null : mFormat.toString());
//        stream.addProperty("hasSeconds", mHasSeconds);
//    }
}
