package edu.ben.materialchip.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import edu.ben.materialchip.R;
import edu.ben.materialchip.R2;
import edu.ben.materialchip.model.ChipInterface;
import edu.ben.materialchip.util.ColorUtil;
import edu.ben.materialchip.util.LetterTileProvider;
import edu.ben.materialchip.util.RTLUtil;
import edu.ben.materialchip.util.ViewUtil;


public class DetailedChipView extends RelativeLayout {

    private static final String TAG = DetailedChipView.class.toString();
    // context
    private Context mContext;
    // xml elements
    @BindView(R2.id.content) LinearLayout mContentLayout;
    @BindView(R2.id.avatar_icon) CircleImageView mAvatarIconImageView;
    @BindView(R2.id.name) TextView mNameTextView;
    @BindView(R2.id.info) TextView mInfoTextView;
    @BindView(R2.id.delete_button) ImageButton mDeleteButton;
    // letter tile provider
    private static LetterTileProvider mLetterTileProvider;
    // attributes
    private ColorStateList mBackgroundColor;

    public DetailedChipView(Context context) {
        super(context);
        mContext = context;
        init(null);
    }

    public DetailedChipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    /**
     * Inflate the view according to attributes
     *
     * @param attrs the attributes
     */
    private void init(AttributeSet attrs) {
        // inflate layout
        View rootView = inflate(getContext(), R.layout.detailed_chip_view, this);
        // butter knife
        ButterKnife.bind(this, rootView);
        // letter tile provider
        mLetterTileProvider = new LetterTileProvider(mContext);

        // hide on first
        setVisibility(GONE);
        // hide on touch outside
        hideOnTouchOutside();
    }

    /**
     * Hide the view on touch outside of it
     */
    private void hideOnTouchOutside() {
        // set focusable
        setFocusable(true);
        setFocusableInTouchMode(true);
        setClickable(true);
    }

    /**
     * Fade in
     */
    public void fadeIn() {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(200);
        startAnimation(anim);
        setVisibility(VISIBLE);
        // focus on the view
        requestFocus();
    }

    /**
     * Fade out
     */
    public void fadeOut() {
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(200);
        startAnimation(anim);
        setVisibility(GONE);
        // fix onclick issue
        clearFocus();
        setClickable(false);
    }

    public void setAvatarIcon(Drawable icon) {
        mAvatarIconImageView.setImageDrawable(icon);
    }

    public void setAvatarIcon(Bitmap icon) {
        mAvatarIconImageView.setImageBitmap(icon);
    }

    public void setAvatarIcon(Uri icon) {
        mAvatarIconImageView.setImageURI(icon);
    }

    public void setAvatarIcon(final String icon, final Bitmap letterTile) {
        Picasso.with(mContext).load(icon)
                .centerCrop()
                .resize(50,50)
                .error(R.drawable.ic_broken_image_grey_24dp)
                .into(mAvatarIconImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        mAvatarIconImageView.setImageBitmap(letterTile);
                    }
                });
    }

    public void setName(String name) {
        mNameTextView.setText(name);
    }

    public void setInfo(String info) {
        if(info != null) {
            mInfoTextView.setVisibility(VISIBLE);
            mInfoTextView.setText(info);
        }
        else {
            mInfoTextView.setVisibility(GONE);
        }
    }

    public void setTextColor(ColorStateList color) {
        mNameTextView.setTextColor(color);
        mInfoTextView.setTextColor(ColorUtil.alpha(color.getDefaultColor(), 150));
    }

    public void setBackGroundColor(ColorStateList color) {
        mBackgroundColor = color;
        mContentLayout.getBackground().setColorFilter(color.getDefaultColor(), PorterDuff.Mode.SRC_ATOP);
    }

    public int getBackgroundColor() {
        return mBackgroundColor == null ? ContextCompat.getColor(mContext, R.color.colorAccent) : mBackgroundColor.getDefaultColor();
    }

    public void setDeleteIconColor(ColorStateList color) {
        mDeleteButton.getDrawable().mutate().setColorFilter(color.getDefaultColor(), PorterDuff.Mode.SRC_ATOP);
    }

    public void setOnDeleteClicked(OnClickListener onClickListener) {
        mDeleteButton.setOnClickListener(onClickListener);
    }

    public void alignLeft() {
        LayoutParams params = (LayoutParams) mContentLayout.getLayoutParams();
        params.leftMargin = 0;
        mContentLayout.setLayoutParams(params);
    }

    public void alignRight() {
        LayoutParams params = (LayoutParams) mContentLayout.getLayoutParams();
        params.rightMargin = ViewUtil.dpToPx(12);
        mContentLayout.setLayoutParams(params);
    }

    public static class Builder {
        private Context context;
        private Uri avatarUri;
        private String avatarUrl;
        private Drawable avatarDrawable;
        private String name;
        private String info;
        private ColorStateList textColor;
        private ColorStateList backgroundColor;
        private ColorStateList deleteIconColor;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder avatar(Uri avatarUri) {
            this.avatarUri = avatarUri;
            return this;
        }

        public Builder avatar(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public Builder avatar(Drawable avatarDrawable) {
            this.avatarDrawable = avatarDrawable;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder info(String info) {
            this.info = info;
            return this;
        }

        public Builder chip(ChipInterface chip) {
            this.avatarUri = chip.getAvatarUri();
            this.avatarUrl = chip.getAvatarUrl();
            this.avatarDrawable = chip.getAvatarDrawable();
            this.name = chip.getLabel();
            this.info = chip.getInfo();
            return this;
        }

        public Builder textColor(ColorStateList textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder backgroundColor(ColorStateList backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder deleteIconColor(ColorStateList deleteIconColor) {
            this.deleteIconColor = deleteIconColor;
            return this;
        }

        public DetailedChipView build() {
            return DetailedChipView.newInstance(this);
        }
    }

    private static DetailedChipView newInstance(Builder builder) {
        DetailedChipView detailedChipView = new DetailedChipView(builder.context);

        //Change Layout direction to RTL or LTR
        if (RTLUtil.checkRTL(builder.name))
            RTLUtil.changLayoutDirection(detailedChipView, true);
        else
            RTLUtil.changLayoutDirection(detailedChipView, false);

        // avatar
        if(builder.avatarUri != null)
            detailedChipView.setAvatarIcon(builder.avatarUri);
        else if (builder.avatarUrl != null && !builder.avatarUrl.equals(""))
            //Set image url if an error set letter tile
            detailedChipView.setAvatarIcon(builder.avatarUrl, mLetterTileProvider.getLetterTile(builder.name));
        else if(builder.avatarDrawable != null)
            detailedChipView.setAvatarIcon(builder.avatarDrawable);
        else
            detailedChipView.setAvatarIcon(mLetterTileProvider.getLetterTile(builder.name));

        // background color
        if(builder.backgroundColor != null)
            detailedChipView.setBackGroundColor(builder.backgroundColor);

        // text color
        if(builder.textColor != null)
            detailedChipView.setTextColor(builder.textColor);
        else if(ColorUtil.isColorDark(detailedChipView.getBackgroundColor()))
            detailedChipView.setTextColor(ColorStateList.valueOf(Color.WHITE));
        else
            detailedChipView.setTextColor(ColorStateList.valueOf(Color.BLACK));

        // delete icon color
        if(builder.deleteIconColor != null)
            detailedChipView.setDeleteIconColor(builder.deleteIconColor);
        else if(ColorUtil.isColorDark(detailedChipView.getBackgroundColor()))
            detailedChipView.setDeleteIconColor(ColorStateList.valueOf(Color.WHITE));
        else
            detailedChipView.setDeleteIconColor(ColorStateList.valueOf(Color.BLACK));

        detailedChipView.setName(builder.name);
        detailedChipView.setInfo(builder.info);
        return detailedChipView;
    }
}
