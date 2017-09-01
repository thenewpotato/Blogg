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

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by thenewpotato on 7/15/17.
 */

public class TimeSetter implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {

    private TextView mTvTimeDisplay;
    private Calendar calendar;
    private Context mContext;
    private SimpleDateFormat simpleDateFormat;

    public TimeSetter(TextView tvTimeDisplay, Context context){
        mTvTimeDisplay = tvTimeDisplay;
        mTvTimeDisplay.setOnClickListener(this);
        calendar = Calendar.getInstance();
        mContext = context;

        simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
        tvTimeDisplay.setText(simpleDateFormat.format(calendar.getTime()));
    }

    @Override
    public void onClick(View view){
        new TimePickerDialog(mContext, this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true).show();
    }

    @Override
    public void onTimeSet(TimePicker view, int i, int i1){
        mTvTimeDisplay.setText(i + ":" + i1);
    }

}
