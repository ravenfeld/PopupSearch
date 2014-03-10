/*
 * Copyright 2014. Ravenfeld
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under th License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package fr.ravenfeld.librairies.popupsearch;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ExampleActivity extends Activity {

    private RelativeLayout mSearchViewLayout;
    private EditText mSearchView;
    private ImageView mSearchViewClose;
    private ActionPopupSearch mActionPopupSearch;
    private List<String> mList;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mList = getList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.button_search);
        mSearchViewLayout = (RelativeLayout) searchItem.getActionView();
        mSearchViewClose = (ImageView) mSearchViewLayout.findViewById(R.id.close);
        mSearchViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionPopupSearch.dismiss();
            }
        });
        mSearchView = (EditText) mSearchViewLayout.findViewById(R.id.search);

        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mActionPopupSearch.isShowing()) {
                    mActionPopupSearch.show();
                    mSearchViewClose.setVisibility(View.VISIBLE);
                }
            }
        }
        );
        mSearchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mSearchViewClose.setVisibility(View.VISIBLE);
                }
            }
        });

        initPopupSearch();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initPopupSearch() {
        mActionPopupSearch = new ActionPopupSearch(this, mSearchView);
        mActionPopupSearch.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (mSearchView != null) {
                    mSearchViewClose.setVisibility(View.INVISIBLE);
                    mSearchView.getText().clear();
                    mList = getList();
                }

            }
        });
        mActionPopupSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplication(), "Position: " + position, Toast.LENGTH_SHORT).show();
            }
        });

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mList);
        mActionPopupSearch.setAdapter(mAdapter);

        Button button = (Button) mActionPopupSearch.mRootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DataRetriever().execute();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActionPopupSearch.dismiss();
    }

    private class DataRetriever extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.e("TEST", "Something went wrong with making the thread sleep...");
            }

            return "New - Alexis Lecanu";
        }

        @Override
        protected void onPostExecute(String newApi) {
            if (newApi != null) {
                mList.add(0, newApi);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private List<String> getList() {
        List<String> list = new ArrayList<String>();
        list.add("Arryn");
        list.add("Baratheon");
        list.add("Greyjoy");
        list.add("Lannister");
        list.add("Martell");
        list.add("Stark");
        list.add("Targaryen");
        list.add("Tully");
        list.add("Tyrell");
        return list;
    }
}
