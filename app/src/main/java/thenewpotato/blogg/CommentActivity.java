package thenewpotato.blogg;

import android.accounts.Account;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.blogger.Blogger;
import com.google.api.services.blogger.model.Post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import thenewpotato.blogg.managers.CommentsAdapter;
import thenewpotato.blogg.managers.GetDetailedCommentsTask;
import thenewpotato.blogg.objects.Comment;

import static thenewpotato.blogg.Tools.log;
import static thenewpotato.blogg.Tools.loge;

public class CommentActivity extends AppCompatActivity {

    private static final String TAG = "CommentActivity";
    private static HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    // flag_loading is explicitly for onScrollListener, so that the listener does not invoke itself multiple
    // times while the previous invocation is in process
    private boolean flag_loading = false;
    // flag_no_more_comments tracks whether or not there is any more comments in mPrimitiveComments to sort
    private boolean flag_no_more_comments = false;
    // startingCount is explicitly for the sake of simplicity when filtering comments
    // it tracks where in the mPrimitiveComments the last filter has left off
    // so the next filter process can simply pick up where left off
    private int startingCount = 0;

    ListView lvMain;
    ArrayList<Comment> mPrimitiveComments;
    Comment mComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Intent mIntent = getIntent();
        mPrimitiveComments = mIntent.getParcelableArrayListExtra(Tools.KEY_PRIMITIVE_COMMENTS);
        mComment = mIntent.getParcelableExtra(Tools.KEY_ROOT_COMMENT);

        // action bar
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(R.string.title_activity_comment);
            // setDisplayHomeAsUpEnabled(true) enables the back arrow button on the ActionBar
            actionBar.setDisplayHomeAsUpEnabled(true);
        } else {
            log("actionBar is null!");
        }

        lvMain = (ListView) findViewById(R.id.lv_comment_details);
        // ListView will be initialized with only the root comment
        final CommentsAdapter adapter =
                new CommentsAdapter(this, new ArrayList<>(Collections.singletonList(mComment)), true);
        lvMain.setAdapter(adapter);
        GetDetailedCommentsTask task = new GetDetailedCommentsTask(MainActivity.mAuthorizedAccount, this){
            @Override
            protected void onPostExecute(ArrayList<Comment> result){
                super.onPostExecute(result);
                adapter.addAll(result);
                adapter.notifyDataSetChanged();
            }
        };
        task.execute(getTenFilteredComments(mComment));
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 0) {
                    Comment comment = (Comment) adapterView.getAdapter().getItem(i);
                    Intent intent = new Intent(CommentActivity.this, CommentActivity.class);
                    intent.putExtra(Tools.KEY_ROOT_COMMENT, comment);
                    intent.putParcelableArrayListExtra(Tools.KEY_PRIMITIVE_COMMENTS, mPrimitiveComments);
                    startActivity(intent);
                }
            }
        });
        // onScrollListener invoked when reaching the bottom of the ListView
        lvMain.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                // nothing here
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
                {
                    // if there is no more comments to sort, there is no reason to keep invoking the filtering process
                    if(!flag_loading && !flag_no_more_comments)
                    {
                        flag_loading = true;
                        GetDetailedCommentsTask taskMore =
                                new GetDetailedCommentsTask(MainActivity.mAuthorizedAccount, CommentActivity.this){
                                    @Override
                                    protected void onPostExecute(ArrayList<Comment> result){
                                        super.onPostExecute(result);
                                        adapter.addAll(result);
                                        adapter.notifyDataSetChanged();
                                        flag_loading = false;
                                    }
                                };
                        taskMore.execute(getTenFilteredComments(mComment));
                    }
                }
            }
        });

        Button btnSendComment = (Button) findViewById(R.id.btn_send_comment);
        btnSendComment.setText("Reply to " + mComment.authorName + " in your browser...");
        btnSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetUrlOfPostTask task = new GetUrlOfPostTask(MainActivity.mAuthorizedAccount);
                task.execute(mComment);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<Comment> getTenFilteredComments(Comment rootComment){
        ArrayList<Comment> result = new ArrayList<>();
        for(int i = 0; i < 10; startingCount++){
            log(String.valueOf(startingCount) + " " + String.valueOf(mPrimitiveComments.size()));
            if(startingCount == mPrimitiveComments.size()){
                flag_no_more_comments = true;
                break;
            }
            if(mPrimitiveComments.get(startingCount).inReplyToId != null &&
                    mPrimitiveComments.get(startingCount).inReplyToId.equals(rootComment.id)){
                result.add(mPrimitiveComments.get(startingCount));
                i++;
                log(String.valueOf(i) + " " + String.valueOf(startingCount));
            }
        }
        return result;
    }

    private class GetUrlOfPostTask extends AsyncTask<Comment, Void, String> {

        private ProgressDialog progressDialog;
        Account mAccount;
        GetUrlOfPostTask(Account account){ mAccount = account; }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(CommentActivity.this);
            progressDialog.setMessage("Retrieving URL...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(thenewpotato.blogg.objects.Comment... params){
            thenewpotato.blogg.objects.Comment mComment = params[0];
            GoogleAccountCredential googleAccountCredential =
                    GoogleAccountCredential.usingOAuth2(
                            CommentActivity.this,
                            Collections.singleton(
                                    "https://www.googleapis.com/auth/blogger")
                    );
            googleAccountCredential.setSelectedAccount(mAccount);
            Blogger service = new Blogger.Builder(HTTP_TRANSPORT, JSON_FACTORY, googleAccountCredential)
                    .setApplicationName("Blogger")
                    .setHttpRequestInitializer(googleAccountCredential)
                    .build();
            try {
                Blogger.Posts.Get get = service.posts().get(mComment.blogId, mComment.postId);
                get.setFields("url");
                Post post = get.execute();
                return post.getUrl();
            }catch (IOException e){
                loge(e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String url){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
            progressDialog.dismiss();
        }

    }
}
