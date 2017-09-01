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

package thenewpotato.blogg.objects;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
/**
 * Created by thenewpotato on 6/13/17.
 *
 * The necessity of this Object has been debated internally (with myself wwwwww):
 * Blogger API provides a Comment object. However, that Comment Object neither implements Serializable nor Parcelable
 * Which means, that, an ArrayList containing it cannot be passed through the parameters in a fragment with Bundle
 * There might be other ways to pass such ArrayList into the Fragment, however; but Bundle seems to be the safest choice
 */

public class Comment implements Parcelable {

    public String authorName;
    public Bitmap authorImage;
    public String blogId;
    public String postId;
    public String id;
    public String inReplyToId;
    public String updateTime;
    public String content;
    public String postName;

    public Comment(String authorName, Bitmap authorImage, String blogId, String postId, String id,
                   String inReplyToId, String updateTime, String content, String postName){
        this.authorName = authorName;
        this.authorImage = authorImage;
        this.blogId = blogId;
        this.postId = postId;
        this.id = id;
        this.inReplyToId = inReplyToId;
        this.updateTime = updateTime;
        this.content = content;
        this.postName = postName;
    }

    private Comment(Parcel in){
        authorName = in.readString();
        authorImage = in.readParcelable(ClassLoader.getSystemClassLoader());
        blogId = in.readString();
        postId = in.readString();
        id = in.readString();
        inReplyToId = in.readString();
        updateTime = in.readString();
        content = in.readString();
        postName = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel parcel) {
            return new Comment(parcel);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(authorName);
        dest.writeParcelable(authorImage, flags);
        dest.writeString(blogId);
        dest.writeString(postId);
        dest.writeString(id);
        dest.writeString(inReplyToId);
        dest.writeString(updateTime);
        dest.writeString(content);
        dest.writeString(postName);
    }

    public int describeContents(){
        return 0;
    }

}
