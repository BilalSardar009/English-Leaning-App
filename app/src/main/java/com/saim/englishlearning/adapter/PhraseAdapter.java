package com.saim.englishlearning.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saim.englishlearning.R;
import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.model.Phrase;
import com.saim.englishlearning.model.Word;
import com.saim.englishlearning.util.Anim;

import java.util.ArrayList;
import java.util.List;

public class PhraseAdapter extends RecyclerView.Adapter<PhraseAdapter.Holder> {

    public interface Listener {
        void onSpeak(Phrase phrase);

        void onSpeakExample(Phrase phrase);

        void onToggleMastered(Phrase phrase, View row);
    }

    private final List<Phrase> phrases;
    private final ProgressManager progress;
    private final Listener listener;

    public PhraseAdapter(List<Phrase> phrases, ProgressManager progress, Listener listener) {
        this.phrases = new ArrayList<>(phrases);
        this.progress = progress;
        this.listener = listener;
    }

    public void replace(List<Phrase> items) {
        phrases.clear();
        phrases.addAll(items);
        notifyDataSetChanged();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        final TextView phrase;
        final TextView meaning;
        final TextView urdu;
        final TextView example;
        final TextView level;
        final TextView badge;
        final ImageButton speak;

        Holder(View item) {
            super(item);
            phrase = item.findViewById(R.id.textPhrase);
            meaning = item.findViewById(R.id.textPhraseMeaning);
            urdu = item.findViewById(R.id.textPhraseUrdu);
            example = item.findViewById(R.id.textPhraseExample);
            level = item.findViewById(R.id.textPhraseLevel);
            badge = item.findViewById(R.id.textPhraseBadge);
            speak = item.findViewById(R.id.buttonSpeakPhrase);
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_phrase, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {
        final Phrase item = phrases.get(position);

        holder.phrase.setText(item.phrase);
        holder.meaning.setText(item.meaning);
        holder.example.setText(item.example);
        holder.level.setText(Word.levelName(item.level));

        if (progress.isUrduEnabled()) {
            holder.urdu.setVisibility(View.VISIBLE);
            holder.urdu.setText(item.urdu);
        } else {
            holder.urdu.setVisibility(View.GONE);
        }

        boolean mastered = progress.isPhraseMastered(item.id);
        holder.badge.setText(mastered ? R.string.badge_mastered : R.string.badge_tap_to_learn);
        holder.badge.setBackgroundResource(mastered
                ? R.drawable.bg_badge_done : R.drawable.bg_badge_todo);

        holder.speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                listener.onSpeak(item);
            }
        });

        holder.example.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSpeakExample(item);
            }
        });

        holder.badge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onToggleMastered(item, holder.itemView);
                boolean now = progress.isPhraseMastered(item.id);
                holder.badge.setText(now ? R.string.badge_mastered : R.string.badge_tap_to_learn);
                holder.badge.setBackgroundResource(now
                        ? R.drawable.bg_badge_done : R.drawable.bg_badge_todo);
            }
        });

        Anim.enter(holder.itemView, Math.min(position, 8) * 35L);
    }

    @Override
    public int getItemCount() {
        return phrases.size();
    }
}
