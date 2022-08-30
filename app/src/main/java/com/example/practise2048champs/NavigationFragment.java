package com.example.practise2048champs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

public class NavigationFragment extends Fragment {
    private OnNavigationFragmentInteractionListener mListener;
    private AppCompatButton pregameFragmentButton;
    private AppCompatButton announcementsFragmentButton;
    private AppCompatButton settingsFragmentButton;

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
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        pregameFragmentButton = view.findViewById(R.id.pregame_fragment_temp_button);
        announcementsFragmentButton = view.findViewById(R.id.announcements_fragment_temp_button);
        settingsFragmentButton = view.findViewById(R.id.settings_fragment_temp_button);

        pregameFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onNavigationFragmentPreGameClicked();
                }
            }
        });
        announcementsFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onNavigationFragmentAnnouncementsClicked();
                }
            }
        });
        settingsFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onNavigationFragmentSettingsClicked();
                }
            }
        });

        return view;
    }

    public interface OnNavigationFragmentInteractionListener {
        void onNavigationFragmentSettingsClicked();
        void onNavigationFragmentAnnouncementsClicked();
        void onNavigationFragmentPreGameClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNavigationFragmentInteractionListener) {
            mListener = (OnNavigationFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
