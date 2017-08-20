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
                new CreditItem("Android-RTEditor", "https://github.com/1gravity/Android-RTEditor", "2015-2017 Emanuel Moecklin", "(Apache 2.0) https://github.com/1gravity/Android-RTEditor/blob/master/LICENSE"),
                new CreditItem("roboto", "https://github.com/google/roboto", "2015 Google Inc.", "(Apache 2.0) https://github.com/google/roboto/blob/master/LICENSE")
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
