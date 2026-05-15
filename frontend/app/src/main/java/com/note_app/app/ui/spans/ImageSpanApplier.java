package com.note_app.app.ui.spans;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.widget.EditText;

import androidx.core.content.res.ResourcesCompat;

import com.note_app.app.R;
import com.note_app.app.util.AppContext;
import com.note_app.app.util.BackgroundExecutor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImageSpanApplier {

    static final Pattern pattern = Pattern.compile("\\[image,([a-fA-F0-9]+)\\]");
    public static void apply(Context context, EditText editText, Editable s, AppContext app, Set<String> imageTracker) {
        Matcher matcher = pattern.matcher(s.toString());

        Set<String> currentIds = new HashSet<>();

        while (matcher.find()) {
            String fileId = matcher.group(1);
            currentIds.add(fileId);
        }

        // 3. Sync the tracker after the loop
        imageTracker.retainAll(currentIds);
        matcher = pattern.matcher(s.toString());

        while (matcher.find()) {
            String fileId = matcher.group(1);
            int start = matcher.start();
            int end = matcher.end();

            // Skip if already loading or loaded
            if (imageTracker.contains(fileId)) continue;
            imageTracker.add(fileId);

            Drawable placeholder = ResourcesCompat.getDrawable(context.getResources(), R.drawable.outline_arrow_cool_down_24, null);
            placeholder.setBounds(0, 0, 100, 100);
            ImageSpan placeholderSpan = new PlaceholderImageSpan(placeholder, ImageSpan.ALIGN_BOTTOM);
            s.setSpan(placeholderSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            fetchImage(context, app, fileId, new ImageFetchCallback() {
                @Override
                public void onSuccess(Drawable drawable) {
                    int currentStart = s.getSpanStart(placeholderSpan);
                    int currentEnd = s.getSpanEnd(placeholderSpan);

                    if (currentStart != -1 && currentEnd != -1) {
                        s.removeSpan(placeholderSpan);

                        int width  = editText.getWidth() - editText.getPaddingLeft() - editText.getPaddingRight();
                        int height = (int) (width * ((float) drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth()));
                        drawable.setBounds(0, 0, width, height);

                        s.setSpan(new LoadedImageSpan(drawable, ImageSpan.ALIGN_BOTTOM),
                            currentStart, currentEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }

                @Override
                public void onError(Exception error) {
                    // Remove from set so user can retry manually if needed
                    imageTracker.remove(fileId);

                    int currentStart = s.getSpanStart(placeholderSpan);
                    int currentEnd = s.getSpanEnd(placeholderSpan);

                    Drawable broken = ResourcesCompat.getDrawable(context.getResources(), R.drawable.outline_broken_image_24, null);
                    broken.setBounds(0, 0, 100, 100);

                    if (currentStart != -1 && currentEnd != -1) {
                        s.removeSpan(placeholderSpan);
                        s.setSpan(new PlaceholderImageSpan(broken, ImageSpan.ALIGN_BOTTOM),
                            currentStart, currentEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            });
        }
    }

    public interface ImageFetchCallback {
        void onSuccess(Drawable drawable);
        void onError(Exception error);
    }

    private static void fetchImage(Context context, AppContext app, String fileId, ImageFetchCallback callback) {
        BackgroundExecutor.run(
            () -> {
                // Runs on background thread
                byte[] bytes = app.files().download(fileId);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            },
            new BackgroundExecutor.Callback<Drawable>() {
                @Override
                public void onSuccess(Drawable drawable) {
                    callback.onSuccess(drawable);
                }

                @Override
                public void onError(Throwable error) {
                    callback.onError(new Exception(error));
                }
            }
        );
    }

    static public Drawable bytesToDrawable(Context context, byte[] imageBytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        return drawable;
    }

    public static void clear(Editable s) {
        for (PlaceholderImageSpan span : s.getSpans(0, s.length(), PlaceholderImageSpan.class))
            s.removeSpan(span);
    }

    public static String convertNormalString(String input) {
        if (input == null) return null;

        Matcher matcher = pattern.matcher(input);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String state = matcher.group(1);

            String replacement = "🖼️";

            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(sb);

        return sb.toString();
    }
}
