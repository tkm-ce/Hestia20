package tkmce.hestia20.home_main;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import tkmce.hestia20.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_hestia);

        findViewById(R.id.user_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
