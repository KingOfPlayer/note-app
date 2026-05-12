package com.note_app.app.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.note_app.app.R;
import com.note_app.app.api.UserApi;
import com.note_app.app.util.AppContext;
import com.note_app.app.util.BackgroundExecutor;

public class ProfileActivity extends AppCompatActivity {

    private AppContext app;
    private EditText nameField;
    private EditText emailField;
    private TextView roleText;
    private TextView idText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        app = new AppContext(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Profilim");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        nameField = findViewById(R.id.field_name);
        emailField = findViewById(R.id.field_email);
        roleText = findViewById(R.id.text_role);
        idText = findViewById(R.id.text_id);

        Button saveBtn = findViewById(R.id.btn_save);
        Button deleteBtn = findViewById(R.id.btn_delete);

        saveBtn.setOnClickListener(v -> saveProfile());
        deleteBtn.setOnClickListener(v -> confirmDelete());

        loadProfile();
    }

    private void loadProfile() {
        BackgroundExecutor.run(
                () -> app.users().me(),
                new BackgroundExecutor.Callback<UserApi.UserInfo>() {
                    @Override
                    public void onSuccess(UserApi.UserInfo result) {
                        nameField.setText(result.name);
                        emailField.setText(result.email);
                        roleText.setText("Rol: " + result.role);
                        idText.setText("Id: " + result.id);
                    }

                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(ProfileActivity.this,
                                "Yuklenemedi: " + error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveProfile() {
        String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Ad ve e-posta bos olamaz", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = app.session().getUserId();
        BackgroundExecutor.run(
                () -> app.users().update(userId, name, email),
                new BackgroundExecutor.Callback<UserApi.UserInfo>() {
                    @Override
                    public void onSuccess(UserApi.UserInfo result) {
                        Toast.makeText(ProfileActivity.this,
                                "Profil guncellendi", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(ProfileActivity.this,
                                "Guncellenemedi: " + error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Hesabi sil")
                .setMessage("Hesabiniz ve tum verileriniz silinecek. Emin misiniz?")
                .setPositiveButton("Sil", (d, w) -> deleteAccount())
                .setNegativeButton("Vazgec", null)
                .show();
    }

    private void deleteAccount() {
        String userId = app.session().getUserId();
        BackgroundExecutor.run(
                () -> { app.users().delete(userId); return true; },
                new BackgroundExecutor.Callback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        app.session().clear();
                        Toast.makeText(ProfileActivity.this,
                                "Hesap silindi", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                        finishAffinity();
                    }

                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(ProfileActivity.this,
                                "Silinemedi: " + error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
