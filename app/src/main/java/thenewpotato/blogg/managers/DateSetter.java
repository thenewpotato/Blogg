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

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by thenewpotato on 7/15/17.
 */

public class DateSetter implements View.OnClickListener, DatePickerDialog.OnDateSetListener{

    private TextView mTvDateDisplay;
    private Calendar calendar;
    private Context mContext;
    private final String format = "EEE, MMM d, yyyy";
    private SimpleDateFormat simpleDateFormat;

    public DateSetter(TextView tvDateDisplay, Context context){
        mTvDateDisplay = tvDateDisplay;
        mTvDateDisplay.setOnClickListener(this);
        calendar = Calendar.getInstance();
        mContext = context;

        simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        // this shows system time indicating today
        mTvDateDisplay.setText(simpleDateFormat.format(calendar.getTime()));
    }

    @Override
    public void onClick(View v){
        new DatePickerDialog(mContext, this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        calendar.set(Calendar.YEAR, i);
        calendar.set(Calendar.MONTH, i1);
        calendar.set(Calendar.DAY_OF_MONTH, i2);

        mTvDateDisplay.setText(simpleDateFormat.format(calendar.getTime()));
    }

}
