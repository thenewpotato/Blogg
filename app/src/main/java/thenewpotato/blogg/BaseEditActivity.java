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
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;

import com.onegravity.rteditor.RTEditText;
import com.onegravity.rteditor.RTManager;
import com.onegravity.rteditor.RTToolbar;
import com.onegravity.rteditor.api.RTApi;
import com.onegravity.rteditor.api.RTMediaFactoryImpl;
import com.onegravity.rteditor.api.RTProxyImpl;
import com.onegravity.rteditor.api.format.RTFormat;

import static thenewpotato.blogg.Tools.log;

/* All extending activities of BaseEditActivity MUST override and define methods:
   * onOptionsItemSelected,
   * getHtmlContent,
   * getSubject,
   * getSubjectHint,
   * getMenuResourceId
*/

public abstract class BaseEditActivity extends AppCompatActivity{

    RTManager rtManager;

    EditText etSubject;
    ActionBar actionBar;
    RTEditText rtEditText;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);

        // required by RTEditor, if removed, activity will not be able to start
        // MUST be placed before setContentView
        setTheme(R.style.RTE_ThemeLight);

        setContentView(R.layout.activity_edit);

        // create RTManager
        RTApi rtApi = new RTApi(this, new RTProxyImpl(this), new RTMediaFactoryImpl(this, true));
        rtManager = new RTManager(rtApi, bundle);

        // register toolbar
        ViewGroup toolbarContainer = (ViewGroup) findViewById(R.id.rte_toolbar_container);
        RTToolbar rtToolbar = (RTToolbar) findViewById(R.id.rte_toolbar);
        if (rtToolbar != null) {
            rtManager.registerToolbar(toolbarContainer, rtToolbar);
        }

        // register editor & set text
        rtEditText = (RTEditText) findViewById(R.id.rtEditText);
        rtManager.registerEditor(rtEditText, true);
        rtEditText.setRichTextEditing(true, getHtmlContent());

        // title bar
        etSubject = (EditText) findViewById(R.id.subject);

            // version check for the deprecation of Html.fromHtml
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            etSubject.setText(Html.fromHtml(getSubject(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            etSubject.setText(Html.fromHtml(getSubject()));
        }

        etSubject.setHint(getSubjectHint()); /*abs*/

        // action bar
        actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(R.string.title_activity_edit);
            // setDisplayHomeAsUpEnabled(true) enables the back arrow button on the ActionBar
            actionBar.setDisplayHomeAsUpEnabled(true);
        } else {
            log("actionBar is null!");
        }

    }

    protected abstract String getHtmlContent();

    protected abstract String getSubject();

    protected abstract String getSubjectHint();

    protected abstract int getMenuResourceId();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        rtManager.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        rtManager.onDestroy(isFinishing());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(getMenuResourceId(), menu);
        return true;
    }

    // this handles the back action alert on the ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Closing Activity")
                        .setMessage("Are you sure you want to close this activity? All unsaved changes will be abandoned.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // this handles the mechanical (virtual) system back button alert
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to close this activity? All unsaved changes will be abandoned.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

}
