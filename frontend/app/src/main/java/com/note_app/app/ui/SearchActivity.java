package com.note_app.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.note_app.app.R;
import com.note_app.app.model.Note;
import com.note_app.app.util.AppContext;
import com.note_app.app.util.BackgroundExecutor;

import java.util.Collections;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private static final String[] TYPES = new String[]{"all", "title", "content"};
    private static final String[] TYPE_LABELS = new String[]{"Hepsi", "Baslik", "Icerik"};

    private AppContext app;
    private NoteAdapter adapter;
    private EditText queryField;
    private Spinner typeSpinner;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        app = new AppContext(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Notlarda Ara");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        queryField = findViewById(R.id.field_query);
        typeSpinner = findViewById(R.id.spinner_type);
        emptyView = findViewById(R.id.empty_view);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, TYPE_LABELS);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(spinnerAdapter);

        RecyclerView recycler = findViewById(R.id.recycler_results);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoteAdapter(note -> {
            Intent edit = new Intent(this, NoteEditActivity.class);
            edit.putExtra(NoteEditActivity.EXTRA_NOTE_ID, note.getId());
            startActivity(edit);
        });
        recycler.setAdapter(adapter);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { runSearch(); }
        };
        queryField.addTextChangedListener(watcher);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { runSearch(); }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void runSearch() {
        String q = queryField.getText().toString().trim();
        if (q.length() < 2) {
            adapter.setItems(Collections.emptyList());
            emptyView.setText("En az 2 karakter girin");
            emptyView.setVisibility(View.VISIBLE);
            return;
        }
        String type = TYPES[typeSpinner.getSelectedItemPosition()];
        BackgroundExecutor.run(
                () -> app.notes().search(type, q),
                new BackgroundExecutor.Callback<List<Note>>() {
                    @Override
                    public void onSuccess(List<Note> result) {
                        adapter.setItems(result);
                        if (result.isEmpty()) {
                            emptyView.setText("Sonuc bulunamadi");
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            emptyView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(SearchActivity.this,
                                "Arama hatasi: " + error.getMessage(),
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
