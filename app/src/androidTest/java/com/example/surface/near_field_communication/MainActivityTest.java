package com.example.surface.near_field_communication;

import android.test.ActivityUnitTestCase;
import android.view.View;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by patrick on 15/04/18.
 */
public class MainActivityTest {
    @Rule
    public ActivityUnitTestCase<MainActivity> mainActivityActivityTestRule = new ActivityUnitTestCase<MainActivity>(MainActivity.class) {
        @Override
        public MainActivity getActivity() {
            return super.getActivity();
        }
    };

    private MainActivity mActivity = null;

    @org.junit.Before
    public void setUp() throws Exception {
        mActivity = mainActivityActivityTestRule.getActivity();
    }

    @org.junit.After
    public void tearDown() throws Exception {
        mActivity = null;
    }

    @org.junit.Test
    public void testLaunch(){
        View v = mActivity.findViewById(R.id.helpButton);

        assertNotNull(v);
    }

}