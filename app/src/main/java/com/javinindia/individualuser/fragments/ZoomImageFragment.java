package com.javinindia.individualuser.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.javinindia.individualuser.R;
import com.javinindia.individualuser.utility.TouchImageView;
import com.javinindia.individualuser.utility.Utility;


/**
 * Created by Ashish on 15-03-2017.
 */

public class ZoomImageFragment extends BaseFragment {

    ProgressBar progressBar;
    TouchImageView imageDetail;
    String img;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        img = getArguments().getString("img");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getFragmentLayout(), container, false);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        ImageView imgBtnCancel = (ImageView) view.findViewById(R.id.imgBtnCancel);
        imgBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onBackPressed();
            }
        });
        imageDetail = (TouchImageView) view.findViewById(R.id.img);

        if (!TextUtils.isEmpty(img)) {
            Utility.imageLoadGlideLibrary(activity, progressBar, imageDetail, img);
        } else {
            imageDetail.setImageResource(R.drawable.no_image_icon);
        }

        return view;
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.image_view;
    }

    @Override
    public int getToolbarMenu() {
        return 0;
    }

    @Override
    public void onNetworkConnected() {

    }

}
