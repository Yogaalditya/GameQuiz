package com.example.aplicationgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import pl.droidsonroids.gif.GifImageView;

public class GameOverActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "GamePrefs";
    private static final String KEY_FONT_SIZE = "fontSize";
    private static final String KEY_THEME = "theme";
    private MediaPlayer resultSound;
    private Button playAgainButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        applySettings();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);

        playAgainButton = findViewById(R.id.play_again_button);
        GifImageView gifImageView = findViewById(R.id.gif_result);
        TextView scoreText = findViewById(R.id.final_score_text);
        backButton = findViewById(R.id.back_button);
        int score = getIntent().getIntExtra("SCORE", 0);
        scoreText.setText(getString(R.string.final_score, score));

        backButton.setOnClickListener(v -> finish());


        playAgainButton.setOnClickListener(v -> {
            if (resultSound != null) {
                resultSound.stop();
            }
            startActivity(new Intent(GameOverActivity.this, MainActivity.class));
            finish();
        });

        // Set GIF dan suara berdasarkan skor
        if (score >= 0 && score < 50) {
            gifImageView.setImageResource(R.drawable.bad_score);
            resultSound = MediaPlayer.create(this, R.raw.game_over);
        } else if (score >= 50 && score < 100) {
            gifImageView.setImageResource(R.drawable.good_score);
            resultSound = MediaPlayer.create(this, R.raw.good_score_sound);
        } else if (score == 100) {
            gifImageView.setImageResource(R.drawable.perfect_score);
            resultSound = MediaPlayer.create(this, R.raw.perfect_score_sound);
        }

        // play sound
        if (resultSound != null) {
            resultSound.start();
        }

        // Terapkan ukuran font
        String fontSize = sharedPreferences.getString(KEY_FONT_SIZE, "medium");
        float size;
        switch (fontSize) {
            case "small":
                size = 18f;
                break;
            case "large":
                size = 30f;
                break;
            case "medium":
            default:
                size = 24f;
                break;
        }
        scoreText.setTextSize(size);
    }

    private void applySettings() {
        // Terapkan Tema
        int theme = sharedPreferences.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(theme);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resultSound != null) {
            resultSound.release();
            resultSound = null;
        }
    }
}