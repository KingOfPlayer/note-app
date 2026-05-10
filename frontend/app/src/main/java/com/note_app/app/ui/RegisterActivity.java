package com.note_app.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.note_app.app.R;
import com.note_app.app.api.AuthApi;
import com.note_app.app.util.AppContext;
import com.note_app.app.util.BackgroundExecutor;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        AppContext app = new AppContext(this);

        EditText nameField = findViewById(R.id.field_name);
        EditText emailField = findViewById(R.id.field_email);
        EditText passwordField = findViewById(R.id.field_password);
        Button registerBtn = findViewById(R.id.btn_register);

        registerBtn.setOnClickListener(v -> {
            String name = nameField.getText().toString().trim();
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString();
            if (name.isEmpty() || email.isEmpty() || password.length() < 6) {
                Toast.makeText(this,
                        "Tum alanlari doldurun, sifre en az 6 karakter olmali",
                        Toast.LENGTH_LONG).show();
                return;
            }
            registerBtn.setEnabled(false);
            BackgroundExecutor.run(
                    () -> app.auth().register(name, email, password),
                    new BackgroundExecutor.Callback<AuthApi.AuthResult>() {
                        @Override
                        public void onSuccess(AuthApi.AuthResult result) {
                            app.session().save(result.userId, result.name, result.token, result.role);
                            Toast.makeText(RegisterActivity.this,
                                    "Kayit tamamlandi", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, NoteListActivity.class));
                            finishAffinity();
                        }

                        @Override
                        public void onError(Throwable error) {
                            registerBtn.setEnabled(true);
                            Toast.makeText(RegisterActivity.this,
                                    "Kayit basarisiz: " + error.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}
