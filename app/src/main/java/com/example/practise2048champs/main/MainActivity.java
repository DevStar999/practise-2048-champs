package com.example.practise2048champs.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.practise2048champs.AnnouncementsFragment;
import com.example.practise2048champs.BlockDesignFragment;
import com.example.practise2048champs.GameActivity;
import com.example.practise2048champs.LogoLottieFragment;
import com.example.practise2048champs.NavigationFragment;
import com.example.practise2048champs.R;
import com.example.practise2048champs.SettingsFragment;
import com.example.practise2048champs.ShopFragment;
import com.example.practise2048champs.dialogs.ArrivingFeatureDialog;
import com.example.practise2048champs.dialogs.GameExitDialog;
import com.example.practise2048champs.pregame.PreGameFragment;

public class MainActivity extends AppCompatActivity implements
        NavigationFragment.OnNavigationFragmentInteractionListener,
        PreGameFragment.OnPreGameFragmentInteractionListener,
        AnnouncementsFragment.OnAnnouncementsFragmentInteractionListener,
        SettingsFragment.OnSettingsFragmentInteractionListener,
        BlockDesignFragment.OnBlockDesignFragmentInteractionListener,
        ShopFragment.OnShopFragmentInteractionListener {
    private LogoLottieFragment logoLottieFragment;
    private NavigationFragment navigationFragment;

    private void initialise() {
        logoLottieFragment = new LogoLottieFragment();
        navigationFragment = new NavigationFragment();
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
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        initialise();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.logo_lottie_fragment_container, logoLottieFragment)
                .replace(R.id.navigation_fragment_container, navigationFragment)
                .commit();
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
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            // Back button was pressed from activity
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
        } else {
            // Back button was pressed from fragment
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onNavigationFragmentPreGameClicked() {
        PreGameFragment fragment = new PreGameFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.full_screen_fragment_container, fragment, "PREGAME_FRAGMENT")
                .commit();
    }

    @Override
    public void onNavigationFragmentAnnouncementsClicked() {
        AnnouncementsFragment fragment = new AnnouncementsFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.full_screen_fragment_container, fragment, "ANNOUNCEMENTS_FRAGMENT")
                .commit();
    }

    @Override
    public void onNavigationFragmentLeaderboardsClicked() {
        Toast.makeText(MainActivity.this, "Leaderboards Clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNavigationFragmentSettingsClicked() {
        SettingsFragment fragment = new SettingsFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.full_screen_fragment_container, fragment, "SETTINGS_FRAGMENT")
                .commit();
    }

    @Override
    public void onNavigationFragmentShopClicked() {
        ShopFragment fragment = new ShopFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.full_screen_fragment_container, fragment,
                "SHOP_FRAGMENT").commit();
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
    public void onAnnouncementsFragmentInteractionBackClicked() {
        onBackPressed();
    }

    @Override
    public void onSettingsFragmentInteractionBackClicked() {
        onBackPressed();
    }

    @Override
    public void onSettingsFragmentInteractionGetPremiumClicked() {
        ShopFragment fragment = new ShopFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.full_screen_fragment_container, fragment,
                        "SHOP_FRAGMENT").commit();
    }

    @Override
    public void onSettingsFragmentInteractionChangeThemeClicked() {
        // TODO -> Remove toast and implement the ChangeThemeFragment
        Toast.makeText(MainActivity.this, "Change Theme Clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSettingsFragmentInteractionToggleRotatingLightClicked(boolean isChecked) {
        logoLottieFragment.updateRotatingLightState(isChecked);
    }

    @Override
    public void onSettingsFragmentInteractionBlockDesignClicked() {
        BlockDesignFragment fragment = new BlockDesignFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.full_screen_fragment_container, fragment, "BLOCK_DESIGN_FRAGMENT")
                .commit();
    }

    @Override
    public void onSettingsFragmentInteractionHowToPlayClicked() {
        // TODO -> Remove toast and implement the 'How To Play' fragment
        Toast.makeText(MainActivity.this, "'How To Play' button clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSettingsFragmentInteractionHelpClicked() {
        // TODO -> Remove toast and implement the 'Help' fragment where we would answer FAQs
        Toast.makeText(MainActivity.this, "'Help' button clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBlockDesignFragmentInteractionBackClicked() {
        onBackPressed();
    }

    @Override
    public void onShopFragmentInteractionBackClicked() {
        onBackPressed();
    }
}