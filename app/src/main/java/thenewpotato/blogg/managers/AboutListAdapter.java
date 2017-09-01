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

package thenewpotato.blogg.managers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import thenewpotato.blogg.R;
import thenewpotato.blogg.objects.ImageTextListItem;

/**
 * Created by thenewpotato on 8/13/17.
 * Template by https://github.com/codepath
 */

public class AboutListAdapter extends ArrayAdapter<ImageTextListItem>{

    public AboutListAdapter(Context context, ArrayList<ImageTextListItem> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ImageTextListItem user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_about, parent, false);
        }
        // Lookup view for data population
        TextView tv = (TextView) convertView.findViewById(R.id.tv_about_item);
        ImageView iv = (ImageView) convertView.findViewById(R.id.iv_about_item);
        // Populate the data into the template view using the data object
        tv.setText(user.text);
        iv.setImageDrawable(user.image);
        // Return the completed view to render on screen
        return convertView;
    }

}
