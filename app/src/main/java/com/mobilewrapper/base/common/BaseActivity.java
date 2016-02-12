package com.mobilewrapper.base.common;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.mobilewrapper.base.R;

/**
 * Created by ppark on 2015-08-20.
 */
public class BaseActivity extends AppCompatActivity {

    public void startActivityWithTransition(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    public void startActivityWithTransitionForResult(Intent intent) {
        super.startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    public void finishActivity() {
        finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
