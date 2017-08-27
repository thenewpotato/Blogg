package thenewpotato.blogg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import thenewpotato.blogg.managers.CreditsAdapter;
import thenewpotato.blogg.objects.CreditItem;

public class CreditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        getSupportActionBar().setTitle("Credits");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayList<CreditItem> input = new ArrayList<>(Arrays.asList(
                new CreditItem(
                    "Android-RTEditor", 
                    "https://github.com/1gravity/Android-RTEditor", 
                    "2015-2017 Emanuel Moecklin", 
                    "(Apache 2.0) https://github.com/1gravity/Android-RTEditor/blob/master/LICENSE"),
                new CreditItem(
                    "roboto", 
                    "https://github.com/google/roboto", 
                    "2015 Google Inc.", 
                    "(Apache 2.0) https://github.com/google/roboto/blob/master/LICENSE"),
                new CreditItem(
                    "jsoup",
                    "https://jsoup.org/packages/jsoup-1.10.3.jar",
                    "2009-2017 Jonathan Hedley <jonathan@hedley.net>",
                    "(MIT) https://github.com/jhy/jsoup/blob/master/LICENSE"),
                new CreditItem(
                    "Apache Commons IO",
                    "https://github.com/apache/commons-io",
                    "2002-2016 The Apache Software Foundation <http://www.apache.org/>",
                    "(Apache 2.0) https://github.com/apache/commons-io/blob/master/LICENSE.txt"),
                new CreditItem(
                    "Google APIs Client Library",
                    "https://github.com/google/google-api-java-client",
                    "2015 Google Inc.",
                    "(Apache 2.0) https://github.com/google/google-api-java-client/blob/dev/LICENSE"),
                new CreditItem(
                    "JUnit4",
                    "https://github.com/junit-team/junit4",
                    "2002-2017 JUnit <http://junit.org/junit4/>",
                    "(EPL-1.0) https://github.com/junit-team/junit4/blob/master/LICENSE-junit.txt"),
                new CreditItem(
                    "Android Testing Support Library",
                    "https://github.com/google/android-testing-support-library",
                    "2017 Google Inc.",
                    "(Apache 2.0) https://github.com/google/android-testing-support-library/blob/master/LICENSE"),
                new CreditItem(
                    "AOSP Support Library",
                    "https://android.googlesource.com/platform/frameworks/support/",
                    "Google Inc.", "N/A"),
                new CreditItem(
                    "Google Play Services Library (Auth)",
                    "https://developers.google.com/android/guides/overview#the_google_play_services_client_library",
                    "Google Inc.", "N/A"),
                new CreditItem(
                    "Google API Services Library (Blogger)",
                    "https://developers.google.com/blogger/docs/3.0/api-lib/java",
                    "Google Inc.", "N/A")
        ));
        CreditsAdapter adapter = new CreditsAdapter(this, input);
        ListView lvCredits = (ListView) findViewById(R.id.lv_credits);
        lvCredits.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
