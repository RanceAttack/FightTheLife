package cn.somehui.fightthelife;

import android.widget.Toast;

import cn.somehui.publiclibrary.BaseActivity;

/**
 * Created by somehui on 17/1/11.
 */

public class MyBaseActivity extends BaseActivity {
    @Override
    protected Toast getToast() {
        return MyApplication.getToast();
    }
}
