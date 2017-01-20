package cn.somehui.fightthelife;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import cn.somehui.baselibrary.PixelUtils;

/**
 * Created by somehui on 17/1/19.
 */

public class BatteryView extends View {

    private Bitmap mChargeBitmap;

    private int mAnodeRadius = 9;
    private int mChargeRadius = 12;
    private int mStrokeWidth = 12;

    private float mProgress = 0.6f;
    private boolean isCharging = false;

    public void setCharging(boolean charging) {
        isCharging = charging;
        invalidate();
    }

    public void setProgress(float progress) {
        mProgress = progress;
        invalidate();
    }

    public BatteryView(Context context) {
        super(context);
        init();
    }

    public BatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BatteryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public BatteryView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        try{
            mChargeBitmap = ResProvider.bitmap(R.drawable.screen_charge);
        }catch (Exception e){
            e.printStackTrace();
            mChargeBitmap = null;
        }
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(0xffffffff);
        mPaint.setTextAlign(Paint.Align.LEFT);
        try{
            mStrokeWidth = PixelUtils.getPxByDp(4);
            mChargeRadius = PixelUtils.getPxByDp(4);
            mAnodeRadius = PixelUtils.getPxByDp(3);
        }catch (Exception e){
            e.printStackTrace();
        }
        mPaint.setStrokeWidth(mStrokeWidth);
    }

    private Paint mPaint;

    private RectF mTempRectF = new RectF();

    private synchronized void tempRect(float l,float t,float r, float b,float padding){
        mTempRectF.left = l;
        mTempRectF.top = t;
        mTempRectF.right = r;
        mTempRectF.bottom = b;

        mTempRectF.left += padding;
        mTempRectF.top += padding;
        mTempRectF.right -= padding;
        mTempRectF.bottom -= padding;
    }

    private int drawAnode(Canvas canvas){
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(0xffffffff);
        int height = getHeight()*2/5;
        int width = height/3+mStrokeWidth/2;
        int top = (getHeight()-height)/2;
        tempRect(0,top,width+mStrokeWidth/2,top+height,0);
        canvas.drawRoundRect(mTempRectF,mAnodeRadius,mAnodeRadius,mPaint);
        mTempRectF.left = mTempRectF.right-mStrokeWidth;
        canvas.drawRect(mTempRectF,mPaint);
        return width;
    }

    private void drawChargeBg(Canvas canvas, int loffset){
        tempRect(loffset+(int)((getWidth()-loffset)* (1f-mProgress)),0,getWidth(),getHeight(),mStrokeWidth);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(0x50ffffff);
        canvas.drawRect(mTempRectF,mPaint);
        tempRect(loffset,0,getWidth(),getHeight(),mStrokeWidth/2);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(0xffffffff);
        canvas.drawRoundRect(mTempRectF,mChargeRadius,mChargeRadius,mPaint);
    }

    private String getText(){
        return Integer.toString((int)(mProgress*100));
    }

    private int getIconSize(){
        return getHeight()*55/200;
    }

    private Rect mTempRect = new Rect();

    private void drawText(Canvas canvas, int loffset){
        String text = getText();
        int textSize = getHeight()*144/200;
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(textSize);
        float textWidth = mPaint.measureText(text);
        float totalWidth = textWidth + getIconSize();
        float textLeft = loffset + (getWidth()-loffset-totalWidth)/2;
        float bottom = (getHeight()/2) ;
        float largeDesent = mPaint.descent();
        float largeAsent = mPaint.ascent();
        canvas.drawText(text,textLeft,bottom - ((largeDesent + largeAsent) / 2),mPaint);
        int smallTextSize = textSize*64/144;
        mPaint.setTextSize(smallTextSize);
        canvas.drawText("%",textLeft+textWidth+6,bottom - ((largeDesent + largeAsent) / 2) ,mPaint);
        if(isCharging) {
            if (mChargeBitmap != null) {
                mTempRect.left = (int) (textLeft + textWidth + 6);
                mTempRect.top = (int) (bottom - (largeDesent + largeAsent) / 2 + largeAsent + PixelUtils.getPxByDp(5));
                mTempRect.right = mTempRect.left + smallTextSize * 38 / 62;
                mTempRect.bottom = mTempRect.top + smallTextSize;
                canvas.drawBitmap(mChargeBitmap, null, mTempRect, mPaint);
            } else {
                canvas.drawRect(mTempRect, mPaint);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int leftoffset = drawAnode(canvas);
        drawChargeBg(canvas,leftoffset);
        drawText(canvas,leftoffset);
    }
}
