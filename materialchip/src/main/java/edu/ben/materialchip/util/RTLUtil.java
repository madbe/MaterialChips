package edu.ben.materialchip.util;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.View;

import java.util.Locale;

import static android.view.View.LAYOUT_DIRECTION_LTR;
import static android.view.View.LAYOUT_DIRECTION_RTL;

/**
 * Created by Ben on 07-Dec-17.
 */

public class RTLUtil {

    public static boolean isRTL() {
        return isRTL(Locale.getDefault());
    }

    private static boolean isRTL(Locale locale) {
        final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC ;
    }

    public static boolean isRTL(View view) {
        return ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isRTL(Context context) {
        Configuration config = context.getResources().getConfiguration();
        return config.getLayoutDirection() == LAYOUT_DIRECTION_RTL;
    }

    public static boolean checkRTL(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        char c = text.charAt(0);
        return c >= 0x590 && c <= 0x6ff;
    }

    public static void changLayoutDirection(View contentLayout, boolean isRTL) {
        if (isRTL) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                contentLayout.setLayoutDirection(LAYOUT_DIRECTION_RTL);
            else
                ViewCompat.setLayoutDirection(contentLayout, ViewCompat.LAYOUT_DIRECTION_RTL);
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                contentLayout.setLayoutDirection(LAYOUT_DIRECTION_LTR);
            else
                ViewCompat.setLayoutDirection(contentLayout, ViewCompat.LAYOUT_DIRECTION_LTR);
        }
    }
}
