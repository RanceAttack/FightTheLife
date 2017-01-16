package cn.somehui.baselibrary.permission;

/**
 * Created by somehui on 17/1/11.
 */

public abstract class SelfPermissionCallback {
    private String permission;
    private boolean done = false;

    public String getPermission() {
        return permission;
    }

    public boolean done() {
        return done;
    }

    public SelfPermissionCallback(String permission) {
        this.permission = permission;
    }

    public void allow() {
        if (!done) {
            run();
            done = true;
        }
    }

    public void deny() {
        if (!done) {
            bust();
            done = true;
        }
    }

    protected abstract void run();

    protected abstract void bust();
}