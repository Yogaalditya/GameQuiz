package com.example.aplicationgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView questionText, timerText, scoreText, doublePointsNotif, levelText;
    private Button optionA, optionB, optionC, optionD, submitButton, detailButton;
    private ImageButton hintButton;
    private List<Question> allQuestions;
    private int score = 0;
    private CountDownTimer timer;
    private boolean isAnswerSubmitted = false;
    private  static final long TIME_PER_QUESTION = 120000; // 2 minutes
    private static final long DOUBLE_POINTS_TIME = 60000; // 1 minute
    private boolean isWithinDoublePoints = true;
    private String selectedAnswer = null;
    private Button selectedButton = null;
    private int currentHintIndex = 0;
    private MediaPlayer correctSound, wrongSound;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "GamePrefs";
    private static final String KEY_FONT_SIZE = "fontSize";
    private static final String KEY_THEME = "theme";
    private static final String KEY_USERS = "users";
    private int currentLevel = 1;
    private Question currentQuestion;
    private long levelStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences userPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String nickname = userPrefs.getString("nickname", null);

        if (nickname == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Nickname Diperlukan")
                    .setMessage("Silakan masukkan nickname untuk melanjutkan")
                    .setPositiveButton("OK", (dialog, which) -> {
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .setCancelable(false)
                    .show();
            return;
        }
        applySettings();
        setContentView(R.layout.activity_main);

        // Get selected level from intent
        Intent intent = getIntent();
        currentLevel = intent.getIntExtra("SELECTED_LEVEL", 1);

        correctSound = MediaPlayer.create(this, R.raw.correct);
        wrongSound = MediaPlayer.create(this, R.raw.incorrect);

        questionText = findViewById(R.id.question_text);
        timerText = findViewById(R.id.timer_text);
        scoreText = findViewById(R.id.score_text);
        optionA = findViewById(R.id.option_a);
        optionB = findViewById(R.id.option_b);
        optionC = findViewById(R.id.option_c);
        optionD = findViewById(R.id.option_d);
        submitButton = findViewById(R.id.submit_button);
        hintButton = findViewById(R.id.hint_button);
        detailButton = findViewById(R.id.detail_button);
        doublePointsNotif = findViewById(R.id.double_points_notif);
        levelText = findViewById(R.id.level_text);

        applyFontSize();
        submitButton.setText(R.string.submit);
        detailButton.setText(R.string.detail);

        allQuestions = new ArrayList<>();
        initializeQuestions();
        loadLevel(currentLevel);

        optionA.setOnClickListener(v -> selectOption(optionA));
        optionB.setOnClickListener(v -> selectOption(optionB));
        optionC.setOnClickListener(v -> selectOption(optionC));
        optionD.setOnClickListener(v -> selectOption(optionD));
        submitButton.setOnClickListener(v -> submitAnswer());
        hintButton.setOnClickListener(v -> showNextHint());
        detailButton.setOnClickListener(v -> showDetailFragment());
    }

    private void applySettings() {
        int theme = sharedPreferences.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(theme);
    }

    private void applyFontSize() {
        String fontSize = sharedPreferences.getString(KEY_FONT_SIZE, "medium");
        float size;
        switch (fontSize) {
            case "small":
                size = 14f;
                break;
            case "large":
                size = 22f;
                break;
            case "medium":
            default:
                size = 18f;
                break;
        }
        questionText.setTextSize(size);
        timerText.setTextSize(size);
        scoreText.setTextSize(size);
        optionA.setTextSize(size);
        optionB.setTextSize(size);
        optionC.setTextSize(size);
        optionD.setTextSize(size);
        submitButton.setTextSize(size);
        detailButton.setTextSize(size);
    }

    private void initializeQuestions() {
        // Same question data as before (Level 1 example)
        allQuestions.add(new Question(
                "Pada malam 22 Juni 2025, Profesor Kartika ditemukan tewas di ruang baca universitas setelah kuliah terakhirnya selesai. Pintu ruang baca terkunci dari dalam dan hanya jendela besar menganga. Di samping tubuhnya tergeletak sebuah buku catatan terbuka dengan tulisan tinta hitam terputus:\n" +
                        "“…saklar di bawah…”\n" +
                        "Di lantai, ada tumpahan air setengah gelas, jejak sapuan tangan berdebu di meja, dan seutas tali kecil terselip di bawah karpet.\n" +
                        "Empat Tersangka\n" +
                        "A. Rahmat, Asisten Laboratorium\n" +
                        "B. Dina, Mahasiswa Bimbingan\n" +
                        "C. Anton, Rekan Dosen\n" +
                        "D. Sari, Pustakawati\n" +
                        "Siapakah pembunuh Profesor Kartika? Jelaskan alasan logis berdasarkan petunjuk di atas.",
                "Anton, Rekan Dosen",
                new String[]{"Rahmat, Asisten Laboratorium", "Dina, Mahasiswa Bimbingan", "Anton, Rekan Dosen", "Sari, Pustakawati"},
                "Pulpen & Tinta\n" +
                        "Tulisan “…saklar di bawah…” menggunakan tinta hitam yang sama dengan pulpen Anton—korban tidak memiliki pulpen jenis itu, jadi tulisan kemungkinan dibuat Anton.\n" +
                        "Sidik Jari & Debu Saklar\n" +
                        "Sidik jari berdebu menunjukkan seseorang menyalakan saklar lalu menyentuh meja. Hanya Anton yang masuk dan butuh menyalakan lampu saat membawa map tebal.\n" +
                        "Air & Tali = Pengalihan\n" +
                        "Tumpahan air dan tali kecil hanya pengalihan (red herring) untuk menyalahkan Sari—tidak relevan dengan tanda-tanda utama.\n" +
                        "Alibi Lemah\n" +
                        "Anton terlihat keluar pukul 20:20, waktu yang cocok dengan kematian korban. Alibinya tidak diperkuat saksi.\n" +
                        "Motif Kuat\n" +
                        "Anton hendak dicoret dari publikasi jurnal oleh Profesor Kartika—cukup alasan untuk membunuh demi reputasi akademik.",
                "Petunjuk\n" +
                        "1. Waktu & Alibi\n" +
                        "Rahmat: Mengaku mengisi ulang tabung gas di laboratorium pukul 20:00–20:45; beberapa rekan mendengar suara letupan kecil.\n" +
                        "Dina: Meninggalkan ruang baca untuk membeli kopi pukul 19:50 dan kembali pukul 20:10; struk kopi di sakunya hanya satu.\n" +
                        "Anton: Terlihat keluar dari ruang baca pukul 20:20; saksi melihat ia membawa map tebal.\n" +
                        "Sari: Patroli perpustakaan mencatat ia sedang memeriksa rak film dokumenter pukul 20:00–20:30.\n" +
                        "2. Jejak di Lokasi\n" +
                        "Air Tumpah: Setengah gelas masih berisi air putih di lantai dekat rak film.\n" +
                        "Jejak Debu: Sidik jari samar di atas meja—ditemukan bekas debu papan saklar lampu.\n" +
                        "Tali Kecil: Tali tambang tipis, seperti tali penganjal buku besar.\n" +
                        "3. Alat Tulis & Buku Catatan\n" +
                        "Buku catatan tergeletak di lantai; tinta hitam sama dengan pulpen Anton yang setiap pagi ia taruh di saku jas.\n" +
                        "4. Tulisan Robek\n" +
                        "Tulisan “…saklar di bawah…” diduga komentar korban:\n" +
                        "“…seklar di bawah meja saksi…”\n" +
                        "“…taruh pemantik di bawah…”\n" +
                        "5. Motif\n" +
                        "Rahmat: Ingin membocorkan riset rahasia profesor ke industri farmasi.\n" +
                        "Dina: Nilai skripsinya turun drastis setelah koreksi terakhir.\n" +
                        "Anton: Berseteru soal publikasi jurnal internasional.\n" +
                        "Sari: Merasa diabaikan saat beasiswa perpustakaan dicabut.",
                Arrays.asList(
                        "Hint 1: Perhatikan tali kecil—untuk apa dan siapa yang biasa membawanya?",
                        "Hint 2: Tinta pulpen menunjukkan siapa yang sempat menulis sekilas di buku catatan.",
                        "Hint 3: Jejak debu di saklar lampu: kenapa korban menulis “saklar di bawah…” sebelum tewas?",
                        "Hint 4: Air tumpah dekat rak film—siapa yang mungkin membawa gelas itu?"
                ),
                1 // Level 1
        ));
    }

    private void loadLevel(int level) {
        for (Question q : allQuestions) {
            if (q.getLevel() == level) {
                currentQuestion = q;
                break;
            }
        }
        levelText.setText("Level " + level);
        displayQuestion();
    }

    private void displayQuestion() {
        if (currentQuestion != null) {
            questionText.setText(currentQuestion.getQuestionText());
            optionA.setText(currentQuestion.getOptions()[0]);
            optionB.setText(currentQuestion.getOptions()[1]);
            optionC.setText(currentQuestion.getOptions()[2]);
            optionD.setText(currentQuestion.getOptions()[3]);
            scoreText.setText(getString(R.string.score_label, score));
            isAnswerSubmitted = false;
            isWithinDoublePoints = true;
            selectedAnswer = null;
            selectedButton = null;
            currentHintIndex = 0;

            showDoublePointsNotification(false);
            doublePointsNotif.clearAnimation();
            hintButton.setVisibility(View.GONE);
            hintButton.setEnabled(false);
            resetButtonColors();
            levelStartTime = System.currentTimeMillis();
            startTimer();
        } else {
            endGame();
        }
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }

        showDoublePointsNotification(true);

        timer = new CountDownTimer(TIME_PER_QUESTION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                timerText.setText(getString(R.string.time_label, secondsLeft));

                if (secondsLeft > 60) {
                    doublePointsNotif.setText("POINT 2x AKTIF! (" + (secondsLeft - 60) + "s)");
                } else if (secondsLeft == 60) {
                    showDoublePointsNotification(false);
                    isWithinDoublePoints = false;
                    showHintButtonWithBlinkEffect();
                }
            }

            @Override
            public void onFinish() {
                if (!isAnswerSubmitted) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.wrong_title)
                            .setMessage(R.string.time_up_message)
                            .setPositiveButton(R.string.next, (dialog, which) -> {
                                if (currentLevel < 5) {
                                    currentLevel++;
                                    loadLevel(currentLevel);
                                } else {
                                    endGame();
                                }
                            })
                            .setCancelable(false)
                            .show();
                }
            }
        }.start();
    }

    private void completeLevel(int level, int points, long timeInSeconds) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String nickname = prefs.getString("nickname", null);
        if (nickname != null) {
            List<UserScore> users = getUsersFromPrefs();
            for (UserScore user : users) {
                if (user.getNickname().equals(nickname)) {
                    user.setPoints(user.getPoints() + points);
                    user.setTime(user.getTime() + timeInSeconds);
                    break;
                }
            }
            saveUsersToPrefs(users);
        }

        // Update highest completed level
        int highestCompletedLevel = sharedPreferences.getInt("highestCompletedLevel", 0);
        if (level > highestCompletedLevel) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("highestCompletedLevel", level);
            editor.apply();
        }
    }

    private void showDoublePointsNotification(boolean show) {
        if (show) {
            doublePointsNotif.setVisibility(View.VISIBLE);
            AlphaAnimation blinkAnimation = new AlphaAnimation(0.3f, 1.0f);
            blinkAnimation.setDuration(800);
            blinkAnimation.setRepeatMode(Animation.REVERSE);
            blinkAnimation.setRepeatCount(Animation.INFINITE);
            doublePointsNotif.startAnimation(blinkAnimation);
        } else {
            doublePointsNotif.clearAnimation();
            doublePointsNotif.setVisibility(View.GONE);
        }
    }

    private void showHintButtonWithBlinkEffect() {
        hintButton.setVisibility(View.VISIBLE);
        AlphaAnimation blinkAnimation = new AlphaAnimation(0.2f, 1.0f);
        blinkAnimation.setDuration(500);
        blinkAnimation.setRepeatMode(Animation.REVERSE);
        blinkAnimation.setRepeatCount(3);

        hintButton.startAnimation(blinkAnimation);

        blinkAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                hintButton.setEnabled(true);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                hintButton.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void selectOption(Button button) {
        if (isAnswerSubmitted) return;
        resetButtonColors();
        selectedButton = button;
        selectedAnswer = button.getText().toString();
        selectedButton.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.darker_gray));
    }

    private void resetButtonColors() {
        optionA.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.blue));
        optionB.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
        optionC.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.yellow));
        optionD.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));
    }

    private void submitAnswer() {
        if (isAnswerSubmitted) return;
        isAnswerSubmitted = true;
        timer.cancel();

        if (selectedAnswer == null) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.warning_title)
                    .setMessage(R.string.select_answer_warning)
                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                        isAnswerSubmitted = false;
                        startTimer();
                    })
                    .setCancelable(false)
                    .show();
            return;
        }

        if (selectedAnswer.equals(currentQuestion.getCorrectAnswer())) {
            handleCorrectAnswer();
        } else {
            if (wrongSound != null) {
                wrongSound.start();
            }
            new AlertDialog.Builder(this)
                    .setTitle(R.string.wrong_title)
                    .setMessage(getString(R.string.wrong_message, currentQuestion.getCorrectAnswer(), currentQuestion.getExplanation()))
                    .setPositiveButton(R.string.next, (dialog, which) -> {
                        if (currentLevel < 5) {
                            currentLevel++;
                            loadLevel(currentLevel);
                        } else {
                            endGame();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    private void handleCorrectAnswer() {
        int points = isWithinDoublePoints ? 20 : 10;
        score += points;

        if (correctSound != null) {
            correctSound.start();
        }

        long timeInSeconds = (System.currentTimeMillis() - levelStartTime) / 1000;

        completeLevel(currentLevel, points, timeInSeconds);

        if (currentLevel < 5) {
            new AlertDialog.Builder(this)
                    .setTitle("Level Complete!")
                    .setMessage("Anda mendapatkan " + points + " poin! Lanjut ke level berikutnya?")
                    .setPositiveButton("Ya", (d, w) -> {
                        currentLevel++;
                        loadLevel(currentLevel);
                    })
                    .setCancelable(false)
                    .show();
        } else {
            endGame();
        }
    }

    private void showNextHint() {
        if (currentQuestion != null) {
            List<String> hints = currentQuestion.getHints();
            if (currentHintIndex < hints.size()) {
                String hint = hints.get(currentHintIndex);
                new AlertDialog.Builder(this)
                        .setTitle(R.string.hint)
                        .setMessage(hint)
                        .setPositiveButton(R.string.ok, null)
                        .show();
                currentHintIndex++;
                if (currentHintIndex >= hints.size()) {
                    hintButton.setEnabled(false);
                }
            }
        }
    }

    private void showDetailFragment() {
        if (currentQuestion != null) {
            String detail = currentQuestion.getDetail();
            DetailFragment detailFragment = DetailFragment.newInstance(detail);
            detailFragment.show(getSupportFragmentManager(), "detail");
        }
    }

    private void endGame() {
        Intent intent = new Intent(MainActivity.this, GameOverActivity.class);
        intent.putExtra("SCORE", score);
        startActivity(intent);
        finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (correctSound != null) {
            correctSound.release();
            correctSound = null;
        }
        if (wrongSound != null) {
            wrongSound.release();
            wrongSound = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyFontSize();
    }
}