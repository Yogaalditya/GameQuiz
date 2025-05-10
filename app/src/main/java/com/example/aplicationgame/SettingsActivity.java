package com.example.aplicationgame;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    private Spinner languageSpinner, fontSizeSpinner;
    private Switch themeSwitch;
    private Button saveButton, backButton;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "GamePrefs";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_FONT_SIZE = "fontSize";
    private static final String KEY_THEME = "theme";

    // Variabel untuk menyimpan nilai awal pengaturan
    private String initialLanguage;
    private String initialFontSize;
    private boolean initialDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        languageSpinner = findViewById(R.id.language_spinner);
        fontSizeSpinner = findViewById(R.id.font_size_spinner);
        themeSwitch = findViewById(R.id.theme_switch);
        saveButton = findViewById(R.id.save_button);
        backButton = findViewById(R.id.back_button);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Simpan nilai awal pengaturan untuk perbandingan
        initialLanguage = sharedPreferences.getString(KEY_LANGUAGE, "id");
        initialFontSize = sharedPreferences.getString(KEY_FONT_SIZE, "medium");
        initialDarkMode = sharedPreferences.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                == AppCompatDelegate.MODE_NIGHT_YES;

        // Inisialisasi Spinner untuk Language
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(this,
                R.array.language_options, android.R.layout.simple_spinner_item);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(languageAdapter);
        languageSpinner.setSelection(initialLanguage.equals("id") ? 0 : 1);

        // Inisialisasi Spinner untuk Font Size
        ArrayAdapter<CharSequence> fontSizeAdapter = ArrayAdapter.createFromResource(this,
                R.array.font_size_options, android.R.layout.simple_spinner_item);
        fontSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontSizeSpinner.setAdapter(fontSizeAdapter);
        switch (initialFontSize) {
            case "small":
                fontSizeSpinner.setSelection(0);
                break;
            case "medium":
                fontSizeSpinner.setSelection(1);
                break;
            case "large":
                fontSizeSpinner.setSelection(2);
                break;
        }

        // Inisialisasi Switch untuk Dark Mode
        themeSwitch.setChecked(initialDarkMode);

        // Listener untuk mendeteksi perubahan
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkForChanges();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        fontSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkForChanges();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> checkForChanges());

        // Simpan Pengaturan
        saveButton.setOnClickListener(v -> saveSettings());

        // Kembali tanpa menyimpan
        backButton.setOnClickListener(v -> finish());
    }

    private void checkForChanges() {
        // Ambil nilai saat ini
        String selectedLanguage = languageSpinner.getSelectedItemPosition() == 0 ? "id" : "en";
        String selectedFontSize = fontSizeSpinner.getSelectedItem().toString().toLowerCase();
        boolean selectedDarkMode = themeSwitch.isChecked();

        // Bandingkan dengan nilai awal
        boolean hasChanges = !selectedLanguage.equals(initialLanguage) ||
                !selectedFontSize.equals(initialFontSize) ||
                selectedDarkMode != initialDarkMode;

        // Tampilkan atau sembunyikan tombol Save berdasarkan perubahan
        saveButton.setVisibility(hasChanges ? View.VISIBLE : View.GONE);
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Simpan Bahasa
        String selectedLanguage = languageSpinner.getSelectedItemPosition() == 0 ? "id" : "en";
        editor.putString(KEY_LANGUAGE, selectedLanguage);
        setLocale(selectedLanguage);

        // Simpan Ukuran Font
        String selectedFontSize = fontSizeSpinner.getSelectedItem().toString().toLowerCase();
        editor.putString(KEY_FONT_SIZE, selectedFontSize);

        // Simpan Tema
        int selectedTheme = themeSwitch.isChecked() ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        editor.putInt(KEY_THEME, selectedTheme);
        AppCompatDelegate.setDefaultNightMode(selectedTheme);

        editor.apply();
        finish(); // Kembali ke MainActivity
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources res = getResources();
        Configuration config = res.getConfiguration();
        config.setLocale(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());
    }
}