package thenewpotato.blogg.managers;

import android.accounts.Account;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.blogger.Blogger;
import com.google.api.services.blogger.model.CommentList;
import com.google.api.services.blogger.model.Post;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;

import thenewpotato.blogg.Tools;
import thenewpotato.blogg.objects.Comment;

import static thenewpotato.blogg.Tools.HTTP_TRANSPORT;
import static thenewpotato.blogg.Tools.JSON_FACTORY;
import static thenewpotato.blogg.Tools.getCroppedBitmap;
import static thenewpotato.blogg.Tools.getResizedBitmap;
import static thenewpotato.blogg.Tools.log;
import static thenewpotato.blogg.Tools.loge;
import static thenewpotato.blogg.Tools.parseDateTime;

/**
 * Created by thenewpotato on 7/2/17.
 * Singled out into its own class because multiple classes will be using this AsyncTask
 *
 * This task takes in a primitive comments list with basic information: blogId, postId, id, inReplyToId
 * The task fills in the missing parameters of a Comment(thenewpotato package)
 * The task then returns a comments list with the exact same size and same comments
 *
 */

public class GetDetailedCommentsTask extends AsyncTask<ArrayList<Comment>, Void, ArrayList<Comment>> {

    private ProgressDialog progressDialog;
    private Account mAccount;
    private Context mContext;
    private ArrayList<Comment> comments;
    protected GetDetailedCommentsTask(Account account, Context context){
        mAccount = account;
        mContext = context;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Loading Comments...");
        progressDialog.show();
    }

    @Override
    protected ArrayList<thenewpotato.blogg.objects.Comment> doInBackground(ArrayList<Comment>... params){
        // comments is only a primitive list containing basic information: blogId, postId, id, and inReplyToId
        comments = params[0];

        GoogleAccountCredential googleAccountCredential =
                GoogleAccountCredential.usingOAuth2(
                        mContext,
                        Collections.singleton(
                                "https://www.googleapis.com/auth/blogger")
                );
        googleAccountCredential.setSelectedAccount(mAccount);
        Blogger service = new Blogger.Builder(HTTP_TRANSPORT, JSON_FACTORY, googleAccountCredential)
                .setApplicationName("Blogger")
                .setHttpRequestInitializer(googleAccountCredential)
                .build();
        try {
            for (int i = 0; i < comments.size(); i++) {
                Comment comment = comments.get(i);
                Blogger.Comments.Get getComment = service.comments().get(comment.blogId, comment.postId, comment.id);
                getComment.setFields("author/displayName,updated,content,author/image/url");
                com.google.api.services.blogger.model.Comment resultComment = getComment.execute();
                Blogger.Posts.Get getPost = service.posts().get(comment.blogId, comment.postId);
                getPost.setFields("title");
                replaceCommentItem(i, resultComment, getPost.execute());
            }
        } catch (IOException e){
            loge(e.getMessage());
        }
        return comments;
    }

    @Override
    protected void onPostExecute(ArrayList<thenewpotato.blogg.objects.Comment> comments){
        progressDialog.dismiss();
    }

    private void replaceCommentItem(int index, com.google.api.services.blogger.model.Comment comment, Post post){
        // getting author picture
        Bitmap authorPic;
        InputStream in = null;
        try {
            in = new java.net.URL("https:" + comment.getAuthor().getImage().getUrl()).openStream();
        } catch (IOException e2){
            Tools.loge(e2.getMessage());
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        // Bitmaps are taking way to long to load -> Density = low
        options.inDensity = DisplayMetrics.DENSITY_LOW;
        options.inTargetDensity = mContext.getResources().getDisplayMetrics().densityDpi;
        authorPic = BitmapFactory.decodeStream(in, null, options);
        log(String.valueOf(authorPic == null));
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        // minus padding...
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density - 10;
        int pxSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpWidth * Float.valueOf("0.1"),
                mContext.getResources().getDisplayMetrics());
        authorPic = getCroppedBitmap(getResizedBitmap(authorPic, pxSize, pxSize));

        comments.set(index, new Comment(
                comment.getAuthor().getDisplayName(),
                authorPic,
                comments.get(index).blogId,
                comments.get(index).postId,
                comments.get(index).id,
                comments.get(index).inReplyToId,
                parseDateTime(comment.getUpdated(), mContext),
                comment.getContent(),
                post.getTitle()
        ));
        log("Detailed comment: " +
                        comment.getAuthor().getDisplayName() + " " +
                    String.valueOf(authorPic == null) + " " +
                    comments.get(index).blogId + " " +
                    comments.get(index).postId + " " +
                    comments.get(index).id + " " +
                    comments.get(index).inReplyToId + " " +
                    parseDateTime(comment.getUpdated(), mContext) + " " +
                    comment.getContent() + " " +
                    post.getTitle()
        );
    }

}
