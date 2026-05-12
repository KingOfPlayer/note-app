package com.note_app.app.util;

import android.content.Context;

import com.note_app.app.api.ApiClient;
import com.note_app.app.api.AuthApi;
import com.note_app.app.api.CategoryApi;
import com.note_app.app.api.NoteApi;
import com.note_app.app.session.SessionStore;

public class AppContext {

    public static final String BASE_URL = "http://10.0.2.2:8080";

    private final SessionStore session;
    private final ApiClient client;
    private final AuthApi authApi;
    private final NoteApi noteApi;
    private final CategoryApi categoryApi;

    public AppContext(Context context) {
        this.session = new SessionStore(context);
        this.client = new ApiClient(BASE_URL, session);
        this.authApi = new AuthApi(client);
        this.noteApi = new NoteApi(client);
        this.categoryApi = new CategoryApi(client);
    }

    public SessionStore session() { return session; }
    public AuthApi auth() { return authApi; }
    public NoteApi notes() { return noteApi; }
    public CategoryApi categories() { return categoryApi; }
}
