package com.saim.englishlearning;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.util.Anim;

import java.util.ArrayList;
import java.util.List;

/** A short guided tour shown once after the first launch. */
public class TourActivity extends AppCompatActivity {

    private static class Page {
        final String emoji;
        final String title;
        final String body;

        Page(String emoji, String title, String body) {
            this.emoji = emoji;
            this.title = title;
            this.body = body;
        }
    }

    private final List<Page> pages = new ArrayList<>();
    private ViewPager2 pager;
    private LinearLayout dots;
    private Button next;
    private Button skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);

        buildPages();

        pager = findViewById(R.id.pagerTour);
        dots = findViewById(R.id.layoutDots);
        next = findViewById(R.id.buttonNext);
        skip = findViewById(R.id.buttonSkip);

        pager.setAdapter(new TourAdapter());
        buildDots();

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDots(position);
                next.setText(position == pages.size() - 1
                        ? R.string.tour_finish : R.string.tour_next);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                int current = pager.getCurrentItem();
                if (current < pages.size() - 1) {
                    pager.setCurrentItem(current + 1, true);
                } else {
                    finishTour();
                }
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishTour();
            }
        });
    }

    private void buildPages() {
        pages.add(new Page("🦉", getString(R.string.tour1_title), getString(R.string.tour1_body)));
        pages.add(new Page("📚", getString(R.string.tour2_title), getString(R.string.tour2_body)));
        pages.add(new Page("✏️", getString(R.string.tour3_title), getString(R.string.tour3_body)));
        pages.add(new Page("💬", getString(R.string.tour4_title), getString(R.string.tour4_body)));
        pages.add(new Page("⏳", getString(R.string.tour5_title), getString(R.string.tour5_body)));
        pages.add(new Page("🌐", getString(R.string.tour6_title), getString(R.string.tour6_body)));
    }

    private void buildDots() {
        dots.removeAllViews();
        int size = (int) (9 * getResources().getDisplayMetrics().density);
        int margin = (int) (5 * getResources().getDisplayMetrics().density);
        for (int i = 0; i < pages.size(); i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(margin, 0, margin, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(R.drawable.bg_dot_inactive);
            dots.addView(dot);
        }
        updateDots(0);
    }

    private void updateDots(int active) {
        for (int i = 0; i < dots.getChildCount(); i++) {
            View dot = dots.getChildAt(i);
            dot.setBackgroundResource(i == active
                    ? R.drawable.bg_dot_active : R.drawable.bg_dot_inactive);
            dot.animate().scaleX(i == active ? 1.3f : 1f)
                    .scaleY(i == active ? 1.3f : 1f).setDuration(200).start();
        }
    }

    private void finishTour() {
        new ProgressManager(this).setTourDone(true);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private class TourAdapter extends RecyclerView.Adapter<TourAdapter.Holder> {

        class Holder extends RecyclerView.ViewHolder {
            final TextView emoji;
            final TextView title;
            final TextView body;

            Holder(View item) {
                super(item);
                emoji = item.findViewById(R.id.textTourEmoji);
                title = item.findViewById(R.id.textTourTitle);
                body = item.findViewById(R.id.textTourBody);
            }
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_tour_page, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            Page page = pages.get(position);
            holder.emoji.setText(page.emoji);
            holder.title.setText(page.title);
            holder.body.setText(page.body);
            Anim.enter(holder.emoji, 0);
            Anim.enter(holder.title, 80);
            Anim.enter(holder.body, 160);
        }

        @Override
        public int getItemCount() {
            return pages.size();
        }
    }
}
