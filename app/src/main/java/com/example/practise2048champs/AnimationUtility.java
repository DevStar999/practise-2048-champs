package com.example.practise2048champs;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.List;

public class AnimationUtility {
    public static void gamePreviewShrinkAnimation(LottieAnimationView rotatingLightView, AppCompatImageView gameImage,
                                                  AppCompatImageView modeLeft, AppCompatImageView modeRight,
                                                  AppCompatImageView sizeLeft, AppCompatImageView sizeRight,
                                                  int duration, List<Boolean> gamePreviewAnimationsDone) {
        ObjectAnimator gameImageShrinkXAnimator =
                ObjectAnimator.ofFloat(gameImage, View.SCALE_X, 1f, 0.5f).setDuration(duration);
        ObjectAnimator gameImageShrinkYAnimator =
                ObjectAnimator.ofFloat(gameImage, View.SCALE_Y, 1f, 0.5f).setDuration(duration);
        ObjectAnimator rotatingLightViewFade =
                ObjectAnimator.ofFloat(rotatingLightView, View.ALPHA, 1f, 0f).setDuration(duration);

        AnimatorSet setupGamePreviewAnimator = new AnimatorSet();
        setupGamePreviewAnimator.playTogether(gameImageShrinkXAnimator, gameImageShrinkYAnimator, rotatingLightViewFade);
        setupGamePreviewAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                modeLeft.setClickable(false); modeRight.setClickable(false);
                sizeLeft.setClickable(false); sizeRight.setClickable(false);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                gamePreviewAnimationsDone.set(0, true);
            }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        setupGamePreviewAnimator.start();
    }

    public static void gamePreviewEmergeAnimation(LottieAnimationView rotatingLightView, AppCompatImageView gameImage,
                                                  AppCompatImageView modeLeft, AppCompatImageView modeRight,
                                                  AppCompatImageView sizeLeft, AppCompatImageView sizeRight,
                                                  int duration, List<Boolean> gamePreviewAnimationsDone) {
        ObjectAnimator gameImageEmergeXAnimator =
                ObjectAnimator.ofFloat(gameImage, View.SCALE_X, 0.5f, 1f).setDuration(duration);
        ObjectAnimator gameImageEmergeYAnimator =
                ObjectAnimator.ofFloat(gameImage, View.SCALE_Y, 0.5f, 1f).setDuration(duration);
        ObjectAnimator rotatingLightViewAppear =
                ObjectAnimator.ofFloat(rotatingLightView, View.ALPHA, 0f, 1f).setDuration(duration);

        AnimatorSet setupGamePreviewAnimator = new AnimatorSet();
        setupGamePreviewAnimator.playTogether(gameImageEmergeXAnimator, gameImageEmergeYAnimator, rotatingLightViewAppear);
        setupGamePreviewAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                gamePreviewAnimationsDone.set(0, false);
                modeLeft.setClickable(true); modeRight.setClickable(true);
                sizeLeft.setClickable(true); sizeRight.setClickable(true);
            }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        setupGamePreviewAnimator.start();
    }
}
