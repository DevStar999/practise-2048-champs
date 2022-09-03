package com.example.practise2048champs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

public class AnnouncementsFragment extends Fragment {
    private OnAnnouncementsFragmentInteractionListener mListener;

    public AnnouncementsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announcements, container, false);

        // Back button in the title text of the fragment
        AppCompatImageView backButton = view.findViewById(R.id.title_back_announcements_fragment_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onAnnouncementsFragmentInteractionBackClicked();
                }
            }
        });

        return view;
    }

    public interface OnAnnouncementsFragmentInteractionListener {
        void onAnnouncementsFragmentInteractionBackClicked();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnAnnouncementsFragmentInteractionListener) {
            mListener = (OnAnnouncementsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAnnouncementsFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}