package com.note_app.app.ui;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.note_app.app.R;
import com.note_app.app.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.Holder> {

    public interface OnActionListener {
        void onDelete(Category category);
    }

    private final List<Category> items = new ArrayList<>();
    private final OnActionListener listener;

    public CategoryAdapter(OnActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Category> data) {
        items.clear();
        items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new Holder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Category c = items.get(position);
        holder.name.setText(c.getName());
        int defaultColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.color_category_default);
        int fill = parseColor(c.getColor(), defaultColor);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(fill);
        holder.colorDot.setBackground(shape);
        holder.deleteBtn.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(c);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private int parseColor(String hex, int fallback) {
        if (hex == null || hex.isEmpty()) return fallback;
        try {
            return Color.parseColor(hex);
        } catch (IllegalArgumentException ex) {
            return fallback;
        }
    }

    static class Holder extends RecyclerView.ViewHolder {
        final View colorDot;
        final TextView name;
        final ImageButton deleteBtn;

        Holder(@NonNull View itemView) {
            super(itemView);
            colorDot = itemView.findViewById(R.id.color_dot);
            name = itemView.findViewById(R.id.text_name);
            deleteBtn = itemView.findViewById(R.id.btn_delete);
        }
    }
}
