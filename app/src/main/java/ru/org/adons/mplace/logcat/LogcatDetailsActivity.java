package ru.org.adons.mplace.logcat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import ru.org.adons.clog.RecyclerAdapter;
import ru.org.adons.mplace.R;

public class LogcatDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logcat_details);

        TextView tv = (TextView) findViewById(android.R.id.text1);
        tv.setText(getIntent().getStringExtra(LogcatMainActivity.EXTRA_MESSAGE));
    }

    @Override
    protected void onDestroy() {
        Log.d(RecyclerAdapter.LOG_TAG, this.getLocalClassName() + ": destroy");
        super.onDestroy();
    }

}
