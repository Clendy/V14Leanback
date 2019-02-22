package io.github.clendy.sample.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;

import io.github.clendy.sample.R;

public class MainActivity extends Activity implements View.OnClickListener {

    Button mVerticalBtn;
    Button mHorizontalBtn;
    Button mSpanBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVerticalBtn = ((Button) findViewById(R.id.vertical));
        mHorizontalBtn = ((Button) findViewById(R.id.horizontal));
        mSpanBtn = ((Button) findViewById(R.id.span));
        mVerticalBtn.setOnClickListener(this);
        mHorizontalBtn.setOnClickListener(this);
        mSpanBtn.setOnClickListener(this);

        getWindow().getDecorView().getRootView().getViewTreeObserver()
                .addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
                    @Override
                    public void onGlobalFocusChanged(View view, View view1) {

                    }
                });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.vertical:
                startActivity(new Intent(MainActivity.this, VerticalActivity.class));
                break;
            case R.id.horizontal:
                startActivity(new Intent(MainActivity.this, HorizontalActivity.class));
                break;
            case R.id.span:
                startActivity(new Intent(MainActivity.this, SpanActivity.class));
                break;
        }
    }
}
