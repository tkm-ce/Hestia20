package tkmce.hestia20.login_page;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import tkmce.hestia20.R;

import static tkmce.hestia20.Constants.KEY_COLLEGE;
import static tkmce.hestia20.Constants.KEY_EMAIL;
import static tkmce.hestia20.Constants.KEY_FULL_NAME;
import static tkmce.hestia20.Constants.KEY_PHONE;

public class LoginPage extends AppCompatActivity {
    private static final String TAG = LoginPage.class.getSimpleName() + "trrr";

    private CircleImageView profileImg;
    private TextView displayName;
    private EditText phoneField;
    private AutoCompleteTextView collegeField;
    private Button btnSubmit;
    private GoogleSignInAccount account;
    private LottieAnimationView progress;

    private void init() {
        account = GoogleSignIn.getLastSignedInAccount(this);

        profileImg = findViewById(R.id.boarding_profile_img);
        displayName = findViewById(R.id.boarding_name);
        phoneField = findViewById(R.id.boarding_number_field);
        collegeField = findViewById(R.id.boarding_college_field);
        btnSubmit = findViewById(R.id.boarding_submit);

        progress = findViewById(R.id.progress);

        collegeField.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyboard(LoginPage.this);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        init();
        Picasso.with(this)
                .load(account.getPhotoUrl())
                .resize(512, 512)
                .placeholder(R.drawable.landing_placeholder)
                .into(profileImg);


        displayName.setText(String.format(getString(R.string.welcome), account.getDisplayName()));

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate())
                    makeRequest();
                else
                    Snackbar.make(v, R.string.fill_all_details, Snackbar.LENGTH_SHORT).show();
            }
        });
    }


    private boolean validate() {
        return phoneField.getText().toString().length() == 10 && collegeField.getText().toString().length() != 0;
    }

    void makeRequest() {
        progress.playAnimation();
        progress.setVisibility(View.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "InsertNewUser",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        startActivity(new Intent(getApplicationContext(), LoginPage.class));
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Network Error", error);
                        progress.setVisibility(View.GONE);
                        Toast.makeText(LoginPage.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_FULL_NAME, Objects.requireNonNull(account.getDisplayName()));
                params.put(KEY_EMAIL, Objects.requireNonNull(account.getEmail()));
                params.put(KEY_PHONE, phoneField.getText().toString());
                params.put(KEY_COLLEGE, collegeField.getText().toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
