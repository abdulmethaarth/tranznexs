package co.webnexs.tranznexs.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;

//import com.twitter.sdk.android.Twitter;
//import com.twitter.sdk.android.core.TwitterAuthConfig;

//import io.fabric.sdk.android.Fabric;

public class TwitterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
      //  TwitterAuthConfig authConfig =  new TwitterAuthConfig("p7hxQ54W4XzgDOhZe5XY84knH", "cnRvnxTpFsQYtTi2O9ogzmx6hDldKdg2e85hOog4teIOrL8uhC");
      //  Log.d("authConfig","authConfig = "+authConfig);
       // Fabric.with(this, new Twitter(authConfig));
    }

    private static SharedPreferences sSharedPreferences;

    public static SharedPreferences getPreferences(Context context) {
        if (sSharedPreferences == null) {
            sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        }

        return sSharedPreferences;
    }

    public static void setBrainTreeClientToken(Context context,String clientToken) {
        getPreferences(context).edit().putString("braintree_client_token",clientToken).apply();
    }
    public static String getBrainTreeClientToken(Context context) {
        return getPreferences(context).getString("braintree_client_token", null);
    }

}
