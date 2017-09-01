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


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import thenewpotato.blogg.managers.AboutListAdapter;
import thenewpotato.blogg.objects.ImageTextListItem;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutFragment extends Fragment {

    public AboutFragment() {
        // Required empty public constructor
    }

    public static AboutFragment newInstance () {
        AboutFragment fragment = new AboutFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        ArrayList<ImageTextListItem> imageTextListItems =
                new ArrayList<>(
                        Arrays.asList(
                                new ImageTextListItem(getString(R.string.text_about_github), getResources().getDrawable(R.drawable.github_mark_32px)),
                            new ImageTextListItem(getString(R.string.text_about_ver) + " " + getString(R.string.app_ver), null),
                            new ImageTextListItem(getString(R.string.text_about_credits), null)));
        AboutListAdapter adapter = new AboutListAdapter(getActivity(), imageTextListItems);
        ListView lvAbout = (ListView) view.findViewById(R.id.lv_about);
        lvAbout.setAdapter(adapter);
        lvAbout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // GitHub link
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/thenewpotato/Blogg"));
                    startActivity(browserIntent);
                } else if (position == 2) {
                    // Credits page
                    Intent intent = new Intent(getActivity(), CreditsActivity.class);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

}
