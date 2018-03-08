package com.filelug.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.filelug.android.R;
import com.filelug.android.ui.fragment.WizardFragment;

/**
 * Created by Vincent Chang on 2015/12/28.
 * Copyright (c) 2015 Filelug. All rights reserved.
 */
public class GettingStartedActivity extends AppCompatActivity {

    private static final String ARG_POSITION = "position";

    private ViewGroup mLayout;
    private MyPagerAdapter adapter;
    private ViewPager pager;
    private TextView skipButton;
    private TextView nextButton;
    private TextView doneButton;
    private TextView navigator;
    private int mCurrentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        setContentView(R.layout.layout_getting_started);

        mCurrentItem = 0;

        mLayout = (ViewGroup) findViewById(R.id.init_wizard_layout);
        pager = (ViewPager) findViewById(R.id.initial_wizard_pager);
        skipButton = (TextView) findViewById(R.id.skip_button);
        nextButton = (TextView) findViewById(R.id.next_button);
        doneButton = (TextView) findViewById(R.id.done_button);
        navigator = (TextView) findViewById(R.id.wizard_position);

        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setCurrentItem(mCurrentItem);

        setNavigator();

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                boolean isDone = false;
                int colorRes = -1;
                if (position == 0) {
                    colorRes = R.color.getting_started_color_1;
                } else if (position == 1) {
                    colorRes = R.color.getting_started_color_2;
                } else if (position == 2) {
                    colorRes = R.color.getting_started_color_3;
                } else if (position == 3) {
                    colorRes = R.color.getting_started_color_4;
                } else {
                    colorRes = R.color.getting_started_color_5;
                    isDone = true;
                }
                skipButton.setVisibility(isDone ? View.INVISIBLE : View.VISIBLE);
                nextButton.setVisibility(isDone ? View.GONE : View.VISIBLE);
                doneButton.setVisibility(isDone ? View.VISIBLE : View.GONE);
                mLayout.setBackgroundResource(colorRes);
                mLayout.invalidate();
                mCurrentItem = position;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int position) {
                setNavigator();
            }

        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pager.getCurrentItem() != (pager.getAdapter().getCount() - 1)) {
                    pager.setCurrentItem(pager.getCurrentItem() + 1);
                }
                setNavigator();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });

    }

    public void setNavigator() {
        String navigation = "";
        for (int i = 0; i < adapter.getCount(); i++) {
            if (i == pager.getCurrentItem()) {
                navigation += "\u25CF" + ""; // BLACK CIRCLE
                //navigation += "\u2022" + " "; // BULLET
            } else {
                //navigation += "\u25CB" + " "; // WHITE CIRCLE
                navigation += "\u25E6" + ""; // WHITE BULLET
            }
        }
        navigator.setText(navigation);
    }

    private void closeActivity() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();

        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_POSITION, this.mCurrentItem);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pager.setCurrentItem(savedInstanceState.getInt(ARG_POSITION, 0));
        setNavigator();
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Fragment getItem(int position) {
            return WizardFragment.newInstance(position);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}
