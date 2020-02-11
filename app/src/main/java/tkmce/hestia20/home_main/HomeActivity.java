package tkmce.hestia20.home_main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import tkmce.hestia20.Adapter.EventListAdapter;
import tkmce.hestia20.Adapter.TopEventAdapter;
import tkmce.hestia20.R;
import tkmce.hestia20.model.EventBasicModel;
import tkmce.hestia20.user_dash.UserHome;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Error:";
    private DrawerLayout drawer;
    private ArrayList<EventBasicModel> topEvents = new ArrayList<>();
    private ArrayList<EventBasicModel> allEvents = new ArrayList<>();
    private BottomSheetBehavior bottomSheetBehavior;
    private ImageView section1;
    private ImageView section2;
    private ImageView section3;
    private ImageView section4;
    private ImageView section5;
    private LottieAnimationView anim;
    private RecyclerView allEventList;

    private GoogleSignInAccount account;

    private ShimmerFrameLayout shimmer_top_events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
        account = GoogleSignIn.getLastSignedInAccount(this);

        ImageView boarding_profile_img = findViewById(R.id.boarding_profile_img);

        Picasso.with(this).load(account.getPhotoUrl()).into(boarding_profile_img);

        section1.performClick();
        //Setting menuBtn
        drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        ImageButton menu_btn = findViewById(R.id.home_menu_btn);
        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        findViewById(R.id.boarding_profile_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UserHome.class));
            }
        });

        findViewById(R.id.about_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
            }
        });

        findViewById(R.id.sponsors_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SponsorsActivity.class));
            }
        });

//        prepareData();

        //Setting topEventList
//        RecyclerView topEventsList = findViewById(R.id.events_featured_recycler);
//        TopEventAdapter topEventAdapter = new TopEventAdapter(topEvents, this);
//        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false);
//        topEventsList.setLayoutManager(linearLayoutManager);
//        topEventsList.setAdapter(topEventAdapter);


        //Setting allEventList
        allEventList = findViewById(R.id.home_event_list);
        EventListAdapter eventListAdapter = new EventListAdapter(allEvents, this);
        RecyclerView.LayoutManager linearLayoutManager3 = new LinearLayoutManager(getApplicationContext());
        allEventList.setLayoutManager(linearLayoutManager3);
        allEventList.setAdapter(eventListAdapter);


        //setup bottom sheet
        View bottomSheet = findViewById(R.id.home_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        //setup anim
        anim = findViewById(R.id.home_load_anim);
        anim_toggle(0);
    }

    private void anim_toggle(int i) {
        if (i == 0) {
            anim.setVisibility(View.GONE);
            allEventList.setVisibility(View.VISIBLE);
        } else {
            anim.setVisibility(View.VISIBLE);
            allEventList.setVisibility(View.GONE);
        }
    }

    private void init() {
        section1 = findViewById(R.id.section_icon1);
        section2 = findViewById(R.id.section_icon2);
        section3 = findViewById(R.id.section_icon3);
        section4 = findViewById(R.id.section_icon4);
        section5 = findViewById(R.id.section_icon5);

        section1.setOnClickListener(this);
        section2.setOnClickListener(this);
        section3.setOnClickListener(this);
        section4.setOnClickListener(this);
        section5.setOnClickListener(this);

        shimmer_top_events = findViewById(R.id.shimmer_top_events);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getTopEvents();
            }
        },2500);
    }

    private void getTopEvents() {

        String url = "https://www.hestia.live/App_api/GetTopEvents";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final StringRequest request = new StringRequest(Request.Method.GET, url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Type listType = new TypeToken<List<EventBasicModel>>() {
                        }.getType();
                        List<EventBasicModel> top_events = new Gson().fromJson(s, listType);
                        parseTopEvents(top_events);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "onErrorResponse: ", volleyError.getCause());
                Toast.makeText(HomeActivity.this, getString(R.string.error_toast), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    private void parseTopEvents(List<EventBasicModel> top_events) {
        shimmer_top_events.stopShimmerAnimation();
        shimmer_top_events.setVisibility(View.GONE);

        RecyclerView topEventsList = findViewById(R.id.events_featured_recycler);
        TopEventAdapter topEventAdapter = new TopEventAdapter(top_events, this);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false);
        topEventsList.setLayoutManager(linearLayoutManager);
        topEventsList.setAdapter(topEventAdapter);
    }


    private void toggleColor(ImageView... views) {
        for (ImageView v : views) {
            v.setBackgroundResource(R.drawable.btn_circle_disabled);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == section1) {
            toggleColor(section2, section3, section4, section5);
            section1.setBackgroundResource(R.drawable.circle_go);
            //getCategoryData(1);
        } else if (v == section2) {
            toggleColor(section1, section3, section4, section5);
            section2.setBackgroundResource(R.drawable.circle_go);
            getCategoryData(2);
        } else if (v == section3) {
            toggleColor(section2, section1, section4, section5);
            section3.setBackgroundResource(R.drawable.circle_go);
            getCategoryData(3);
        } else if (v == section4) {
            toggleColor(section2, section3, section1, section5);
            section4.setBackgroundResource(R.drawable.circle_go);
            getCategoryData(4);
        } else if (v == section5) {
            toggleColor(section2, section3, section4, section1);
            section5.setBackgroundResource(R.drawable.circle_go);
            getCategoryData(5);
        }
    }

    private void getCategoryData(int i) {
        anim_toggle(1);
        String url = "https:// string_url/" + i;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            JSONArray initArray = new JSONArray(jsonObject);
                            for (int i = 0; i < initArray.length(); i++) {
                                JSONObject item = initArray.getJSONObject(i);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "onErrorResponse: ", volleyError.getCause());
                Toast.makeText(HomeActivity.this, getString(R.string.error_toast), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);

    }


    @Override
    public void onResume() {
        super.onResume();
        shimmer_top_events.startShimmerAnimation();
    }

    @Override
    protected void onPause() {
        shimmer_top_events.stopShimmerAnimation();
        super.onPause();
    }
}
