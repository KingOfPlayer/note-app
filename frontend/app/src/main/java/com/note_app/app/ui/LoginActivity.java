package com.note_app.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.note_app.app.R;
import com.note_app.app.api.AuthApi;
import com.note_app.app.util.AppContext;
import com.note_app.app.util.BackgroundExecutor;

public class LoginActivity extends AppCompatActivity {

    private AppContext app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        app = new AppContext(this);

        if (app.session().isLoggedIn()) {
            goToList();
            return;
        }

        EditText emailField = findViewById(R.id.field_email);
        EditText passwordField = findViewById(R.id.field_password);
        Button loginBtn = findViewById(R.id.btn_login);
        TextView toRegister = findViewById(R.id.link_register);

        loginBtn.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "E-posta ve sifre zorunludur", Toast.LENGTH_SHORT).show();
                return;
            }
            loginBtn.setEnabled(false);
            BackgroundExecutor.run(
                    () -> app.auth().login(email, password),
                    new BackgroundExecutor.Callback<AuthApi.AuthResult>() {
                        @Override
                        public void onSuccess(AuthApi.AuthResult result) {
                            app.session().save(result.userId, result.name, result.token, result.role);
                            goToList();
                        }

                        @Override
                        public void onError(Throwable error) {
                            loginBtn.setEnabled(true);
                            Toast.makeText(LoginActivity.this,
                                    "Giris basarisiz: " + error.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        toRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void goToList() {
        startActivity(new Intent(this, NoteListActivity.class));
        finish();
    }
}
