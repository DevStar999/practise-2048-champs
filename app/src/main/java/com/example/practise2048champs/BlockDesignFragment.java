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

public class BlockDesignFragment extends Fragment {
    private OnBlockDesignFragmentInteractionListener mListener;

    public BlockDesignFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_block_design, container, false);

        // Back button in the title text of the fragment
        AppCompatImageView backButton = view.findViewById(R.id.title_back_block_design_fragment_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onBlockDesignFragmentInteractionBackClicked();
                }
            }
        });

        return view;
    }

    public interface OnBlockDesignFragmentInteractionListener {
        void onBlockDesignFragmentInteractionBackClicked();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnBlockDesignFragmentInteractionListener) {
            mListener = (OnBlockDesignFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBlockDesignFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}