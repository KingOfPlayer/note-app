package com.note_app.app.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.note_app.app.model.Note;
import com.note_app.app.ui.widget.NoteCardView;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.CardHolder> {

    public interface OnNoteClickListener {
        void onClick(Note note);
    }

    private final List<Note> items = new ArrayList<>();
    private final OnNoteClickListener listener;

    public NoteAdapter(OnNoteClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Note> notes) {
        items.clear();
        items.addAll(notes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NoteCardView card = new NoteCardView(parent.getContext());
        card.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        return new CardHolder(card);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {
        Note note = items.get(position);
        holder.card.setNote(note);
        holder.card.setOnClickListener(v -> {
            if (listener != null) listener.onClick(note);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CardHolder extends RecyclerView.ViewHolder {
        final NoteCardView card;

        CardHolder(@NonNull NoteCardView card) {
            super(card);
            this.card = card;
        }
    }
}
