package com.note_app.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.note_app.app.R;
import com.note_app.app.model.Note;
import com.note_app.app.util.AppContext;
import com.note_app.app.util.BackgroundExecutor;

import java.util.List;

public class NoteListActivity extends AppCompatActivity {

    private AppContext app;
    private NoteAdapter adapter;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        app = new AppContext(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Notlarim");
        }

        RecyclerView recycler = findViewById(R.id.recycler_notes);
        emptyView = findViewById(R.id.empty_view);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NoteAdapter(note -> {
            Intent edit = new Intent(this, NoteEditActivity.class);
            edit.putExtra(NoteEditActivity.EXTRA_NOTE_ID, note.getId());
            startActivity(edit);
        });
        recycler.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_new_note);
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, NoteEditActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    private void loadNotes() {
        BackgroundExecutor.run(
                () -> app.notes().list(0, 50),
                new BackgroundExecutor.Callback<List<Note>>() {
                    @Override
                    public void onSuccess(List<Note> result) {
                        adapter.setItems(result);
                        emptyView.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(NoteListActivity.this,
                                "Yuklenemedi: " + error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        }
        if (id == R.id.action_categories) {
            startActivity(new Intent(this, CategoryListActivity.class));
            return true;
        }
        if (id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        if (id == R.id.action_logout) {
            app.session().clear();
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
