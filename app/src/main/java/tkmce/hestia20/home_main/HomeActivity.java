package tkmce.hestia20.home_main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import tkmce.hestia20.Adapter.CategoryEventAdapter;
import tkmce.hestia20.Adapter.EventListAdapter;
import tkmce.hestia20.Adapter.TopEventAdapter;
import tkmce.hestia20.EventDetailed.EventDetailed;
import tkmce.hestia20.R;
import tkmce.hestia20.model.EventBasicModel;
import tkmce.hestia20.model.EventModel;
import tkmce.hestia20.user_dash.UserHome;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, TopEventAdapter.OnRowClickedListener,
        CategoryEventAdapter.OnRowClickedListener {

    private static final String TAG = "Error:";
    private DrawerLayout drawer;
    private ArrayList<EventBasicModel> allEvents = new ArrayList<>();
    private BottomSheetBehavior bottomSheetBehavior;
    private ImageView section1;
    //private ImageView section2;
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
        allEventList = findViewById(R.id.home_event_list);

        //setup anim
        anim = findViewById(R.id.home_load_anim);
        anim_toggle(0);

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

        findViewById(R.id.calendar_items).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ScheduleActivity.class));
            }
        });

        //Setting allEventList
        allEventList = findViewById(R.id.home_event_list);
        EventListAdapter eventListAdapter = new EventListAdapter(allEvents, this);
        RecyclerView.LayoutManager linearLayoutManager3 = new LinearLayoutManager(getApplicationContext());
        allEventList.setLayoutManager(linearLayoutManager3);
        allEventList.setAdapter(eventListAdapter);


        //setup bottom sheet
        View bottomSheet = findViewById(R.id.home_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
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
        //section2 = findViewById(R.id.section_icon2);
        section3 = findViewById(R.id.section_icon3);
        section4 = findViewById(R.id.section_icon4);
        section5 = findViewById(R.id.section_icon5);

        section1.setOnClickListener(this);
        //section2.setOnClickListener(this);
        section3.setOnClickListener(this);
        section4.setOnClickListener(this);
        section5.setOnClickListener(this);

        shimmer_top_events = findViewById(R.id.shimmer_top_events);

        getTopEvents();
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
        TopEventAdapter topEventAdapter = new TopEventAdapter(top_events, this, this);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false);
        topEventsList.setLayoutManager(linearLayoutManager);
        topEventsList.setAdapter(topEventAdapter);
    }

    private void parseCategoricalEvents(List<EventBasicModel> top_events) {
        CategoryEventAdapter topEventAdapter = new CategoryEventAdapter(top_events, this, this);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        allEventList.setLayoutManager(linearLayoutManager);
        allEventList.setAdapter(topEventAdapter);
        anim_toggle(0);
    }


    private void toggleColor(ImageView... views) {
        for (ImageView v : views) {
            v.setBackgroundResource(R.drawable.btn_circle_disabled);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == section1) {
            toggleColor(section3, section4, section5);
            section1.setBackgroundResource(R.drawable.circle_go);
//            getCategoryData(5);
            getCategoryData("technical");
        } else if (v == section3) {
            toggleColor(section1, section4, section5);
            section3.setBackgroundResource(R.drawable.circle_go);
//            getCategoryData(3);
            getCategoryData("general");
        } else if (v == section4) {
            toggleColor(section3, section1, section5);
            section4.setBackgroundResource(R.drawable.circle_go);
//            getCategoryData(4);
            getCategoryData("cultural");
        } else if (v == section5) {
            toggleColor(section3, section4, section1);
            section5.setBackgroundResource(R.drawable.circle_go);
//            getCategoryData(1);
            getCategoryData("workshop");
        }
    }

    private void getCategoryData(final String i) {
        anim_toggle(1);
        String url = "https://www.hestia.live/App_api/GetEventsByCatLike";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Gson gson = new Gson().newBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                        Type listType = new TypeToken<List<EventBasicModel>>() {
                                                    }.getType();
                        List<EventBasicModel> category_events = gson.fromJson(s, listType);
                        parseCategoricalEvents(category_events);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "onErrorResponse: ", volleyError.getCause());
                Toast.makeText(HomeActivity.this, getString(R.string.error_toast), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("catname", Objects.requireNonNull(i));
                return params;
            }

        };
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

    @Override
    public void onRowClicked(String event_id) {
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
                Toast.makeText(HomeActivity.this, getString(R.string.error_toast), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);
    }

}
