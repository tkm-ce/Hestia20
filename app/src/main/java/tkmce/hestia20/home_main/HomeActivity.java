package tkmce.hestia20.home_main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

import tkmce.hestia20.Adapter.EventListAdapter;
import tkmce.hestia20.R;
import tkmce.hestia20.model.EventBasicModel;
import tkmce.hestia20.user_dash.UserHome;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    private ImageButton menu_btn;
    private ArrayList<EventBasicModel> allEvents = new ArrayList<>();
    private BottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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

        prepareData();

        //Setting allEventList
        RecyclerView allEventList = findViewById(R.id.home_event_list);
        EventListAdapter eventListAdapter = new EventListAdapter(allEvents, this);
        RecyclerView.LayoutManager linearLayoutManager3 = new LinearLayoutManager(getApplicationContext());
        allEventList.setLayoutManager(linearLayoutManager3);
        allEventList.setAdapter(eventListAdapter);


        //setup bottom sheet
        View bottomSheet = findViewById(R.id.home_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

    }


    private void prepareData() {
        EventBasicModel sample1 = new EventBasicModel();
        sample1.setTitle("Extra");
        sample1.setImg("https://square.github.io/picasso/static/icon-github.png");
        sample1.setFile1("12/02/2022");
        sample1.setPrize("30k");
        allEvents.add(sample1);
        allEvents.add(sample1);
        allEvents.add(sample1);
        allEvents.add(sample1);
        allEvents.add(sample1);
        allEvents.add(sample1);
        allEvents.add(sample1);
        allEvents.add(sample1);
        allEvents.add(sample1);


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
}
