package com.example.practise2048champs.main;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.practise2048champs.BuildConfig;
import com.example.practise2048champs.GameActivity;
import com.example.practise2048champs.R;
import com.example.practise2048champs.dialogs.ArrivingFeatureDialog;
import com.example.practise2048champs.dialogs.ErrorOccurredDialog;
import com.example.practise2048champs.dialogs.GameExitDialog;
import com.example.practise2048champs.dialogs.UpdateAppStaticAvailableDialog;
import com.example.practise2048champs.dialogs.UpdateAppStaticUnavailableDialog;
import com.example.practise2048champs.enums.GameModes;
import com.example.practise2048champs.enums.ScoringAchievements;
import com.example.practise2048champs.enums.TileUnlockAchievements;
import com.example.practise2048champs.enums.UndoToolAchievements;
import com.example.practise2048champs.fragments.BlockDesignFragment;
import com.example.practise2048champs.fragments.LeaderboardsFragment;
import com.example.practise2048champs.fragments.LogoLottieFragment;
import com.example.practise2048champs.fragments.NavigationFragment;
import com.example.practise2048champs.fragments.SettingsFragment;
import com.example.practise2048champs.fragments.ShopFragment;
import com.example.practise2048champs.fragments.PreGameFragment;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.AnnotatedData;
import com.google.android.gms.games.AuthenticationResult;
import com.google.android.gms.games.GamesSignInClient;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.PlayGames;
import com.google.android.gms.games.PlayGamesSdk;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.games.leaderboard.Leaderboard;
import com.google.android.gms.games.leaderboard.LeaderboardBuffer;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        NavigationFragment.OnNavigationFragmentInteractionListener,
        PreGameFragment.OnPreGameFragmentInteractionListener,
        LeaderboardsFragment.OnLeaderboardsFragmentInteractionListener,
        ShopFragment.OnShopFragmentInteractionListener,
        SettingsFragment.OnSettingsFragmentInteractionListener,
        BlockDesignFragment.OnBlockDesignFragmentInteractionListener {
    private SharedPreferences sharedPreferences;

    // Attributes for Google Play Games Services (GPGS) features
    private boolean isUserSignedIn;
    private GamesSignInClient gamesSignInClient;
    private static final int RC_ACHIEVEMENT_UI = 9003;
    private AchievementsClient achievementsClient;
    public static final int RC_LEADERBOARD_UI = 9004;
    private LeaderboardsClient leaderboardsClient;

    // Attributes required for In app updates feature
    public static final int UPDATE_REQUEST_CODE = 100;
    private AppUpdateManager appUpdateManager;
    private InstallStateUpdatedListener installStateUpdatedListener;

    private void initialise() {
        isUserSignedIn = true;
        sharedPreferences = getSharedPreferences("com.nerdcoredevelopment.game2048champsfinal", Context.MODE_PRIVATE);
        gamesSignInClient = PlayGames.getGamesSignInClient(MainActivity.this);
        achievementsClient = PlayGames.getAchievementsClient(MainActivity.this);
        leaderboardsClient = PlayGames.getLeaderboardsClient(MainActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        Following lines of code hide the status bar at the very top of the screen which battery
        indicator, network status other icons etc. Note, this is done before setting the layout with
        the line -> setContentView(R.layout.activity_main);
        */
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        // To hide the navigation bar as default i.e. it will hide by itself if left unused or unattended
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        initialise();

        LogoLottieFragment logoLottieFragment = new LogoLottieFragment();
        NavigationFragment navigationFragment = new NavigationFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.logo_lottie_main_activity_fragment_container, logoLottieFragment, "LOGO_LOTTIE_FRAGMENT")
                .replace(R.id.navigation_main_activity_fragment_container, navigationFragment, "NAVIGATION_FRAGMENT")
                .commit();

        PlayGamesSdk.initialize(this);

        new CountDownTimer(500, 10000) {
            @Override
            public void onTick(long l) {}
            @Override
            public void onFinish() {
                verifyPlayGamesSignIn(false);
            }
        }.start();

        setupInAppUpdate();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* Persisting the screen settings even if the user leaves the app mid use for when he/she
           returns to use the app again
        */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            // Back button was pressed from activity, do nothing as we want to eliminate this option
            // to exit from the homepage
        } else {
            // Back button was pressed from fragment
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (appUpdateManager != null) {
            appUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                // If the update is cancelled or fails, we can ignore this if we are implementing a 'Flexible Update'
            }
        }
    }

    private boolean isInternetConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) { return false; }
        /* NetworkInfo is deprecated in API 29 so we have to check separately for higher API Levels */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Network network = cm.getActiveNetwork();
            if (network == null) { return false; }
            NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(network);
            if (networkCapabilities == null) { return false; }
            boolean isInternetSuspended = !networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_SUSPENDED);
            return (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                    && !isInternetSuspended);
        } else {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        }
    }

    private void setupInAppUpdate() {
        installStateUpdatedListener = installState -> {
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate();
            }
        };
        appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateManager.registerListener(installStateUpdatedListener);
    }

    private void popupSnackbarForCompleteUpdate() { // Displays the snackbar notification and call to action.
        Snackbar snackbar = Snackbar.make(findViewById(R.id.root_layout_main_activity),
                "An update has been downloaded", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("INSTALL", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appUpdateManager.completeUpdate();
            }
        });
        snackbar.setActionTextColor(getColor(R.color.white));
        snackbar.show();
    }

    private void launchInAppUpdateFlowForStaticButton() {
        if (!isInternetConnected()) {
            /* Using a feel right as of now. Since, the user may repeatedly press this button it isn't wise to show a dialog
               for prompting the user to check their internet connection
             */
            Toast.makeText(MainActivity.this, "Network connection failed. Please check " +
                    "Internet connectivity", Toast.LENGTH_LONG).show();
            return;
        }

        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                int oldVersion = BuildConfig.VERSION_CODE;
                int newVersion = appUpdateInfo.availableVersionCode();
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                        UpdateAppStaticAvailableDialog updateAppStaticAvailableDialog =
                                new UpdateAppStaticAvailableDialog(MainActivity.this);
                        updateAppStaticAvailableDialog.setUpdateAppStaticAvailableDialogListener(response -> {
                            if (response) {
                                try {
                                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE,
                                            MainActivity.this, UPDATE_REQUEST_CODE);
                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        updateAppStaticAvailableDialog.show();
                    }
                } else if (oldVersion == newVersion) {
                    // Note: Even in the case when there was no internet we get UpdateAvailability.UPDATE_NOT_AVAILABLE.
                    // So we made that check earlier
                    new UpdateAppStaticUnavailableDialog(MainActivity.this).show();
                } else {
                    // This is the final error code branch
                    new ErrorOccurredDialog(MainActivity.this, "Oops! Something went wrong").show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new ErrorOccurredDialog(MainActivity.this, "Oops! Something went wrong").show();
            }
        });
    }

    private void checkForNewSignedInPlayer() {
        PlayGames.getPlayersClient(MainActivity.this).getCurrentPlayer()
                .addOnCompleteListener(new OnCompleteListener<Player>() {
            @Override
            public void onComplete(@NonNull Task<Player> task) {
                String playerId = task.getResult().getPlayerId();
                String previousSignedInPlayerId = sharedPreferences.getString("previousSignedInPlayerId",
                        "<Default Signed In Player>");
                String currentSignedInPlayerId = sharedPreferences.getString("currentSignedInPlayerId",
                        previousSignedInPlayerId); // Expected currently signed in player
                if (currentSignedInPlayerId.equals(playerId)) { // The previous player has
                    // All is good here
                } else {
                    sharedPreferences.edit().putString("previousSignedInPlayerId",
                            currentSignedInPlayerId).apply();
                    sharedPreferences.edit().putString("currentSignedInPlayerId", playerId).apply();
                    updateAchievementsProgress(0);
                    updateLeaderboardsProgress(0);
                }
            }
        });
    }

    private void verifyPlayGamesSignIn(boolean isSignInAttemptManual) {
        gamesSignInClient.isAuthenticated().addOnCompleteListener(new OnCompleteListener<AuthenticationResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthenticationResult> isAuthenticatedTask) {
                boolean isAuthenticated = (isAuthenticatedTask.isSuccessful()
                        && isAuthenticatedTask.getResult().isAuthenticated());
                if (isAuthenticated) {
                    isUserSignedIn = true;
                    hideSignInButtonThroughoutApp();
                    checkForNewSignedInPlayer();
                } else {
                    isUserSignedIn = false;
                    revealSignInButtonThroughoutApp();
                    if (isSignInAttemptManual) {
                        if (isInternetConnected()) {
                            /* TODO -> Replace this toast with something better like a dialog etc. and more descriptive
                                       (Sign In feature)
                            */
                            Toast.makeText(MainActivity.this, "Download the 'Google Play Games' app and " +
                                    "select an account to play this game", Toast.LENGTH_LONG).show();
                        } else { // Internet is NOT connected
                            Toast.makeText(MainActivity.this, "Network connection failed. Please check " +
                                    "Internet connectivity", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }

    private void verifyPlayGamesSignInPreAchievementsDisplay() {
        gamesSignInClient.isAuthenticated().addOnCompleteListener(new OnCompleteListener<AuthenticationResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthenticationResult> isAuthenticatedTask) {
                boolean isAuthenticated = (isAuthenticatedTask.isSuccessful()
                        && isAuthenticatedTask.getResult().isAuthenticated());
                if (isAuthenticated) {
                    isUserSignedIn = true;
                    hideSignInButtonThroughoutApp();
                    checkForNewSignedInPlayer();
                } else {
                    isUserSignedIn = false;
                    revealSignInButtonThroughoutApp();
                    if (isInternetConnected()) {
                        /* TODO -> Replace this toast with something better like a dialog etc. and more descriptive
                                   (Achievements feature)
                        */
                        Toast.makeText(MainActivity.this, "Cannot access this feature without being Signed In",
                                Toast.LENGTH_LONG).show();
                    } else { // Internet is NOT connected
                        Toast.makeText(MainActivity.this, "Network connection failed. Please check " +
                                "Internet connectivity", Toast.LENGTH_LONG).show();
                    }
                }

                if (isUserSignedIn) {
                    showAchievementsDisplayPostVerification();
                }
            }
        });
    }

    private void verifyPlayGamesSignInPreLeaderboardsDisplay() {
        gamesSignInClient.isAuthenticated().addOnCompleteListener(new OnCompleteListener<AuthenticationResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthenticationResult> isAuthenticatedTask) {
                boolean isAuthenticated = (isAuthenticatedTask.isSuccessful()
                        && isAuthenticatedTask.getResult().isAuthenticated());
                if (isAuthenticated) {
                    isUserSignedIn = true;
                    hideSignInButtonThroughoutApp();
                    checkForNewSignedInPlayer();
                } else {
                    isUserSignedIn = false;
                    revealSignInButtonThroughoutApp();
                    if (isInternetConnected()) {
                        /* TODO -> Replace this toast with something better like a dialog etc. and more descriptive
                                   (Leaderboards feature)
                        */
                        Toast.makeText(MainActivity.this, "Ensure you are Signed In and 'Everyone can see your " +
                                "game activity' in 'Google Play Games' app settings", Toast.LENGTH_LONG).show();
                    } else { // Internet is NOT connected
                        Toast.makeText(MainActivity.this, "Network connection failed. Please check " +
                                "Internet connectivity", Toast.LENGTH_LONG).show();
                    }
                }

                if (isUserSignedIn) {
                    showLeaderboardsDisplayPostVerification();
                }
            }
        });
    }

    private void hideSignInButtonThroughoutApp() {
        List<Fragment> fragments = new ArrayList<>(getSupportFragmentManager().getFragments());
        for (int index = 0; index < fragments.size(); index++) {
            Fragment currentFragment = fragments.get(index);
            if (currentFragment != null && currentFragment.getTag() != null
                    && !currentFragment.getTag().isEmpty()) {
                if (currentFragment.getTag().equals("NAVIGATION_FRAGMENT")) {
                    ((NavigationFragment) currentFragment).hideSignInButton();
                } else if (currentFragment.getTag().equals("SETTINGS_FRAGMENT")) {
                    ((SettingsFragment) currentFragment).hideSignInButton();
                }
            }
        }
    }

    private void revealSignInButtonThroughoutApp() {
        List<Fragment> fragments = new ArrayList<>(getSupportFragmentManager().getFragments());
        for (int index = 0; index < fragments.size(); index++) {
            Fragment currentFragment = fragments.get(index);
            if (currentFragment != null && currentFragment.getTag() != null
                    && !currentFragment.getTag().isEmpty()) {
                if (currentFragment.getTag().equals("NAVIGATION_FRAGMENT")) {
                    ((NavigationFragment) currentFragment).revealSignInButton();
                } else if (currentFragment.getTag().equals("SETTINGS_FRAGMENT")) {
                    ((SettingsFragment) currentFragment).revealSignInButton();
                }
            }
        }
    }

    private void updateAchievementsProgress(int retryAttempt) {
        Log.i("Custom Debugging", "inside updateAchievementsProgress()");
        if (retryAttempt >= 3) {
            return;
        }

        achievementsClient.load(true).addOnSuccessListener(new OnSuccessListener<AnnotatedData<AchievementBuffer>>() {
            @Override
            public void onSuccess(AnnotatedData<AchievementBuffer> achievementBufferAnnotatedData) {
                AchievementBuffer achievementBuffer = achievementBufferAnnotatedData.get();
                if (achievementBuffer != null) {
                    int count = achievementBuffer.getCount();
                    Log.i("inside updateAchievementsProgress()", "Count of Achievements = " + count);
                    for (int index = 0; index < count; index++) {
                        Log.i("inside updateAchievementsProgress()", "current index value = " + index);
                        Achievement achievement = achievementBuffer.get(index);
                        String achievementId = achievement.getAchievementId();
                        Log.i("inside updateAchievementsProgress()", "Achievement = " + achievement.getName());
                        // Update the progress related to ScoringAchievements
                        for (int scoringAchievementIndex = 0; scoringAchievementIndex < ScoringAchievements.values().length;
                             scoringAchievementIndex++) {
                            ScoringAchievements currentScoringAchievement =
                                    ScoringAchievements.values()[scoringAchievementIndex];
                            if (achievementId.equals(getString(currentScoringAchievement.getAchievementStringResourceId()))) {
                                if (achievement.getState() == Achievement.STATE_UNLOCKED) {
                                    sharedPreferences.edit().putBoolean("scoringAchievement" + "_" +
                                            getString(currentScoringAchievement.getAchievementStringResourceId()), true).apply();
                                } else {
                                    sharedPreferences.edit().putBoolean("scoringAchievement" + "_" +
                                            getString(currentScoringAchievement.getAchievementStringResourceId()), false).apply();
                                }
                                break;
                            }
                        }

                        // Update the progress related to TileUnlockAchievements
                        for (int tileUnlockAchievementIndex = 0; tileUnlockAchievementIndex < TileUnlockAchievements.values().length;
                             tileUnlockAchievementIndex++) {
                            TileUnlockAchievements currentTileUnlockAchievement =
                                    TileUnlockAchievements.values()[tileUnlockAchievementIndex];
                            if (achievementId.equals(getString(currentTileUnlockAchievement.getAchievementStringResourceId()))) {
                                if (achievement.getState() == Achievement.STATE_UNLOCKED) {
                                    sharedPreferences.edit().putBoolean("tileUnlockAchievement" + "_" +
                                            getString(currentTileUnlockAchievement.getAchievementStringResourceId()), true).apply();
                                } else {
                                    sharedPreferences.edit().putBoolean("tileUnlockAchievement" + "_" +
                                            getString(currentTileUnlockAchievement.getAchievementStringResourceId()), false).apply();
                                }
                                break;
                            }
                        }

                        // Update the progress related to UndoToolAchievements
                        for (int undoToolsAchievementIndex = 0; undoToolsAchievementIndex < UndoToolAchievements.values().length;
                             undoToolsAchievementIndex++) {
                            UndoToolAchievements currentUndoToolAchievement =
                                    UndoToolAchievements.values()[undoToolsAchievementIndex];
                            if (achievementId.equals(getString(currentUndoToolAchievement.getAchievementStringResourceId()))) {
                                sharedPreferences.edit().putInt("undoToolAchievement" + "_" +
                                        getString(currentUndoToolAchievement.getAchievementStringResourceId()),
                                        achievement.getState()).apply();
                                Log.i("inside updateAchievementsProgress()", "currentUndoToolAchievement = " +
                                        currentUndoToolAchievement);
                                if (achievement.getState() == Achievement.STATE_HIDDEN) {
                                    Log.i("inside updateAchievementsProgress()", "currentState = " + "STATE_HIDDEN");
                                } else if (achievement.getState() == Achievement.STATE_REVEALED) {
                                    Log.i("inside updateAchievementsProgress()", "currentState = " + "STATE_REVEALED");
                                } else if (achievement.getState() == Achievement.STATE_UNLOCKED) {
                                    Log.i("inside updateAchievementsProgress()", "currentState = " + "STATE_UNLOCKED");
                                }
                                break;
                            }
                        }
                    }
                }
                
                if (achievementBuffer != null) {
                    achievementBuffer.release();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                updateAchievementsProgress(retryAttempt + 1);
            }
        });
    }

    private void updateLeaderboardsProgress(int retryAttempt) {
        Log.i("Custom Debugging", "inside updateLeaderboardsProgress()");
        if (retryAttempt >= 3) {
            return;
        }

        leaderboardsClient.loadLeaderboardMetadata(true)
                .addOnSuccessListener(new OnSuccessListener<AnnotatedData<LeaderboardBuffer>>() {
            @Override
            public void onSuccess(AnnotatedData<LeaderboardBuffer> leaderboardBufferAnnotatedData) {
                LeaderboardBuffer leaderboardBuffer = leaderboardBufferAnnotatedData.get();
                if (leaderboardBuffer != null) {
                    int count = leaderboardBuffer.getCount();
                    for (int index = 0; index < count; index++) {
                        Leaderboard leaderboard = leaderboardBuffer.get(index);
                        String leaderboardId = leaderboard.getLeaderboardId();
                        Log.i("Custom Debugging", "Leaderboard  = " + leaderboard.getDisplayName());
                        // Update progress related to the best scores in various game modes
                        for (int currentGameModeIndex = 0; currentGameModeIndex < GameModes.values().length; 
                             currentGameModeIndex++) {
                            GameModes currentGameMode = GameModes.values()[currentGameModeIndex];
                            if (leaderboardId.equals(getString(currentGameMode.getLeaderboardStringResourceId()))) {
                                List<LeaderboardVariant> leaderboardVariants = leaderboard.getVariants();
                                long leaderboardBestScore = Long.MIN_VALUE;
                                long gameModeSavedBestScore = sharedPreferences.getLong("bestScore" + " " +
                                        currentGameMode.getMode() + " " + currentGameMode.getDimensions(), 0L);
                                for (int variantIndex = 0; variantIndex < leaderboardVariants.size(); variantIndex++) {
                                    LeaderboardVariant currentVariant = leaderboardVariants.get(variantIndex);
                                    long currentLeaderboardBestScore = currentVariant.getRawPlayerScore();
                                    leaderboardBestScore = Math.max(leaderboardBestScore, currentLeaderboardBestScore);
                                }
                                if (leaderboardBestScore < gameModeSavedBestScore) {
                                    leaderboardsClient.submitScore(leaderboardId, gameModeSavedBestScore);
                                } else {
                                    sharedPreferences.edit().putLong("bestScore" + " " + currentGameMode.getMode()
                                            + " " + currentGameMode.getDimensions(), leaderboardBestScore).apply();
                                }
                            }
                        }
                        
                        // Updating the progress related to the most number of coins saved
                        if (leaderboardId.equals(getString(R.string.leaderboard_coins_leaderboard))) {
                            List<LeaderboardVariant> leaderboardVariants = leaderboard.getVariants();
                            int savedMostCoins = sharedPreferences.getInt("mostCoins", 3000);
                            int leaderboardMostCoins = Integer.MIN_VALUE;
                            for (int variantIndex = 0; variantIndex < leaderboardVariants.size(); variantIndex++) {
                                LeaderboardVariant currentVariant = leaderboardVariants.get(variantIndex);
                                int currentLeaderboardMostCoins = (int) currentVariant.getRawPlayerScore();
                                leaderboardMostCoins = Math.max(leaderboardMostCoins, currentLeaderboardMostCoins);
                            }
                            if (leaderboardMostCoins < savedMostCoins) {
                                leaderboardsClient.submitScore(leaderboardId, savedMostCoins);
                            } else {
                                sharedPreferences.edit().putInt("mostCoins", leaderboardMostCoins).apply();
                            }
                        }

                        // Updating the progress related to the use count of 'Undo' tool
                        if (leaderboardId.equals(getString(R.string.leaderboard_undo_tool_masters))) {
                            Log.i("Custom Debugging", "Undo Leaderboard encountered");
                            List<LeaderboardVariant> leaderboardVariants = leaderboard.getVariants();
                            int savedUndoToolUseCountSubmitted =
                                    sharedPreferences.getInt("undoToolUseCountSubmitted", 0);
                            int savedUndoToolCurrentUseCount =
                                    sharedPreferences.getInt("undoToolCurrentUseCount", savedUndoToolUseCountSubmitted);
                            Log.i("Custom Debugging", "savedUndoToolUseCountSubmitted " +
                                    "(just after sharedPreferences fetch) = " + savedUndoToolUseCountSubmitted);
                            Log.i("Custom Debugging", "savedUndoToolCurrentUseCount " +
                                    "(just after sharedPreferences fetch) = " + savedUndoToolCurrentUseCount);
                            int leaderboardUndoToolUseCount = Integer.MIN_VALUE;
                            for (int variantIndex = 0; variantIndex < leaderboardVariants.size(); variantIndex++) {
                                LeaderboardVariant currentVariant = leaderboardVariants.get(variantIndex);
                                int currentLeaderboardUndoToolUseCount = (int) currentVariant.getRawPlayerScore();
                                leaderboardUndoToolUseCount = Math.max(leaderboardUndoToolUseCount, currentLeaderboardUndoToolUseCount);
                            }
                            Log.i("Custom Debugging", "leaderboardUndoToolUseCount (among all variants) = " + leaderboardUndoToolUseCount);
                            // (1) If the score was never submitted, then value of 'leaderboardUndoToolUseCount' will be -1.
                            // So we first submit the score to the leaderboard
                            if (leaderboardUndoToolUseCount < 0) {
                                Log.i("Custom Debugging", "Value NOT submitted to leaderboard ever before " + leaderboardUndoToolUseCount);
                                leaderboardUndoToolUseCount = 0;
                                leaderboardsClient.submitScore(leaderboardId, leaderboardUndoToolUseCount);
                            }

                            // (2) Always update 'undoToolUseCountSubmitted' in saved data
                            savedUndoToolUseCountSubmitted = leaderboardUndoToolUseCount;
                            sharedPreferences.edit().putInt("undoToolUseCountSubmitted", savedUndoToolUseCountSubmitted).apply();

                            // (3) Based on value of 'undoToolUseCountSubmitted' save the value of 'undoToolCurrentUseCount'
                            if (savedUndoToolCurrentUseCount >= savedUndoToolUseCountSubmitted
                                    && savedUndoToolCurrentUseCount < savedUndoToolUseCountSubmitted + 10) {
                            } else {
                                savedUndoToolCurrentUseCount = savedUndoToolUseCountSubmitted;
                            }
                            sharedPreferences.edit().putInt("undoToolCurrentUseCount", savedUndoToolCurrentUseCount).apply();
                            Log.i("Custom Debugging", "savedUndoToolUseCountSubmitted " +
                                    "(after update) = " + savedUndoToolUseCountSubmitted);
                            Log.i("Custom Debugging", "savedUndoToolCurrentUseCount " +
                                    "(after update) = " + savedUndoToolCurrentUseCount);

                            // (4) Verify if the state of the achievements is in accordance with the above 2 values
                            List<Integer> levelWiseExpectedState = new ArrayList<>() {{
                                add(Achievement.STATE_REVEALED); add(Achievement.STATE_HIDDEN); add(Achievement.STATE_HIDDEN);
                            }};
                            if (savedUndoToolUseCountSubmitted >= 100 && savedUndoToolUseCountSubmitted < 250) {
                                levelWiseExpectedState.set(0, Achievement.STATE_UNLOCKED);
                                levelWiseExpectedState.set(1, Achievement.STATE_REVEALED);
                                levelWiseExpectedState.set(2, Achievement.STATE_HIDDEN);
                            } else if (savedUndoToolUseCountSubmitted >= 250 && savedUndoToolUseCountSubmitted < 500) {
                                levelWiseExpectedState.set(0, Achievement.STATE_UNLOCKED);
                                levelWiseExpectedState.set(1, Achievement.STATE_UNLOCKED);
                                levelWiseExpectedState.set(2, Achievement.STATE_REVEALED);
                            } else if (savedUndoToolUseCountSubmitted >= 500) {
                                levelWiseExpectedState.set(0, Achievement.STATE_UNLOCKED);
                                levelWiseExpectedState.set(1, Achievement.STATE_UNLOCKED);
                                levelWiseExpectedState.set(2, Achievement.STATE_UNLOCKED);
                            }
                            Log.i("Custom Debugging", "Going inside levelWiseExpectedState as follows - ");
                            for (int tempIndex = 0; tempIndex < levelWiseExpectedState.size(); tempIndex++) {
                                String debugMessage = "tempIndex = " + tempIndex;
                                if (levelWiseExpectedState.get(tempIndex) == Achievement.STATE_HIDDEN) {
                                    debugMessage += ", expectedState = STATE_HIDDEN";
                                } else if (levelWiseExpectedState.get(tempIndex) == Achievement.STATE_REVEALED) {
                                    debugMessage += ", expectedState = STATE_REVEALED";
                                } else if (levelWiseExpectedState.get(tempIndex) == Achievement.STATE_UNLOCKED) {
                                    debugMessage += ", expectedState = STATE_UNLOCKED";
                                }
                                Log.i("Custom Debugging", debugMessage);
                            }

                            for (int undoToolAchievementsIndex = 0; undoToolAchievementsIndex <
                                    UndoToolAchievements.values().length; undoToolAchievementsIndex++) {
                                UndoToolAchievements currentUndoToolAchievement =
                                        UndoToolAchievements.values()[undoToolAchievementsIndex];
                                int currentStateOfAchievement = sharedPreferences.getInt("undoToolAchievement" + "_" +
                                                getString(currentUndoToolAchievement.getAchievementStringResourceId()),
                                        currentUndoToolAchievement.getInitialAchievementState());

                                Log.i("Custom Debugging", "inside updateLeaderboardsProgress(), Achievement = " +
                                        currentUndoToolAchievement.getNameOfAchievement());
                                if (levelWiseExpectedState.get(undoToolAchievementsIndex) == currentStateOfAchievement) {
                                    // All good here
                                    Log.i("Custom Debugging", "State of the above achievement is as expected");
                                } else {
                                    Log.i("Custom Debugging", "State of the above achievement is NOT as expected");
                                    if (levelWiseExpectedState.get(undoToolAchievementsIndex) == Achievement.STATE_HIDDEN) {
                                        // Then, currentStateOfAchievement is either 'STATE_REVEALED' or 'STATE_UNLOCKED'.
                                        // Either way, nothing we can do
                                    } else if (levelWiseExpectedState.get(undoToolAchievementsIndex) == Achievement.STATE_REVEALED) {
                                        // Then, currentStateOfAchievement is either 'STATE_HIDDEN' or 'STATE_UNLOCKED'.
                                        // if currentStateOfAchievement is 'STATE_HIDDEN' we can do the following
                                        if (currentStateOfAchievement == Achievement.STATE_HIDDEN) {
                                            currentStateOfAchievement = Achievement.STATE_REVEALED;
                                            achievementsClient.reveal(getString(currentUndoToolAchievement.getAchievementStringResourceId()));
                                        }
                                    } else if (levelWiseExpectedState.get(undoToolAchievementsIndex) == Achievement.STATE_UNLOCKED) {
                                        // Then, currentStateOfAchievement is either 'STATE_REVEALED' or 'STATE_UNLOCKED'.
                                        // Either way, we can unlock the achievement
                                        currentStateOfAchievement = Achievement.STATE_UNLOCKED;
                                        achievementsClient.unlock(getString(currentUndoToolAchievement.getAchievementStringResourceId()));
                                    }
                                }

                                sharedPreferences.edit().putInt("undoToolAchievement" + "_" +
                                                getString(currentUndoToolAchievement.getAchievementStringResourceId()),
                                                currentStateOfAchievement).apply();
                            }
                        }
                    }
                }
                
                if (leaderboardBuffer != null) {
                    leaderboardBuffer.release();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                updateLeaderboardsProgress(retryAttempt + 1);
            }
        });
    }

    private void updateCoins(int currentCoins) {
        sharedPreferences.edit().putInt("currentCoins", currentCoins).apply();
        List<Fragment> fragments = new ArrayList<>(getSupportFragmentManager().getFragments());
        for (int index = 0; index < fragments.size(); index++) {
            Fragment currentFragment = fragments.get(index);
            if (currentFragment != null && currentFragment.getTag() != null
                    && !currentFragment.getTag().isEmpty()) {
                if (currentFragment.getTag().equals("SHOP_FRAGMENT")) {
                    ((ShopFragment) currentFragment).updateCoinsShopFragment(currentCoins);
                }
            }
        }

        // Check if current coins count is greater than the highest most coins count
        int mostCoins = sharedPreferences.getInt("mostCoins", 3000);
        if (currentCoins >= mostCoins + 1000) {
            sharedPreferences.edit().putInt("mostCoins", currentCoins).apply();
            leaderboardsClient.submitScore(getString(R.string.leaderboard_coins_leaderboard), currentCoins);
        }
    }

    @Override
    public void onNavigationFragmentGPGSSignInClicked() {
        gamesSignInClient.signIn();
        new CountDownTimer(1000, 10000) {
            @Override
            public void onTick(long l) {}
            @Override
            public void onFinish() { verifyPlayGamesSignIn(true); }
        }.start();
    }

    @Override
    public void onNavigationFragmentPreGameClicked() {
        // If PreGameFragment was opened and is currently on top, then return
        int countOfFragments = getSupportFragmentManager().getFragments().size();
        if (countOfFragments > 0) {
            Fragment topMostFragment = getSupportFragmentManager().getFragments().get(countOfFragments-1);
            if (topMostFragment != null && topMostFragment.getTag() != null && !topMostFragment.getTag().isEmpty()
                    && topMostFragment.getTag().equals("PREGAME_FRAGMENT")) {
                return;
            }
        }

        PreGameFragment fragment = new PreGameFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.main_activity_full_screen_fragment_container,
                fragment, "PREGAME_FRAGMENT").commit();
    }

    private void showAchievementsDisplayPostVerification() {
        achievementsClient.getAchievementsIntent().addOnSuccessListener(new OnSuccessListener<Intent>() {
            @Override
            public void onSuccess(Intent intent) {
                startActivityForResult(intent, RC_ACHIEVEMENT_UI);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (!isInternetConnected()) { // Internet is not connected which can be the cause of this failure
                    Toast.makeText(MainActivity.this, "Network connection failed. Please check " +
                            "Internet connectivity", Toast.LENGTH_LONG).show();
                } else { // Some unknown error has occurred
                    new ErrorOccurredDialog(MainActivity.this, "Oops! Something went wrong").show();
                }
            }
        });
    }

    @Override
    public void onNavigationFragmentAchievementsClicked() {
        if (!isUserSignedIn) {
            gamesSignInClient.signIn();
            new CountDownTimer(1000, 10000) {
                @Override
                public void onTick(long l) {}
                @Override
                public void onFinish() {
                    verifyPlayGamesSignInPreAchievementsDisplay();
                }
            }.start();
        } else {
            showAchievementsDisplayPostVerification();
        }
    }

    private void showLeaderboardsDisplayPostVerification() {
        // If LeaderboardsFragment was opened and is currently on top, then return
        int countOfFragments = getSupportFragmentManager().getFragments().size();
        if (countOfFragments > 0) {
            Fragment topMostFragment = getSupportFragmentManager().getFragments().get(countOfFragments-1);
            if (topMostFragment != null && topMostFragment.getTag() != null && !topMostFragment.getTag().isEmpty()
                    && topMostFragment.getTag().equals("LEADERBOARDS_FRAGMENT")) {
                return;
            }
        }

        LeaderboardsFragment fragment = new LeaderboardsFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.main_activity_full_screen_fragment_container,
                fragment, "LEADERBOARDS_FRAGMENT").commit();
    }

    @Override
    public void onNavigationFragmentLeaderboardsClicked() {
        if (!isUserSignedIn) {
            gamesSignInClient.signIn();
            new CountDownTimer(1000, 10000) {
                @Override
                public void onTick(long l) {}
                @Override
                public void onFinish() {
                    verifyPlayGamesSignInPreLeaderboardsDisplay();
                }
            }.start();
        } else {
            showLeaderboardsDisplayPostVerification();
        }
    }

    @Override
    public void onNavigationFragmentSettingsClicked() {
        // If SettingsFragment was opened and is currently on top, then return
        int countOfFragments = getSupportFragmentManager().getFragments().size();
        if (countOfFragments > 0) {
            Fragment topMostFragment = getSupportFragmentManager().getFragments().get(countOfFragments-1);
            if (topMostFragment != null && topMostFragment.getTag() != null && !topMostFragment.getTag().isEmpty()
                    && topMostFragment.getTag().equals("SETTINGS_FRAGMENT")) {
                return;
            }
        }

        SettingsFragment fragment = SettingsFragment.newInstance(isUserSignedIn);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.main_activity_full_screen_fragment_container,
                fragment, "SETTINGS_FRAGMENT").commit();
    }

    @Override
    public void onNavigationFragmentShopClicked() {
        // If ShopFragment was opened and is currently on top, then return
        int countOfFragments = getSupportFragmentManager().getFragments().size();
        if (countOfFragments > 0) {
            Fragment topMostFragment = getSupportFragmentManager().getFragments().get(countOfFragments-1);
            if (topMostFragment != null && topMostFragment.getTag() != null && !topMostFragment.getTag().isEmpty()
                    && topMostFragment.getTag().equals("SHOP_FRAGMENT")) {
                return;
            }
        }

        ShopFragment fragment = new ShopFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.main_activity_full_screen_fragment_container,
                fragment, "SHOP_FRAGMENT").commit();
    }

    @Override
    public void onPreGameFragmentInteractionBackClicked() {
        onBackPressed();
    }

    @Override
    public void onPreGameFragmentInteractionStartGame(String gameMode, int gameMatrixColumns, int gameMatrixRows) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra("gameMode", gameMode);
        intent.putExtra("gameMatrixColumns", gameMatrixColumns);
        intent.putExtra("gameMatrixRows", gameMatrixRows);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPreGameFragmentInteractionShowArrivingFeatureDialog() {
        new ArrivingFeatureDialog(this).show();
    }

    @Override
    public void onLeaderboardsFragmentInteractionBackClicked() {
        onBackPressed();
    }

    @Override
    public void onLeaderboardsFragmentInteractionShowLeaderboard(int leaderboardStringResourceId) {
        leaderboardsClient.getLeaderboardIntent(getString(leaderboardStringResourceId))
            .addOnSuccessListener(new OnSuccessListener<Intent>() {
                @Override
                public void onSuccess(Intent intent) {
                    startActivityForResult(intent, RC_LEADERBOARD_UI);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (!isInternetConnected()) { // Internet is not connected which can be the cause of this failure
                        Toast.makeText(MainActivity.this, "Network connection failed. Please check " +
                                "Internet connectivity", Toast.LENGTH_LONG).show();
                    } else { // Some unknown error has occurred
                        new ErrorOccurredDialog(MainActivity.this, "Oops! Something went wrong").show();
                    }
                }
            });
    }

    @Override
    public void onSettingsFragmentInteractionBackClicked() {
        onBackPressed();
    }

    @Override
    public void onSettingsFragmentInteractionGPGSSignInClicked() {
        gamesSignInClient.signIn();
        new CountDownTimer(1000, 10000) {
            @Override
            public void onTick(long l) {}
            @Override
            public void onFinish() { verifyPlayGamesSignIn(true); }
        }.start();
    }

    @Override
    public void onSettingsFragmentInteractionGetPremiumClicked() {
        // If ShopFragment was opened and is currently on top, then return
        int countOfFragments = getSupportFragmentManager().getFragments().size();
        if (countOfFragments > 0) {
            Fragment topMostFragment = getSupportFragmentManager().getFragments().get(countOfFragments-1);
            if (topMostFragment != null && topMostFragment.getTag() != null && !topMostFragment.getTag().isEmpty()
                    && topMostFragment.getTag().equals("SHOP_FRAGMENT")) {
                return;
            }
        }

        ShopFragment fragment = new ShopFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.main_activity_full_screen_fragment_container,
                fragment, "SHOP_FRAGMENT").commit();
    }

    @Override
    public void onSettingsFragmentInteractionToggleRotatingLightClicked(boolean isChecked) {
        List<Fragment> fragments = new ArrayList<>(getSupportFragmentManager().getFragments());
        for (int index = 0; index < fragments.size(); index++) {
            Fragment currentFragment = fragments.get(index);
            if (currentFragment != null && currentFragment.getTag() != null
                    && !currentFragment.getTag().isEmpty()) {
                if (currentFragment.getTag().equals("LOGO_LOTTIE_FRAGMENT")) {
                    ((LogoLottieFragment) currentFragment).updateRotatingLightState(isChecked);
                }
            }
        }
    }

    @Override
    public void onSettingsFragmentInteractionBlockDesignClicked() {
        // If BlockDesignFragment was opened and is currently on top, then return
        int countOfFragments = getSupportFragmentManager().getFragments().size();
        if (countOfFragments > 0) {
            Fragment topMostFragment = getSupportFragmentManager().getFragments().get(countOfFragments-1);
            if (topMostFragment != null && topMostFragment.getTag() != null && !topMostFragment.getTag().isEmpty()
                    && topMostFragment.getTag().equals("BLOCK_DESIGN_FRAGMENT")) {
                return;
            }
        }

        BlockDesignFragment fragment = new BlockDesignFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.main_activity_full_screen_fragment_container,
                fragment, "BLOCK_DESIGN_FRAGMENT").commit();
    }

    @Override
    public void onSettingsFragmentInteractionHowToPlayClicked() {
        Toast.makeText(MainActivity.this, "'How To Play' button clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSettingsFragmentInteractionHelpClicked() {
        Toast.makeText(MainActivity.this, "'Help' button clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSettingsFragmentInteractionCheckUpdatesClicked() {
        launchInAppUpdateFlowForStaticButton();
    }

    @Override
    public void onSettingsFragmentInteractionExitClicked() {
        GameExitDialog gameExitDialog = new GameExitDialog(this);
        gameExitDialog.show();
        gameExitDialog.setGameExitDialogListener(new GameExitDialog.GameExitDialogListener() {
            @Override
            public void getResponseOfExitDialog(boolean response) {
                if (response) {
                    MainActivity.this.finish();
                }
            }
        });
    }

    @Override
    public void onBlockDesignFragmentInteractionBackClicked() {
        onBackPressed();
    }

    @Override
    public void onShopFragmentInteractionBackClicked() {
        onBackPressed();
    }

    @Override
    public void onShopFragmentInteractionUpdateCoins(int currentCoins) {
        updateCoins(currentCoins);
    }
}