package live.hestia.app.home_main;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import live.hestia.app.Adapter.ScheduleAdapter;
import live.hestia.app.R;
import live.hestia.app.model.ScheduleEventModel;

public class ScheduleActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ScheduleActivity.class.getSimpleName();

    private ProgressDialog dialogLoad;
    private List<ScheduleEventModel> scheduleList = new ArrayList<>();
    private Snackbar networkSnack;

    private ScheduleAdapter mAdapter;

    private CheckedTextView sch1, sch2, sch3, sch4;
    private ShimmerFrameLayout shimmer_schedule;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_event);
    }

    private void init() {
        shimmer_schedule = findViewById(R.id.shimmer_schedule);

        RecyclerView recyclerView = findViewById(R.id.schedule_list_recycler);

        scheduleList.clear();
        mAdapter = new ScheduleAdapter(scheduleList, this);

        sch1 = findViewById(R.id.schedule_day_1);
        sch2 = findViewById(R.id.schedule_day_2);
        sch3 = findViewById(R.id.schedule_day_3);
        sch4 = findViewById(R.id.schedule_day_4);

        sch1.setOnClickListener(this);
        sch2.setOnClickListener(this);
        sch3.setOnClickListener(this);
        sch4.setOnClickListener(this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        getScheduleList();

        findViewById(R.id.user_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
        shimmer_schedule.startShimmerAnimation();
    }


    @Override
    public void onClick(View view) {
        sch1.setChecked(false);
        sch2.setChecked(false);
        sch3.setChecked(false);
        sch4.setChecked(false);
        ((CheckedTextView) view).setChecked(true);
        mAdapter.filter(view.getTag().toString());
    }


    private void getScheduleList() {
//        if (dialogLoad != null) dialogLoad.dismiss();
//        dialogLoad = new ProgressDialog(this);
//        dialogLoad.show();

        RequestQueue queue = Volley.newRequestQueue(this);

        String EVENT_URL = "https://www.hestia.live/App_api/getSchedule/";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, EVENT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        scheduleList.clear();
                        Log.d(TAG, "onResponse: ");
                        parseResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: ", error);
                        errorOccurred(getString(R.string.network_error));
                    }
                });

        queue.add(stringRequest);
    }

    private void parseResponse(String response) {
        try {
            JSONArray registrations = new JSONArray(response);
            for (int i = 0; i < registrations.length(); i++) {
                JSONObject item = registrations.getJSONObject(i);
                ScheduleEventModel eve = new ScheduleEventModel();
                eve.setEvent_name(item.getString("title"));
                eve.setEvent_label(item.getString("label"));
                eve.setEvent_time(item.getString("start_time"));
                eve.setEvent_venue(item.getString("venue"));
                scheduleList.add(eve);
            }

            shimmer_schedule.setVisibility(View.GONE);
            mAdapter.dataSetChanged();
//            dialogLoad.dismiss();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Date today = new Date(System.currentTimeMillis());
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String day = format.format(today);

//        if (day.compareTo("27-02-2020") <= 0)
//            sch1.performClick();
//        else if (day.compareTo("29-02-2020") >= 0)
//            sch4.performClick();
//        else if (day.substring(0, 2).equals("30"))
//            sch3.performClick();
//        else
//            sch2.performClick();

        try {
            if (today.compareTo(format.parse("05-03-2020")) <= 0)
                sch1.performClick();
            else if (today.compareTo(format.parse("06-03-2020")) == 0)
                sch2.performClick();
            else if (today.compareTo(format.parse("07-03-2020")) == 0)
                sch3.performClick();
            else
                sch4.performClick();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void errorOccurred(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                dialogLoad.dismiss();
                if (networkSnack != null && networkSnack.isShown()) networkSnack.dismiss();
                networkSnack = Snackbar.make(sch4, msg, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                networkSnack.show();
            }
        });
    }

    @Override
    protected void onPause() {
        shimmer_schedule.stopShimmerAnimation();
        super.onPause();
    }
}
