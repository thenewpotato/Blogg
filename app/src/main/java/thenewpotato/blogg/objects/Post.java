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

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.client.util.DateTime;

import java.util.Comparator;

/**
 * Created by thenewpotato on 4/16/17.
 *
 * The necessity of this Object has been debated internally (with myself wwwwww):
 * Blogger API provides a Post object. However, that Post Object neither implements Serializable nor Parcelable
 * Which means, that, an ArrayList containing it cannot be passed through the parameters in a fragment with Bundle
 * There might be other ways to pass such ArrayList into the Fragment, however; but Bundle seems to be the safest choice
 *
 * Considering features:
 * blog construct which specifies which blog a Post belongs to
 */

public class Post implements Parcelable{

    public static final String STATUS_LIVE = "live";
    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_SCHEDULED = "scheduled";

    public String title;
    public DateTime updateTime;
    public String tag;
    public String id;
    public String url;
    public String type;

    public Post(String title, DateTime updateTime, String tag, String id, String url, String type){
        this.title = title;
        this.updateTime = updateTime;
        this.tag = tag;
        this.id = id;
        this.url = url;
        this.type = type;
    }

    protected Post(Parcel in){
        title = in.readString();
        updateTime = (DateTime) in.readSerializable();
        tag = in.readString();
        id = in.readString();
        url = in.readString();
        type = in.readString();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel parcel) {
            return new Post(parcel);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(title);
        dest.writeSerializable(updateTime);
        dest.writeString(tag);
        dest.writeString(id);
        dest.writeString(url);
        dest.writeString(type);
    }

    public int describeContents(){
        return 0;
    }

    public static Comparator<Post> getTitleComparator(boolean isReverse) {
        if(isReverse) {
            return new Comparator<Post>() {
                @Override
                public int compare(Post post, Post t1) {
                    return -post.title.compareToIgnoreCase(t1.title);
                }
            };
        }else{
            return new Comparator<Post>() {
                @Override
                public int compare(Post post, Post t1) {
                    return post.title.compareToIgnoreCase(t1.title);
                }
            };
        }
    }

    public static Comparator<Post> getDateComparator(boolean isReverse) {
        if(isReverse) {
            return new Comparator<Post>() {
                @Override
                public int compare(Post post, Post t1) {
                    return -post.updateTime.toString().compareToIgnoreCase(t1.updateTime.toString());
                }
            };
        }else{
            return new Comparator<Post>() {
                @Override
                public int compare(Post post, Post t1) {
                    return post.updateTime.toString().compareToIgnoreCase(t1.updateTime.toString());
                }
            };
        }
    }

}
