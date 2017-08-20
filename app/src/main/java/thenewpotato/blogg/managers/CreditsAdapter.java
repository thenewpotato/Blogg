package thenewpotato.blogg.managers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import thenewpotato.blogg.R;
import thenewpotato.blogg.objects.CreditItem;

import static thenewpotato.blogg.objects.CreditItem.COPYRIGHT;
import static thenewpotato.blogg.objects.CreditItem.LICENSE;
import static thenewpotato.blogg.objects.CreditItem.PROJECT_CODE;

/**
 * Created by thenewpotato on 8/20/17.
 * Template by https://github.com/codepath
 */

public class CreditsAdapter extends ArrayAdapter<CreditItem> {

    public CreditsAdapter(Context context, ArrayList<CreditItem> credits) {
        super(context, 0, credits);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        CreditItem credit = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_credit, parent, false);
        }
        // Lookup view for data population
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_credits_title);
        TextView tvCode = (TextView) convertView.findViewById(R.id.tv_credits_code);
        TextView tvCopyright = (TextView) convertView.findViewById(R.id.tv_credits_copyright);
        TextView tvLicense = (TextView) convertView.findViewById(R.id.tv_credits_license);
        // Populate the data into the template view using the data object
        tvTitle.setText(credit.name);
        tvCode.setText(PROJECT_CODE + credit.codeUrl);
        tvCopyright.setText(COPYRIGHT + credit.copyrightInfo);
        tvLicense.setText(LICENSE + credit.licenseInfo);
        // Return the completed view to render on screen
        return convertView;
    }

}
