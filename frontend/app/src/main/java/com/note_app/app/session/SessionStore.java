package com.note_app.app.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionStore {

    private static final String PREFS = "noteapp_session";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_ROLE = "role";

    private final SharedPreferences prefs;

    public SessionStore(Context context) {
        this.prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void save(String userId, String userName, String token, String role) {
        prefs.edit()
                .putString(KEY_USER_ID, userId)
                .putString(KEY_USER_NAME, userName)
                .putString(KEY_TOKEN, token)
                .putString(KEY_ROLE, role)
                .apply();
    }

    public String getUserId() { return prefs.getString(KEY_USER_ID, null); }
    public String getUserName() { return prefs.getString(KEY_USER_NAME, null); }
    public String getToken() { return prefs.getString(KEY_TOKEN, null); }
    public String getRole() { return prefs.getString(KEY_ROLE, "USER"); }

    public boolean isLoggedIn() { return getUserId() != null; }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
