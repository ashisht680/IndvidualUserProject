package com.javinindia.individualuser.fragments;

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
import com.javinindia.individualuser.apiparsing.storeInMallParsing.ShopData;
import com.javinindia.individualuser.apiparsing.storeInMallParsing.StoreInMallResponse;
import com.javinindia.individualuser.constant.Constants;
import com.javinindia.individualuser.font.FontAsapRegularSingleTonClass;
import com.javinindia.individualuser.preference.SharedPreferencesManager;
import com.javinindia.individualuser.recyclerview.MallStoreAdaptar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Ashish on 11-11-2016.
 */
public class FavoriteStoreFragment extends BaseFragment implements View.OnClickListener, MallStoreAdaptar.MyClickListener, StoreTabsFragment.OnCallBackShopFavListener, TextWatcher {

    private MallStoreAdaptar adapter;
    private RecyclerView recyclerview;
    private int startLimit = 0;
    private int countLimit = 5000;
    private boolean loading = true;
    private RequestQueue requestQueue;
    String mallId;
    AppCompatTextView txtDataNotFound;
    LinearLayout llSearch;
    AppCompatEditText etSearch;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getFragmentLayout(), container, false);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initialize(view);
        sendRequestOnStoreInMallListFeed(startLimit, countLimit);
        return view;
    }

    private void sendRequestOnStoreInMallListFeed(final int AstartLimit, final int AcountLimit) {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.FAVORITE_SHOP_LIST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        StoreInMallResponse responseparsing = new StoreInMallResponse();
                        responseparsing.responseParseMethod(response);
                        if (response.length() != 0) {
                            if (responseparsing.getStatus() == 1) {
                                if (responseparsing.getShopDataArrayList().size() > 0) {
                                    ArrayList arrayList = responseparsing.getShopDataArrayList();
                                    if (arrayList.size() > 0) {
                                        txtDataNotFound.setVisibility(View.GONE);
                                        llSearch.setVisibility(View.VISIBLE);
                                        adapter.setData(arrayList);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        txtDataNotFound.setVisibility(View.VISIBLE);
                                        llSearch.setVisibility(View.GONE);
                                    }
                                } else {
                                    txtDataNotFound.setVisibility(View.VISIBLE);
                                    llSearch.setVisibility(View.GONE);
                                }

                            } else {
                                txtDataNotFound.setVisibility(View.VISIBLE);
                                llSearch.setVisibility(View.GONE);
                            }
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
                return params;
            }

        };
        stringRequest.setTag(this.getClass().getSimpleName());
        volleyDefaultTimeIncreaseMethod(stringRequest);
        requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        disableTouchOfBackFragment(savedInstanceState);
    }

    private void initialize(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        recyclerview = (RecyclerView) view.findViewById(R.id.recyclerviewStores);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerview.setLayoutManager(layoutManager);

        adapter = new MallStoreAdaptar(activity);
        LinearLayoutManager layoutMangerDestination
                = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        recyclerview.setLayoutManager(layoutMangerDestination);
        recyclerview.setAdapter(adapter);
        adapter.setMyClickListener(FavoriteStoreFragment.this);

        txtDataNotFound = (AppCompatTextView) view.findViewById(R.id.txtDataNotFound);
        txtDataNotFound.setTypeface(FontAsapRegularSingleTonClass.getInstance(activity).getTypeFace());
        llSearch = (LinearLayout) view.findViewById(R.id.llSearch);

        etSearch = (AppCompatEditText) view.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(this);

    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.favorite_store_layout;
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

    }

    @Override
    public void onItemClick(int position, ShopData model) {
        String shopId = model.getId().trim();
        SharedPreferencesManager.setShopId(activity, shopId);
        String ownername = model.getOwnerName().trim();
        String mallId = model.getMall().trim();
        SharedPreferencesManager.setMAllId(activity,mallId);
        String shopName = model.getStoreName().trim();
        String shopPic = model.getBanner().trim();
        String shopRating = model.getRating().trim();
        String mallName = model.getMallName().trim();
        int totalOffers = model.getShopOfferCount();
        int favStatus = model.getFavStatus();
        StoreTabsFragment fragment = new StoreTabsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putString("ownername",ownername);
        bundle.putString("shopId", shopId);
        bundle.putString("shopName", shopName);
        bundle.putString("shopPic", shopPic);
        bundle.putString("mallName", mallName);
        bundle.putString("shopRating", shopRating);
        bundle.putInt("totalOffers", totalOffers);
        bundle.putInt("favStatus", favStatus);
        fragment.setArguments(bundle);
        fragment.setMyCallBackShopFavListener(this);
        callFragmentMethod(fragment, this.getClass().getSimpleName(), R.id.navigationContainer);
    }

    @Override
    public void onShopFavClick(int position, ShopData model) {
        int fav = model.getFavStatus();
        String shopId = model.getId().trim();
        String uId = SharedPreferencesManager.getUserID(activity);
        if (fav == 0) {
            String Yes = "1";
            favHitOnApi(uId, shopId, Yes, position);
        } else {
            String No = "0";
            favHitOnApi(uId, shopId, No, position);
        }
    }

    private void favHitOnApi(final String uId, final String shopId, final String yes, final int position) {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.ADD_FAVORITE_SHOP_URL,
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
                            if (jsonObject.has("shopid"))
                                mallid = jsonObject.optString("shopid");
                            if (jsonObject.has("action"))
                                action = jsonObject.optInt("action");
                            List list = adapter.getData();
                            ShopData wd = (ShopData) list.get(position);
                            wd.setFavStatus(action);
                            adapter.notifyItemChanged(position);

                        } else {
                            if (!TextUtils.isEmpty(msg)) {
                                // showDialogMethod(msg);
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
                params.put("shopid", shopId);
                params.put("status", yes);
                return params;
            }

        };
        stringRequest.setTag(this.getClass().getSimpleName());
        volleyDefaultTimeIncreaseMethod(stringRequest);
        requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(stringRequest);
    }

    @Override
    public void OnCallBackShopFav(int pos, int action) {
        List list = adapter.getData();
        ShopData wd = (ShopData) list.get(pos);
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

    @Override
    public void onPause() {
        super.onPause();
        etSearch.setText("");
    }
}