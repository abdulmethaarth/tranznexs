package co.webnexs.tranznexs;

import android.app.Application;
import android.content.Context;

public class CrashToReOpen extends Application {

    private static Context mContext;

    public static CrashToReOpen instace;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        instace = this;
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public static CrashToReOpen getIntance() {
        return instace;
    }
}
