package com.apolis.android.swipingcontainer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
    private SwipingContainer mSwipingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipingContainer = (SwipingContainer) findViewById(R.id.swipingContainer);
        mSwipingContainer.setVisibleIndexChangeListener(new SwipingContainer.VisibleIndexChangeListener() {
            @Override
            public void onVisibleIndexChanging(float index) {
                Log.d("MainActivity", "current visible index is " + index);
            }

            @Override
            public void onVisibleIndexChange(int index) {
                Toast.makeText(MainActivity.this, "index is " + index, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void btnClickHandler(View btn) {
        switch (btn.getId()) {
            case R.id.btnFirst:
                mSwipingContainer.swipeToIndex(1, true);
                break;
            case R.id.btnSecond:
                mSwipingContainer.swipeToIndex(2, true);
                break;
            case R.id.btnThird:
                mSwipingContainer.swipeToIndex(3, true);
                break;
            case R.id.btnForth:
                mSwipingContainer.swipeToIndex(0, true);
                break;
        }
    }
}
