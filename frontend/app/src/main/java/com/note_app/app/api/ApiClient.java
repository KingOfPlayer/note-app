package com.note_app.app.api;

import com.note_app.app.session.SessionStore;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final String baseUrl;
    private final SessionStore session;
    private final OkHttpClient client;

    public ApiClient(String baseUrl, SessionStore session) {
        this.baseUrl = baseUrl;
        this.session = session;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public String get(String path) throws IOException {
        Request.Builder b = new Request.Builder().url(baseUrl + path).get();
        applyAuth(b);
        return execute(b.build());
    }

    public String post(String path, String json, boolean withAuth) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request.Builder b = new Request.Builder().url(baseUrl + path).post(body);
        if (withAuth) applyAuth(b);
        return execute(b.build());
    }

    public String put(String path, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request.Builder b = new Request.Builder().url(baseUrl + path).put(body);
        applyAuth(b);
        return execute(b.build());
    }

    public String delete(String path) throws IOException {
        Request.Builder b = new Request.Builder().url(baseUrl + path).delete();
        applyAuth(b);
        return execute(b.build());
    }

    private void applyAuth(Request.Builder b) {
        String token = session.getToken();
        if (token != null && !token.isBlank()) {
            b.header("Authorization", "Bearer " + token);
        }
    }

    private String execute(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            String bodyString = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                throw new ApiException(response.code(), bodyString);
            }
            return bodyString;
        }
    }
}
