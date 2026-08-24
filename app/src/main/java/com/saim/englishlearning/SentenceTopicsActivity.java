package com.saim.englishlearning;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.saim.englishlearning.data.SentenceBank;
import com.saim.englishlearning.util.Anim;

import java.util.List;

/** Pick a place, then practise the sentences people actually say there. */
public class SentenceTopicsActivity extends AppCompatActivity {

    private static final int[] CIRCLES = {
            R.drawable.bg_circle_purple, R.drawable.bg_circle_teal, R.drawable.bg_circle_coral,
            R.drawable.bg_circle_amber, R.drawable.bg_circle_sky, R.drawable.bg_circle_pink,
            R.drawable.bg_circle_mint, R.drawable.bg_circle_plum, R.drawable.bg_circle_blue,
            R.drawable.bg_circle_orange
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence_topics);

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ((TextView) findViewById(R.id.textTopicsSub))
                .setText(getString(R.string.topics_sub, SentenceBank.size()));

        buildTopics();
    }

    private void buildTopics() {
        LinearLayout container = findViewById(R.id.layoutTopics);
        container.removeAllViews();

        float density = getResources().getDisplayMetrics().density;
        int pad = (int) (16 * density);
        List<SentenceBank.Topic> topics = SentenceBank.topics();

        for (int i = 0; i < topics.size(); i++) {
            final SentenceBank.Topic topic = topics.get(i);

            MaterialCardView card = new MaterialCardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.bottomMargin = (int) (10 * density);
            card.setLayoutParams(cardParams);
            card.setRadius(18 * density);
            card.setCardElevation(2 * density);
            card.setClickable(true);
            card.setFocusable(true);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(pad, pad, pad, pad);

            TextView emoji = new TextView(this);
            int size = (int) (48 * density);
            emoji.setLayoutParams(new LinearLayout.LayoutParams(size, size));
            emoji.setBackgroundResource(CIRCLES[i % CIRCLES.length]);
            emoji.setGravity(Gravity.CENTER);
            emoji.setText(topic.emoji);
            emoji.setTextSize(21f);
            row.addView(emoji);

            LinearLayout texts = new LinearLayout(this);
            texts.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            textParams.leftMargin = (int) (14 * density);
            texts.setLayoutParams(textParams);

            TextView title = new TextView(this);
            title.setText(topic.title);
            title.setTextSize(17f);
            title.setTypeface(title.getTypeface(), android.graphics.Typeface.BOLD);
            title.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            texts.addView(title);

            TextView detail = new TextView(this);
            detail.setText(topic.description);
            detail.setTextSize(13f);
            detail.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            texts.addView(detail);
            row.addView(texts);

            TextView count = new TextView(this);
            count.setText(String.valueOf(SentenceBank.countInTopic(topic.key)));
            count.setTextSize(13f);
            count.setTypeface(count.getTypeface(), android.graphics.Typeface.BOLD);
            count.setBackgroundResource(R.drawable.bg_pill_light);
            count.setPadding(pad - 4, pad / 2 - 2, pad - 4, pad / 2 - 2);
            count.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            row.addView(count);

            card.addView(row);
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Anim.pop(v);
                    Intent intent = new Intent(SentenceTopicsActivity.this,
                            SentenceBuilderActivity.class);
                    intent.putExtra(SentenceBuilderActivity.EXTRA_TOPIC, topic.key);
                    startActivity(intent);
                }
            });

            container.addView(card);
            Anim.enter(card, i * 45L);
        }
    }
}
