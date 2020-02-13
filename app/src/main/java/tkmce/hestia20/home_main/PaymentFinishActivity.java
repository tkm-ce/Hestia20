package tkmce.hestia20.home_main;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

import tkmce.hestia20.R;

public class PaymentFinishActivity extends AppCompatActivity {

    public static final String KEY_RESULT = "PaymentFinishActivity.RESULT";
    public static final String SUCCESS = "PaymentFinishActivity.SUCCESS";
    public static final String FAILURE = "PaymentFinishActivity.FAILURE";

    private LottieAnimationView progress;
    private TextView status;

    private void init() {
        progress = findViewById(R.id.anim_pb);
        status = findViewById(R.id.txtStatus);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_finish);

        init();

        switch (getIntent().getStringExtra(KEY_RESULT)) {
            case SUCCESS: {
                setResult(Activity.RESULT_OK);
                progress.setAnimation(R.raw.success);
                status.setText(R.string.payment_success);
                break;
            }
            case FAILURE: {
                setResult(Activity.RESULT_CANCELED);
                progress.setAnimation(R.raw.failure);
                status.setText(R.string.payment_failed);
                break;
            }
        }
        progress.playAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 5000);
    }
}