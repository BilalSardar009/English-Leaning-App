package com.saim.englishlearning.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saim.englishlearning.R;
import com.saim.englishlearning.util.Anim;

import java.util.List;

public class UnitAdapter extends RecyclerView.Adapter<UnitAdapter.Holder> {

    public static class Unit {
        public final int number;
        public final String firstWord;
        public final String lastWord;
        public final int done;
        public final int total;
        public final List<Integer> wordIds;

        public Unit(int number, String firstWord, String lastWord, int done, int total,
                    List<Integer> wordIds) {
            this.number = number;
            this.firstWord = firstWord;
            this.lastWord = lastWord;
            this.done = done;
            this.total = total;
            this.wordIds = wordIds;
        }
    }

    public interface Listener {
        void onUnitClicked(Unit unit);
    }

    private final List<Unit> units;
    private final Listener listener;

    public UnitAdapter(List<Unit> units, Listener listener) {
        this.units = units;
        this.listener = listener;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView range;
        final TextView count;
        final TextView emoji;
        final ProgressBar bar;

        Holder(View item) {
            super(item);
            title = item.findViewById(R.id.textUnitTitle);
            range = item.findViewById(R.id.textUnitRange);
            count = item.findViewById(R.id.textUnitCount);
            emoji = item.findViewById(R.id.textUnitEmoji);
            bar = item.findViewById(R.id.progressUnit);
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_unit, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        final Unit unit = units.get(position);
        holder.title.setText(holder.itemView.getContext()
                .getString(R.string.unit_title, unit.number));
        holder.range.setText(unit.firstWord + " → " + unit.lastWord);
        holder.count.setText(unit.done + "/" + unit.total);
        holder.bar.setMax(unit.total);
        holder.bar.setProgress(unit.done);

        if (unit.done == 0) {
            holder.emoji.setText("🌱");
        } else if (unit.done < unit.total) {
            holder.emoji.setText("🌿");
        } else {
            holder.emoji.setText("🌳");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                listener.onUnitClicked(unit);
            }
        });

        Anim.enter(holder.itemView, Math.min(position, 8) * 40L);
    }

    @Override
    public int getItemCount() {
        return units.size();
    }
}
