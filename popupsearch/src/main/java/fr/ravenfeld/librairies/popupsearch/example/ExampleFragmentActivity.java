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

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import fr.ravenfeld.librairies.popupsearch.R;

/**
 * Created by alecanu on 12/03/2014.
 */
public class ExampleFragmentActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_frame);

        switchFragment();

    }

    @Override
    public void onBackPressed() {
        //getFragmentManager().getBackStackEntryCount();
        Fragment ft = getSupportFragmentManager().findFragmentByTag("HOME");
        if (ft != null && ft.isVisible()) {
            showDialogExit();
        } else {
            switchFragmentAnimationLeftRight();
        }
        //switchFragmentAnimationLeftRight();

        //showDialogExit();

    }

    public void showDialogExit() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder
                .setMessage(R.string.exit_message)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ExampleFragmentActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void switchFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_frame, new HomeFragment(), "HOME");
        ft.commit();
    }

    public void switchFragmentAnimationRightLeft(String label) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        ft.replace(R.id.activity_frame, new DetailFragment(label), "DETAIL");
        ft.commit();

    }

    public void switchFragmentAnimationLeftRight() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        ft.replace(R.id.activity_frame, new HomeFragment(), "HOME").commit();
    }
}
