package live.hestia.app.home_main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import live.hestia.app.Adapter.CategoryEventAdapter;
import live.hestia.app.Adapter.EventListAdapter;
import live.hestia.app.Adapter.TopEventAdapter;
import live.hestia.app.EventDetailed.EventDetailed;
import live.hestia.app.R;
import live.hestia.app.helper.ProgressDialog;
import live.hestia.app.model.EventBasicModel;
import live.hestia.app.model.EventModel;
import live.hestia.app.user_dash.UserHome;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, TopEventAdapter.OnRowClickedListener,
        CategoryEventAdapter.OnRowClickedListener {

    private static final String TAG = "Error:";
    private DrawerLayout drawer;
    private ArrayList<EventBasicModel> allEvents = new ArrayList<>();
    private BottomSheetBehavior bottomSheetBehavior;
    private ImageView section1;
    private ImageView section3;
    private ImageView section4;
    private ImageView section5;
    private LottieAnimationView anim;
    private RecyclerView allEventList;
    private RecyclerView topEventsList;
    private TextView topEventTitle;

    private GoogleSignInAccount account;

    private ShimmerFrameLayout shimmer_top_events;
    private int back_prompt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        topEventsList = findViewById(R.id.events_featured_recycler);
        topEventTitle = findViewById(R.id.featured_events_title);
        init();
        setCTF();
        fixPeekHeight();
        account = GoogleSignIn.getLastSignedInAccount(this);
        allEventList = findViewById(R.id.home_event_list);

        //setup anim
        anim = findViewById(R.id.home_load_anim);
        anim_toggle(0);

        ImageView boarding_profile_img = findViewById(R.id.boarding_profile_img);

        Picasso.with(this).load(account.getPhotoUrl())
                .placeholder(R.drawable.landing_placeholder)
                .into(boarding_profile_img);

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
                drawer.closeDrawers();
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
    }


    private void fixPeekHeight() {
        //setup bottom sheet
        final View bottomSheet = findViewById(R.id.home_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);


        //get shimmer layout height
        final Display display = getWindowManager().getDefaultDisplay();
        final View view = findViewById(R.id.shimmer_1_container);
        view.measure(display.getWidth(), display.getHeight());
        view.getMeasuredHeight();

        //get shimmer height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;

        view.post(new Runnable() {
            @Override
            public void run() {
                //get shimmer layout absolute vertical position
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                final int y = location[1];

                bottomSheet.post(new Runnable() {
                    @Override
                    public void run() {
                        bottomSheetBehavior.setPeekHeight(height - y - view.getMeasuredHeight());
                    }
                });

            }
        });

        // Toast.makeText(getApplicationContext(), ""+view.getMeasuredWidth(), Toast.LENGTH_SHORT).show();

        //Toast.makeText(getApplicationContext(), ""+height, Toast.LENGTH_SHORT).show();


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
        section3 = findViewById(R.id.section_icon3);
        section4 = findViewById(R.id.section_icon4);
        section5 = findViewById(R.id.section_icon5);

        section1.setOnClickListener(this);
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
            if (back_prompt == 0) {
                back_prompt++;
                Toast.makeText(getApplicationContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
            } else {
                super.onBackPressed();
            }
        }
    }

    private void parseTopEvents(List<EventBasicModel> top_events) {
        shimmer_top_events.stopShimmerAnimation();
        shimmer_top_events.setVisibility(View.GONE);

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
        if (!isNetworkConnected()) {
            errorOccurred(null, 2, i);
            return;
        }

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
                errorOccurred(null, 2, i);
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

    private ProgressDialog dialogLoad;

    @Override
    public void onRowClicked(final String event_id) {

        if (!isNetworkConnected()) {
            errorOccurred(event_id, 2, null);
            return;
        }

        if (dialogLoad != null) dialogLoad.dismiss();
        dialogLoad = new ProgressDialog();
        dialogLoad.setCancelable(false);
        dialogLoad.show(getSupportFragmentManager(), "");


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

                        dialogLoad.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "onErrorResponse: ", volleyError.getCause());
                errorOccurred(event_id, 2, null);
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


    private void errorOccurred(final String event_id, final int flag, final String i) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialogLoad.dismiss();
                View view = findViewById(R.id.home_root);
                Snackbar.make(view, "Network Error", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (flag == 1) {
                                    onRowClicked(event_id);
                                } else if (flag == 2) {
                                    getCategoryData(i);
                                }

                            }
                        }).show();
            }
        });
    }

    private void setCTF() {
        final SharedPreferences date_prefs=getApplicationContext().getSharedPreferences("login_prefs",0);
        final SharedPreferences.Editor editor=date_prefs.edit();
        ImageView logo=findViewById(R.id.logo_menu);

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Date currentDate= Calendar.getInstance().getTime();
        Date endDate=null;
        try {
            endDate=simpleDateFormat.parse("05/03/2020");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (!currentDate.before(endDate)) {
            return;
        }

        logo.setOnClickListener(new View.OnClickListener() {
            int counter=date_prefs.getInt("counter",0);
            @Override
            public void onClick(View view) {
                if (counter<3) {
                    counter++;
                    int n=4-counter;
                    Toast.makeText(HomeActivity.this, "You're "+n+" click away from Flag clue !", Toast.LENGTH_SHORT).show();
                    if (counter==3) {
                        editor.putInt("counter",counter);
                        editor.apply();
                    }
                }else {
                    Toast.makeText(HomeActivity.this, "You'll know it when you see it", Toast.LENGTH_SHORT).show();
                    Toast.makeText(HomeActivity.this,
                            "1(11:1-12:3)-2(5:1-12:5)\n" +
                            "3(1:5-3:2-8:1)\n" +
                            "3(12:6)-4(1:1-3:2-4:3-10:7)-5(2:1-3:1-6:3-8:1-9:1-9:8-10:1)-6(3:1) \n" +
                            "6(5:2-8:1-10:6-11:4-17:1)-7(4:6)-8(3:6)\n" +
                            "8(5:7-7:1-11:1)-10(2:6)\n" +
                            "10(6:4-13:1-14:1)\n" +
                            "10(15:4)-11(3:3-4:1-6:2-8:3-14:2)\n" +
                            "12(15:1)\n" +
                            "14(7:7)-16(9:4)-18(7:1)", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

}
