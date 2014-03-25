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
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.ravenfeld.librairies.popupsearch.ActionPopupSearch;
import fr.ravenfeld.librairies.popupsearch.R;


public class HomeFragment extends AdvancedFragment   {

    private static final String TAG = "PopSearchExample";
    private RelativeLayout mSearchViewLayout;
    private EditText mSearchView;
    private ImageView mSearchViewClose;
    private ActionPopupSearch mActionPopupSearch;
    private List<String> mList;
    private ArrayAdapter<String> mAdapter;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(getActivity() instanceof ExampleFragmentActivity)) {
            throw new RuntimeException();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mList = getList(1);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
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
                    if (!mActionPopupSearch.isShowing()) {
                        mActionPopupSearch.show();
                    }else{
                        mActionPopupSearch.update();
                    }
                    mSearchViewClose.setVisibility(View.VISIBLE);
                }else{
                     InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(mSearchView.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                }
            }
        });
        mSearchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    mActionPopupSearch.hideKeyboard();
                }
                return false;
            }
        });
        initPopupSearch();
        super.onCreateOptionsMenu(menu, inflater);
    }


    private void initPopupSearch() {
        mActionPopupSearch = new ActionPopupSearch(getActivity(), mSearchView);
        mActionPopupSearch.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (mSearchView != null) {
                    mSearchViewClose.setVisibility(View.INVISIBLE);
                    mSearchView.getText().clear();
                    mList.clear();
                    mList.addAll(getList(1));
                }

            }
        });
        mActionPopupSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mActionPopupSearch.dismiss();
                ExampleFragmentActivity fca = (ExampleFragmentActivity) getActivity();
                fca.switchFragmentAnimationRightLeft(mList.get(position));


            }
        });

        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mList);
        mActionPopupSearch.setAdapter(mAdapter);

        Button button = (Button) mActionPopupSearch.getView().findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DataRetriever().execute();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, null);

        return rootView;

    }

    public void onBackPressed() {
        if(mActionPopupSearch!=null && mActionPopupSearch.isShowing()) {
            mActionPopupSearch.dismiss();
        }else {
            showDialogExit();
        }
    }


    public void showDialogExit() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

        alertDialogBuilder
                .setMessage(R.string.exit_message)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().finish();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mActionPopupSearch!=null) {
            mActionPopupSearch.dismiss();
        }
    }

    private class DataRetriever extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                return false;
            } catch (InterruptedException e) {
                Log.e(TAG, "Something went wrong with making the thread sleep...");
                return true;
            }

        }

        @Override
        protected void onPostExecute(Boolean error) {
            if (!error) {
                int index = mList.size();
                mList.clear();
                mList.addAll(getList(index + 1));
                mAdapter.notifyDataSetChanged();
                mActionPopupSearch.getView().invalidate();
            }
        }
    }

    private List<String> getList(int nbElements) {
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
        list.add("Sorry there don't exist other families");
        if (nbElements > list.size()) {
            return list;
        } else {
            return list.subList(0, nbElements);
        }
    }


}
