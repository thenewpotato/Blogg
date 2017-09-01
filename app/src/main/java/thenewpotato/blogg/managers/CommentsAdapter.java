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
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import thenewpotato.blogg.R;
import thenewpotato.blogg.objects.Comment;

import static thenewpotato.blogg.Tools.loge;

public class CommentsAdapter extends ArrayAdapter<Comment> {

    private boolean isParentActivity;

    private static class ViewHolder {
        ImageView ivAccountPic;
        TextView tvContent;
        TextView tvInfo;
        TextView tvUpdatedDate;
    }

    public CommentsAdapter(Context context, ArrayList<Comment> comments, boolean isParentActivity) {
        super(context, R.layout.item_comment, comments);
        this.isParentActivity = isParentActivity;
    }

    @NonNull
    @Override
    public View getView(int position, View converterView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        Comment comment = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (converterView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            converterView = inflater.inflate(R.layout.item_comment, parent, false);
            viewHolder.ivAccountPic = (ImageView) converterView.findViewById(R.id.iv_comment_user_pic);
            viewHolder.tvContent = (TextView) converterView.findViewById(R.id.tv_comment_content);
            viewHolder.tvInfo = (TextView) converterView.findViewById(R.id.tv_comment_info);
            viewHolder.tvUpdatedDate = (TextView) converterView.findViewById(R.id.tv_comment_updated_date);
            // Cache the viewHolder object inside the fresh view
            converterView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) converterView.getTag();
        }

        // this block renders the first row yellow
        // the ELSE statement is VERY IMPORTANT since rows are reused, if not included
        // scrolling will cause bottom rows to also render yellow
        if(isParentActivity && position == 0){
            converterView.setBackgroundColor(Color.parseColor("#FFD480"));
            viewHolder.tvContent.setMaxLines(Integer.MAX_VALUE);
            viewHolder.tvInfo.setMaxLines(Integer.MAX_VALUE);
        }else{
            converterView.setBackgroundColor(0x00000000);
            viewHolder.tvContent.setMaxLines(2);
            viewHolder.tvInfo.setMaxLines(1);
        }
        if (comment != null) {
            // Populate the data from the data object via the viewHolder object
            // into the template view.
            viewHolder.ivAccountPic.setImageBitmap(comment.authorImage);

            // version check for the deprecation of Html.fromHtml
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                viewHolder.tvContent.setText(Html.fromHtml(comment.content, Html.FROM_HTML_MODE_LEGACY));
                viewHolder.tvInfo.setText(Html.fromHtml(comment.postName, Html.FROM_HTML_MODE_LEGACY));
            } else {
                viewHolder.tvContent.setText(Html.fromHtml(comment.content));
                viewHolder.tvInfo.setText(Html.fromHtml(comment.postName));
            }

            // updateTime is already parsed in GetDetailedCommentsTask
            viewHolder.tvUpdatedDate.setText(
                    comment.authorName + ", " + comment.updateTime);
        } else {
            loge("Comment object does not exist!");
        }
        // Return the completed view to render on screen
        return converterView;
    }

}

