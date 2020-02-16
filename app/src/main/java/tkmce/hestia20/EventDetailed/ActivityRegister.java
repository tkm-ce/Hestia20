package tkmce.hestia20.EventDetailed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import tkmce.hestia20.Adapter.ParticipantListAdapter;
import tkmce.hestia20.R;

public class ActivityRegister extends AppCompatActivity {
    private static final String TAG = ActivityRegister.class.getSimpleName();
    private final int PAYMENT_REQUEST = 105, PAYMENT_SHOW = 106;

    private String eventId;
    private ArrayList<String> emails;
    private EditText referField;
    private HashMap<String, String> accommodationMap;
    private GoogleSignInAccount account;

    //    private ProgressDialog progress;
    private Snackbar networkSnack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            emails = bundle.getStringArrayList("emails");
            eventId = bundle.getString("id");
        }
        accommodationMap = new HashMap<>();

        for (String email : emails) {
            accommodationMap.put(email, "n");
        }

        account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        RecyclerView mailList = findViewById(R.id.mail_list);
        referField = findViewById(R.id.reg_refer);
        Button btnSubmit = findViewById(R.id.reg_submit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerEvent();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        ParticipantListAdapter adapter = new ParticipantListAdapter(emails, getApplicationContext());
        mailList.setLayoutManager(layoutManager);
        mailList.setAdapter(adapter);

//        progress = new ProgressDialog();
//        progress.setCancelable(false);
    }

    private void registerEvent() {
        String email = account.getEmail();
        String referral = referField.getText().toString();
        String id = eventId;

        JSONArray jsonArray = new JSONArray();
        for (String mail : emails) {
            JSONObject mailObject = new JSONObject();
            try {
                mailObject.put("email", mail);
                mailObject.put("acc", accommodationMap.get(mail));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(mailObject);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("referral_code", referral);
            jsonObject.put("reg_email", email);
            jsonObject.put("event_id", id);
            jsonObject.put("emails", jsonArray);

            Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("data", jsonObject.toString());
            intent.putExtras(bundle);
//            progress.show(getSupportFragmentManager(), TAG);
            startActivityForResult(intent, PAYMENT_REQUEST);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PAYMENT_REQUEST) {
            getStatus();
        } else if (requestCode == PAYMENT_SHOW) {
            if (resultCode == Activity.RESULT_OK)
                finish();
            else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        progress.dismiss();
                    }
                }, 500);
            }
        } else
            super.onActivityResult(requestCode, resultCode, data);

    }

    private void getStatus() {
        String url = "https://www.hestia.live/Mapp_api/currenteventstatus/" + eventId + "/" + account.getEmail();
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent intent = new Intent(ActivityRegister.this, PaymentFinishActivity.class);
                        if (response.replace("\"", "").equals("booked")) { // payment success
                            setResult(Activity.RESULT_OK);
                            intent.putExtra(PaymentFinishActivity.KEY_RESULT, PaymentFinishActivity.SUCCESS);
                            startActivityForResult(intent, PAYMENT_SHOW);
                        } else { // payment failure
                            setResult(Activity.RESULT_CANCELED);
                            intent.putExtra(PaymentFinishActivity.KEY_RESULT, PaymentFinishActivity.FAILURE);
                            startActivityForResult(intent, PAYMENT_SHOW);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Network Error", error);
                        errorOccurred(getString(R.string.network_error));
                    }
                });

        queue.add(stringRequest);
    }

    private void errorOccurred(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                progress.dismiss();
                if (networkSnack != null && networkSnack.isShown()) networkSnack.dismiss();
                networkSnack = Snackbar.make(referField, msg, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getStatus();
                            }
                        });
                networkSnack.show();
            }
        });
    }
}