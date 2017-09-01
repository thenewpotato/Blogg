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

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.util.DateTime;
import com.onegravity.rteditor.api.format.RTFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import thenewpotato.blogg.managers.DateSetter;
import thenewpotato.blogg.managers.TimeSetter;

import static thenewpotato.blogg.Tools.RC_NEW;
import static thenewpotato.blogg.Tools.RC_UPDATE;
import static thenewpotato.blogg.Tools.log;
import static thenewpotato.blogg.Tools.loge;

/**
 * Created by thenewpotato on 6/6/17.
 */

public class AddActivity extends BaseEditActivity{

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_post:
                // makes sure that the post has content, supposedly, blank posts should not be a problem
                // however, an issue arises when a post is published under scheduled condition without content
                // took a while to figure out the problem, turns out, publishing scheduled blank posts causes
                // broken posts with invalid IDs
                if(rtEditText.getText().toString().equals("")) {
                    Toast.makeText(this, R.string.notif_blank_post, Toast.LENGTH_SHORT).show();
                    return true;
                }

                View dialogView = View.inflate(this, R.layout.dialog_publish, null);
                final TextView tvDatePicker = (TextView) dialogView.findViewById(R.id.tv_date_picker);
                final TextView tvTimePicker = (TextView) dialogView.findViewById(R.id.tv_time_picker);
                final RadioButton rbPublishNow = (RadioButton) dialogView.findViewById(R.id.rb_publish_now);
                final RadioButton rbSchedulePost = (RadioButton) dialogView.findViewById(R.id.rb_schedule_post);

                rbPublishNow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            rbSchedulePost.setChecked(false);
                        }
                    }
                });

                rbSchedulePost.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            rbPublishNow.setChecked(false);
                        }
                    }
                });

                new DateSetter(tvDatePicker, this);
                new TimeSetter(tvTimePicker, this);

                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_cloud_done_black_24dp)
                        .setTitle("Publishing Post")
                        .setView(dialogView)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intentPost = new Intent();
                                intentPost.putExtra("isDraft", false);
                                intentPost.putExtra("title", "" + etSubject.getText());
                                intentPost.putExtra("content", "" + rtEditText.getText(RTFormat.HTML));

                                // if the user did specify a time, publish at the specified time
                                if (rbSchedulePost.isChecked()) {
                                    log("went through scheduled publishing");
                                    SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, yyyy hh:mm", Locale.US);
                                    try {
                                        Date convertedDate = format.parse(tvDatePicker.getText() + " " + tvTimePicker.getText());
                                        intentPost.putExtra("schedule", new DateTime(convertedDate));
                                    } catch (ParseException e) {
                                        loge(e.getMessage());
                                    }
                                }

                                setResult(RC_NEW, intentPost);
                                finish();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            case R.id.action_save_draft:
                // makes sure that the post has content, supposedly, blank posts should not be a problem
                // however, an issue arises when a post is published under scheduled condition without content
                // took a while to figure out the problem, turns out, publishing scheduled blank posts causes
                // broken posts with invalid IDs
                if(rtEditText.getText().toString().equals("")) {
                    Toast.makeText(this, R.string.notif_blank_post, Toast.LENGTH_SHORT).show();
                    return true;
                }

                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_mode_edit_black_24dp)
                        .setTitle("Saving Draft")
                        .setMessage("Are you sure you want save this post as draft?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intentPost = new Intent();
                                intentPost.putExtra("isDraft", true);
                                intentPost.putExtra("title", "" + etSubject.getText());
                                intentPost.putExtra("content", "" + rtEditText.getText(RTFormat.HTML));
                                setResult(RC_NEW, intentPost);
                                finish();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected String getHtmlContent(){
        // empty post does not have content
        return "";
    }

    @Override
    protected String getSubject(){
        // empty post does not have subject
        return "";
    }

    @Override
    protected String getSubjectHint(){
        return "untitled";
    }

    @Override
    protected int getMenuResourceId(){
        return R.menu.activity_add_bar;
    }

}
