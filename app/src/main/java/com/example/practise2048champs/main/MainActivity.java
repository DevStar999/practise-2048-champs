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
import com.google.android.gms.games.Player;
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
    private Map<String, GameModes> leaderboardIdToGameMode;

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
        leaderboardIdToGameMode = new HashMap<>() {{
            put(getString(R.string.leaderboard_4x4__square), GameModes.SQUARE_4X4);
            put(getString(R.string.leaderboard_5x5__square), GameModes.SQUARE_5X5);
            put(getString(R.string.leaderboard_6x6__square), GameModes.SQUARE_6X6);
            put(getString(R.string.leaderboard_4x5__rectangle), GameModes.RECTANGLE_4X5);
            put(getString(R.string.leaderboard_4x6__rectangle), GameModes.RECTANGLE_4X6);
            put(getString(R.string.leaderboard_5x6__rectangle), GameModes.RECTANGLE_5X6);
            put(getString(R.string.leaderboard_5x5__block_middle_sq_), GameModes.BLOCK_MIDDLE_SQUARE_5X5);
            put(getString(R.string.leaderboard_4x6__block_middle_rec_), GameModes.BLOCK_MIDDLE_RECTANGLE_4X6);
            put(getString(R.string.leaderboard_5x5__block_2_corners_sq_), GameModes.BLOCK_2_CORNERS_SQUARE_5X5);
            put(getString(R.string.leaderboard_4x6__block_2_corners_rec_), GameModes.BLOCK_2_CORNERS_RECTANGLE_4X6);
        }};
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

        verifyPlayGamesSignIn(false);

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
                    updateAchievementsProgress();
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
                            // TODO -> Show a dialog for GPGS Sign In Troubleshooting
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
                        /* TODO -> Give the message to the user that he/she cannot access this feature unless they
                                   are signed in (Achievements Feature)
                        */
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
                        /* TODO -> Give the message to the user that he/she cannot access this feature unless they
                                   are signed in (Leaderboards Feature)
                        */
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

    private void updateAchievementsProgress() {
        // TODO -> Complete the code to update the Achievements Progress data for the newly signed in player
    }

    private void updateLeaderboardsProgress(int retryAttempt) {
        if (retryAttempt >= 3) {
            return;
        }

        leaderboardsClient.loadLeaderboardMetadata(false)
                .addOnSuccessListener(new OnSuccessListener<AnnotatedData<LeaderboardBuffer>>() {
            @Override
            public void onSuccess(AnnotatedData<LeaderboardBuffer> leaderboardBufferAnnotatedData) {
                LeaderboardBuffer leaderboardBuffer = leaderboardBufferAnnotatedData.get();
                if (leaderboardBuffer != null) {
                    int count = leaderboardBuffer.getCount();
                    for (int index = 0; index < count; index++) {
                        Leaderboard leaderboard = leaderboardBuffer.get(index);
                        String leaderboardId = leaderboard.getLeaderboardId();
                        // Update progress related to the best scores in various game modes
                        if (leaderboardIdToGameMode.containsKey(leaderboardId)) {
                            GameModes currentGameMode = leaderboardIdToGameMode.get(leaderboardId);
                            List<LeaderboardVariant> leaderboardVariants = leaderboard.getVariants();
                            for (int variantIndex = 0; variantIndex < leaderboardVariants.size(); variantIndex++) {
                                LeaderboardVariant currentVariant = leaderboardVariants.get(variantIndex);
                                if (currentVariant.getTimeSpan() == LeaderboardVariant.TIME_SPAN_ALL_TIME &&
                                        currentVariant.getCollection() == LeaderboardVariant.COLLECTION_PUBLIC) {
                                    long leaderboardBestScore = currentVariant.getRawPlayerScore();
                                    long gameModeSavedBestScore = sharedPreferences.getLong("bestScore" + " " +
                                            currentGameMode.getMode() + " " + currentGameMode.getDimensions(), 0L);
                                    if (leaderboardBestScore < gameModeSavedBestScore) {
                                        leaderboardsClient.submitScore(leaderboardId, gameModeSavedBestScore);
                                    } else {
                                        sharedPreferences.edit().putLong("bestScore" + " " + currentGameMode.getMode()
                                                + " " + currentGameMode.getDimensions(), leaderboardBestScore).apply();
                                    }
                                }
                            }
                        }
                        // Updating the progress related to the most number of coins saved
                        if (leaderboardId.equals(getString(R.string.leaderboard_coins_leaderboard))) {
                            List<LeaderboardVariant> leaderboardVariants = leaderboard.getVariants();
                            for (int variantIndex = 0; variantIndex < leaderboardVariants.size(); variantIndex++) {
                                LeaderboardVariant currentVariant = leaderboardVariants.get(variantIndex);
                                if (currentVariant.getTimeSpan() == LeaderboardVariant.TIME_SPAN_ALL_TIME &&
                                        currentVariant.getCollection() == LeaderboardVariant.COLLECTION_PUBLIC) {
                                    int leaderboardMostCoins = (int) currentVariant.getRawPlayerScore();
                                    int savedMostCoins = sharedPreferences.getInt("mostCoins", 0);
                                    if (leaderboardMostCoins < savedMostCoins) {
                                        leaderboardsClient.submitScore(leaderboardId, savedMostCoins);
                                    } else {
                                        sharedPreferences.edit().putInt("mostCoins", leaderboardMostCoins).apply();
                                    }
                                }
                            }
                        }
                    }
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
        int mostCoins = sharedPreferences.getInt("mostCoins", 0);
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