package com.note_app.app.api;

import com.note_app.app.model.FileMeta;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FileApi {

    private final ApiClient client;

    public FileApi(ApiClient client) {
        this.client = client;
    }

    public FileMeta upload(Long noteId, String filename, byte[] bytes, String mimeType)
            throws IOException, JSONException {
        MediaType type = mimeType != null ? MediaType.parse(mimeType) : MediaType.parse("application/octet-stream");
        RequestBody filePart = RequestBody.create(bytes, type);
        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", filename != null ? filename : "dosya", filePart)
                .build();
        String path = "/api/files" + (noteId != null ? "?noteId=" + noteId : "");
        String response = client.postMultipart(path, body);
        return parseSingle(response);
    }

    public List<FileMeta> listForNote(long noteId) throws IOException, JSONException {
        String response = client.get("/api/files?noteId=" + noteId);
        return parseList(new JSONObject(response).getJSONArray("data"));
    }

    public void delete(String fileId) throws IOException {
        client.delete("/api/files/" + fileId);
    }

    public String downloadUrl(String fileId) {
        return "/api/files/" + fileId + "/download";
    }

    private FileMeta parseSingle(String response) throws JSONException {
        JSONObject root = new JSONObject(response);
        return fromJson(root.getJSONObject("data"));
    }

    private List<FileMeta> parseList(JSONArray items) throws JSONException {
        List<FileMeta> result = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            result.add(fromJson(items.getJSONObject(i)));
        }
        return result;
    }

    private FileMeta fromJson(JSONObject json) throws JSONException {
        FileMeta meta = new FileMeta();
        meta.setId(json.getString("id"));
        meta.setFilename(json.optString("filename"));
        meta.setContentType(json.optString("contentType", null));
        meta.setSize(json.optLong("size"));
        meta.setUserId(json.optString("userId"));
        if (!json.isNull("noteId")) meta.setNoteId(json.getLong("noteId"));
        meta.setUploadedAt(json.optString("uploadedAt"));
        return meta;
    }

    public byte[] download(String filename)
        throws IOException, JSONException {
        String filenamePath = downloadUrl(filename);
        return client.get_raw(filenamePath);
    }
}
