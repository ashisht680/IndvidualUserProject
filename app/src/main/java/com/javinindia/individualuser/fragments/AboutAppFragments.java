package com.javinindia.individualuser.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.javinindia.individualuser.R;
import com.javinindia.individualuser.font.FontAsapBoldSingleTonClass;
import com.javinindia.individualuser.font.FontAsapRegularSingleTonClass;


/**
 * Created by Ashish on 14-12-2016.
 */

public class AboutAppFragments extends BaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getFragmentLayout(), container, false);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initToolbar(view);
        AppCompatTextView txtTitle,txtQuestion,txtAns,txtPoints;
        txtTitle = (AppCompatTextView)view.findViewById(R.id.txtTitle);
        txtTitle.setTypeface(FontAsapBoldSingleTonClass.getInstance(activity).getTypeFace());
        txtQuestion = (AppCompatTextView)view.findViewById(R.id.txtQuestion);
        txtQuestion.setTypeface(FontAsapBoldSingleTonClass.getInstance(activity).getTypeFace());
        txtAns = (AppCompatTextView)view.findViewById(R.id.txtAns);
        txtAns.setTypeface(FontAsapRegularSingleTonClass.getInstance(activity).getTypeFace());
        txtPoints = (AppCompatTextView)view.findViewById(R.id.txtPoints);
        txtPoints.setTypeface(FontAsapRegularSingleTonClass.getInstance(activity).getTypeFace());
        return view;
    }
    private void initToolbar(View view) {
        setToolbarTitle("About application");
    }
    @Override
    protected int getFragmentLayout() {
        return R.layout.abut_app_layout;
    }

    @Override
    public int getToolbarMenu() {
        return 0;
    }

    @Override
    public void onNetworkConnected() {

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu != null)
            menu.clear();
    }
}
