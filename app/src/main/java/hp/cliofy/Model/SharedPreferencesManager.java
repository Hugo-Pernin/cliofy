package hp.cliofy.Model;

import android.content.Context;
import android.content.SharedPreferences;

import hp.cliofy.MyApp;

/**
 * Singleton which manages the shared preferences
 */
public class SharedPreferencesManager {
    private static SharedPreferencesManager instance;
    private final SharedPreferences prefs;

    private SharedPreferencesManager() {
        this.prefs = MyApp.getAppContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    /**
     * Returns the singleton instance of the manager
     * @return the singleton instance of the manager
     */
    public static SharedPreferencesManager getInstance() {
        if (SharedPreferencesManager.instance == null) {
            instance = new SharedPreferencesManager();
        }
        return SharedPreferencesManager.instance;
    }

    /**
     * Returns the refresh token
     * @return the refresh token
     */
    public String getRefreshToken() {
        return this.prefs.getString("refreshToken", null);
    }

    /**
     * Modifies the refresh token
     * @param refreshToken the new refresh token
     */
    public void setRefreshToken(String refreshToken) {
        SharedPreferences.Editor editor = this.prefs.edit();
        editor.putString("refreshToken", refreshToken);
        editor.apply();
    }
}
