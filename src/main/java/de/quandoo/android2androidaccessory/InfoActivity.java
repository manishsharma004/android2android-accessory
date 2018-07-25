package de.quandoo.android2androidaccessory;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

public class InfoActivity extends ActionBarActivity {
    private static String TAG = "InfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: savedInstancestate=" + savedInstanceState);

        setContentView(R.layout.activity_info);
    }

}
