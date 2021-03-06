package live.hestia.app.user_dash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import net.glxn.qrgen.android.QRCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import live.hestia.app.Adapter.TopEventAdapter;
import live.hestia.app.Constants;
import live.hestia.app.EventDetailed.EventDetailed;
import live.hestia.app.R;
import live.hestia.app.helper.ProgressDialog;
import live.hestia.app.login_page.SplashActivity;
import live.hestia.app.model.EventBasicModel;
import live.hestia.app.model.EventModel;

import static android.view.View.VISIBLE;

public class UserHome extends AppCompatActivity implements TopEventAdapter.OnRowClickedListener, BottomSheetLogoutFragment.logoutListener {

    private static final String TAG = UserHome.class.getSimpleName();

    private ArrayList<EventBasicModel> regEvents = new ArrayList<>();
    private TopEventAdapter adapter;
    private TextView noEvents;
    private ImageView imgQR;

    private ProgressDialog dialogLoad;
    private Snackbar networkSnack;

    private GoogleSignInAccount account;

    private AlertDialog dialog;
    private File qr_file;
    private BottomSheetLogoutFragment logoutSheetFragment;
//    private ShimmerFrameLayout shimmer1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        init();
    }

    private void init() {
        CircleImageView user_pic = findViewById(R.id.user_pic);
        account = GoogleSignIn.getLastSignedInAccount(this);

        TextView title = findViewById(R.id.user_name);

//        shimmer1 = findViewById(R.id.event_shimmer);

        title.setText(String.format("Hi, %s", account.getDisplayName()));

        Picasso.with(this)
                .load(account.getPhotoUrl())
                .placeholder(R.drawable.landing_placeholder)
                .into(user_pic);

        noEvents = findViewById(R.id.no_events_registered);
        imgQR = findViewById(R.id.imgQR);

        noEvents.setVisibility(View.GONE);

        fetchEvents();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder.create();

        getBarcodeAccount();

        imgQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        findViewById(R.id.user_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutDialog();
            }
        });

        findViewById(R.id.user_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    public void showLogoutDialog() {
        logoutSheetFragment = BottomSheetLogoutFragment.newInstance(this);
        logoutSheetFragment.show(getSupportFragmentManager(), logoutSheetFragment.getTag());
    }


    private void getBarcodeAccount() {
        RequestQueue queue = Volley.newRequestQueue(this);

        String EVENT_URL = "https://www.hestia.live/Mapp_api/ha_sh";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EVENT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String hash = new JSONObject(response).getString("hashcode");
                            String coll_name = new JSONObject(response).getString(Constants.KEY_COLLEGE);

                            TextView college_name = findViewById(R.id.user_college);
                            college_name.setText(coll_name);

                            qr_file = QRCode.from(hash).withSize(250, 250).file();
                            Log.d(TAG, "onResponse: " + hash);
                            Bitmap qr_bitmap = decodeFile(qr_file, 250);
                            imgQR.setImageBitmap(qr_bitmap);
                            imgQR.setVisibility(VISIBLE);

                            View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_layout, null);
                            ((ImageView) dialogLayout.findViewById(R.id.goProDialogImage)).setImageBitmap(qr_bitmap);
                            ((TextView) dialogLayout.findViewById(R.id.helloTitleQR)).setText(account.getEmail());
                            dialog.setView(dialogLayout);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: ", error);
                        //errorOccurred(getString(R.string.network_error));
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.KEY_EMAIL, Objects.requireNonNull(account.getEmail()));
                return params;
            }

        };

        queue.add(stringRequest);
    }

    @Nullable
    private static Bitmap decodeFile(File file, int REQUIRED_SIZE) {
        try {
            // Decode image size
            BitmapFactory.Options options1 = new BitmapFactory.Options();
            options1.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, options1);

            // REQUIRED_SIZE The new size we want to scale to

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (options1.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    options1.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options options2 = new BitmapFactory.Options();
            options2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(file), null, options2);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "decodeFile: ", e);
            return null;
        }
    }

    private void fetchEvents() {
        if (dialogLoad != null) dialogLoad.dismiss();
        dialogLoad = new ProgressDialog();
        dialogLoad.show(getSupportFragmentManager(), "");

        RequestQueue queue = Volley.newRequestQueue(this);

        String EVENT_URL = "https://www.hestia.live/App_api/GetUserEventsList";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EVENT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Stopping Shimmer Effect's animation after data is loaded to ListView
//                        shimmer1.stopShimmerAnimation();
//                        shimmer1.setVisibility(View.GONE);

                        regEvents.clear();
                        parseResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: ", error);
                        errorOccurred(getString(R.string.network_error));
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.KEY_EMAIL, Objects.requireNonNull(account.getEmail()));
                return params;
            }

        };

        queue.add(stringRequest);
    }

    private void parseResponse(String response) {
        Gson gson = new Gson().newBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        Type listType = new TypeToken<List<EventBasicModel>>() {
        }.getType();
        regEvents = gson.fromJson(response, listType);

        adapter = new TopEventAdapter(regEvents, this, this);
        RecyclerView eventsList = findViewById(R.id.registered_events_list);
        eventsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        eventsList.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        dialogLoad.dismiss();

        if (regEvents.size() == 0) {
            noEvents.setVisibility(VISIBLE);
        }
    }

    private void errorOccurred(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialogLoad.dismiss();
                if (networkSnack != null && networkSnack.isShown()) networkSnack.dismiss();
                networkSnack = Snackbar.make(noEvents, msg, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fetchEvents();
                                if (imgQR.getVisibility() != VISIBLE) getBarcodeAccount();
                            }
                        });
                networkSnack.show();
            }
        });
    }

    @Override
    public void onLogout() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);

        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    SharedPreferences preferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.commit();
                    Intent intent=new Intent(getApplicationContext(), SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(UserHome.this, "Cannot Logout at the moment", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void dismissSheet() {
        logoutSheetFragment.dismiss();
    }

    @Override
    public void onRowClicked(String event_id) {
        if (!isNetworkConnected()) {
            errorOccurred(event_id);
            return;
        }

        String url = "https://www.hestia.live/Mapp_api/event_by_id/" + event_id;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Gson gson = new Gson().newBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                        EventModel eventDetails = gson.fromJson(s, EventModel.class);

                        Intent intent = new Intent(getApplicationContext(), EventDetailed.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(EventDetailed.EVENT, eventDetails);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "onErrorResponse: ", volleyError.getCause());
                Toast.makeText(UserHome.this, getString(R.string.error_toast), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
        }

        return true;
    }
}
