package com.apolis.android.swipingcontainer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private SwipingContainer mSwipingContainer;

    private TextView mTxtIndex;
    private EditText mEditTxtGoToIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CheckBox cbCyclic = (CheckBox) findViewById(R.id.cbCyclic);
        cbCyclic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSwipingContainer.setCyclic(isChecked);
            }
        });
        mTxtIndex = (TextView) findViewById(R.id.txtIndex);

        mEditTxtGoToIndex = (EditText) findViewById(R.id.editTxtGoToIndex);
        Button btnGoTo = (Button) findViewById(R.id.btnGoTo);
        btnGoTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mEditTxtGoToIndex.getText().toString();
                try {
                    int index = Integer.parseInt(text);
                    index = index % mSwipingContainer.getChildCount();
                    mSwipingContainer.swipeToIndex(index, true);
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Please input a integer", Toast.LENGTH_SHORT).show();
                }
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
                mTxtIndex.setText("Visible Index: " + index);
            }
        });

        cbCyclic.setChecked(mSwipingContainer.isCyclic());
        mTxtIndex.setText("Visible Index: " + mSwipingContainer.getVisibleIndex());
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
