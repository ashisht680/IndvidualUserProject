package com.javinindia.individualuser.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.javinindia.individualuser.R;
import com.javinindia.individualuser.utility.Utility;


/**
 * Created by Ashish on 15-03-2017.
 */

public class ZoomImageFragment extends BaseFragment {

    private Bitmap myBitmap;
    Bitmap image1;
    ProgressBar progressBar;


    ImageView imageDetail;
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    PointF BigenPoint = new PointF();
    PointF startPoint = new PointF();
    PointF midPoint = new PointF();
    float oldDist = 1f;

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;
    float a1;
    float a2;
    boolean status2=false;

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
        ImageView imgBtnCancel = (ImageView)view.findViewById(R.id.imgBtnCancel);
        imgBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onBackPressed();
            }
        });
        imageDetail = (ImageView) view.findViewById(R.id.img);

        if (!TextUtils.isEmpty(img)){
            Utility.imageLoadGlideLibrary(activity, progressBar, imageDetail, img);
        }else {
            imageDetail.setImageResource(R.drawable.no_image_icon);
        }


        imageDetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) v;
               // Log.e("matrix=" , savedMatrix.toString());
                BigenPoint.set(event.getX(), event.getY());

                if(status2==false) {
                    a1 = event.getX();
                    a2 = event.getY();
                    status2=true;
                }

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        savedMatrix.set(matrix);
                        startPoint.set(event.getX(), event.getY());
                        mode = DRAG;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        if (oldDist > 10f) {
                            savedMatrix.set(matrix);
                            midPoint(midPoint, event);
                            mode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            matrix.set(savedMatrix);
                            matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
                        } else if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist > 250f) {

                                Log.e("zoom",a1+"\t"+midPoint.x+"\t"+a2+"\t"+midPoint.y+"\t"+newDist+"\t"+newDist / oldDist+" ");
                              // if (midPoint.x<a1 && midPoint.y<a2){
                                    matrix.set(savedMatrix);
                                    float scale = newDist / oldDist;
                                    matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                              //  }

                            }
                        }
                        break;
                }
                view.setImageMatrix(matrix);
                return true;
            }

            @SuppressLint("FloatMath")
            private float spacing(MotionEvent event) {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                return(float)Math.sqrt(x * x + y * y);
               // return FloatMath.sqrt(x * x + y * y);
            }

            private void midPoint(PointF point, MotionEvent event) {
                float x = event.getX(0) + event.getX(1);
                float y = event.getY(0) + event.getY(1);
                point.set(x / 2, y / 2);
            }
        });

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
