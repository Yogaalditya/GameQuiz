package com.example.aplicationgame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class LevelSelectionFragment extends Fragment {

    private static final int TOTAL_LEVELS = 10; // Total level yang ditampilkan
    private static final int IMPLEMENTED_LEVELS = 5; // Level yang sudah diimplementasikan

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level_selection, container, false);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        int highestCompletedLevel = sharedPreferences.getInt("highestCompletedLevel", 0);

        GridLayout gridLayout = view.findViewById(R.id.level_grid);
        setupLevelGrid(gridLayout, highestCompletedLevel);

        return view;
    }

    private void setupLevelGrid(GridLayout gridLayout, int highestCompletedLevel) {
        for (int i = 1; i <= TOTAL_LEVELS; i++) {
            View levelView = LayoutInflater.from(getContext()).inflate(R.layout.item_level, null);

            Button levelButton = levelView.findViewById(R.id.level_button);
            TextView levelText = levelView.findViewById(R.id.level_text);
            ImageView levelOverlay = levelView.findViewById(R.id.level_overlay);

            final int level = i;

            if (level <= IMPLEMENTED_LEVELS) {
                levelText.setText(String.valueOf(level));
                if (level <= highestCompletedLevel) {
                    // Level selesai: angka + hijau samar + ikon centang
                    levelButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.level_completed));
                    levelOverlay.setImageResource(R.drawable.ic_checkmark);
                    levelOverlay.setVisibility(View.VISIBLE);
                    levelButton.setEnabled(true);
                } else if (level == highestCompletedLevel + 1 || level == 1) {
                    // Level terbuka: hanya angka (level 1 selalu terbuka)
                    levelButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.level_unlocked));
                    levelOverlay.setVisibility(View.GONE);
                    levelButton.setEnabled(true);
                } else {
                    // Level terkunci: angka + gembok
                    levelButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.level_locked));
                    levelOverlay.setImageResource(R.drawable.ic_lock);
                    levelOverlay.setVisibility(View.VISIBLE);
                    levelButton.setEnabled(false);
                }
            } else {
                // Level "Coming Soon"
                levelText.setText("Coming Soon");
                levelButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.level_coming_soon));
                levelOverlay.setVisibility(View.GONE);
                levelButton.setEnabled(false);
            }

            levelButton.setOnClickListener(v -> {
                if (level <= IMPLEMENTED_LEVELS && (level <= highestCompletedLevel + 1 || level == 1)) {
                    startLevel(level);
                }
            });

            // Atur posisi di GridLayout
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED);
            params.setMargins(10, 10, 10, 10);
            levelView.setLayoutParams(params);

            gridLayout.addView(levelView);
        }
    }

    private void startLevel(int level) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("SELECTED_LEVEL", level);
        startActivity(intent);
    }
}