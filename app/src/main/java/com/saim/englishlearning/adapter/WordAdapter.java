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
import com.saim.englishlearning.model.Word;
import com.saim.englishlearning.util.Anim;

import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.Holder> {

    public interface Listener {
        void onSpeak(Word word);

        void onSpeakExample(Word word);

        void onToggleLearned(Word word, View row);

        void onToggleFavourite(Word word, View star);
    }

    private final List<Word> words;
    private final ProgressManager progress;
    private final Listener listener;

    public WordAdapter(List<Word> words, ProgressManager progress, Listener listener) {
        this.words = words;
        this.progress = progress;
        this.listener = listener;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        final TextView word;
        final TextView meaning;
        final TextView urdu;
        final TextView example;
        final TextView badge;
        final ImageButton speak;
        final ImageButton favourite;
        final View details;

        Holder(View item) {
            super(item);
            word = item.findViewById(R.id.textWord);
            meaning = item.findViewById(R.id.textMeaning);
            urdu = item.findViewById(R.id.textUrdu);
            example = item.findViewById(R.id.textExample);
            badge = item.findViewById(R.id.textLearnedBadge);
            speak = item.findViewById(R.id.buttonSpeakWord);
            favourite = item.findViewById(R.id.buttonFavourite);
            details = item.findViewById(R.id.layoutWordDetails);
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_word, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {
        final Word item = words.get(position);

        holder.word.setText(item.word);
        holder.meaning.setText(item.meaning);
        holder.example.setText(item.example);

        if (progress.isUrduEnabled()) {
            holder.urdu.setVisibility(View.VISIBLE);
            holder.urdu.setText(item.urdu);
        } else {
            holder.urdu.setVisibility(View.GONE);
        }

        boolean learned = progress.isLearned(item.id);
        holder.badge.setText(learned ? R.string.badge_learned : R.string.badge_tap_to_learn);
        holder.badge.setBackgroundResource(learned
                ? R.drawable.bg_badge_done : R.drawable.bg_badge_todo);

        holder.favourite.setImageResource(progress.isFavourite(item.id)
                ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);

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

        holder.favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onToggleFavourite(item, v);
                holder.favourite.setImageResource(progress.isFavourite(item.id)
                        ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
            }
        });

        holder.badge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onToggleLearned(item, holder.itemView);
                boolean nowLearned = progress.isLearned(item.id);
                holder.badge.setText(nowLearned ? R.string.badge_learned : R.string.badge_tap_to_learn);
                holder.badge.setBackgroundResource(nowLearned
                        ? R.drawable.bg_badge_done : R.drawable.bg_badge_todo);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean open = holder.details.getVisibility() == View.VISIBLE;
                if (open) {
                    Anim.fadeOut(holder.details);
                } else {
                    Anim.fadeIn(holder.details);
                }
            }
        });

        holder.details.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return words.size();
    }
}
