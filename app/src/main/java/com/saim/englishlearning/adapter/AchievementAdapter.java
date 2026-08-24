package com.saim.englishlearning.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saim.englishlearning.R;
import com.saim.englishlearning.model.Achievement;
import com.saim.englishlearning.util.Anim;

import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.Holder> {

    private final List<Achievement> items;

    public AchievementAdapter(List<Achievement> items) {
        this.items = items;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        final TextView emoji;
        final TextView title;
        final TextView detail;
        final TextView status;
        final ProgressBar bar;

        Holder(View item) {
            super(item);
            emoji = item.findViewById(R.id.textAchievementEmoji);
            title = item.findViewById(R.id.textAchievementTitle);
            detail = item.findViewById(R.id.textAchievementDetail);
            status = item.findViewById(R.id.textAchievementStatus);
            bar = item.findViewById(R.id.progressAchievement);
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_achievement, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Achievement item = items.get(position);
        holder.emoji.setText(item.emoji);
        holder.title.setText(item.title);
        holder.detail.setText(item.detail);
        holder.bar.setMax(100);
        holder.bar.setProgress(item.percent());
        holder.emoji.setAlpha(item.unlocked ? 1f : 0.35f);

        holder.status.setText(item.unlocked
                ? holder.itemView.getContext().getString(R.string.achievement_done)
                : item.progress + " / " + item.target);
        holder.status.setBackgroundResource(item.unlocked
                ? R.drawable.bg_badge_done : R.drawable.bg_badge_todo);

        Anim.enter(holder.itemView, Math.min(position, 8) * 35L);
        if (item.unlocked) Anim.pop(holder.emoji);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
