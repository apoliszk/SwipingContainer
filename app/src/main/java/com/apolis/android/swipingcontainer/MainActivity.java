package com.apolis.android.swipingcontainer;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class MainActivity extends Activity {
    private SwipingContainer mSwipingContainer;

    private GradientButton mBtn1;
    private GradientButton mBtn2;
    private GradientButton mBtn3;
    private GradientButton mBtn4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtn1 = (GradientButton) findViewById(R.id.btn1);
        initButton(mBtn1, 0);
        mBtn2 = (GradientButton) findViewById(R.id.btn2);
        initButton(mBtn2, 1);
        mBtn3 = (GradientButton) findViewById(R.id.btn3);
        initButton(mBtn3, 2);
        mBtn4 = (GradientButton) findViewById(R.id.btn4);
        initButton(mBtn4, 3);

        mSwipingContainer = (SwipingContainer) findViewById(R.id.swipingContainer);
        mSwipingContainer.setVisibleIndexChangeListener(new SwipingContainer.VisibleIndexChangeListener() {
            @Override
            public void onVisibleIndexChanging(float index) {
                updateButtons(index);
            }

            @Override
            public void onVisibleIndexChange(int index) {
                updateButtons(index);
            }
        });
        updateButtons(mSwipingContainer.getVisibleIndex());

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.custom_action_bar);

            CheckBox cbCyclic = (CheckBox) actionBar.getCustomView().findViewById(R.id.cbCyclic);
            cbCyclic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mSwipingContainer.setCyclic(isChecked);
                }
            });
            cbCyclic.setChecked(mSwipingContainer.isCyclic());
        }
    }

    private void updateButtons(float index) {
        mBtn1.setRatio(index);
        mBtn1.setMaxIndex(3);
        mBtn2.setRatio(index);
        mBtn2.setMaxIndex(3);
        mBtn3.setRatio(index);
        mBtn3.setMaxIndex(3);
        mBtn4.setRatio(index);
        mBtn4.setMaxIndex(3);
    }

    private void initButton(GradientButton btn, int index) {
        btn.setIndex(index);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GradientButton button = (GradientButton) v;
                mSwipingContainer.swipeToIndex(button.getIndex(), true);
            }
        });
    }
}
