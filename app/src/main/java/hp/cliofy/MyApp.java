package hp.cliofy;

import android.app.Application;
import android.content.Context;

/**
 * Class representing my app
 */
public class MyApp extends Application {
    private static MyApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    /**
     * Returns the global application context
     * @return the global application context
     */
    public static Context getAppContext() {
        return instance.getApplicationContext();
    }
}
