package com.javinindia.individualuser.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.javinindia.individualuser.R;
import com.javinindia.individualuser.apiparsing.mallListParsing.MallDetail;
import com.javinindia.individualuser.apiparsing.mallListParsing.MallListResponseParsing;
import com.javinindia.individualuser.constant.Constants;
import com.javinindia.individualuser.font.FontAsapRegularSingleTonClass;
import com.javinindia.individualuser.location.NewLoc;
import com.javinindia.individualuser.preference.SharedPreferencesManager;
import com.javinindia.individualuser.recyclerview.MallAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Ashish on 11-11-2016.
 */
public class FavoriteMallFragment extends BaseFragment implements View.OnClickListener, MallAdapter.MyClickListener, MallDetailTabBarFragment.OnCallBackMallFavListener, TextWatcher {
    private MallAdapter adapter;
    private RecyclerView recyclerview;
    private int startLimit = 0;
    private int countLimit = 10;
    private boolean loading = true;
    private RequestQueue requestQueue;
    LinearLayout llSearch;
    AppCompatEditText etSearch;
    NewLoc gps;
    double latitude = 0.0;
    double longitude = 0.0;
    AppCompatTextView txtDataNotFound;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity.getSupportActionBar().show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        disableTouchOfBackFragment(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getFragmentLayout(), container, false);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initialize(view);
        getLocationMethod();
        return view;
    }

    private void getLocationMethod() {
        gps = new NewLoc(activity);
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();
        sendRequestOnMallListFeed(0, 10, latitude, longitude);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void sendRequestOnMallListFeed(final int AstartLimit, final int AcountLimit, final double latitude, final double longitude) {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.FAVORITE_MALL_LIST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        MallListResponseParsing responseparsing = new MallListResponseParsing();
                        responseparsing.responseParseMethod(response);
                        ArrayList arrayList = responseparsing.getMallDetailsArrayList();
                        if (responseparsing.getStatus() == 1) {
                            if (arrayList.size() > 0) {
                                txtDataNotFound.setVisibility(View.GONE);
                                llSearch.setVisibility(View.VISIBLE);
                                if (adapter.getData() != null && adapter.getData().size() > 0) {
                                    adapter.getData().addAll(arrayList);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    adapter.setData(arrayList);
                                    adapter.notifyDataSetChanged();

                                }
                            }
                        } else {
                            txtDataNotFound.setVisibility(View.VISIBLE);
                            llSearch.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        volleyErrorHandle(error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", SharedPreferencesManager.getUserID(activity));
                params.put("startlimit", String.valueOf(AstartLimit));
                params.put("countlimit", String.valueOf(AcountLimit));
                params.put("lat", String.valueOf(latitude));
                params.put("long", String.valueOf(longitude));
                return params;
            }

        };
        stringRequest.setTag(this.getClass().getSimpleName());
        volleyDefaultTimeIncreaseMethod(stringRequest);
        requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(stringRequest);
    }


    private void initialize(View view) {
        progressBar = (ProgressBar)view.findViewById(R.id.progress);
        recyclerview = (RecyclerView) view.findViewById(R.id.recyclerviewFavMall);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerview.setLayoutManager(layoutManager);
        adapter = new MallAdapter(activity);
        LinearLayoutManager layoutMangerDestination
                = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        recyclerview.setLayoutManager(layoutMangerDestination);
        recyclerview.addOnScrollListener(new mallScrollListener());
        recyclerview.setAdapter(adapter);
        adapter.setMyClickListener(FavoriteMallFragment.this);

        txtDataNotFound = (AppCompatTextView) view.findViewById(R.id.txtDataNotFound);
        txtDataNotFound.setTypeface(FontAsapRegularSingleTonClass.getInstance(activity).getTypeFace());
        llSearch = (LinearLayout) view.findViewById(R.id.llSearch);

        etSearch = (AppCompatEditText) view.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(this);
    }


    @Override
    protected int getFragmentLayout() {
        return R.layout.favorite_mall_layout;
    }

    @Override
    public int getToolbarMenu() {
        return 0;
    }

    @Override
    public void onNetworkConnected() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }

    }

    @Override
    public void onFavourite(int position, MallDetail modal) {
        int fav = modal.getFavStatus();
        String mallId = modal.getId().trim();
        String uId = SharedPreferencesManager.getUserID(activity);
        if (fav == 0) {
            String Yes = "1";
            favHitOnApi(uId, mallId, Yes, position);
        } else {
            String No = "0";
            favHitOnApi(uId, mallId, No, position);
        }
    }

    @Override
    public void onItemClick(int position, MallDetail model) {
        int pos = position;
        String mallId = model.getId().trim();
        String mallName = model.getMallName().trim();
        String mallRating = model.getRating().trim();
        double distance = model.getDistance();
        String mallPic = model.getMallPic().trim();
        int favStatus = model.getFavStatus();
        int totalOffer = model.getOfferCount();
        String address = "";

        String mallLandmark = model.getMallLandmark().trim();
        String city = model.getCity().trim();
        final ArrayList<String> data = new ArrayList<>();
        if (!TextUtils.isEmpty(mallLandmark)) {
            data.add(mallLandmark);
        }
        if (!TextUtils.isEmpty(city)) {
            data.add(city);
        }

        if (data.size() > 0) {
            String str = Arrays.toString(data.toArray());
            address = str.replaceAll("[\\[\\](){}]", "");
        }

        MallDetailTabBarFragment fragment1 = new MallDetailTabBarFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("pos", pos);
        bundle.putString("mallId", mallId);
        bundle.putString("mallName", mallName);
        bundle.putString("mallRating", mallRating);
        bundle.putDouble("distance", distance);
        bundle.putString("mallPic", mallPic);
        bundle.putInt("favStatus", favStatus);
        bundle.putInt("totalOffer", totalOffer);
        bundle.putString("address", address);
        fragment1.setArguments(bundle);
        SharedPreferencesManager.setMAllId(activity, mallId);
        fragment1.setMyCallBackMallFavListener(this);
        Constants.VIEW_PAGER_MALL_CURRENT_POSITION = 1;
        callFragmentMethod(fragment1, this.getClass().getSimpleName(), R.id.navigationContainer);
    }

    @Override
    public void onMallNameClick(int position, MallDetail modal) {
        int pos = position;
        String mallId = modal.getId().trim();
        String mallName = modal.getMallName().trim();
        String mallRating = modal.getRating().trim();
        double distance = modal.getDistance();
        String mallPic = modal.getMallPic().trim();
        int favStatus = modal.getFavStatus();
        int totalOffer = modal.getOfferCount();
        String address = "";

        String mallLandmark = modal.getMallLandmark().trim();
        String city = modal.getCity().trim();
        final ArrayList<String> data = new ArrayList<>();
        if (!TextUtils.isEmpty(mallLandmark)) {
            data.add(mallLandmark);
        }
        if (!TextUtils.isEmpty(city)) {
            data.add(city);
        }

        if (data.size() > 0) {
            String str = Arrays.toString(data.toArray());
            address = str.replaceAll("[\\[\\](){}]", "");
        }

        MallDetailTabBarFragment fragment1 = new MallDetailTabBarFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("pos", pos);
        bundle.putString("mallId", mallId);
        bundle.putString("mallName", mallName);
        bundle.putString("mallRating", mallRating);
        bundle.putDouble("distance", distance);
        bundle.putString("mallPic", mallPic);
        bundle.putInt("favStatus", favStatus);
        bundle.putInt("totalOffer", totalOffer);
        bundle.putString("address", address);
        fragment1.setArguments(bundle);
        SharedPreferencesManager.setMAllId(activity, mallId);
        fragment1.setMyCallBackMallFavListener(this);
        Constants.VIEW_PAGER_MALL_CURRENT_POSITION = 0;
        callFragmentMethod(fragment1, this.getClass().getSimpleName(), R.id.navigationContainer);
    }

    @Override
    public void onDirectionClick(int position, MallDetail modal) {
        String mallLat = modal.getMallLat().trim();
        String mallLong = modal.getMallLong().trim();
        String mallName = modal.getMallName().trim();
        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", Double.parseDouble(mallLat), Double.parseDouble(mallLong), mallName);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        activity.startActivity(intent);
    }

    @Override
    public void onAddressClick(int position, MallDetail mallDetail) {
        String mallName = mallDetail.getMallName().trim();
        String mallAddress = mallDetail.getMallAddress().trim();
        String mallLandmark = mallDetail.getMallLandmark().trim();
        String state = mallDetail.getState().trim();
        String city = mallDetail.getCity().trim();
        String pinCode = mallDetail.getPinCode().trim();
        double distance = mallDetail.getDistance();
        final ArrayList<String> data = new ArrayList<>();
        if (!TextUtils.isEmpty(mallAddress)) {
            data.add(mallAddress);
        }
        if (!TextUtils.isEmpty(mallLandmark)) {
            data.add(mallLandmark);
        }
        if (!TextUtils.isEmpty(city)) {
            data.add(city);
        }
        if (!TextUtils.isEmpty(state)) {
            data.add(state);
        }
        if (!TextUtils.isEmpty(pinCode)) {
            data.add(pinCode);
        }

        if (data.size() > 0) {
            String str = Arrays.toString(data.toArray());
            String test = str.replaceAll("[\\[\\](){}]", "");
            showNewDialog(mallName, test);
        } else {
            // viewHolder.txtAddress.setText("Address: Not found");
        }
    }

    private void favHitOnApi(final String uId, final String mallId, final String no, final int position) {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.ADD_FAVORITE_MALL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        String userid = null, msg = null, username = null, password = null, mallid = null, otp = null;
                        int status = 0, action = 0;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.has("status"))
                                status = jsonObject.optInt("status");
                            if (jsonObject.has("msg"))
                                msg = jsonObject.optString("msg");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (status == 1) {
                            if (jsonObject.has("userid"))
                                userid = jsonObject.optString("userid");
                            if (jsonObject.has("mallid"))
                                mallid = jsonObject.optString("mallid");
                            if (jsonObject.has("action"))
                                action = jsonObject.optInt("action");
                            List list = adapter.getData();
                            MallDetail wd = (MallDetail) list.get(position);
                            wd.setFavStatus(action);
                            adapter.notifyItemChanged(position);

                        } else {
                            if (!TextUtils.isEmpty(msg)) {
                                //  showDialogMethod(msg);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        noInternetToast(error);
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", uId);
                params.put("mallid", mallId);
                params.put("status", no);
                return params;
            }

        };
        stringRequest.setTag(this.getClass().getSimpleName());
        volleyDefaultTimeIncreaseMethod(stringRequest);
        requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(stringRequest);
    }

    @Override
    public void OnCallBackMallfav(int pos, int action) {
        List list = adapter.getData();
        MallDetail wd = (MallDetail) list.get(pos);
        wd.setFavStatus(action);
        adapter.notifyItemChanged(pos);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String text = s.toString().toLowerCase(Locale.getDefault());
        if (etSearch.getText().toString().length() > 0) {
            adapter.filter(text);
        }
    }


    public class mallScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            LinearLayoutManager recyclerLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int visibleItemCount = recyclerView.getChildCount();
            int totalItemCount = recyclerLayoutManager.getItemCount();

            int visibleThreshold = ((totalItemCount / 2) < 20) ? totalItemCount / 2 : 20;
            int firstVisibleItem = recyclerLayoutManager.findFirstVisibleItemPosition();

            if (loading) {
                if (totalItemCount > startLimit) {
                    loading = false;
                    startLimit = totalItemCount;
                }
            } else {
                int nonVisibleItemCounts = totalItemCount - visibleItemCount;
                int effectiveVisibleThreshold = firstVisibleItem + visibleThreshold;

                if (nonVisibleItemCounts <= effectiveVisibleThreshold) {
                    startLimit = startLimit + 1;
                    countLimit = 10;

                    showLoader();

                    sendRequestOnMallListFeed(startLimit, countLimit, latitude, longitude);
                    loading = true;
                }
            }
            super.onScrollStateChanged(recyclerView, newState);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        etSearch.setText("");
    }
}
