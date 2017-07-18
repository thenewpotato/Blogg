package thenewpotato.blogg.managers;

import android.content.Context;
import android.content.SharedPreferences;

import static thenewpotato.blogg.Tools.PREFERENCE_NAME;

public class SharedPreferencesManager {

    private static SharedPreferencesManager sInstance;
    private final SharedPreferences mPref;

    private SharedPreferencesManager(Context context) {
        mPref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void initializeInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SharedPreferencesManager(context);
        }
    }

    public static synchronized SharedPreferencesManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException(SharedPreferencesManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }
        return sInstance;
    }

    public void setValue(String KEY_VALUE, int value) {
        mPref.edit()
                .putInt(KEY_VALUE, value)
                .apply();
    }

    public int getValue(String KEY_VALUE, int defaultVal) {
        return mPref.getInt(KEY_VALUE, defaultVal);
    }

}
