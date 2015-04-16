package com.apolis.swipingcontainer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
    private SwipingContainer mSwipingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipingContainer = (SwipingContainer) findViewById(R.id.swipingContainer);
    }

    public void btnClickHandler(View btn) {
        switch (btn.getId()) {
            case R.id.btnFirst:
                mSwipingContainer.swipeToIndex(1);
                break;
            case R.id.btnSecond:
                mSwipingContainer.swipeToIndex(2);
                break;
            case R.id.btnThird:
                mSwipingContainer.swipeToIndex(3);
                break;
            case R.id.btnForth:
                mSwipingContainer.swipeToIndex(0);
                break;
        }
    }
}
