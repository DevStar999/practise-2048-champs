package com.example.practise2048champs.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.practise2048champs.R;

public class NavigationFragment extends Fragment {
    private OnNavigationFragmentInteractionListener mListener;

    public NavigationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        requireActivity().getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        FrameLayout pregameFrameLayout = view.findViewById(R.id.fragment_navigation_pregame_frame_layout);
        FrameLayout announcementsFrameLayout = view.findViewById(R.id.fragment_navigation_announcements_linear_layout);
        FrameLayout leaderboardsFrameLayout = view.findViewById(R.id.fragment_navigation_leaderboards_linear_layout);
        FrameLayout settingsFrameLayout = view.findViewById(R.id.fragment_navigation_settings_linear_layout);
        FrameLayout shopFrameLayout = view.findViewById(R.id.fragment_navigation_shop_linear_layout);

        pregameFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onNavigationFragmentPreGameClicked();
                }
            }
        });
        announcementsFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onNavigationFragmentAnnouncementsClicked();
                }
            }
        });
        leaderboardsFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onNavigationFragmentLeaderboardsClicked();
                }
            }
        });
        settingsFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onNavigationFragmentSettingsClicked();
                }
            }
        });
        shopFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onNavigationFragmentShopClicked();
                }
            }
        });

        return view;
    }

    public interface OnNavigationFragmentInteractionListener {
        void onNavigationFragmentPreGameClicked();
        void onNavigationFragmentAnnouncementsClicked();
        void onNavigationFragmentLeaderboardsClicked();
        void onNavigationFragmentSettingsClicked();
        void onNavigationFragmentShopClicked();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnNavigationFragmentInteractionListener) {
            mListener = (OnNavigationFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnNavigationFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
