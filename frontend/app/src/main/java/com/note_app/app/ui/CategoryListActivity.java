package com.note_app.app.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.note_app.app.R;
import com.note_app.app.model.Category;
import com.note_app.app.ui.widget.ColorPaletteView;
import com.note_app.app.util.AppContext;
import com.note_app.app.util.BackgroundExecutor;

import java.util.List;

public class CategoryListActivity extends AppCompatActivity {

    private AppContext app;
    private CategoryAdapter adapter;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        app = new AppContext(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Kategoriler");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        emptyView = findViewById(R.id.empty_view);
        RecyclerView recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CategoryAdapter(this::confirmDelete);
        recycler.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_new);
        fab.setOnClickListener(v -> showCreateDialog());

        loadCategories();
    }

    private void loadCategories() {
        BackgroundExecutor.run(
                () -> app.categories().list(),
                new BackgroundExecutor.Callback<List<Category>>() {
                    @Override
                    public void onSuccess(List<Category> result) {
                        adapter.setItems(result);
                        emptyView.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(CategoryListActivity.this,
                                "Yuklenemedi: " + error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showCreateDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_new_category, null);
        EditText nameField = view.findViewById(R.id.field_name);
        ColorPaletteView palette = view.findViewById(R.id.palette);
        String[] chosen = new String[]{ palette.getSelectedColor() };
        palette.setOnColorSelectedListener(hex -> chosen[0] = hex);

        new AlertDialog.Builder(this)
                .setTitle("Yeni kategori")
                .setView(view)
                .setPositiveButton("Olustur", (d, w) -> {
                    String name = nameField.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, "Ad bos olamaz", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    BackgroundExecutor.run(
                            () -> app.categories().create(name, chosen[0]),
                            new BackgroundExecutor.Callback<Category>() {
                                @Override
                                public void onSuccess(Category result) {
                                    Toast.makeText(CategoryListActivity.this,
                                            "Kategori olusturuldu", Toast.LENGTH_SHORT).show();
                                    loadCategories();
                                }

                                @Override
                                public void onError(Throwable error) {
                                    Toast.makeText(CategoryListActivity.this,
                                            "Eklenemedi: " + error.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                })
                .setNegativeButton("Vazgec", null)
                .show();
    }

    private void confirmDelete(Category category) {
        new AlertDialog.Builder(this)
                .setTitle("Sil")
                .setMessage("\"" + category.getName() + "\" kategorisi silinsin mi?")
                .setPositiveButton("Sil", (d, w) -> {
                    BackgroundExecutor.run(
                            () -> { app.categories().delete(category.getId()); return true; },
                            new BackgroundExecutor.Callback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean result) {
                                    Toast.makeText(CategoryListActivity.this,
                                            "Silindi", Toast.LENGTH_SHORT).show();
                                    loadCategories();
                                }

                                @Override
                                public void onError(Throwable error) {
                                    Toast.makeText(CategoryListActivity.this,
                                            "Silinemedi: " + error.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                })
                .setNegativeButton("Vazgec", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
