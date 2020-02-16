package tkmce.hestia20.EventDetailed;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import tkmce.hestia20.Adapter.ResultsAdapter;
import tkmce.hestia20.R;
import tkmce.hestia20.model.EventModel;
import tkmce.hestia20.model.ResultModel;

public class EventDetailed extends AppCompatActivity implements View.OnClickListener {

    public static final String EVENT = "EventDetails.EVENT";
    private static final String TAG = EventDetailed.class.getSimpleName();
    private final int REGISTER = 106;
    int resultAvailable = 1;
    GoogleSignInAccount account;
    RecyclerView resultList;
    BottomSheetBehavior<View> bottomSheetResults;
    private Button btnRegister;
    private BottomSheetBehavior mBottomSheetBehavior;
    private View bottomSheetReg;
    private EventModel eventModel;
    private TextView btmParticipantText;
    private Button btnSubmit;
    private EditText mailField;
    private Button btnNext;
    private ArrayList<String> emails = new ArrayList<>();
    private Button btnCoord;
    private TextView col1Text;
    private TextView col1Number;
    private TextView col1Call;
    private BottomSheetBehavior bottomSheetCoord;
    private TextView col2Text;
    private TextView col2Number;
    private TextView col2Call;
    private View btmCoord;
    private TextView title;
    private TextView fee;
    private TextView venue;
    private TextView smallDesc;
    private TextView prizes;
    private TextView details;
    private View scheduleProgress, statusProgress;
    private Snackbar networkSnack;
    private int members;
    public int results_length = 0;

    private void init() {
        btmParticipantText = findViewById(R.id.btm_title);

        btnRegister = findViewById(R.id.register_event);
        bottomSheetReg = findViewById(R.id.btm_register);
        btnSubmit = findViewById(R.id.btm_btn_submit);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheetReg);
        mailField = findViewById(R.id.btm_mail_field);
        btnNext = findViewById(R.id.btm_next);

        btmCoord = findViewById(R.id.btm_coord);
        bottomSheetCoord = BottomSheetBehavior.from(btmCoord);
        col2Text = findViewById(R.id.col_b_text);
        col2Number = findViewById(R.id.col_b_number);
        col2Call = findViewById(R.id.col_b_call);
        col1Text = findViewById(R.id.col_a_text);
        col1Number = findViewById(R.id.col_a_number);
        col1Call = findViewById(R.id.col_a_call);
        btnCoord = findViewById(R.id.event_contact);

        title = findViewById(R.id.row_heading);
        btnRegister.setEnabled(false);
        fee = findViewById(R.id.event_fee);

        venue = findViewById(R.id.event_venue);
        smallDesc = findViewById(R.id.row_small_description);
        prizes = findViewById(R.id.event_first_prize);
        details = findViewById(R.id.expandable_text);

        scheduleProgress = findViewById(R.id.schedule_progress);
        statusProgress = findViewById(R.id.status_progress);

        bottomSheetResults = BottomSheetBehavior.from(findViewById(R.id.results_list_view));
        resultList = findViewById(R.id.results_list);

        account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detailed);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            eventModel = (EventModel) bundle.getSerializable(EVENT);

            Picasso.with(this)
                    .load("https://www.hestia.live/assets/uploads/event_images/" + eventModel.getImg())
                    .resize(700,400)
                    .centerCrop()
                    .placeholder(R.drawable.landing_placeholder)
                    .into((ImageView) findViewById(R.id.detailsImage));
        }

        Log.d(TAG, "onCreate: ");
        
        Toolbar toolbar = findViewById(R.id.detailsToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        init();

        ExpandableTextView expTv1 = findViewById(R.id.expand_text_view);
        expTv1.setText(getString(R.string.icon));

        setText();
        setClickListeners();
        setSchedule();
        setTicketStatus();
    }

    private void setText() {
        title.setText(eventModel.getTitle());

        if (eventModel.getReg_fee() == null) {
            findViewById(R.id.viewRegistration).setVisibility(View.GONE);
            btnRegister.setVisibility(View.GONE);
        } else if (eventModel.getReg_fee() == 0) {
            fee.setText(getString(R.string.free));
        } else {
            String strFee = "₹" + eventModel.getReg_fee() + " per " + eventModel.getFee_type();
            fee.setText(strFee);
        }

        if (!TextUtils.isEmpty(eventModel.getVenue()))
            venue.setText(eventModel.getVenue());
        else
            findViewById(R.id.viewVenue).setVisibility(View.GONE);

        smallDesc.setText(eventModel.getShort_desc());
        if (!TextUtils.isEmpty(eventModel.getPrize())) {
            String prize = getString(R.string.prizes_worth) + " " + eventModel.getPrize();
            prizes.setText(prize);
        } else {
            findViewById(R.id.viewPrize).setVisibility(View.GONE);
        }
        details.setText(Html.fromHtml(eventModel.getDetails()));
        details.setMovementMethod(LinkMovementMethod.getInstance());

        col1Text.setText(eventModel.getCol1_name());
        col2Text.setText(eventModel.getCo2_name());
        col1Number.setText(eventModel.getCol1_no());
        col2Number.setText(eventModel.getCo2_no());
    }

    void call(int i) {
        Uri u;
        if (i == 1) {
            u = Uri.parse("tel:+91" + col1Number.getText());
        } else {
            u = Uri.parse("tel:+91" + col2Number.getText());
        }
        Intent intent = new Intent(Intent.ACTION_DIAL, u);

        try {
            startActivity(intent);
        } catch (SecurityException s) {
            Toast.makeText(getApplicationContext(), "No permission", Toast.LENGTH_LONG)
                    .show();
        }

        bottomSheetCoord.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void setClickListeners() {
        col1Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(1);
            }
        });
        col2Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(2);
            }
        });

        btnCoord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetCoord.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid(mailField.getText().toString())) {
                    if (checkMail(mailField.getText().toString())) {
                        onAdded();
                    } else {
                        Toast.makeText(getApplicationContext(), "Email already entered", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Enter valid G-mail address", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRegister.setOnClickListener(this);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                Intent intent = new Intent(getApplicationContext(), ActivityRegister.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("emails", emails);
                bundle.putString("id", eventModel.getEvent_id());
                intent.putExtras(bundle);
                startActivityForResult(intent, REGISTER);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REGISTER) {
            if (resultCode == Activity.RESULT_OK) {
                btnRegister.setText(R.string.booked);
                btnRegister.setEnabled(false);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    boolean checkMail(String mail) {
        return !emails.contains(mail);
    }

    private void onAdded() {
        members++;
        emails.add(mailField.getText().toString());
        mailField.setText("");
        if (!mailField.isEnabled()) mailField.setEnabled(true);
        if (members >= eventModel.getMin_memb()) {
            btnSubmit.setEnabled(true);
            btnSubmit.setBackground(getDrawable(R.drawable.google_bg));
        }
        btmParticipantText.setText(String.format(getString(R.string.addParticipantAdv), members + 1, eventModel.getMax_memb()));

        if (members >= eventModel.getMax_memb()) {
            hideViews(btmParticipantText, btnNext, mailField);

        }
    }

    void hideViews(View... views) {
        for (View view : views) {
            view.setVisibility(View.GONE);
        }
    }

    void showViews(View... views) {
        for (View view : views) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public boolean isValid(String email) {
        if (email == null) return false;
        if (!mailField.isEnabled()) return true;
        /*String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";*/
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "gmail\\.com$";

        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }

    private void clearSheet() {
        members = 0;
        emails.clear();
        showViews(btmParticipantText, btnNext, mailField);
        btnSubmit.setBackground(getDrawable(R.drawable.btn_disabled_bg));
        btnSubmit.setEnabled(false);
        btmParticipantText.setText(String.format(getString(R.string.addParticipant), eventModel.getMax_memb()));
        mailField.setText(account.getEmail());
        mailField.setEnabled(false);
    }

    private void parseResponse(String response) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                getResources().getConfiguration().locale);
        StringBuilder schedule = new StringBuilder();
        try {
            JSONArray init_Array = new JSONArray(response);
            for (int i = 0; i < init_Array.length(); i++) {
                JSONObject item_obj = init_Array.getJSONObject(i);
                if (!item_obj.optString("label", "null").equals("null"))
                    schedule.append(item_obj.getString("label")).append(": ");
                schedule.append(beautify(dateFormat.parse(item_obj.getString("start_time")), dateFormat.parse(item_obj.getString("end_time"))));
                schedule.append("\n");
            }

            String sch = schedule.toString().trim();
            if (!TextUtils.isEmpty(sch)) {
                ((TextView) findViewById(R.id.txtSchedule)).setText(sch);
                findViewById(R.id.viewSchedule).setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error", e);
            errorOccurred(getString(R.string.network_error));
        } catch (ParseException e) {
            Log.e(TAG, "Date parse error");
        }
    }

    private void errorOccurred(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scheduleProgress.setVisibility(View.GONE);
                statusProgress.setVisibility(View.GONE);
                if (networkSnack != null && networkSnack.isShown()) networkSnack.dismiss();
                networkSnack = Snackbar.make(btnSubmit, msg, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setSchedule();
                                setTicketStatus();
                            }
                        });
                networkSnack.show();
            }
        });
    }

    private void setSchedule() {
        scheduleProgress.setVisibility(View.VISIBLE);

        String url = "https://www.hestia.live/Mapp_api/event_schedule/" + eventModel.getEvent_id();
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        scheduleProgress.setVisibility(View.GONE);
                        parseResponse(response);
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

    private void setTicketStatus() {
        String url = "https://www.hestia.live/Mapp_api/currenteventstatus/" + eventModel.getEvent_id() + "/" + account.getEmail();
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        scheduleProgress.setVisibility(View.GONE);
                        parseStatusResponse(response);
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

    private void parseStatusResponse(@NonNull String response) {
        statusProgress.setVisibility(View.GONE);
        response = response.replace("\"", "");
        switch (response) {
            case "true": {
                btnRegister.setText(R.string.buy_ticket);
                btnRegister.setEnabled(true);
                break;
            }
            case "booked": {
                btnRegister.setText(R.string.booked);
                btnRegister.setBackground(getDrawable(R.color.disabled));
                break;
            }
            case "sold": {
                btnRegister.setText(R.string.sold_out);
                btnRegister.setBackground(getDrawable(R.color.disabled));
                break;
            }
            case "closed": {
                btnRegister.setText(R.string.reg_closed);
                checkResults();
                break;
            }
            default: {
                String regText;
                SimpleDateFormat df = new SimpleDateFormat("dd MMM", getResources().getConfiguration().locale);
                String eDate = df.format(eventModel.getReg_start());
                regText = getString(R.string.starts_on) + " " + eDate;
                btnRegister.setText(regText);
                btnRegister.setBackground(getDrawable(R.color.disabled));
            }
        }
    }

    private void checkResults() {

        String url = "https://www.hestia.live/Mapp_api/event_result/" + eventModel.getEvent_id();

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseResults(response);
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

    private void parseResults(String response) {
        ArrayList<ResultModel> results = new ArrayList<>();
        try {
            JSONArray initArray = new JSONArray(response);
            for (int i = 0; i < initArray.length(); i++) {

                JSONObject item = initArray.getJSONObject(i);
                ResultModel resultModel = new ResultModel();
                resultModel.setLabel(item.getString("label"));
                resultModel.setName(item.getString("fullname"));
                resultModel.setCollege(item.getString("college"));
                results.add(resultModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Collections.sort(results, new Comparator<ResultModel>() {
            @Override
            public int compare(ResultModel o1, ResultModel o2) {
                return o1.getLabel().compareTo(o2.getLabel());
            }
        });

        results_length = results.size();
        if (results_length > 0) {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            ResultsAdapter resultsAdapter = new ResultsAdapter(results);

            resultList.setLayoutManager(layoutManager);
            resultList.setAdapter(resultsAdapter);

            btnRegister.setText(getString(R.string.results));
            statusProgress.setVisibility(View.GONE);
            resultAvailable = 0;
            btnRegister.setEnabled(true);
        } else {
            btnRegister.setBackground(getDrawable(R.color.disabled));
        }

    }

    @NonNull
    private String beautify(Date from, Date to) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", getResources().getConfiguration().locale);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", getResources().getConfiguration().locale);
        if (dateFormat.format(from).equals(dateFormat.format(to))) {
            return dateFormat.format(from) + " " + timeFormat.format(from) + " to " + timeFormat.format(to);
        } else {
            return dateFormat.format(from) + " " + timeFormat.format(from) + " to "
                    + dateFormat.format(to) + " " + timeFormat.format(to);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                Rect outRect = new Rect();
                bottomSheetReg.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

            if (bottomSheetCoord.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                Rect outRect = new Rect();
                btmCoord.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
                    bottomSheetCoord.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

            if (bottomSheetResults.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                Rect outRect = new Rect();
                btmCoord.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
                    bottomSheetResults.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        if (resultAvailable == 1) {
            clearSheet();
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bottomSheetResults.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetCoord.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetCoord.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (bottomSheetResults.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetResults.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}
