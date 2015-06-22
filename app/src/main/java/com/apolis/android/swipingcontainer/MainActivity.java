package com.apolis.android.swipingcontainer;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class MainActivity extends Activity {
    private SwipingContainer mSwipingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar);
        CheckBox cbCyclic = (CheckBox) actionBar.getCustomView().findViewById(R.id.cbCyclic);
        cbCyclic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSwipingContainer.setCyclic(isChecked);
            }
        });

        mSwipingContainer = (SwipingContainer) findViewById(R.id.swipingContainer);
        mSwipingContainer.setVisibleIndexChangeListener(new SwipingContainer.VisibleIndexChangeListener() {
            @Override
            public void onVisibleIndexChanging(float index) {
                Log.d("MainActivity", "current visible index is " + index);
            }

            @Override
            public void onVisibleIndexChange(int index) {

            }
        });

        cbCyclic.setChecked(mSwipingContainer.isCyclic());
    }
}
