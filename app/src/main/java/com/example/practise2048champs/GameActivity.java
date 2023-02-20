package com.example.practise2048champs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatTextView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.practise2048champs.enums.GameModes;
import com.example.practise2048champs.main.MainActivity;

public class GameActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private GameModes currentGameMode;
    private long currentScore;
    private long bestScore;
    private long changeValue;
    private AppCompatTextView currentScoreValueTextView;
    private AppCompatTextView bestScoreValueTextView;

    private void initialise() {
        sharedPreferences = getSharedPreferences("com.nerdcoredevelopment.game2048champsfinal", Context.MODE_PRIVATE);
        currentGameMode = GameModes.getGameModeEnum(
                getIntent().getIntExtra("gameMatrixColumns", 4),
                getIntent().getIntExtra("gameMatrixRows", 4),
                getIntent().getStringExtra("gameMode"));
        currentScore = sharedPreferences.getLong("currentScore" + " " + currentGameMode.getMode()
                + " " + currentGameMode.getDimensions(), 0L);
        bestScore = sharedPreferences.getLong("bestScore" + " " + currentGameMode.getMode()
                + " " + currentGameMode.getDimensions(), 0L);
        changeValue = 10101L;
        currentScoreValueTextView = findViewById(R.id.current_score_value_text_view);
        currentScoreValueTextView.setText(String.valueOf(currentScore));
        bestScoreValueTextView = findViewById(R.id.best_score_value_text_view);
        bestScoreValueTextView.setText(String.valueOf(bestScore));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Practise2048Champs);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        initialise();
    }

    public void backToMainActivity(View view) {
        Intent intent = new Intent(GameActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void increaseCurrentScore(View view) {
        currentScore += changeValue;
        sharedPreferences.edit().putLong("currentScore" + " " + currentGameMode.getMode()
                + " " + currentGameMode.getDimensions(), currentScore).apply();
        currentScoreValueTextView.setText(String.valueOf(currentScore));
    }

    public void decreaseCurrentScore(View view) {
        if (currentScore < changeValue) {
            Toast.makeText(this, "Current Score is too low to decrease", Toast.LENGTH_SHORT).show();
            return;
        }

        currentScore -= changeValue;
        sharedPreferences.edit().putLong("currentScore" + " " + currentGameMode.getMode()
                + " " + currentGameMode.getDimensions(), currentScore).apply();
        currentScoreValueTextView.setText(String.valueOf(currentScore));
    }

    public void increaseBestScore(View view) {
        bestScore += changeValue;
        sharedPreferences.edit().putLong("bestScore" + " " + currentGameMode.getMode()
                + " " + currentGameMode.getDimensions(), bestScore).apply();
        bestScoreValueTextView.setText(String.valueOf(bestScore));
    }

    public void decreaseBestScore(View view) {
        if (bestScore < changeValue) {
            Toast.makeText(this, "Best Score is too low to decrease", Toast.LENGTH_SHORT).show();
            return;
        }

        bestScore -= changeValue;
        sharedPreferences.edit().putLong("bestScore" + " " + currentGameMode.getMode()
                + " " + currentGameMode.getDimensions(), bestScore).apply();
        bestScoreValueTextView.setText(String.valueOf(bestScore));
    }
}