package com.example.aplicationgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private ImageButton leaderboardButton, playGameButton, settingsButton, aboutUsButton;
    private static final String PREFS_NAME = "GamePrefs";
    private static final String KEY_USERS = "users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Initialize ImageButtons
        leaderboardButton = findViewById(R.id.leaderboard_button);
        playGameButton = findViewById(R.id.play_game_button);
        settingsButton = findViewById(R.id.settings_button);
        aboutUsButton = findViewById(R.id.about_us_button);

        // Leaderboard button
        leaderboardButton.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                Intent intent = new Intent(MenuActivity.this, LeaderboardActivity.class);
                startActivity(intent);
            } else {
                showRegisterDialog();
            }
        });

        // Play Game button
        playGameButton.setOnClickListener(v -> {
            LevelSelectionFragment fragment = new LevelSelectionFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Settings button
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // About Us button
        aboutUsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, AboutUsActivity.class);
            startActivity(intent);
        });
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.contains("nickname");
    }

    private void showRegisterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Daftar untuk Leaderboard");
        builder.setMessage("Masukkan nickname untuk masuk ke leaderboard!");

        final EditText input = new EditText(this);
        input.setHint("Masukkan nickname");
        builder.setView(input);

        builder.setPositiveButton("Daftar", (dialog, which) -> {
            String nickname = input.getText().toString().trim();
            if (!nickname.isEmpty()) {
                if (isNicknameUnique(nickname)) {
                    saveNickname(nickname);
                    initializeUserData(nickname);
                    Toast.makeText(MenuActivity.this, "Berhasil mendaftar sebagai " + nickname, Toast.LENGTH_SHORT).show();
                    LevelSelectionFragment fragment = new LevelSelectionFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Toast.makeText(MenuActivity.this, "Nickname sudah digunakan!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MenuActivity.this, "Nickname tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private boolean isNicknameUnique(String nickname) {
        List<UserScore> users = getUsersFromPrefs();
        for (UserScore user : users) {
            if (user.getNickname().equals(nickname)) {
                return false;
            }
        }
        return true;
    }

    private void saveNickname(String nickname) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("nickname", nickname);
        editor.apply();
        Log.d("MenuActivity", "Nickname saved: " + nickname);
    }

    private void initializeUserData(String nickname) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        List<UserScore> users = getUsersFromPrefs();
        users.add(new UserScore(nickname, 0, 0));
        saveUsersToPrefs(users);
        Log.d("MenuActivity", "User data initialized for: " + nickname);
    }

    private List<UserScore> getUsersFromPrefs() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String usersJson = prefs.getString(KEY_USERS, "[]");
        Gson gson = new Gson();
        Type type = new TypeToken<List<UserScore>>(){}.getType();
        List<UserScore> users = gson.fromJson(usersJson, type);
        return users != null ? users : new ArrayList<>();
    }

    private void saveUsersToPrefs(List<UserScore> users) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String usersJson = gson.toJson(users);
        editor.putString(KEY_USERS, usersJson);
        editor.apply();
    }
}