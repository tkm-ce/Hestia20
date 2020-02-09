package tkmce.hestia20.user_dash;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import net.glxn.qrgen.android.QRCode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import tkmce.hestia20.Adapter.TopEventAdapter;
import tkmce.hestia20.Constants;
import tkmce.hestia20.R;
import tkmce.hestia20.model.EventBasicModel;

import static android.view.View.VISIBLE;

public class UserHome extends AppCompatActivity implements TopEventAdapter.OnRowClickedListener {

    private static final String TAG = UserHome.class.getSimpleName();

    private ArrayList<EventBasicModel> regEvents = new ArrayList<>();
    private TopEventAdapter adapter;
    private TextView noEvents;
    private ImageView imgQR;

    private ProgressDialog dialogLoad;
    private Snackbar networkSnack;

    private GoogleSignInAccount account;

    private AlertDialog dialog;

    private ShimmerFrameLayout shimmer1, shimmer2 ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        account = GoogleSignIn.getLastSignedInAccount(this);

        TextView title = findViewById(R.id.user_name);

        shimmer1 = findViewById(R.id.event_shimmer);
        shimmer2 = findViewById(R.id.all_events_shimmer);

        if (account != null) {
            title.setText(String.format("Hi, %s", account.getDisplayName()));

            Picasso.with(this)
                    .load(account.getPhotoUrl())
                    .resize(400, 400)
                    .placeholder(R.drawable.landing_placeholder)
                    .centerCrop();
        }
        noEvents = findViewById(R.id.no_events_registered);
        imgQR = findViewById(R.id.imgQR);

        adapter = new TopEventAdapter(regEvents, this);
        RecyclerView eventsList = findViewById(R.id.registered_events_list);
        eventsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        eventsList.setAdapter(adapter);

        fetchEvents(0);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder.create();

        getBarcodeAccount();

        imgQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        findViewById(R.id.accommodation_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAccomodationDialog();
            }
        });

        findViewById(R.id.user_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutDialog();
            }
        });
    }

    public void showAccomodationDialog() {
        BottomSheetAccommodationFragment bottomSheetFragment = new BottomSheetAccommodationFragment(this);
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    public void showLogoutDialog() {
        BottomSheetLogoutFragment bottomSheetFragment = BottomSheetLogoutFragment.newInstance(this);
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    File qr_file;

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

    private void fetchEvents(final int i) {
        if (dialogLoad != null) dialogLoad.dismiss();
        dialogLoad = new ProgressDialog(this);
        dialogLoad.show();

        RequestQueue queue = Volley.newRequestQueue(this);

        String EVENT_URL = "https://www.hestia.live/App_api/GetUserEventsList";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EVENT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Stopping Shimmer Effect's animation after data is loaded to ListView
                        shimmer2.stopShimmerAnimation();
                        shimmer2.setVisibility(View.GONE);

                        regEvents.clear();
                        parseResponse(response, i);
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

    private void parseResponse(String response, int k) {
        try {
            JSONArray registrations = new JSONArray(response);
            for (int i = 0; i < registrations.length(); i++) {
                JSONObject item = registrations.getJSONObject(i);
                EventBasicModel reg = new EventBasicModel();
                reg.setEvent_id(item.getString("event_id"));
                reg.setTitle(item.getString("title"));
                reg.setF1_hint(item.getString("f1_hint"));
                reg.setF2_hint(item.getString("f2_hint"));
                reg.setFile1(item.getString("file1"));
                reg.setFile2(item.getString("file2"));
                regEvents.add(reg);
            }

            adapter.notifyDataSetChanged();
            dialogLoad.dismiss();

            if (regEvents.size() == 0) {
                noEvents.setVisibility(VISIBLE);
            } else {
                onRowClicked(k);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRowClicked(int i) {
        Toast.makeText(this, "Clicked on event: " + regEvents.get(i).getTitle(), Toast.LENGTH_SHORT).show();
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
                                fetchEvents(0);
                                if (imgQR.getVisibility() != VISIBLE) getBarcodeAccount();
                            }
                        });
                networkSnack.show();
            }
        });
    }

    private void logoutUser() {

    }


//    @Override
//    public void onUpdated(int i) {
//        fetchEvents(i);
//    }
}
