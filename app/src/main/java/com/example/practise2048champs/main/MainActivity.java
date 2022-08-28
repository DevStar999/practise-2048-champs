package com.example.practise2048champs.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.example.practise2048champs.GameActivity;
import com.example.practise2048champs.R;
import com.example.practise2048champs.dialogs.ArrivingFeatureDialog;
import com.example.practise2048champs.dialogs.GameExitDialog;
import com.example.practise2048champs.enums.GameModes;
import com.example.practise2048champs.enums.GameStates;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    // Utility class for MainActivity
    private MainManager mainManager;

    // Attributes for determining game state
    private SharedPreferences sharedPreferences;

    // Using the GameModes enum for storing the game mode selected by the user
    private GameModes currentGameMode;

    // Attributes for determining game mode
    private List<String> allGameModes;
    private AppCompatTextView gameModeTextView;

    // Attributes for determining game size
    private List<String> allCurrentGameSizes;
    private AppCompatTextView gameSizeTextView;

    // Game modes customisation UI elements
    private LottieAnimationView gamePreviewSpotLightLottie;
    private AppCompatImageView gamePreviewImageView;
    private LottieAnimationView startGameLottie;
    private AppCompatButton startGameButton;
    private AppCompatImageView modeLeft;
    private AppCompatImageView modeRight;
    private AppCompatImageView sizeLeft;
    private AppCompatImageView sizeRight;

    private void initialise() {
        // Initialising MainManager
        mainManager = new MainManager(MainActivity.this);

        // Initialising sharedPreferences
        sharedPreferences = getSharedPreferences("com.nerdcoredevelopment.game2048champsfinal", Context.MODE_PRIVATE);

        // The default game mode
        currentGameMode = GameModes.getGameModeEnum(
                sharedPreferences.getInt("gameMatrixColumns", 4),
                sharedPreferences.getInt("gameMatrixRows", 4),
                sharedPreferences.getString("gameMode", "SQUARE"));

        // Initialising allGameModes List and gameModeTextView TextView
        allGameModes = GameModes.getAllGameModes();
        gameModeTextView = findViewById(R.id.game_mode_text_view);
        gameModeTextView.setText(currentGameMode.getMode());
        mainManager.updateModeBrowseIcons(currentGameMode.getMode(), allGameModes);

        // Initialising allCurrentGameSizes List and gameSizeTextView TextView
        allCurrentGameSizes = GameModes.getAllGameVariantsOfMode(currentGameMode.getMode());
        gameSizeTextView = findViewById(R.id.game_size_text_view);
        gameSizeTextView.setText(currentGameMode.getDimensions());
        mainManager.updateSizeBrowseIcons(currentGameMode.getDimensions(), allCurrentGameSizes);

        gamePreviewSpotLightLottie = findViewById(R.id.game_preview_spotlight_lottie);
        gamePreviewImageView = findViewById(R.id.game_preview_image_view);
        startGameLottie = findViewById(R.id.start_game_lottie);
        startGameButton = findViewById(R.id.start_game_button);
        if (sharedPreferences.getInt("GameStateEnumIndex" + " " + currentGameMode.getMode()
                + " " + currentGameMode.getDimensions(), 0) == GameStates.GAME_ONGOING.ordinal()) {
            startGameButton.setText("RESUME GAME");
        } else {
            startGameButton.setText("START GAME");
        }
        modeLeft = findViewById(R.id.game_mode_left_arrow_image_view);
        modeRight = findViewById(R.id.game_mode_right_arrow_image_view);
        sizeLeft = findViewById(R.id.game_size_left_arrow_image_view);
        sizeRight = findViewById(R.id.game_size_right_arrow_image_view);

        // Updating the preview accordingly
        mainManager.updatePreview(currentGameMode.getGamePreviewAssetFileName());
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Practise2048Champs);

        /*
        Following lines of code hide the status bar at the very top of the screen which battery
        indicator, network status other icons etc. Note, this is done before setting the layout with
        the line -> setContentView(R.layout.activity_main);
        */
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        // To Disable screen rotation and keep the device in Portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // To set the app theme to 'LIGHT' (even if 'DARK' theme is selected, however if user in their
        // settings enables 'DARK' theme for our individual app, then it will override the following line
        // no matter what)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // To hide the navigation bar as default i.e. it will hide by itself if left unused or unattended
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        initialise();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* Persisting the screen settings even if the user leaves the app mid use for when he/she
           returns to use the app again
        */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onBackPressed() {
        GameExitDialog gameExitDialog = new GameExitDialog(this);
        gameExitDialog.show();
        gameExitDialog.setGameExitDialogListener(new GameExitDialog.GameExitDialogListener() {
            @Override
            public void getResponseOfExitDialog(boolean response) {
                if (response) {
                    MainActivity.super.onBackPressed();
                }
            }
        });
    }

    /**
     * onClick listeners of Game Mode Options Linear Layout are as follows
     */
    // onClick listener for start game button is as follows
    public void startGameButtonPressed(View view) {
        if (currentGameMode.isCanAccess()) {
            ConstraintLayout rootLayout = findViewById(R.id.root_layout_main_activity);
            view.setEnabled(false);
            rootLayout.setEnabled(false);
            modeLeft.setEnabled(false); modeRight.setEnabled(false);
            sizeLeft.setEnabled(false); sizeRight.setEnabled(false);

            gamePreviewSpotLightLottie.setVisibility(View.INVISIBLE);
            gamePreviewImageView.setVisibility(View.INVISIBLE);
            startGameLottie.setVisibility(View.VISIBLE);

            sharedPreferences.edit().putString("gameMode", currentGameMode.getMode()).apply();
            sharedPreferences.edit().putInt("gameMatrixColumns", currentGameMode.getColumns()).apply();
            sharedPreferences.edit().putInt("gameMatrixRows", currentGameMode.getRows()).apply();

            startGameLottie.playAnimation();

            new CountDownTimer(1500, 1500) {
                @Override
                public void onTick(long millisUntilFinished) {}
                @Override
                public void onFinish() {
                    view.setEnabled(true);
                    rootLayout.setEnabled(true);
                    modeLeft.setEnabled(true); modeRight.setEnabled(true);
                    sizeLeft.setEnabled(true); sizeRight.setEnabled(true);
                    startGameLottie.pauseAnimation();

                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
                    intent.putExtra("gameMode", currentGameMode.getMode());
                    intent.putExtra("gameMatrixColumns", currentGameMode.getColumns());
                    intent.putExtra("gameMatrixRows", currentGameMode.getRows());
                    startActivity(intent);
                }
            }.start();
        } else {
            new ArrivingFeatureDialog(this).show();
        }
    }

    // onClick listener to change game mode option is as follows
    public void gameModeBrowse(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            int indexOfCurrentMode = allGameModes.indexOf(currentGameMode.getMode());
            if (view.getId() == R.id.game_mode_left_arrow_image_view) {
                indexOfCurrentMode--;
            } else {
                indexOfCurrentMode++;
            }

            // Make changes to the game mode and size both
            String newGameMode = allGameModes.get(indexOfCurrentMode);
            allCurrentGameSizes = GameModes.getAllGameVariantsOfMode(newGameMode);
            currentGameMode = GameModes.getGameModeEnum(
                    Character.getNumericValue(allCurrentGameSizes.get(0).charAt(0)),
                    Character.getNumericValue(allCurrentGameSizes.get(0)
                            .charAt(allCurrentGameSizes.get(0).length() - 1)), newGameMode);

            // Updating the text views for both mode and size
            gameModeTextView.setText(currentGameMode.getMode());
            gameSizeTextView.setText(currentGameMode.getDimensions());

            // Updating Game Mode Browse Icons
            mainManager.updateModeBrowseIcons(currentGameMode.getMode(), allGameModes);

            // Updating Game Size Browse Icons
            mainManager.updateSizeBrowseIcons(currentGameMode.getDimensions(), allCurrentGameSizes);

            // Updating Preview
            mainManager.updatePreview(currentGameMode.getGamePreviewAssetFileName());

            // Update the text of the start game button
            if (sharedPreferences.getInt("GameStateEnumIndex" + " " + currentGameMode.getMode()
                    + " " + currentGameMode.getDimensions(), 0) == GameStates.GAME_ONGOING.ordinal()) {
                startGameButton.setText("RESUME GAME");
            } else {
                startGameButton.setText("START GAME");
            }
        }
    }

    // onClick listener to change game size option is as follows
    public void gameSizeBrowse(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            int indexOfCurrentSize = allCurrentGameSizes.indexOf(currentGameMode.getDimensions());
            if (view.getId() == R.id.game_size_left_arrow_image_view) {
                indexOfCurrentSize--;
            } else {
                indexOfCurrentSize++;
            }

            // Make changes to the game size
            String newGameSize = allCurrentGameSizes.get(indexOfCurrentSize);
            currentGameMode = GameModes.getGameModeEnum(
                    Character.getNumericValue(newGameSize.charAt(0)),
                    Character.getNumericValue(newGameSize.charAt(newGameSize.length() - 1)),
                    currentGameMode.getMode());
            gameSizeTextView.setText(currentGameMode.getDimensions());

            // Updating Game Size Browse Icons
            mainManager.updateSizeBrowseIcons(currentGameMode.getDimensions(), allCurrentGameSizes);

            // Updating Preview
            mainManager.updatePreview(currentGameMode.getGamePreviewAssetFileName());

            // Update the text of the start game button
            if (sharedPreferences.getInt("GameStateEnumIndex" + " " + currentGameMode.getMode()
                    + " " + currentGameMode.getDimensions(), 0) == GameStates.GAME_ONGOING.ordinal()) {
                startGameButton.setText("RESUME GAME");
            } else {
                startGameButton.setText("START GAME");
            }
        }
    }
}