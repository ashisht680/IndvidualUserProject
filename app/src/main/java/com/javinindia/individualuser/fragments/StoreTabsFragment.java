package com.javinindia.individualuser.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.javinindia.individualuser.R;
import com.javinindia.individualuser.apiparsing.Singles.SingleShopResponse;
import com.javinindia.individualuser.constant.Constants;
import com.javinindia.individualuser.font.FontAsapBoldSingleTonClass;
import com.javinindia.individualuser.font.FontAsapRegularSingleTonClass;
import com.javinindia.individualuser.preference.SharedPreferencesManager;
import com.javinindia.individualuser.recyclerview.ViewPagerAdapter;
import com.javinindia.individualuser.utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ashish on 29-09-2016.
 */
public class StoreTabsFragment extends BaseFragment {
    private RequestQueue requestQueue;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    AppCompatTextView txtStoreName, txtRating, txtStoreMall, txtOffers, txtStoreAddress;
    ImageView imgStore;
    ProgressBar progress;
    CheckBox chkImageShop;
    RatingBar ratingBar;
    String shopId;
    String shopName, address,ownername;
    String shopPic;
    String shopRating;
    String mallName;
    int totalOffers = 0;
    int favStatus = 0;
    int position;
    int click = 0;

    private OnCallBackShopFavListener onCallBackShopFavListener;

    public interface OnCallBackShopFavListener {
        void OnCallBackShopFav(int pos, int action);
    }

    public void setMyCallBackShopFavListener(OnCallBackShopFavListener callback) {
        this.onCallBackShopFavListener = callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        click = getArguments().getInt("click");
        ownername = getArguments().getString("ownername");
        position = getArguments().getInt("position");
        shopId = getArguments().getString("shopId");
        shopName = getArguments().getString("shopName");
        mallName = getArguments().getString("mallName");
        shopRating = getArguments().getString("shopRating");
        shopPic = getArguments().getString("shopPic");
        favStatus = getArguments().getInt("favStatus");
        totalOffers = getArguments().getInt("totalOffers");
        address = getArguments().getString("address");
        SharedPreferencesManager.setShopId(activity, shopId);
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
        initToolbar(view);
        intialize(view);
        if (click == 0) {
            hitShopApiMethod(shopId);
        } else {
            setDateMethod();
        }
        return view;
    }

    private void hitShopApiMethod(final String shopId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SINGLE_SHOP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SingleShopResponse responseparsing = new SingleShopResponse();
                        responseparsing.responseParseMethod(response);
                        int status = responseparsing.getStatus();
                        if (status == 1) {

                            if (!TextUtils.isEmpty(responseparsing.getDetailsListArrayList().get(0).getOwnerName().trim())) {
                                shopName = responseparsing.getDetailsListArrayList().get(0).getOwnerName().trim();
                                txtStoreName.setText(Utility.fromHtml(shopName));
                            }
                            if (!TextUtils.isEmpty(responseparsing.getDetailsListArrayList().get(0).getProfilepic().trim())) {
                                shopPic = responseparsing.getDetailsListArrayList().get(0).getProfilepic().trim();
                                Utility.imageLoadGlideLibrary(activity, progress, imgStore, shopPic);
                            } else {
                                imgStore.setImageResource(R.drawable.no_image_icon);
                            }
                            if (!TextUtils.isEmpty(responseparsing.getDetailsListArrayList().get(0).getMallName().trim())) {
                                mallName = responseparsing.getDetailsListArrayList().get(0).getMallName().trim();
                                txtStoreMall.setText(Utility.fromHtml(mallName));
                            } else {
                                txtStoreMall.setText("No Mall Detail");
                            }
                            if (responseparsing.getDetailsListArrayList().get(0).getShopOfferCount() != 0) {
                                totalOffers = responseparsing.getDetailsListArrayList().get(0).getShopOfferCount();
                                if (totalOffers == 1) {
                                    txtOffers.setText(totalOffers + " offer");
                                } else {
                                    txtOffers.setText(totalOffers + " offers");
                                }
                            } else {
                                txtOffers.setText("No offers");
                            }

                            if (!TextUtils.isEmpty(responseparsing.getDetailsListArrayList().get(0).getRating().trim())) {
                                shopRating = responseparsing.getDetailsListArrayList().get(0).getRating().trim();
                                txtRating.setText("Rating: 3.5/5");
                                ratingBar.setRating(Float.parseFloat("3.5"));
                            } else {
                                txtRating.setText("Rating: 3.5/5");
                                ratingBar.setRating((float) 3.5);
                            }

                            final ArrayList<String> data = new ArrayList<>();
                            if (!TextUtils.isEmpty(responseparsing.getDetailsListArrayList().get(0).getShopNo().trim())) {
                                data.add(responseparsing.getDetailsListArrayList().get(0).getShopNo().trim());
                            }
                            if (!TextUtils.isEmpty(responseparsing.getDetailsListArrayList().get(0).getCity().trim())) {
                                data.add(responseparsing.getDetailsListArrayList().get(0).getCity().trim());
                            }
                            if (!TextUtils.isEmpty(responseparsing.getDetailsListArrayList().get(0).getState().trim())) {
                                data.add(responseparsing.getDetailsListArrayList().get(0).getState().trim());
                            }
                            if (!TextUtils.isEmpty(responseparsing.getDetailsListArrayList().get(0).getFloor().trim())) {
                                data.add(responseparsing.getDetailsListArrayList().get(0).getFloor().trim());
                            }

                            if (data.size() > 0) {
                                String str = Arrays.toString(data.toArray());
                                address = str.replaceAll("[\\[\\](){}]", "");
                            }
                            if (!TextUtils.isEmpty(address)) {
                                txtStoreAddress.setText(Utility.fromHtml(address));
                            }
                            if (responseparsing.getDetailsListArrayList().get(0).getFavStatus() == 1) {
                                favStatus = responseparsing.getDetailsListArrayList().get(0).getFavStatus();
                                chkImageShop.setChecked(true);
                            } else {
                                chkImageShop.setChecked(false);
                            }

                            chkImageShop.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String uId = SharedPreferencesManager.getUserID(activity);
                                    if (favStatus == 0) {
                                        String Yes = "1";
                                        favHitOnApi(uId, shopId, Yes);
                                    } else {
                                        String No = "0";
                                        favHitOnApi(uId, shopId, No);
                                    }
                                }
                            });

                        } else {

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
                String uid = SharedPreferencesManager.getUserID(activity);
                params.put("uid", uid);
                params.put("shopid", shopId);
                return params;
            }

        };
        stringRequest.setTag(this.getClass().getSimpleName());
        volleyDefaultTimeIncreaseMethod(stringRequest);
        requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(stringRequest);
    }

    private void initToolbar(View view) {
        if (!TextUtils.isEmpty(ownername)) {
            setToolbarTitle(ownername);
        } else {
            setToolbarTitle("Seller");
        }
    }

    private void setDateMethod() {
        if (!TextUtils.isEmpty(ownername)) {
            txtStoreName.setText(Utility.fromHtml(ownername));
        }
        if (!TextUtils.isEmpty(shopPic)) {
            Utility.imageLoadGlideLibrary(activity, progress, imgStore, shopPic);
        } else {
            imgStore.setImageResource(R.drawable.no_image_icon);
        }
        if (!TextUtils.isEmpty(mallName)) {
            txtStoreMall.setText(Utility.fromHtml(mallName));
        } else {
            txtStoreMall.setText("No Mall Detail");
        }
        if (totalOffers != 0) {
            if (totalOffers == 1) {
                txtOffers.setText(totalOffers + " offer");
            } else {
                txtOffers.setText(totalOffers + " offers");
            }
        } else {
            txtOffers.setText("No offers");
        }

        if (!TextUtils.isEmpty(shopRating)) {
            txtRating.setText("Rating: 3.5/5");
            ratingBar.setRating(Float.parseFloat("3.5"));
        } else {
            txtRating.setText("Rating: 3.5/5");
            ratingBar.setRating((float) 3.5);
        }

        if (!TextUtils.isEmpty(address)) {
            txtStoreAddress.setText(Utility.fromHtml(address));
        }
        if (favStatus == 1) {
            chkImageShop.setChecked(true);
        } else {
            chkImageShop.setChecked(false);
        }

        chkImageShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uId = SharedPreferencesManager.getUserID(activity);
                if (favStatus == 0) {
                    String Yes = "1";
                    favHitOnApi(uId, shopId, Yes);
                } else {
                    String No = "0";
                    favHitOnApi(uId, shopId, No);
                }
            }
        });
    }

    private void intialize(View view) {
        txtStoreName = (AppCompatTextView) view.findViewById(R.id.txtStoreName);
        txtStoreName.setTypeface(FontAsapBoldSingleTonClass.getInstance(activity).getTypeFace());
        txtRating = (AppCompatTextView) view.findViewById(R.id.txtRating);
        txtRating.setTypeface(FontAsapRegularSingleTonClass.getInstance(activity).getTypeFace());
        txtStoreMall = (AppCompatTextView) view.findViewById(R.id.txtStoreMall);
        txtStoreMall.setTypeface(FontAsapRegularSingleTonClass.getInstance(activity).getTypeFace());
        txtOffers = (AppCompatTextView) view.findViewById(R.id.txtOffers);
        txtOffers.setTypeface(FontAsapRegularSingleTonClass.getInstance(activity).getTypeFace());
        txtStoreAddress = (AppCompatTextView) view.findViewById(R.id.txtStoreAddress);
        txtStoreAddress.setTypeface(FontAsapRegularSingleTonClass.getInstance(activity).getTypeFace());
        imgStore = (ImageView) view.findViewById(R.id.imgStore);
        progress = (ProgressBar) view.findViewById(R.id.progress);
        chkImageShop = (CheckBox) view.findViewById(R.id.chkImageShop);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        //   viewPager.setCurrentItem(Constants.VIEW_PAGER_CURRENT_POSITION);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        imgStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });
    }

    private void favHitOnApi(final String uId, final String shopId, final String yes) {
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
                            onCallBackShopFavListener.OnCallBackShopFav(position, action);
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
    protected int getFragmentLayout() {
        return R.layout.store_tab_layout;
    }

    @Override
    public int getToolbarMenu() {
        return 0;
    }

    @Override
    public void onNetworkConnected() {

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new StoreOffersFragment(), "Offers");
        //  adapter.addFragment(new StoreEventsFragment(), "Events");
        viewPager.setAdapter(adapter);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu != null)
            menu.clear();
    }
}
