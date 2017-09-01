/*
 *    Copyright 2017 Jiahua Wang
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License
 */

package thenewpotato.blogg;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import thenewpotato.blogg.managers.SharedPreferencesManager;

import static thenewpotato.blogg.Tools.FN_ABOUT;
import static thenewpotato.blogg.Tools.FN_ACTIVITIES;
import static thenewpotato.blogg.Tools.FN_APP_SETTINGS;
import static thenewpotato.blogg.Tools.FN_DRAFT;
import static thenewpotato.blogg.Tools.FN_LIVE;
import static thenewpotato.blogg.Tools.KEY_SORT_OPTION;
import static thenewpotato.blogg.Tools.KEY_STARTUP_FRAGMENT;
import static thenewpotato.blogg.Tools.OPTION_A_TO_Z;
import static thenewpotato.blogg.Tools.OPTION_NEWEST;
import static thenewpotato.blogg.Tools.OPTION_OLDEST;
import static thenewpotato.blogg.Tools.OPTION_Z_TO_A;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link AppSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppSettingsFragment extends Fragment {

    public AppSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AppSettingsFragment.
     */
    public static AppSettingsFragment newInstance() {
        AppSettingsFragment fragment = new AppSettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_settings, container, false);

        // sort button is not needed in this fragment
        ImageView ivSort = (ImageView) getActivity().findViewById(R.id.iv_sort);
        ivSort.setVisibility(View.GONE);

        Spinner spinnerFragPref = (Spinner) view.findViewById(R.id.spinner_fragment_pref);
        Spinner spinnerSortPref = (Spinner) view.findViewById(R.id.spinner_sort_pref);

        final SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance();

        switch (sharedPreferencesManager.getValue(KEY_STARTUP_FRAGMENT, FN_LIVE)){
            case FN_LIVE:
                spinnerFragPref.setSelection(0);
                break;
            case FN_DRAFT:
                spinnerFragPref.setSelection(1);
                break;
            case FN_ACTIVITIES:
                spinnerFragPref.setSelection(2);
                break;
            case FN_APP_SETTINGS:
                spinnerFragPref.setSelection(3);
                break;
            case FN_ABOUT:
                spinnerFragPref.setSelection(4);
                break;
        }

        switch (sharedPreferencesManager.getValue(KEY_SORT_OPTION, OPTION_A_TO_Z)){
            case OPTION_A_TO_Z:
                spinnerSortPref.setSelection(0);
                break;
            case OPTION_Z_TO_A:
                spinnerSortPref.setSelection(1);
                break;
            case OPTION_NEWEST:
                spinnerSortPref.setSelection(2);
                break;
            case OPTION_OLDEST:
                spinnerSortPref.setSelection(3);
                break;
        }


        spinnerFragPref.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        sharedPreferencesManager.setValue(KEY_STARTUP_FRAGMENT, FN_LIVE);
                        break;
                    case 1:
                        sharedPreferencesManager.setValue(KEY_STARTUP_FRAGMENT, FN_DRAFT);
                        break;
                    case 2:
                        sharedPreferencesManager.setValue(KEY_STARTUP_FRAGMENT, FN_ACTIVITIES);
                        break;
                    case 3:
                        sharedPreferencesManager.setValue(KEY_STARTUP_FRAGMENT, FN_APP_SETTINGS);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerSortPref.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        sharedPreferencesManager.setValue(KEY_SORT_OPTION, OPTION_A_TO_Z);
                        break;
                    case 1:
                        sharedPreferencesManager.setValue(KEY_SORT_OPTION, OPTION_Z_TO_A);
                        break;
                    case 2:
                        sharedPreferencesManager.setValue(KEY_SORT_OPTION, OPTION_NEWEST);
                        break;
                    case 3:
                        sharedPreferencesManager.setValue(KEY_SORT_OPTION, OPTION_OLDEST);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}
