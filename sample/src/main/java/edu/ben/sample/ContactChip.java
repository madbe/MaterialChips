package edu.ben.sample;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import edu.ben.materialchip.model.ChipInterface;


/**
 * Created by Ben on 07-Dec-17.
 *
 */

class ContactChip implements ChipInterface {

    private String id;
    private Uri avatarUri;
    private String avatarUrl;
    private String name;
    private String phoneNumber;

    ContactChip(String id, Uri avatarUri, String name, String phoneNumber) {
        this.id = id;
        this.avatarUri = avatarUri;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    ContactChip(String id, String avatarUrl, String name, String phoneNumber) {
        this.id = id;
        this.avatarUrl = avatarUrl;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public Uri getAvatarUri() {
        return avatarUri;
    }

    @Override
    public String getAvatarUrl() {
        return avatarUrl;
    }

    @Override
    public Drawable getAvatarDrawable() {
        return null;
    }

    @Override
    public String getLabel() {
        return name;
    }

    @Override
    public String getInfo() {
        return phoneNumber;
    }
}