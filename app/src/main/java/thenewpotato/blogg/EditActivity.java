package thenewpotato.blogg;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import com.onegravity.rteditor.api.format.RTFormat;

import thenewpotato.blogg.objects.Post;

import static thenewpotato.blogg.Tools.RC_UPDATE;

public class EditActivity extends BaseEditActivity {

    String mContent;
    Post mPost;

    @Override
    public void onCreate(Bundle bundle){
        // these variables are required in the Intent.putExtra when launching
        mContent = getIntent().getStringExtra("content");
        mPost = getIntent().getParcelableExtra("post");

        // super should be at first line
        // however, we need the variables mContent and mTitle to be initialized before any abstract method that uses these
        // variables get called
        // super will execute parent onCreate, thus putting it after child's
        super.onCreate(bundle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_changes:
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_mode_edit_black_24dp)
                        .setTitle("Saving Changes")
                        .setMessage("Are you sure you want update this post?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intentPost = new Intent();
                                intentPost.putExtra("id", mPost.id);
                                intentPost.putExtra("title", "" + etSubject.getText());
                                intentPost.putExtra("content", "" + rtEditText.getText(RTFormat.HTML));
                                setResult(RC_UPDATE, intentPost);
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
        return mContent;
    }

    @Override
    protected String getSubject(){
        return mPost.title;
    }

    @Override
    protected String getSubjectHint(){
        // hint is not necessary for that title is be defined
        return null;
    }

    @Override
    protected int getMenuResourceId(){
        return R.menu.activity_edit_bar;
    }

}
