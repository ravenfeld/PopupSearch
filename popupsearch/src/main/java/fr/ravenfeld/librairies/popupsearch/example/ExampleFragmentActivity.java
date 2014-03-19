/*
 * Copyright 2014. Ravenfeld
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under th License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package fr.ravenfeld.librairies.popupsearch.example;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import fr.ravenfeld.librairies.popupsearch.R;


public class ExampleFragmentActivity extends FragmentActivity {
    private AdvancedFragment mCurrentFragment;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_frame);

        switchFragment();

    }

    @Override
    public void onBackPressed() {
        mCurrentFragment.onBackPressed();
    }

    public void switchFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mCurrentFragment = new HomeFragment();
        ft.replace(R.id.activity_frame, mCurrentFragment, "HOME");
        ft.commit();
    }

    public void switchFragmentAnimationRightLeft(String label) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        mCurrentFragment = new DetailFragment(label);
        ft.replace(R.id.activity_frame, mCurrentFragment, "DETAIL");
        ft.commit();

    }

    public void switchFragmentAnimationLeftRight() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        mCurrentFragment = new HomeFragment();
        ft.replace(R.id.activity_frame, mCurrentFragment, "HOME").commit();
    }
}
