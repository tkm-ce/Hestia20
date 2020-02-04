package tkmce.hestia20.home_main;

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

import tkmce.hestia20.Adapter.TopEventAdapter;
import tkmce.hestia20.R;
import tkmce.hestia20.model.EventModel;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    private ImageButton menu_btn;
    private RecyclerView topEventsList;
    private ArrayList<EventModel> topEvents = new ArrayList<>();
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

        prepareData();
        //Setting topEventList
        topEventsList = findViewById(R.id.events_featured_recycler);
        TopEventAdapter topEventAdapter = new TopEventAdapter(topEvents, this);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        topEventsList.setLayoutManager(linearLayoutManager);
        topEventsList.setAdapter(topEventAdapter);

        //setup bottom sheet
        View bottomSheet = findViewById(R.id.home_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

    }


    private void prepareData() {
        EventModel sample = new EventModel();
        sample.setTitle("Extra");
        sample.setImg("https://square.github.io/picasso/static/icon-github.png");
        sample.setFile1("12/02/2022");
        topEvents.add(sample);
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
