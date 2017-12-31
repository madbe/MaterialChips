package edu.ben.materialchip.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;

public class ViewUtil {

    private static int windowWidthPortrait = 0;
    private static int windowWidthLandscape = 0;

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int getWindowWidth(Context context) {
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            return getWindowWidthPortrait(context);
        } else {
            return getWindowWidthLandscape(context);
        }
    }

    public static int getLayoutWidth(final View layout) {
        final int[] width = new int[1];
//        final int[] height = new int[1];
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                width[0] = layout.getMeasuredWidth();
//                height[0] = layout.getMeasuredHeight();

            }
        });
        return width[0];
    }

    private static int getWindowWidthPortrait(Context context) {
        if (windowWidthPortrait == 0) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            windowWidthPortrait = metrics.widthPixels;
        }

        return windowWidthPortrait;
    }

    private static int getWindowWidthLandscape(Context context) {
        if (windowWidthLandscape == 0) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            windowWidthLandscape = metrics.widthPixels;
        }

        return windowWidthLandscape;
    }

    public static int getNavBarHeight(Context context) {
        int result = 0;
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey && !hasBackKey) {
            //The device has a navigation bar
            Resources resources = context.getResources();

            int orientation = context.getResources().getConfiguration().orientation;
            int resourceId;
            if (isTablet(context)) {
                resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape", "dimen", "android");
            } else {
                resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_width", "dimen", "android");
            }

            if (resourceId > 0) {
                return context.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    public static int getWindowBottomNavBarHeight(Context context) {
        Resources resources = context.getResources();
        int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId);
            }
        } else {
            int resourceId = resources.getIdentifier("navigation_bar_height_landscape", "dimen", "android");
            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId);
            }
        }
        return 0;
    }
    private static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static int getScreenOrientation(Context context) {
        int orientation = context.getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return Configuration.ORIENTATION_PORTRAIT;
        }
        return Configuration.ORIENTATION_LANDSCAPE;
    }

}
