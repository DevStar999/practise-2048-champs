package com.example.practise2048champs.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.practise2048champs.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ShopFragment extends Fragment {
    private Context context;
    private OnShopFragmentInteractionListener mListener;
    private SharedPreferences sharedPreferences;
    private AppCompatTextView currentCoinsTextView;
    private AppCompatImageView backButton;
    private AppCompatButton restorePurchase;

    private List<ConstraintLayout> shopCoinsConstraintLayouts;

    private List<AppCompatButton> shopCoinsPurchaseButtons;

    public ShopFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void settingOnClickListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onShopFragmentInteractionBackClicked();
                }
            }
        });
        restorePurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onShopFragmentInteractionRestorePurchaseClicked();
                }
            }
        });

        for (int index = 0; index < shopCoinsConstraintLayouts.size(); index++) {
            shopCoinsConstraintLayouts.get(index).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onShopFragmentInteractionPurchaseOptionClicked(view.getId());
                    }
                }
            });
        }

        for (int index = 0; index < shopCoinsPurchaseButtons.size(); index++) {
            shopCoinsPurchaseButtons.get(index).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onShopFragmentInteractionPurchaseOptionClicked(view.getId());
                    }
                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        requireActivity().getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        sharedPreferences = context.getSharedPreferences("com.nerdcoredevelopment.game2048champsfinal", Context.MODE_PRIVATE);

        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        currentCoinsTextView = view.findViewById(R.id.current_coins_shop_fragment_text_view);
        currentCoinsTextView.setText(String.valueOf(sharedPreferences.getInt("currentCoins", 2000)));
        backButton = view.findViewById(R.id.title_back_shop_fragment_button);
        restorePurchase = view.findViewById(R.id.restore_purchases_shop_fragment_button);

        shopCoinsConstraintLayouts = new ArrayList<>();
        for (int level = 1; level <= 7; level++) {
            int layoutResId = context.getResources().getIdentifier("shop_coins_level" + level +
                    "_constraint_layout", "id", context.getPackageName());
            shopCoinsConstraintLayouts.add(view.findViewById(layoutResId));
        }

        shopCoinsPurchaseButtons = new ArrayList<>();
        for (int level = 1; level <= 7; level++) {
            int layoutResId = context.getResources().getIdentifier("shop_coins_level" + level +
                    "_purchase_button", "id", context.getPackageName());
            shopCoinsPurchaseButtons.add(view.findViewById(layoutResId));
        }

        settingOnClickListeners();

        return view;
    }

    public interface OnShopFragmentInteractionListener {
        void onShopFragmentInteractionBackClicked();
        void onShopFragmentInteractionRestorePurchaseClicked();
        void onShopFragmentInteractionPurchaseOptionClicked(int purchaseOptionViewId);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnShopFragmentInteractionListener) {
            mListener = (OnShopFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnShopFragmentInteractionListener");
        }
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}