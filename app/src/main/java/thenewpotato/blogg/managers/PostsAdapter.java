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

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.client.util.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import thenewpotato.blogg.R;
import thenewpotato.blogg.Tools;
import thenewpotato.blogg.objects.Post;

import static thenewpotato.blogg.Tools.log;
import static thenewpotato.blogg.Tools.loge;
import static thenewpotato.blogg.objects.Post.STATUS_SCHEDULED;

public class PostsAdapter extends ArrayAdapter<Post>{

    private activity mActivityCaller = (activity) getContext();
    private ArrayList<Integer> mSelection = new ArrayList<>();
    private TextView tvTitle;
    private TextView tvUpdateTime;
    private ImageView ivScheduledIndicator;
    private ColorStateList oldColors;

    public PostsAdapter(Context context, ArrayList<Post> posts){
        super(context, 0, posts);
    }
    
    @NonNull
    @Override
    public View getView(final int position, View converterView, @NonNull ViewGroup parent){
        final Post post = getItem(position);
        if(converterView == null){
            converterView = LayoutInflater.from(getContext()).inflate(R.layout.item_post, parent, false);
        }

        tvTitle = (TextView) converterView.findViewById(R.id.textview_title_listview_item_post);
        tvUpdateTime = (TextView) converterView.findViewById(R.id.textview_updatetime_listview_item_post);
        ivScheduledIndicator = (ImageView) converterView.findViewById(R.id.iv_schedule_indicator);

        converterView.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.background_light));
        if (mSelection.contains(position)) {
            // this is a CAB selection, so change color!
            converterView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
        
        if (post != null) {
            // version check for the deprecation of Html.fromHtml
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                tvTitle.setText(Html.fromHtml(post.title, Html.FROM_HTML_MODE_LEGACY));
            } else {
                tvTitle.setText(Html.fromHtml(post.title));
            }

            tvUpdateTime.setText(Tools.parseDateTime(post.updateTime, getContext()));

            // show the scheduled indicator if the post is scheduled (should only happen under Drafts tab)
            if (post.type.equals("SCHEDULED")){
                ivScheduledIndicator.setVisibility(View.VISIBLE);
            } else {
                ivScheduledIndicator.setVisibility(View.GONE);
            }

        } else {
            loge("Post is null!");
        }
        return converterView;
    }

    public void setNewSelection(int position) {
        mSelection.add(position);
        notifyDataSetChanged();
    }

    public boolean isPositionChecked(int position) {
        return mSelection.contains(position);
    }

    public void removeSelection(int position) {
        mSelection.removeAll(Collections.singletonList(position));
        notifyDataSetChanged();
    }

    public void delete(ArrayList<Post> mPosts) {
        ArrayList<String> deletePostIds = new ArrayList<>();
        for(int i = 0; i < mSelection.size(); i++){
            log("deletePostId: " + mPosts.get(mSelection.get(i)).id);
            deletePostIds.add(mPosts.get(mSelection.get(i)).id);
        }
        log("deletePostIds length: " + deletePostIds.size());
        mActivityCaller.deletePosts(deletePostIds);
    }

    public void publish(ArrayList<Post> mPosts, Date scheuldedTime) {
        ArrayList<String> publishPostIds = new ArrayList<>();
        for(int i = 0; i < mSelection.size(); i++){
            log("publishPostId: " + mPosts.get(mSelection.get(i)).id);
            publishPostIds.add(mPosts.get(mSelection.get(i)).id);
        }
        log("publishPostIds length: " + publishPostIds.size());
        if(scheuldedTime != null) {
            mActivityCaller.publishPosts(publishPostIds, new DateTime(scheuldedTime));
        } else{
            mActivityCaller.publishPosts(publishPostIds, null);
        }
    }

    public void clearSelection(){
        mSelection.clear();
        notifyDataSetChanged();
    }

    public interface activity{
        void deletePosts(ArrayList<String> postId);
        void publishPosts(ArrayList<String> postId, DateTime scheduledTime);
    }

}
