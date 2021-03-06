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

import android.accounts.Account;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.DateTime;
import com.google.api.services.blogger.Blogger;
import com.google.api.services.blogger.model.Blog;
import com.google.api.services.blogger.model.BlogList;
import com.google.api.services.blogger.model.Comment;
import com.google.api.services.blogger.model.CommentList;
import com.google.api.services.blogger.model.Pageviews;
import com.google.api.services.blogger.model.Post;
import com.google.api.services.blogger.model.PostList;
import com.onegravity.rteditor.utils.io.FilenameUtils;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;
import thenewpotato.blogg.managers.PostsAdapter;
import thenewpotato.blogg.managers.SharedPreferencesManager;

import static thenewpotato.blogg.Tools.FN_ABOUT;
import static thenewpotato.blogg.Tools.FN_ACTIVITIES;
import static thenewpotato.blogg.Tools.FN_APP_SETTINGS;
import static thenewpotato.blogg.Tools.FN_DRAFT;
import static thenewpotato.blogg.Tools.FN_LIVE;
import static thenewpotato.blogg.Tools.HTTP_TRANSPORT;
import static thenewpotato.blogg.Tools.JSON_FACTORY;
import static thenewpotato.blogg.Tools.KEY_SORT_OPTION;
import static thenewpotato.blogg.Tools.KEY_STARTUP_BLOG;
import static thenewpotato.blogg.Tools.KEY_STARTUP_FRAGMENT;
import static thenewpotato.blogg.Tools.OPTION_A_TO_Z;
import static thenewpotato.blogg.Tools.OPTION_NEWEST;
import static thenewpotato.blogg.Tools.OPTION_OLDEST;
import static thenewpotato.blogg.Tools.OPTION_Z_TO_A;
import static thenewpotato.blogg.Tools.RC_AUTHORIZE;
import static thenewpotato.blogg.Tools.RC_NEW;
import static thenewpotato.blogg.Tools.RC_UPDATE;
import static thenewpotato.blogg.Tools.VAL_NULL;
import static thenewpotato.blogg.Tools.getCroppedBitmap;
import static thenewpotato.blogg.Tools.getResizedBitmap;
import static thenewpotato.blogg.Tools.log;
import static thenewpotato.blogg.Tools.loge;
import static thenewpotato.blogg.objects.Post.STATUS_DRAFT;
import static thenewpotato.blogg.objects.Post.STATUS_LIVE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener, PostsAdapter.activity {

    private static final String IMAGE_TYPE_JPEG = "image/jpeg";
    private static final String IMAGE_TYPE_BMP = "image/bmp";
    private static final String IMAGE_TYPE_GIF = "image/gif";
    private static final String IMAGE_TYPE_PNG = "image/png";

    private GoogleSignInClient mGoogleSignInClient;
    public static Account mAuthorizedAccount;
    GoogleSignInAccount googleSignInAccount;
    SignInButton buttonSignIn;

    String urlUserPhoto;
    SparseArray<String> mapBlogs;
    ArrayList<thenewpotato.blogg.objects.Post> posts;
    int intSelectedFragment = VAL_NULL;
    int intSelectedBlogPos = 0;
    int intSelectedSortOption;

    Button buttonSignOut;
    ImageView imageviewAccount;
    TextView textviewAccountName, textviewAccountEmail;
    Spinner spinnerBlogChoice;
    FrameLayout framelayoutMain;
    ArrayAdapter<String> arrayadapterSpinner;
    NavigationView navigationView;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //AUTO-GENERATED PRE-INITIALIZATION
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAuthorizedAccount != null) {
                    Intent intent = new Intent(MainActivity.this, AddActivity.class);
                    MainActivity.this.startActivityForResult(intent, RC_NEW);
                }else{
                    Toast.makeText(MainActivity.this, "Please login first.", Toast.LENGTH_LONG).show();
                }
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //PUT CODE AFTER THIS LINE

        SharedPreferencesManager.initializeInstance(MainActivity.this);
        intSelectedSortOption = SharedPreferencesManager.getInstance().getValue(KEY_SORT_OPTION, OPTION_A_TO_Z);

        View viewNavHeader = navigationView.getHeaderView(0);
        spinnerBlogChoice = (Spinner) findViewById(R.id.spinner_blog_choice);
        arrayadapterSpinner = new ArrayAdapter<>(this, R.layout.item_spinner_blog);
        arrayadapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBlogChoice.setAdapter(arrayadapterSpinner);
        spinnerBlogChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // checking method using ++check>1 was in place, however, it seems not to be needed
                // honestly, I forgot why it was there... implement again if necessary
                intSelectedBlogPos = i;
                SharedPreferencesManager.getInstance().setValue(KEY_STARTUP_BLOG, i);

                log("Selected spinner pos: " + spinnerBlogChoice.getSelectedItem().toString());

                // this logic statement determines whether the application was booted initially
                // or the user has selected a spinner item
                // if app was booted, intSelectedFragment would have no value (designated by VAL_NULL),
                // thus booting default fragment through SPM
                // Note: see GetListOfBlogTask for more info
                if(intSelectedFragment != VAL_NULL) {
                    loadFragment(intSelectedFragment);
                } else{
                    loadFragment(SharedPreferencesManager.getInstance().getValue(KEY_STARTUP_FRAGMENT, FN_LIVE));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Really should do something here
            }
        });
        buttonSignIn = (SignInButton) viewNavHeader.findViewById(R.id.button_signin);
        buttonSignOut = (Button) viewNavHeader.findViewById(R.id.button_signout);
        imageviewAccount = (ImageView) viewNavHeader.findViewById(R.id.imageview_account);
        textviewAccountName = (TextView) viewNavHeader.findViewById(R.id.textview_account_name);
        textviewAccountEmail = (TextView) viewNavHeader.findViewById(R.id.textview_account_email);
        framelayoutMain = (FrameLayout) findViewById(R.id.framelayout_main);
        buttonSignIn.setOnClickListener(this);
        buttonSignOut.setOnClickListener(this);
        mapBlogs = new SparseArray<>();
        posts = new ArrayList<>();
        final ImageView ivSort = (ImageView) findViewById(R.id.iv_sort);
        //ivSort.setColorFilter(Color.parseColor("#ffffff"));
        ivSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(MainActivity.this, ivSort);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.popup_sort, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.i_a_to_z:
                                intSelectedSortOption = OPTION_A_TO_Z;
                                SharedPreferencesManager.getInstance().setValue(KEY_SORT_OPTION, OPTION_A_TO_Z);
                                break;
                            case R.id.i_z_to_a:
                                intSelectedSortOption = OPTION_Z_TO_A;
                                SharedPreferencesManager.getInstance().setValue(KEY_SORT_OPTION, OPTION_Z_TO_A);
                                break;
                            case R.id.i_newest:
                                intSelectedSortOption = OPTION_NEWEST;
                                SharedPreferencesManager.getInstance().setValue(KEY_SORT_OPTION, OPTION_NEWEST);
                                break;
                            case R.id.i_oldest:
                                intSelectedSortOption = OPTION_OLDEST;
                                SharedPreferencesManager.getInstance().setValue(KEY_SORT_OPTION, OPTION_OLDEST);
                                break;
                        }
                        loadFragment(intSelectedFragment);
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });
        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope("https://www.googleapis.com/auth/blogger"), new Scope("https://picasaweb.google.com/data/"))
                        .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    @Override
    public void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
        if (account != null) {
            googleSignInAccount = account;
            mAuthorizedAccount = account.getAccount();

            GetListOfBlogTask task = new GetListOfBlogTask(mAuthorizedAccount);
            task.execute();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_published) {
            loadFragment(FN_LIVE);
        } else if (id == R.id.nav_draft) {
            loadFragment(FN_DRAFT);
        } else if (id == R.id.nav_activities) {
            loadFragment(FN_ACTIVITIES);
        } else if (id == R.id.nav_app_settings) {
            loadFragment(FN_APP_SETTINGS);
        } else if (id == R.id.nav_about) {
            loadFragment(FN_ABOUT);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult){
        //ERROR HANDLING CODE HERE
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.button_signin:
                Tools.log("authorizeAccess in process");
                authorizeAccess();
                break;
            case R.id.button_signout:
                Tools.log("signOut in process");
                signOut();
                revokeAccess();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_AUTHORIZE){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }else if(resultCode == RC_NEW){
            Tools.log("result from AA: " + data.getStringExtra("content"));
            InsertPostTask task = new InsertPostTask(mAuthorizedAccount, data.getBooleanExtra("isDraft", true), (DateTime) data.getSerializableExtra("schedule"));
            task.execute(data.getStringExtra("title"), data.getStringExtra("content"));
        }else if(resultCode == RC_UPDATE){
            UpdatePostTask task = new UpdatePostTask(mAuthorizedAccount);
            task.execute(data.getStringExtra("id"), data.getStringExtra("title"), data.getStringExtra("content"));
        }
    }

    @Override
    public void deletePosts(ArrayList<String> postIds){
        DeletePostsTask task = new DeletePostsTask(mAuthorizedAccount);
        task.execute(postIds);
    }

    @Override
    public void publishPosts(ArrayList<String> postIds, DateTime scheduledTime){
        PublishPostsTask task = new PublishPostsTask(mAuthorizedAccount, scheduledTime);
        task.execute(postIds);
    }

    public void updatePostCall(thenewpotato.blogg.objects.Post post){
        GetContentOfPostTask task = new GetContentOfPostTask(mAuthorizedAccount);
        task.execute(post);
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account == null) {
            buttonSignIn.setVisibility(View.VISIBLE);
            buttonSignOut.setVisibility(View.GONE);
            imageviewAccount.setImageDrawable(ContextCompat.getDrawable(this, android.R.drawable.sym_def_app_icon));
            imageviewAccount.setVisibility(View.GONE);
            textviewAccountName.setText("");
            textviewAccountEmail.setText("");
            textviewAccountName.setVisibility(View.GONE);
            textviewAccountEmail.setVisibility(View.GONE);
            arrayadapterSpinner.clear();
            arrayadapterSpinner.notifyDataSetChanged();
            textviewAccountName.setText("");
            drawer.openDrawer(Gravity.START);
        } else {
            buttonSignIn.setVisibility(View.GONE);
            buttonSignOut.setVisibility(View.VISIBLE);
            imageviewAccount.setVisibility(View.VISIBLE);
            textviewAccountEmail.setVisibility(View.VISIBLE);
            textviewAccountName.setVisibility(View.VISIBLE);
            textviewAccountName.setText(account.getGivenName() + " " + account.getFamilyName());
            textviewAccountEmail.setText(account.getEmail());
            Uri uriUserPhoto = account.getPhotoUrl();
            if(uriUserPhoto != null) {
                urlUserPhoto = uriUserPhoto.toString();
                new DownloadImageTask(imageviewAccount).execute(urlUserPhoto);
            } else{
                Tools.log("showAuthenticatedUi: User does not have profile pic");
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);

            googleSignInAccount = account;
            mAuthorizedAccount = account.getAccount();

            GetListOfBlogTask task = new GetListOfBlogTask(mAuthorizedAccount);
            task.execute();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Tools.loge("signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void loadFragment(int fragmentId){
        if(mAuthorizedAccount != null) {
            switch (fragmentId) {
                case FN_LIVE:
                    //FRAGMENT TRANSACTION HANDLED IN ASYNC
                    intSelectedFragment = FN_LIVE;
                    posts.clear();
                    GetListOfPostsTask task1 = new GetListOfPostsTask(mAuthorizedAccount, STATUS_LIVE);
                    navigationView.setCheckedItem(R.id.nav_published);
                    Tools.log(String.valueOf(spinnerBlogChoice.getSelectedItemPosition()));
                    task1.execute(mapBlogs.get(intSelectedBlogPos));
                    break;
                case FN_DRAFT:
                    //FRAGMENT TRANSACTION HANDLED IN ASYNC
                    intSelectedFragment = FN_DRAFT;
                    posts.clear();
                    GetListOfPostsTask task2 = new GetListOfPostsTask(mAuthorizedAccount, STATUS_DRAFT);
                    navigationView.setCheckedItem(R.id.nav_draft);
                    Tools.log(String.valueOf(spinnerBlogChoice.getSelectedItemPosition()) + mapBlogs.get(spinnerBlogChoice.getSelectedItemPosition()));
                    task2.execute(mapBlogs.get(intSelectedBlogPos));
                    break;
                case FN_ACTIVITIES:
                    intSelectedFragment = FN_ACTIVITIES;
                    GetPrimitiveListOfCommentsTask task3 = new GetPrimitiveListOfCommentsTask(mAuthorizedAccount);
                    navigationView.setCheckedItem(R.id.nav_activities);
                    Tools.log(String.valueOf(spinnerBlogChoice.getSelectedItemPosition()) + mapBlogs.get(spinnerBlogChoice.getSelectedItemPosition()));
                    task3.execute(mapBlogs.get(intSelectedBlogPos));
                    break;
                /*case FN_STATS:
                    intSelectedFragment = FN_STATS;
                    GetPageViewOfBlogTask task4 = new GetPageViewOfBlogTask(mAuthorizedAccount);
                    navigationView.setCheckedItem(R.id.nav_stats);
                    Tools.log(String.valueOf(spinnerBlogChoice.getSelectedItemPosition()) + mapBlogs.get(spinnerBlogChoice.getSelectedItemPosition()));
                    task4.execute(mapBlogs.get(intSelectedBlogPos));
                    break;*/
                case FN_APP_SETTINGS:
                    AppSettingsFragment fragment = AppSettingsFragment.newInstance();
                    getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, fragment).addToBackStack(null).commit();
                    break;
                case FN_ABOUT:
                    AboutFragment aboutFragment = AboutFragment.newInstance();
                    getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, aboutFragment).addToBackStack(null).commit();
            }
        }else{
            Toast.makeText(this, "Please login first.", Toast.LENGTH_LONG).show();
        }
    }

    private void authorizeAccess(){
        Tools.log("authorizeAccess in process");

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_AUTHORIZE);
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
    }

    private void helperMapPosts(PostList postList){
        try{
            for(Post post : postList.getItems()){
                Tools.log("GetListOfPostsTask: onPostExecute: Title: " + post.getTitle() + " Id: " + post.getId() + " url: " + post.getUrl() + "updated: " + post.getUpdated().toString() + " type: " + post.getStatus());
                posts.add(new thenewpotato.blogg.objects.Post(
                        post.getTitle(),
                        post.getUpdated(),
                        post.getEtag(),
                        post.getId(),
                        post.getUrl(),
                        post.getStatus()
                ));
            }
        } catch (NullPointerException e){
            loge(new Exception().getStackTrace()[0].getLineNumber() + "postList null");
        }
    }

    private ArrayList<thenewpotato.blogg.objects.Comment> helperMapComments
            (CommentList commentList, ArrayList<thenewpotato.blogg.objects.Comment> comments){
        try {
            for (Comment comment : commentList.getItems()) {
                comments.add(new thenewpotato.blogg.objects.Comment(
                        null,
                        null,
                        comment.getBlog().getId(),
                        comment.getPost().getId(),
                        comment.getId(),
                        getCommentInReplyToId(comment),
                        null,
                        null,
                        null
                ));
                log("Comment primitive: " +
                        comment.getBlog().getId() + " " +
                        comment.getPost().getId() + " " +
                        comment.getId() + " " +
                        getCommentInReplyToId(comment)
                );
            }
        }catch (NullPointerException e){
            loge("There is no more comments " + e.getMessage());
        }
        return comments;
    }

    private String getCommentInReplyToId(Comment comment){
        try{
            return comment.getInReplyTo().getId();
        } catch (NullPointerException e){
            log("Comment has no in-reply to!");
            return null;
        }
    }

    // by https://stackoverflow.com/users/898459/brijesh-thakur
    private String getLocalPath(Bitmap bitmap, String filename) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory, filename);

        log(directory.toString() + " " + filename);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                loge(e.getMessage());
            }
        }
        return directory.getAbsolutePath() + "/" + filename;
    }

    private String getImageType(String imagePath){
        if (imagePath == null) {
            return null;
        }
        String mImageType = imagePath.substring(imagePath.lastIndexOf("."));
        log("ImageType = " + mImageType);
        switch (mImageType) {
            case ".jpg":
                return IMAGE_TYPE_JPEG;
            case ".jpeg":
                return IMAGE_TYPE_JPEG;
            case ".png":
                return IMAGE_TYPE_PNG;
            case ".bmp":
                return IMAGE_TYPE_BMP;
            case ".gif":
                return IMAGE_TYPE_GIF;
            default:
                return null;
        }
    }

    private class GetListOfBlogTask extends AsyncTask<Void, Void, BlogList> {

        private ProgressDialog progressDialog;
        Account mAccount;
        GetListOfBlogTask(Account account) {
            mAccount = account;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading Blogs...");
            progressDialog.show();
        }

        @Override
        protected BlogList doInBackground(Void... params){
            BlogList result = null;
            try{
                GoogleAccountCredential googleAccountCredential =
                        GoogleAccountCredential.usingOAuth2(
                                MainActivity.this,
                                Collections.singleton(
                                        "https://www.googleapis.com/auth/blogger")
                        );
                googleAccountCredential.setSelectedAccount(mAccount);
                Blogger service = new Blogger.Builder(HTTP_TRANSPORT, JSON_FACTORY, googleAccountCredential)
                        .setApplicationName("Blogg")
                        .setHttpRequestInitializer(googleAccountCredential)
                        .build();
                Blogger.Blogs.ListByUser blogListByUser = service.blogs().listByUser("self");
                result = blogListByUser.execute();
            } catch (IOException e){
                loge("GetListOfBlogTask: IOException, failed!, message= " + e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(BlogList blogList){
            try {
                for (Blog blog : blogList.getItems()) {
                    Tools.log("blogs: " + blog.getName());
                    arrayadapterSpinner.add(blog.getName());
                    mapBlogs.put(arrayadapterSpinner.getPosition(blog.getName()), blog.getId());
                }
            } catch(NullPointerException e){
                loge("blogList is null");
            }
            arrayadapterSpinner.notifyDataSetChanged();

            SharedPreferencesManager.initializeInstance(MainActivity.this);
            try{
                spinnerBlogChoice.setSelection(SharedPreferencesManager.getInstance().getValue(KEY_STARTUP_BLOG, 0));
                intSelectedBlogPos = SharedPreferencesManager.getInstance().getValue(KEY_STARTUP_BLOG, 0);
            }catch (Exception e){
                loge(e.getMessage());
            }

            progressDialog.dismiss();

            // loadFragment was called at this position previously,
            // however, spinner.setSelection also set off another loadFragment in its onSelect listener
            // thus this one was chosen to be removed
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{
        ImageView mImageview;

        DownloadImageTask(ImageView mImageview){
            this.mImageview = mImageview;
        }

        protected Bitmap doInBackground(String... urls){
            String urldisplay = urls[0];
            Bitmap mAccountPic = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mAccountPic = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                loge(e.getMessage());
            }
            return mAccountPic;
        }

        protected void onPostExecute(Bitmap result) {
            int pxSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
            if (result != null) {
                mImageview.setImageBitmap(getCroppedBitmap(getResizedBitmap(result, pxSize, pxSize)));
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.notif_connection_timeout), Toast.LENGTH_LONG).show();
            }
        }
    }

    private class GetListOfPostsTask extends AsyncTask<String, Void, PostList>{
        private ProgressDialog progressDialog;
        Account mAccount;
        String mStatus;
        GetListOfPostsTask(Account account, String status){ mAccount = account; mStatus = status;}

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading Posts...");
            progressDialog.show();
        }

        @Override
        protected PostList doInBackground(String... params) {
            String blogId = params[0];
            PostList posts = null;
            try {
                GoogleAccountCredential googleAccountCredential =
                        GoogleAccountCredential.usingOAuth2(
                                MainActivity.this,
                                Collections.singleton(
                                        "https://www.googleapis.com/auth/blogger")
                        );
                googleAccountCredential.setSelectedAccount(mAccount);
                Blogger service = new Blogger.Builder(HTTP_TRANSPORT, JSON_FACTORY, googleAccountCredential)
                        .setApplicationName("Blogg")
                        .setHttpRequestInitializer(googleAccountCredential)
                        .build();
                Blogger.Posts.List postsListAction;
                // if the user requests to list the drafts, list the scheduled ones along with it as well
                if(mStatus.equals(STATUS_LIVE)) {
                    postsListAction = service.posts().list(blogId).setStatus(new ArrayList<>(Collections.singletonList(mStatus)));
                } else{
                    postsListAction = service.posts().list(blogId).setStatus(new ArrayList<>(Arrays.asList(mStatus, "SCHEDULED")));
                }
                postsListAction.setFields("items(id,updated,published,title,url,status),nextPageToken");
                postsListAction.setView("ADMIN");
                posts = postsListAction.execute();
                helperMapPosts(posts);
                while(posts.getItems() != null && !posts.getItems().isEmpty()){
                    String pageToken = posts.getNextPageToken();
                    if (pageToken == null) {
                        break;
                    }
                    postsListAction.setPageToken(pageToken);
                    posts = postsListAction.execute();
                    helperMapPosts(posts);
                }
            } catch (IOException e) {
                loge("GetListOfPostsTask: doInBackground: IOException: " + e.getMessage());
            }
            return posts;
        }

        @Override
        protected void onPostExecute(PostList postList){
            PostListFragment fragment = PostListFragment.newInstance(mStatus, posts, intSelectedSortOption);

            getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, fragment).addToBackStack(null).commit();
            //addToBackStack added to prevent blank screen upon transaction
            progressDialog.dismiss();
        }
    }

    private class PublishPostsTask extends AsyncTask<ArrayList<String>, Void, Void>{
        private ProgressDialog progressDialog;
        Account mAccount;
        // adds the feature of post scheduled publishing
        DateTime mScheduledTime;
        PublishPostsTask(Account account, DateTime scheduledTime){ mAccount = account; mScheduledTime = scheduledTime; }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Publishing Posts...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(ArrayList<String>... listDelete) {
            ArrayList<String> passed = listDelete[0];
            GoogleAccountCredential googleAccountCredential =
                    GoogleAccountCredential.usingOAuth2(
                            MainActivity.this,
                            Collections.singleton(
                                    "https://www.googleapis.com/auth/blogger")
                    );
            googleAccountCredential.setSelectedAccount(mAccount);
            Blogger service = new Blogger.Builder(HTTP_TRANSPORT, JSON_FACTORY, googleAccountCredential)
                    .setApplicationName("Blogg")
                    .setHttpRequestInitializer(googleAccountCredential)
                    .build();
            for(int i = 0; i < passed.size(); i++){
                try {
                    Blogger.Posts.Publish postsPublishAction;
                    // if the user specified scheduled publishing, use the specified time, otherwise it shall be null
                    if(mScheduledTime != null) {
                        postsPublishAction = service.posts().publish(
                                mapBlogs.get(intSelectedBlogPos),
                                passed.get(i))
                                .setPublishDate(mScheduledTime);
                    } else{
                        postsPublishAction = service.posts().publish(mapBlogs.get(intSelectedBlogPos), passed.get(i));
                    }
                    postsPublishAction.execute();
                    Tools.log("Publish post " + passed.get(i) + "succeeded");
                }catch (IOException e){
                    loge("Publish post " + passed.get(i) + "failed. Error message " + e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            progressDialog.dismiss();
            loadFragment(intSelectedFragment);
        }
    }

    private class DeletePostsTask extends AsyncTask<ArrayList<String>, Void, Void>{
        private ProgressDialog progressDialog;
        Account mAccount;
        DeletePostsTask(Account account){ mAccount = account; }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Deleting Posts...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(ArrayList<String>... listDelete){
            ArrayList<String> passed = listDelete[0];
            GoogleAccountCredential googleAccountCredential =
                    GoogleAccountCredential.usingOAuth2(
                            MainActivity.this,
                            Collections.singleton(
                                    "https://www.googleapis.com/auth/blogger")
                    );
            googleAccountCredential.setSelectedAccount(mAccount);
            Blogger service = new Blogger.Builder(HTTP_TRANSPORT, JSON_FACTORY, googleAccountCredential)
                    .setApplicationName("Blogg")
                    .setHttpRequestInitializer(googleAccountCredential)
                    .build();
            for(int i = 0; i < passed.size(); i++){
                try {
                    Blogger.Posts.Delete postsDeleteAction = service.posts().delete(mapBlogs.get(intSelectedBlogPos), passed.get(i));
                    postsDeleteAction.execute();
                    Tools.log("DelPostTask: Delete post " + passed.get(i) + "succeeded");
                }catch (IOException e){
                    loge("DelPostTask: Delete post " + passed.get(i) + "failed. Error message " + e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            progressDialog.dismiss();
            loadFragment(intSelectedFragment);
        }
    }

    private class InsertPostTask extends AsyncTask<String, Void, Void>{

        private ProgressDialog progressDialog;
        Account mAccount;
        boolean mIsDraft;
        // adds the feature of post scheduled publishing
        DateTime mScheduledTime;
        InsertPostTask(Account account, boolean isDraft, DateTime scheduledTime) {
            mAccount = account;
            mIsDraft = isDraft;
            mScheduledTime = scheduledTime;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            if(!mIsDraft) {
                progressDialog.setMessage("Uploading Post...");
            }else{
                progressDialog.setMessage("Saving Changes...");
            }
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params){
            String title = params[0];
            String html = params[1];
            GoogleAccountCredential googleAccountCredential =
                    GoogleAccountCredential.usingOAuth2(
                            MainActivity.this,
                            Arrays.asList("https://www.googleapis.com/auth/blogger",
                                    "https://picasaweb.google.com/data/")
                    );
            googleAccountCredential.setSelectedAccount(mAccount);
            Blogger service = new Blogger.Builder(HTTP_TRANSPORT, JSON_FACTORY, googleAccountCredential)
                    .setApplicationName("Blogg")
                    .setHttpRequestInitializer(googleAccountCredential)
                    .build();

            String mAccessToken = "";
            try {
                mAccessToken = googleAccountCredential.getToken();
                log("Access token = " + mAccessToken);
            } catch (Exception e){
                loge(e.getMessage());
            }

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost("https://picasaweb.google.com/data/feed/api/user/default/albumid/default");
            httpPost.addHeader("Authorization",  "Bearer " + mAccessToken);

            Document htmlDocument = Jsoup.parse(html);
            for (Element imgElement : htmlDocument.select("img")) {
                String imgLocalSrc = imgElement.attr("src");
                byte[] imageContent;
                File image = new File(imgLocalSrc);
                try {
                    imageContent = FileUtils.readFileToByteArray(image);
                } catch (IOException e){
                    loge(e.getMessage());
                    return null;
                }

                httpPost.addHeader("Content-Type", getImageType(imgLocalSrc));
                // "Slug" header sets the file name of the uploaded image
                httpPost.addHeader("Slug", image.getName());

                String xmlResponse = "";
                try {
                    httpPost.setEntity(new ByteArrayEntity(imageContent));
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    xmlResponse = EntityUtils.toString(httpResponse.getEntity());
                    log(xmlResponse);
                } catch (IOException e){
                    loge(e.getMessage());
                }

                // by https://stackoverflow.com/users/1818911/su
                String uploadedImageUrl = "";
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder;
                InputSource source;
                try {
                    builder = factory.newDocumentBuilder();
                    source = new InputSource(new StringReader(xmlResponse));
                    org.w3c.dom.Document doc = builder.parse(source);
                    NodeList list = doc.getElementsByTagName("media:content");
                    uploadedImageUrl = list.item(0).getAttributes().getNamedItem("url").getNodeValue();
                } catch (Exception e){
                    loge(e.getMessage());
                }
                log(uploadedImageUrl);

                imgElement.attr("src", uploadedImageUrl);
            }

            Post content = new Post();
            content.setTitle(title);
            log(htmlDocument.html());
            content.setContent(htmlDocument.html());
            try {
                Blogger.Posts.Insert postInsertAction;
                if(!mIsDraft) {
                    // checks if scheduled time is null or not, if so, publish post immediately
                    if(mScheduledTime == null) {
                        postInsertAction = service.posts().insert(mapBlogs.get(intSelectedBlogPos), content);
                        postInsertAction.execute();
                    }else{
                        // Blogger API does not allow (for some reason) posts to be inserted with a publish time
                        // so this block goes around this limitation by drafting the post first, then publishing it
                        // a scheduled time can be included upon the publishing step
                        Blogger.Posts.Insert draftInsertAction = service.posts().insert(mapBlogs.get(intSelectedBlogPos),
                                content).setIsDraft(true);
                        Post draft = draftInsertAction.execute();
                        Blogger.Posts.Publish publishAction = service.posts().publish(mapBlogs.get(intSelectedBlogPos),
                                draft.getId()).setPublishDate(mScheduledTime);
                        publishAction.execute();
                    }
                }else{
                    postInsertAction = service.posts().insert(mapBlogs.get(intSelectedBlogPos), content).setIsDraft(true);
                    postInsertAction.execute();
                }
            }catch (IOException e){
                loge("InsertPostTask: error: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            progressDialog.dismiss();
            loadFragment(intSelectedFragment);
        }

    }

    private class GetContentOfPostTask extends AsyncTask<thenewpotato.blogg.objects.Post, Void, String>{

        private ProgressDialog progressDialog;
        Account mAccount;
        thenewpotato.blogg.objects.Post mPost;
        GetContentOfPostTask(Account account){ mAccount = account; }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading Content...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(thenewpotato.blogg.objects.Post... params){
            mPost = params[0];
            GoogleAccountCredential googleAccountCredential =
                    GoogleAccountCredential.usingOAuth2(
                            MainActivity.this,
                            Collections.singleton(
                                    "https://www.googleapis.com/auth/blogger")
                    );
            googleAccountCredential.setSelectedAccount(mAccount);
            Blogger service = new Blogger.Builder(HTTP_TRANSPORT, JSON_FACTORY, googleAccountCredential)
                    .setApplicationName("Blogg")
                    .setHttpRequestInitializer(googleAccountCredential)
                    .build();
            Post post;
            String content = null;
            try {
                // not adding setView("AUTHOR") will result in 404 errors when trying to retrieve drafts
                Blogger.Posts.Get postsGetAction = service.posts().get(mapBlogs.get(intSelectedBlogPos), mPost.id).setView("AUTHOR");
                postsGetAction.setFields("content");
                post = postsGetAction.execute();
                content = post.getContent();
            }catch (IOException e){
                log(e.getMessage());
            }

            // this block handles any images in the content html code
            // it downloads the image and transfer it to a local source (since RTEditor does not download images for you)
            Document htmlDocument = Jsoup.parse(content);
            for (Element imgElement : htmlDocument.select("img")) {
                String imgUrl = imgElement.attr("src");
                Bitmap bitmap = null;
                try {
                    InputStream in = new java.net.URL(imgUrl).openStream();
                    bitmap = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    loge(e.getMessage());
                }
                String imgLocalSrc = getLocalPath(bitmap, FilenameUtils.getName(imgUrl));
                imgElement.attr("src", imgLocalSrc);
            }

            log(htmlDocument.html());

            return htmlDocument.html();
        }

        // the main function of this onPostExecute is to launch the updating post screen
        // it is considerable to put this functionality in its own function to avoid confusion
        @Override
        protected void onPostExecute(String content){
            Intent intent = new Intent(MainActivity.this, EditActivity.class);
            intent.putExtra("post", mPost);
            intent.putExtra("content", content);

            progressDialog.dismiss();

            MainActivity.this.startActivityForResult(intent, RC_UPDATE);
        }

    }

    private class UpdatePostTask extends AsyncTask<String, Void, Void>{

        private ProgressDialog progressDialog;
        Account mAccount;
        String mId;
        String mTitle;
        String mContent;
        UpdatePostTask(Account account){ mAccount = account; }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Updating Post...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params){
            mId = params[0];
            mTitle = params[1];
            mContent = params[2];

            GoogleAccountCredential googleAccountCredential =
                    GoogleAccountCredential.usingOAuth2(
                            MainActivity.this,
                            Arrays.asList("https://www.googleapis.com/auth/blogger",
                                    "https://picasaweb.google.com/data/")
                    );
            googleAccountCredential.setSelectedAccount(mAccount);
            Blogger service = new Blogger.Builder(HTTP_TRANSPORT, JSON_FACTORY, googleAccountCredential)
                    .setApplicationName("Blogg")
                    .setHttpRequestInitializer(googleAccountCredential)
                    .build();


            String mAccessToken = "";
            try {
                mAccessToken = googleAccountCredential.getToken();
                log("Access token = " + mAccessToken);
            } catch (Exception e){
                loge(e.getMessage());
            }

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost("https://picasaweb.google.com/data/feed/api/user/default/albumid/default");
            httpPost.addHeader("Authorization",  "Bearer " + mAccessToken);

            Document htmlDocument = Jsoup.parse(mContent);
            for (Element imgElement : htmlDocument.select("img")) {
                String imgLocalSrc = imgElement.attr("src");
                byte[] imageContent;
                File image = new File(imgLocalSrc);
                try {
                    imageContent = FileUtils.readFileToByteArray(image);
                } catch (IOException e){
                    loge(e.getMessage());
                    return null;
                }

                httpPost.addHeader("Content-Type", getImageType(imgLocalSrc));
                // "Slug" header sets the file name of the uploaded image
                httpPost.addHeader("Slug", image.getName());

                String xmlResponse = "";
                try {
                    httpPost.setEntity(new ByteArrayEntity(imageContent));
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    xmlResponse = EntityUtils.toString(httpResponse.getEntity());
                    log(xmlResponse);
                } catch (IOException e){
                    loge(e.getMessage());
                }

                // by https://stackoverflow.com/users/1818911/su
                String uploadedImageUrl = "";
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder;
                InputSource source;
                try {
                    builder = factory.newDocumentBuilder();
                    source = new InputSource(new StringReader(xmlResponse));
                    org.w3c.dom.Document doc = builder.parse(source);
                    NodeList list = doc.getElementsByTagName("media:content");
                    uploadedImageUrl = list.item(0).getAttributes().getNamedItem("url").getNodeValue();
                } catch (Exception e){
                    loge(e.getMessage());
                }
                log(uploadedImageUrl);

                imgElement.attr("src", uploadedImageUrl);
            }

            Post content = new Post();
            content.setId(mId);
            content.setTitle(mTitle);
            content.setContent(htmlDocument.html());
            try {
                Blogger.Posts.Update postUpdateAction = service.posts().update(mapBlogs.get(intSelectedBlogPos), mId, content);
                postUpdateAction.execute();
            }catch (IOException e){
                loge("UpdatePostTask: doInBackground: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            progressDialog.dismiss();
            loadFragment(intSelectedFragment);
        }

    }

    // the idea of a "primitive list of comments" is intended to lower resource allocation
    // thus only containing the most basic information
    // it is intended for methods to traverse and filter comments without using a large list of comments
    // with large images <- very resource consuming!
    // methods can retrieve a more specific list by using GetDetailedCommentsTask
    private class GetPrimitiveListOfCommentsTask extends AsyncTask<String, Void, ArrayList<thenewpotato.blogg.objects.Comment>>{

        private ProgressDialog progressDialog;
        Account mAccount;
        ArrayList<String> trafficResult = new ArrayList<>();
        GetPrimitiveListOfCommentsTask(Account account){ mAccount = account; }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading Activities...");
            progressDialog.show();
        }

        @Override
        protected ArrayList<thenewpotato.blogg.objects.Comment> doInBackground(String... params){
            String mBlogId = params[0];
            ArrayList<thenewpotato.blogg.objects.Comment> commentArrayList = new ArrayList<>();

            GoogleAccountCredential googleAccountCredential =
                    GoogleAccountCredential.usingOAuth2(
                            MainActivity.this,
                            Collections.singleton(
                                    "https://www.googleapis.com/auth/blogger")
                    );
            googleAccountCredential.setSelectedAccount(mAccount);
            Blogger service = new Blogger.Builder(HTTP_TRANSPORT, JSON_FACTORY, googleAccountCredential)
                    .setApplicationName("Blogg")
                    .setHttpRequestInitializer(googleAccountCredential)
                    .build();
            Pageviews pageviews = null;
            try {
                Blogger.PageViews.Get get = service.pageViews().get(mBlogId);
                get.setRange(Arrays.asList("7DAYS", "30DAYS", "all"));
                pageviews = get.execute();
                Blogger.Comments.ListByBlog listByBlog = service.comments().listByBlog(mBlogId);
                listByBlog.setFields("items(blog/id,post/id,inReplyTo/id,id),nextPageToken");
                CommentList commentList = listByBlog.execute();
                commentArrayList = helperMapComments(commentList, commentArrayList);
                while (commentList.getItems() != null && !commentList.getItems().isEmpty()) {
                    String pageToken = commentList.getNextPageToken();
                    if (pageToken == null) {
                        break;
                    }
                    listByBlog.setPageToken(pageToken);
                    commentList = listByBlog.execute();
                    commentArrayList = helperMapComments(commentList, commentArrayList);
                }
            } catch (IOException e){
                Tools.loge(e.getMessage());
            }
            if (pageviews != null) {
                trafficResult.add(pageviews.getCounts().get(2).getCount().toString());
                trafficResult.add(pageviews.getCounts().get(1).getCount().toString());
                trafficResult.add(pageviews.getCounts().get(0).getCount().toString());
            }
            return commentArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<thenewpotato.blogg.objects.Comment> commentArrayList){
            ActivitiesFragment fragment = ActivitiesFragment.newInstance(trafficResult, commentArrayList);

            getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, fragment).addToBackStack(null).commit();
            // addToBackStack added to prevent blank screen upon transaction

            progressDialog.dismiss();
        }

    }
}
