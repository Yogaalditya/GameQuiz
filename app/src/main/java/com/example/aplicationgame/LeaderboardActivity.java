package com.example.aplicationgame;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    private ListView leaderboardListView;
    private TextView statusTextView;
    private static final String PREFS_NAME = "GamePrefs";
    private static final String KEY_USERS = "users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        leaderboardListView = findViewById(R.id.leaderboard_list);
        statusTextView = findViewById(R.id.status_text);

        displayLeaderboard();
    }

    private void displayLeaderboard() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String nickname = prefs.getString("nickname", null);
        UserScore userScore = null;
        List<UserScore> users = getUsersFromPrefs();
        if (nickname != null) {
            for (UserScore user : users) {
                if (user.getNickname().equals(nickname)) {
                    userScore = user;
                    break;
                }
            }
        }

        // Sort users by points (descending) and time (ascending)
        Collections.sort(users, (u1, u2) -> {
            if (u2.getPoints() != u1.getPoints()) {
                return u2.getPoints() - u1.getPoints();
            }
            return Long.compare(u1.getTime(), u2.getTime());
        });

        // Get top 10
        List<UserScore> top10 = users.size() > 10 ? users.subList(0, 10) : users;
        ArrayList<String> leaderboardData = new ArrayList<>();
        int rank = 1;
        boolean userInTop10 = false;

        for (UserScore score : top10) {
            String entry = rank + ". " + score.getNickname() + " - Points: " + score.getPoints() +
                    ", Time: " + score.getTime() + "s";
            leaderboardData.add(entry);
            if (nickname != null && score.getNickname().equals(nickname)) {
                userInTop10 = true;
            }
            rank++;
        }

        // Display user status
        if (userScore == null || userScore.getPoints() == 0) {
            statusTextView.setText("Mainkan game untuk mendapatkan poin dan masuk leaderboard!");
            statusTextView.setVisibility(View.VISIBLE);
        } else if (!userInTop10) {
            statusTextView.setText("Kamu: " + nickname + " - Points: " + userScore.getPoints() +
                    ", Time: " + userScore.getTime() + "s (Peringkat: " + getUserRank(nickname, users) + ")");
            statusTextView.setVisibility(View.VISIBLE);
        } else {
            statusTextView.setVisibility(View.GONE);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, leaderboardData);
        leaderboardListView.setAdapter(adapter);
    }

    private int getUserRank(String nickname, List<UserScore> users) {
        int rank = 1;
        for (UserScore score : users) {
            if (score.getNickname().equals(nickname)) {
                return rank;
            }
            rank++;
        }
        return rank;
    }

    private List<UserScore> getUsersFromPrefs() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String usersJson = prefs.getString(KEY_USERS, "[]");
        Gson gson = new Gson();
        Type type = new TypeToken<List<UserScore>>(){}.getType();
        List<UserScore> users = gson.fromJson(usersJson, type);
        return users != null ? users : new ArrayList<>();
    }
}