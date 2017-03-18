package com.javinindia.individualuser.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.javinindia.individualuser.R;
import com.javinindia.individualuser.activity.LoginActivity;
import com.javinindia.individualuser.activity.NavigationActivity;
import com.javinindia.individualuser.constant.Constants;
import com.javinindia.individualuser.font.FontAsapBoldSingleTonClass;
import com.javinindia.individualuser.font.FontAsapRegularSingleTonClass;
import com.javinindia.individualuser.picasso.CircleTransform;
import com.javinindia.individualuser.preference.SharedPreferencesManager;
import com.javinindia.individualuser.recyclerview.CustomSpinnerAdater;
import com.javinindia.individualuser.recyclerview.ViewPagerAdapter;
import com.javinindia.individualuser.utility.CheckConnection;
import com.javinindia.individualuser.utility.Utility;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ashish on 08-09-2016.
 */
public class HomeFragment extends BaseFragment implements NavigationAboutFragment.OnCallBackRefreshListener, CheckConnectionFragment.OnCallBackInternetListener {
    private RequestQueue requestQueue;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    TextView txtCityName;

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
        setupDrawerLayout(view);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        if (!TextUtils.isEmpty(SharedPreferencesManager.getDeviceToken(activity))) {
            Log.d("home token", SharedPreferencesManager.getDeviceToken(activity));
        } else {
            Log.d("home token", "not found");
        }
        return view;
    }

    private void initToolbar(View view) {
        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbarHome);
        activity.setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        final ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(null);
        txtCityName = (TextView) view.findViewById(R.id.txtCityName);
        txtCityName.setTypeface(FontAsapRegularSingleTonClass.getInstance(activity).getTypeFace());
        if (!TextUtils.isEmpty(SharedPreferencesManager.getCity(activity)) && !SharedPreferencesManager.getCity(activity).equals(null)) {
            txtCityName.setText(SharedPreferencesManager.getCity(activity));
        }
        txtCityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methodCityDialog();
            }
        });
    }

    private void methodCityDialog() {


        TextView content = new TextView(activity);
        content.setTypeface(FontAsapBoldSingleTonClass.getInstance(activity).getTypeFace());
        final String stateArray[] = {"Delhi NCR", "Mumbai", "Bengaluru", "Chennai", "Kolkata"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setView(content);
        builder.setTitle("SELECT CITY");
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setItems(stateArray, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (!stateArray[item].equals(SharedPreferencesManager.getCity(activity))) {
                    SharedPreferencesManager.setCity(activity, stateArray[item]);
                    Intent refresh = new Intent(activity, NavigationActivity.class);
                    startActivity(refresh);
                    activity.finish();
                }
                Toast.makeText(activity, "Selected  : " + stateArray[item], Toast.LENGTH_LONG).show();
            }
        });
        builder.create();
        builder.show();
    }


    private void setupTabIcons() {

       /* TextView tabOne = (TextView) LayoutInflater.from(activity).inflate(R.layout.custom_tab, null);
        tabOne.setText("malls");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.favourite_malls, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);*/

        TextView tabTwo = (TextView) LayoutInflater.from(activity).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Offers");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.offers, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabTwo);

     /*   TextView tabThree = (TextView) LayoutInflater.from(activity).inflate(R.layout.custom_tab, null);
        tabThree.setText("Event");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.events, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);*/

        TextView tabFour = (TextView) LayoutInflater.from(activity).inflate(R.layout.custom_tab, null);
        tabFour.setText("Search");
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.search_white, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabFour);
    }

    private void setupDrawerLayout(View view) {
        drawerLayout = (DrawerLayout) view.findViewById(R.id.DrawerLayout);
        navigationView = (NavigationView) view.findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                displayView(menuItem.getTitle());
                drawerLayout.closeDrawers();
                return true;
            }
        });

        final ImageView avatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.avatar);
        final TextView email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email);
        final TextView txtOwnerName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txtOwnerName);
        if (!TextUtils.isEmpty(SharedPreferencesManager.getProfileImage(activity))) {
            Picasso.with(activity).load(SharedPreferencesManager.getProfileImage(activity)).transform(new CircleTransform()).into(avatar);
        } else {
            Picasso.with(activity).load(R.drawable.no_image_icon).transform(new CircleTransform()).into(avatar);
        }
        if (!TextUtils.isEmpty(SharedPreferencesManager.getUsername(activity))) {
            txtOwnerName.setText(SharedPreferencesManager.getUsername(activity));
        }
        if (!TextUtils.isEmpty(SharedPreferencesManager.getEmail(activity))) {
            email.setText(SharedPreferencesManager.getEmail(activity));
        }
    }

    private void displayView(CharSequence title) {
        if (CheckConnection.haveNetworkConnection(activity)) {
            if (title.equals("Home")) {
                drawerLayout.closeDrawers();
                Intent refresh = new Intent(activity, NavigationActivity.class);
                startActivity(refresh);//Start the same Activity
                activity.finish();
            } else if (title.equals("Profile")) {
                drawerLayout.closeDrawers();
                NavigationAboutFragment fragment = new NavigationAboutFragment();
                fragment.setMyCallUpdateProfileListener(this);
                callFragmentMethod(fragment, this.getClass().getSimpleName(), R.id.navigationContainer);
            } else if (title.equals("Favorite")) {
                drawerLayout.closeDrawers();
                BaseFragment fragment = new FavoriteTabBarFragment();
                callFragmentMethod(fragment, this.getClass().getSimpleName(), R.id.navigationContainer);
            } else if (title.equals("Invite")) {
                drawerLayout.closeDrawers();
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Sale50 App");
                    String sAux = "\nLet me recommend you this application\n\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=com.javinindia.individualuser\n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "choose one"));
                } catch (Exception e) {

                }

            } else if (title.equals("Rate us")) {
                drawerLayout.closeDrawers();
                final String appPackageName = activity.getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            } else if (title.equals("About App")) {
                drawerLayout.closeDrawers();
                BaseFragment fragment = new AboutAppFragments();
                callFragmentMethod(fragment, this.getClass().getSimpleName(), R.id.navigationContainer);
            }/* else if (title.equals("Settings")) {
            drawerLayout.closeDrawers();
            Toast.makeText(activity, title, Toast.LENGTH_LONG).show();

        }*/ else if (title.equals("Logout")) {
                drawerLayout.closeDrawers();
                dialogBox();
            }
        } else {
            methodCallCheckInternet();
        }
    }

    public void methodCallCheckInternet() {
        CheckConnectionFragment fragment = new CheckConnectionFragment();
        fragment.setMyCallBackInternetListener(this);
        callFragmentMethod(fragment, this.getClass().getSimpleName(), R.id.navigationContainer);
    }

    public void dialogBox() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Logout");
        alertDialogBuilder.setMessage("Thanks for visiting! Be back soon!");
        alertDialogBuilder.setPositiveButton("Ok!",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        sendDataOnLogOutApi();
                    }
                });

        alertDialogBuilder.setNegativeButton("Take me back",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextColor(Utility.getColor(activity, R.color.toolbar_color));
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(Utility.getColor(activity, R.color.toolbar_color));
    }


    private void sendDataOnLogOutApi() {
        final ProgressDialog loading = ProgressDialog.show(activity, "Logging out...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.LOGOUT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        responseImplement(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        volleyErrorHandle(error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", SharedPreferencesManager.getUserID(activity));
                params.put("type", "user");
                return params;
            }

        };
        stringRequest.setTag(this.getClass().getSimpleName());
        volleyDefaultTimeIncreaseMethod(stringRequest);
        requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(stringRequest);
    }

    private void responseImplement(String response) {
        JSONObject jsonObject = null;
        String msg = null;
        int status = 0;
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
            SharedPreferencesManager.setUserID(activity, null);
            SharedPreferencesManager.setUsername(activity, null);
            SharedPreferencesManager.setPassword(activity, null);
            SharedPreferencesManager.setEmail(activity, null);
            SharedPreferencesManager.setLocation(activity, null);
            SharedPreferencesManager.setProfileImage(activity, null);
            SharedPreferencesManager.setDeviceToken(activity, null);
            Intent refresh = new Intent(activity, LoginActivity.class);
            startActivity(refresh);//Start the same Activity
            activity.finish();
        } else {
            if (!TextUtils.isEmpty(msg)) {
                showDialogMethod("Try again");
            }
        }
    }


    @Override
    protected int getFragmentLayout() {
        return R.layout.tab_bar_layout;
    }

    @Override
    public int getToolbarMenu() {
        return 0;
    }

    @Override
    public void onNetworkConnected() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(Gravity.LEFT);
                return true;
            case R.id.action_change_password:
                ChangePasswordFragment fragment = new ChangePasswordFragment();
                callFragmentMethod(fragment, this.getClass().getSimpleName(), R.id.navigationContainer);
                drawerLayout.closeDrawers();
                return true;
            case R.id.action_feedBack:
                FeedbackFragment fragment1 = new FeedbackFragment();
                callFragmentMethod(fragment1, this.getClass().getSimpleName(), R.id.navigationContainer);
                drawerLayout.closeDrawers();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        activity.getMenuInflater().inflate(R.menu.navigation_menu, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        // adapter.addFragment(new MallsFragmet(), "Malls");
        adapter.addFragment(new OffersFragment(), "Offers");
        // adapter.addFragment(new EventFragment(), "Event");
        adapter.addFragment(new SearchTabFragment(), "Search");
        viewPager.setAdapter(adapter);

    }

    @Override
    public void OnCallBackRefresh(String name, String email, String pic) {
        final ImageView avatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.avatar);
        final TextView txtEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email);
        final TextView txtOwnerName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txtOwnerName);
        if (!TextUtils.isEmpty(SharedPreferencesManager.getProfileImage(activity))) {
            Picasso.with(activity).load(SharedPreferencesManager.getProfileImage(activity)).transform(new CircleTransform()).into(avatar);
        } else {
            Picasso.with(activity).load(R.drawable.no_image_icon).transform(new CircleTransform()).into(avatar);
        }
        if (!TextUtils.isEmpty(SharedPreferencesManager.getUsername(activity))) {
            txtOwnerName.setText(SharedPreferencesManager.getUsername(activity));
        }
        if (!TextUtils.isEmpty(SharedPreferencesManager.getEmail(activity))) {
            txtEmail.setText(SharedPreferencesManager.getEmail(activity));
        }
    }

    @Override
    public void onBackPressButton(Activity activity) {
        super.onBackPressButton(activity);
        drawerLayout.closeDrawers();
    }

    @Override
    public void OnCallBackInternet() {
        activity.onBackPressed();
    }
}
