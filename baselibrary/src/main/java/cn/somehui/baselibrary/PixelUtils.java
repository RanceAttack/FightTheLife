package cn.somehui.baselibrary;

import android.content.Context;

/**
 * Created by somehui on 16/7/7.
 */
public class PixelUtils {
    private static final float DEFAULT = 2;
    private static float density = -1;

    public static int getPxByDp( float dp){
        if(density>0){
            return (int) Math.ceil(density * dp);
        }else{
            return (int)(dp * DEFAULT);
        }
    }

    public static int getDpFromPx(int px){
        if(density>0){
            return (int) Math.ceil(px/density);
        }else{
            return (int)(px/DEFAULT);
        }
    }

    public static void init(Context context){
        if(density<=0){
            density = context.getResources().getDisplayMetrics().density;
        }
    }

    public static float getDP(){
        if(density<=0){
            return DEFAULT;
        }else{
            return density;
        }
    }
}
