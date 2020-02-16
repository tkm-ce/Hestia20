package tkmce.hestia20.user_dash;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import tkmce.hestia20.Constants;
import tkmce.hestia20.R;

public class BottomSheetAccommodationFragment extends BottomSheetDialogFragment {

    private View view;
    private Context context;

    BottomSheetAccommodationFragment(Context context) {
        // Required empty public constructor
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.bottom_sheet_accommodation, container, false);

        fetchAccommodationDetails();

        return view;
    }


    private void fetchAccommodationDetails() {
        RequestQueue queue = Volley.newRequestQueue(context);

        String ACCOMMODATION_URL = "https://www.hestia.live/App_api/GetUserAccommodationList";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ACCOMMODATION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseAccommodationResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("BottomAccommodation", "onErrorResponse: ", error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
                if (account != null) {
                    params.put(Constants.KEY_EMAIL, Objects.requireNonNull(account.getEmail()));
                }
                return params;
            }

        };

        queue.add(stringRequest);
    }

    private void parseAccommodationResponse(String response) {
        try {
            JSONObject item = new JSONObject(response);

            ((CheckedTextView) view.findViewById(R.id.accommodation_registered)).setChecked(item.getBoolean("accommodation_registered"));

//            ((CheckedTextView) view.findViewById(R.id.day_1)).setChecked(item.getBoolean("day1"));
//            ((CheckedTextView) view.findViewById(R.id.day_2)).setChecked(item.getBoolean("day2"));
//            ((CheckedTextView) view.findViewById(R.id.day_3)).setChecked(item.getBoolean("day3"));
//            ((CheckedTextView) view.findViewById(R.id.day_4)).setChecked(item.getBoolean("day4"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}