package com.javinindia.individualuser.fragments;

import android.app.ProgressDialog;
import android.content.Intent;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.javinindia.individualuser.R;
import com.javinindia.individualuser.activity.NavigationActivity;
import com.javinindia.individualuser.constant.Constants;
import com.javinindia.individualuser.font.FontAsapRegularSingleTonClass;
import com.javinindia.individualuser.preference.SharedPreferencesManager;
import com.javinindia.individualuser.utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.facebook.FacebookSdk;


public class LoginFragment extends BaseFragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private RequestQueue requestQueue;
    private EditText etUsername;
    private EditText etPassword;
    private CheckBox checkShowPassword;

    private GoogleApiClient mGoogleApiClient;
    private int RC_SIGN_IN = 0;
    private int FB_SIGN_IN = 1;
    //  private SignInButton signInButton;
    //   private Button signOutButton;
    private TextView mStatusTextView;

    // private LoginButton loginButton;
    private CallbackManager callbackmanager;
    String TAG = "google sign";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ///...........code for google login-----///////
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(activity /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();
        //-----------facebook----------------//
        FacebookSdk.sdkInitialize(getActivity());

        try {
            PackageInfo info = activity.getPackageManager().getPackageInfo(
                    "com.javinindia.individualuser",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getFragmentLayout(), container, false);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        callbackmanager = CallbackManager.Factory.create();
        initialize(view);

        return view;
    }


    private void initialize(View view) {
        AppCompatButton buttonLogin = (AppCompatButton) view.findViewById(R.id.btn_login);
        buttonLogin.setTypeface(FontAsapRegularSingleTonClass.getInstance(activity).getTypeFace());
        TextView txtForgotPass = (TextView) view.findViewById(R.id.forgot_password);
        txtForgotPass.setTypeface(FontAsapRegularSingleTonClass.getInstance(activity).getTypeFace());
        etUsername = (EditText) view.findViewById(R.id.et_userMobile);
        etUsername.setTypeface(FontAsapRegularSingleTonClass.getInstance(activity).getTypeFace());
        etPassword = (EditText) view.findViewById(R.id.et_password);
        etPassword.setTypeface(FontAsapRegularSingleTonClass.getInstance(activity).getTypeFace());
        AppCompatButton txtRegistration = (AppCompatButton) view.findViewById(R.id.txtRegistration);
        txtRegistration.setTypeface(FontAsapRegularSingleTonClass.getInstance(activity).getTypeFace());
        checkShowPassword = (CheckBox) view.findViewById(R.id.checkShowPassword);
        checkShowPassword.setTypeface(FontAsapRegularSingleTonClass.getInstance(activity).getTypeFace());
        buttonLogin.setOnClickListener(this);
        txtForgotPass.setOnClickListener(this);
        txtRegistration.setOnClickListener(this);
        checkShowPassword.setOnClickListener(this);
        ImageButton btnFacebook = (ImageButton) view.findViewById(R.id.btnFacebook);
        ImageButton btnGmail = (ImageButton) view.findViewById(R.id.btnGmail);
        btnFacebook.setOnClickListener(this);
        btnGmail.setOnClickListener(this);
        mStatusTextView = (TextView) view.findViewById(R.id.status);
        mStatusTextView.setTypeface(FontAsapRegularSingleTonClass.getInstance(activity).getTypeFace());
    }


    private Bundle getFacebookData(JSONObject object) {

        Bundle bundle = new Bundle();
        String id = null;
        try {
            id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=200");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            mStatusTextView.setText(object.getString("first_name"));
            LoginManager.getInstance().logOut();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bundle;
    }


    private void updateUI(boolean signedIn) {
        if (signedIn) {
            // signInButton.setVisibility(View.GONE);
        } else {
            mStatusTextView.setText("or sign up with");
            // signInButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //-------------for facebook-------------------//
        callbackmanager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            int statusCode = result.getStatus().getStatusCode();
            handleSignInResult(result);
        } else {

        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            final GoogleSignInAccount acct = result.getSignInAccount();

            Plus.PeopleApi.load(mGoogleApiClient, acct.getId()).setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
                @Override
                public void onResult(@NonNull People.LoadPeopleResult loadPeopleResult) {
                    Person person = loadPeopleResult.getPersonBuffer().get(0);

                    mStatusTextView.setText(person.getDisplayName());
                    String personName = person.getDisplayName();
                    String personEmail = acct.getEmail();
                    String personId = acct.getId();

                    String personPhoto;
                    if (!TextUtils.isEmpty(person.getImage().getUrl().toString())) {
                        personPhoto = String.valueOf(person.getImage().getUrl());
                    } else {
                        personPhoto = "";
                    }

                    String phone = "";
                    String password = "google";
                    String gender;


                    if (person.getGender() == 0) {
                        gender = "Male";
                    } else if (person.getGender() == 1) {
                        gender = "Female";
                    } else {
                        gender = "";
                    }

                    String authProvider = "Gmail";
                    String locale ;
                    if (!TextUtils.isEmpty(String.valueOf(person.getCurrentLocation()))) {
                        if (String.valueOf(person.getCurrentLocation()).equals("null")){
                            locale = "";
                        }else {
                            locale = person.getCurrentLocation().toString();
                        }

                    } else {
                        locale = "";
                    }

                    socialNetworkApiHit(phone, personEmail, personName, password, gender, personId, personPhoto, authProvider, locale);
                    updateUI(true);
                }
            });

        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        disableTouchOfBackFragment(savedInstanceState);
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.login_layout;
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
        BaseFragment baseFragment;
        switch (v.getId()) {
            case R.id.btn_login:
                Utility.hideKeyboard(activity);
                loginMethod();
                break;
            case R.id.forgot_password:
                baseFragment = new ForgotPasswordFragment();
                callFragmentMethod(baseFragment, this.getClass().getSimpleName(), R.id.container);
                break;
            case R.id.txtRegistration:
                baseFragment = new SignUpFragment();
                callFragmentMethod(baseFragment, this.getClass().getSimpleName(), R.id.container);
                break;
            case R.id.checkShowPassword:
                if (checkShowPassword.isChecked()) {
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                break;
            case R.id.btnFacebook:
                etPassword.setError(null);
                etUsername.setError(null);
                facebookLoginMethod();
                break;

            case R.id.btnGmail:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
        }
    }

    private void facebookLoginMethod() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("user_photos", "email", "public_profile", "user_location"));
        LoginManager.getInstance().registerCallback(callbackmanager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String accessToken = loginResult.getAccessToken().getToken();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Bundle bFacebookData = getFacebookData(object);
                        String username = bFacebookData.getString("first_name") + " " + bFacebookData.getString("last_name");
                        String email = bFacebookData.getString("email");
                        String authid = bFacebookData.getString("idFacebook");
                        String ppic = bFacebookData.getString("profile_pic");
                        String gender = bFacebookData.getString("gender");
                        String location = "";//bFacebookData.getString("location");
                        String phone = "";
                        String type = "Facebook";
                        String password = "facebook";
                        // showLoader();
                        if (TextUtils.isEmpty(email)) {
                            Toast.makeText(activity, "Please register your email id on facebook or use manual sign up", Toast.LENGTH_LONG).show();
                        } else {
                            socialNetworkApiHit(phone, email, username, password, gender, authid, ppic, type, location);
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                System.out.println("onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                registerNetworkListener();
                showSnackBarMessage(error.toString());
            }
        });
    }

    private void socialNetworkApiHit(final String phone, final String email, final String name, final String pword, final String gender, final String authid, final String ppic, final String type, final String location) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoader();
                        responseImplement(response);
                        Log.d("response login",response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoader();
                        volleyErrorHandle(error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", pword);
                params.put("username", name);
                if (!TextUtils.isEmpty(SharedPreferencesManager.getDeviceToken(activity))) {
                    params.put("deviceToken", SharedPreferencesManager.getDeviceToken(activity));
                } else {
                    params.put("deviceToken", "deviceToken");
                }
                params.put("type", type);
                params.put("authid", authid);
                params.put("gender", gender);
                params.put("location", location);
                params.put("phone", phone);
                params.put("ppic", ppic);
                return params;
            }

        };
        stringRequest.setTag(this.getClass().getSimpleName());
        volleyDefaultTimeIncreaseMethod(stringRequest);
        requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(stringRequest);
    }


    private void loginMethod() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        SharedPreferencesManager.setPassword(activity, password);

        if (validation(username, password)) {
            showLoader();
            sendDataOnLoginApi(username, password);
        }

    }

    private void sendDataOnLoginApi(final String username, final String password) {
        final ProgressDialog loading = ProgressDialog.show(activity, "Loading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.LOGIN_URL,
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
                params.put("email", username);
                params.put("password", password);
                params.put("username", "test");
                if (!TextUtils.isEmpty(SharedPreferencesManager.getDeviceToken(activity))) {
                    params.put("deviceToken", SharedPreferencesManager.getDeviceToken(activity));
                } else {
                    params.put("deviceToken", "deviceToken");
                }
                params.put("type", "manual");
                params.put("authid", "manual");
                params.put("gender", "");
                params.put("location", "Some where in some where");
                params.put("phone", "1234567890");
                params.put("ppic", "");
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
        String userid = null, msg = null, username = null, password = null, mobile = null, email = null, otp = null, pic = null;
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
            if (jsonObject.has("id"))
                userid = jsonObject.optString("id");
            if (jsonObject.has("username"))
                username = jsonObject.optString("username");
            if (jsonObject.has("email"))
                email = jsonObject.optString("email");
            if (jsonObject.has("phone"))
                mobile = jsonObject.optString("phone");
            if (jsonObject.has("profilePic"))
                pic = jsonObject.optString("profilePic");
            saveDataOnPreference(username, mobile, email, userid, pic);
            Intent refresh = new Intent(activity, NavigationActivity.class);
            startActivity(refresh);//Start the same Activity
            activity.finish();
        } else {
            if (!TextUtils.isEmpty(msg)) {
                showDialogMethod("Invalid username/password.");
            }
        }
    }


    private void saveDataOnPreference(String username, String phone, String email, String userId, String pic) {
        SharedPreferencesManager.setUsername(activity, username);
        SharedPreferencesManager.setMobile(activity, phone);
        SharedPreferencesManager.setEmail(activity, email);
        SharedPreferencesManager.setUserID(activity, userId);
        SharedPreferencesManager.setProfileImage(activity, pic);
        SharedPreferencesManager.setCity(activity, "Delhi NCR");
    }

    private boolean validation(String username, String password) {
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Mobile/Email field is empty.");
            etUsername.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(password)) {
            etPassword.setError("Please enter Password.");
            etPassword.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    ///-------------------------extra code for actual facebook login---------------------------------///

  /*  //--------------------for google--------------------------//
    signInButton = (SignInButton) view.findViewById(R.id.sign_in_button);
    //   signOutButton = (Button) view.findViewById(R.id.sign_out_button);

    signInButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }

    });


    *//*    signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                updateUI(false);
                            }
                        });
            }

        });*//*
    //---------------end google----------------//

    //--------------for facebook------------------//
    loginButton = (LoginButton) view.findViewById(R.id.loginButton);
    loginButton.setFragment(this);

    loginButton.registerCallback(callbackmanager, new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            String accessToken = loginResult.getAccessToken().getToken();
            Log.i("accessToken", accessToken);

            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    Log.i("LoginActivity", response.toString());
                    Bundle bFacebookData = getFacebookData(object);
                    String username = bFacebookData.getString("first_name") + " " + bFacebookData.getString("last_name");
                    String email = bFacebookData.getString("email");
                    String id = bFacebookData.getString("idFacebook");
                    String pImg = bFacebookData.getString("profile_pic");
                    String gender = bFacebookData.getString("gender");
                    String location = "";//bFacebookData.getString("location");
                    String phone = "";
                    String authProvider = "Facebook";
                    String password = "facebook";

                    // showLoader();
                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(activity, "Please register your email id on facebook or use manual sign up", Toast.LENGTH_LONG).show();
                    } else {
                        // socialNetworkApiHit(phone, email, username, password, gender, id, pImg, authProvider, location);
                    }
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            System.out.println("onCancel");
        }

        @Override
        public void onError(FacebookException error) {
            registerNetworkListener();
            showSnackBarMessage(error.toString());
        }
    });

    //---------------enf facebook-----------------//*/
}

