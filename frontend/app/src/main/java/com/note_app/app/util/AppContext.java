package com.note_app.app.util;

import android.content.Context;

import com.note_app.app.api.ApiClient;
import com.note_app.app.api.AuthApi;
import com.note_app.app.api.CategoryApi;
import com.note_app.app.api.FileApi;
import com.note_app.app.api.NoteApi;
import com.note_app.app.api.UserApi;
import com.note_app.app.session.SessionStore;

public class AppContext {

    public static final String BASE_URL = "http://10.0.2.2:8080";

    private final SessionStore session;
    private final ApiClient client;
    private final AuthApi authApi;
    private final NoteApi noteApi;
    private final CategoryApi categoryApi;
    private final UserApi userApi;
    private final FileApi fileApi;

    public AppContext(Context context) {
        this.session = new SessionStore(context);
        this.client = new ApiClient(BASE_URL, session);
        this.authApi = new AuthApi(client);
        this.noteApi = new NoteApi(client);
        this.categoryApi = new CategoryApi(client);
        this.userApi = new UserApi(client);
        this.fileApi = new FileApi(client);
    }

    public SessionStore session() { return session; }
    public AuthApi auth() { return authApi; }
    public NoteApi notes() { return noteApi; }
    public CategoryApi categories() { return categoryApi; }
    public UserApi users() { return userApi; }
    public FileApi files() { return fileApi; }
}
