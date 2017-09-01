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

import android.graphics.drawable.Drawable;

/**
 * Created by thenewpotato on 8/13/17.
 */

public class ImageTextListItem {

    public String text;
    public Drawable image;

    public ImageTextListItem(String text, Drawable image){
        this.text = text;
        this.image = image;
    }

}
