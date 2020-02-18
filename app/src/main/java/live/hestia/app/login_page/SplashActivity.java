package live.hestia.app.login_page;

import android.app.LauncherActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import live.hestia.app.Constants;
import live.hestia.app.R;
import live.hestia.app.home_main.HomeActivity;

import static live.hestia.app.Constants.KEY_EMAIL;
import static live.hestia.app.Constants.KEY_PHONE;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LauncherActivity.class.getSimpleName();

    private static final int GOOGLE_SIGN_IN = 101;

    private GoogleSignInClient mGoogleSignInClient;
    private View btnLogin;
    private LottieAnimationView progress, noInternet;
    private Snackbar networkSnack;
    private GoogleSignInAccount account;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private void init() {
        progress = findViewById(R.id.progress);
        btnLogin = findViewById(R.id.sign_in_launcher);
        btnLogin.setVisibility(View.VISIBLE);
        noInternet = findViewById(R.id.noInternet);

        preferences = getSharedPreferences("login_prefs", 0);
        editor = preferences.edit();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_IMMERSIVE
//                        // Set the content to appear under the system bars so that the
//                        // content doesn't resize when the system bars hide and show.
//                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        // Hide the nav bar and status bar
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN);


        init();
        checkLoggedIn();
        btnLogin.setOnClickListener(this);
    }

    private void checkLoggedIn() {
        boolean isLogged = preferences.getBoolean("isLogged", false);
        if (isLogged) {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            loginCompleted(account);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    account = task.getResult(ApiException.class);
                    editor.putBoolean("isLogged", true);
                    editor.apply();
                    loginCompleted(account);
                } catch (ApiException e) {
                    Log.e(TAG, "Login failed", e);
                    Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
                    btnLogin.setEnabled(true);
                    progress.setVisibility(View.GONE);
                }
            } else {
                Log.e(TAG, "Login Cancelled");
                btnLogin.setEnabled(true);
                progress.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.sign_in_launcher: {
                progress.setVisibility(View.VISIBLE);
                v.setEnabled(false);
                attemptLogin();
                startActivityForResult(mGoogleSignInClient.getSignInIntent(), GOOGLE_SIGN_IN);
                break;
            }
        }
    }

    private void attemptLogin() {
        progress.setVisibility(View.VISIBLE);
        noInternet.setVisibility(View.GONE);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            progress.setVisibility(View.GONE);
            //btnLogin.setOnClickListener(this);
            btnLogin.setVisibility(View.VISIBLE);
        } else {
            loginCompleted(account);
        }
    }

    private void loginCompleted(final GoogleSignInAccount account) {
        progress.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.GONE);
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "https://www.hestia.live/Mapp_api/login",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (isProfileComplete(response)) {
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        } else {
                            startActivity(new Intent(getApplicationContext(), LoginPage.class));
                        }
                        finish();
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Network Error", error);
                errorOccurred();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_EMAIL, Objects.requireNonNull(account.getEmail()));
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private boolean isProfileComplete(@NonNull String response) {
        if (response.equals("false")) {
            return false;
        }

        try {
            Log.d(TAG, "isProfileComplete: ");
            JSONObject details = new JSONObject(response);
            if (!"null".equals(details.optString(Constants.KEY_COLLEGE)) &&
                    !"null".equals(details.optString(KEY_PHONE)))
                return true;
        } catch (JSONException e) {
            Log.e(TAG, "JSON parseError", e);
        }
        return false;
    }

    private void errorOccurred() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.setVisibility(View.GONE);
                noInternet.setVisibility(View.VISIBLE);
                noInternet.playAnimation();

                if (networkSnack != null && networkSnack.isShown()) networkSnack.dismiss();
                networkSnack = Snackbar.make(progress, "Network error", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                attemptLogin();
                            }
                        });
                networkSnack.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}