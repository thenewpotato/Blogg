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
