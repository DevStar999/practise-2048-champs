package com.example.practise2048champs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class BlockDesignFragment extends Fragment {
    private Context context;
    private OnBlockDesignFragmentInteractionListener mListener;
    private List<Integer> blockDesignDrawableResourceIds;

    public BlockDesignFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp *density);
    }

    private void initialiseBlockDesignOptions(View layoutView) {
        LinearLayout blockDesignOptionsScrollViewChild = layoutView
                .findViewById(R.id.block_design_options_scrollview_child_linear_layout);

        for (int i = 0; i < blockDesignDrawableResourceIds.size(); i += 3) {
            int rowNumber = (i / 3) + 1;
            RelativeLayout blockDesignOptionsRow = new RelativeLayout(context);
            blockDesignOptionsRow.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            for (int j = 0; j < 3; j++) {
                int optionNumber = j + 1;
                if (i + j >= blockDesignDrawableResourceIds.size()) {
                    continue;
                }
                FrameLayout blockDesignOption = new FrameLayout(context);
                blockDesignOption.setBackgroundResource(R.drawable.rounded_corner_block_design_option);
                RelativeLayout.LayoutParams blockDesignOptionParams =
                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                if (j == 0) {
                    blockDesignOptionParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
                } else if (j == 1) {
                    blockDesignOptionParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                } else if (j == 2) {
                    blockDesignOptionParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
                }
                blockDesignOption.setLayoutParams(blockDesignOptionParams);

                AppCompatImageView blockDesignOptionImageView = new AppCompatImageView(context);
                blockDesignOptionImageView.setBackgroundResource(R.drawable.rounded_corner_block_design_option_image_view);
                blockDesignOptionImageView.setTag("blockOption_row" + rowNumber + "_option" + optionNumber);
                blockDesignOptionImageView.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
                blockDesignOptionImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                blockDesignOptionImageView.setImageResource(blockDesignDrawableResourceIds.get(i + j));
                FrameLayout.LayoutParams blockDesignOptionImageViewParams = new FrameLayout.LayoutParams(dpToPx(72), dpToPx(72));
                blockDesignOptionImageView.setLayoutParams(blockDesignOptionImageViewParams);

                AppCompatImageView blockDesignOptionSelectedBackground = new AppCompatImageView(context);
                blockDesignOptionSelectedBackground.setVisibility(View.GONE);
                blockDesignOptionSelectedBackground.setImageResource(R.drawable.rounded_corner_block_design_option_selected);
                FrameLayout.LayoutParams blockDesignOptionSelectedBackgroundParams = new FrameLayout.LayoutParams(dpToPx(72), dpToPx(72));
                blockDesignOptionSelectedBackground.setLayoutParams(blockDesignOptionSelectedBackgroundParams);

                AppCompatImageView blockDesignOptionSelectedImageView = new AppCompatImageView(context);
                blockDesignOptionSelectedImageView.setVisibility(View.GONE);
                blockDesignOptionSelectedImageView.setScaleX(0.75f);
                blockDesignOptionSelectedImageView.setScaleY(0.75f);
                blockDesignOptionSelectedImageView.setImageResource(R.drawable.block_design_fragment_selected);
                FrameLayout.LayoutParams blockDesignOptionSelectedImageViewParams = new FrameLayout.LayoutParams(dpToPx(72), dpToPx(72));
                blockDesignOptionSelectedImageView.setLayoutParams(blockDesignOptionSelectedImageViewParams);

                blockDesignOption.addView(blockDesignOptionImageView);
                blockDesignOption.addView(blockDesignOptionSelectedBackground);
                blockDesignOption.addView(blockDesignOptionSelectedImageView);

                blockDesignOptionsRow.addView(blockDesignOption);
            }

            blockDesignOptionsScrollViewChild.addView(blockDesignOptionsRow);
        }
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

        blockDesignDrawableResourceIds = new ArrayList<>() {{
            add(R.drawable.block_alien);
            add(R.drawable.block_cell_x);
            add(R.drawable.block_heart);
            add(R.drawable.block_poop);
            add(R.drawable.block_rock);
        }};

        initialiseBlockDesignOptions(view);

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
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}