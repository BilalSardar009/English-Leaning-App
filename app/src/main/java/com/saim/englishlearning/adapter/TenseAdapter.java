package com.saim.englishlearning.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saim.englishlearning.R;
import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.model.Tense;
import com.saim.englishlearning.util.Anim;

import java.util.List;

public class TenseAdapter extends RecyclerView.Adapter<TenseAdapter.Holder> {

    public interface Listener {
        void onTenseClicked(int index);
    }

    private static final String[] EMOJI = {
            "☀️", "🏃", "✅", "⏱️", "🌙", "🎬", "📜", "🕰️", "🚀", "🛫", "🎯", "🌈"
    };

    private final List<Tense> tenses;
    private final ProgressManager progress;
    private final Listener listener;

    public TenseAdapter(List<Tense> tenses, ProgressManager progress, Listener listener) {
        this.tenses = tenses;
        this.progress = progress;
        this.listener = listener;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView urdu;
        final TextView formula;
        final TextView emoji;
        final TextView best;
        final ProgressBar bar;

        Holder(View item) {
            super(item);
            name = item.findViewById(R.id.textTenseItemName);
            urdu = item.findViewById(R.id.textTenseItemUrdu);
            formula = item.findViewById(R.id.textTenseItemFormula);
            emoji = item.findViewById(R.id.textTenseItemEmoji);
            best = item.findViewById(R.id.textTenseItemBest);
            bar = item.findViewById(R.id.progressTenseItem);
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tense, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {
        Tense tense = tenses.get(position);
        holder.name.setText(tense.name);
        holder.formula.setText(tense.formula);
        holder.emoji.setText(EMOJI[position % EMOJI.length]);

        if (progress.isUrduEnabled()) {
            holder.urdu.setVisibility(View.VISIBLE);
            holder.urdu.setText(tense.urduName);
        } else {
            holder.urdu.setVisibility(View.GONE);
        }

        int best = progress.getTenseBest(position);
        int total = tense.practice.size();
        holder.bar.setMax(Math.max(1, total));
        holder.bar.setProgress(best);
        holder.best.setText(best > 0
                ? holder.itemView.getContext().getString(R.string.tense_item_best, best, total)
                : holder.itemView.getContext().getString(R.string.tense_not_practised));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                listener.onTenseClicked(position);
            }
        });

        Anim.enter(holder.itemView, Math.min(position, 8) * 40L);
    }

    @Override
    public int getItemCount() {
        return tenses.size();
    }
}
