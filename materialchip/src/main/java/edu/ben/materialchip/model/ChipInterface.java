package edu.ben.materialchip.model;


import android.graphics.drawable.Drawable;
import android.net.Uri;

public interface ChipInterface {

    Object getId();
    Uri getAvatarUri();
    String getAvatarUrl();
    Drawable getAvatarDrawable();
    String getLabel();
    String getInfo();

}
