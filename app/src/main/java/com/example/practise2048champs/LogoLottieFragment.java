package com.example.practise2048champs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;

public class LogoLottieFragment extends Fragment {
    private LottieAnimationView gamePreviewSpotlightLottie;

    public LogoLottieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logo_lottie, container, false);

        gamePreviewSpotlightLottie = view.findViewById(R.id.game_preview_spotlight_lottie);

        return view;
    }

    public void updateRotatingLightState(boolean isChecked) {
        if (isChecked) {
            gamePreviewSpotlightLottie.playAnimation();
        } else {
            gamePreviewSpotlightLottie.pauseAnimation();
        }
    }
}