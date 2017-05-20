package com.easy.adri;

import android.graphics.Point;
import android.view.View;
import android.view.ViewTreeObserver;

public class ViewHelpers {

    static public int invertVisibility(int visibility) {
        return visibility == View.VISIBLE ? View.GONE : View.VISIBLE;
    }

    static public void setVisibilityNot(View v, int visibility) {
        v.setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    public static void whenViewHasLayout(final View v, final JavaHelpers.Callback cb) {

        ViewTreeObserver vto = v.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                v.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                cb.call();
            }
        });
    }

    public static int[] getLocationCenter(View view) {

        int[] result = new int[2];
        view.getLocationOnScreen(result);
        result[0] += view.getWidth() / 2;
        result[1] += view.getHeight() / 2;

        return result;
    }

    public static Point getLocationMiddle(View v) {
        int[] pos = new int[2];
        v.getLocationOnScreen(pos);
        return new Point(pos[0] + v.getWidth() / 2, pos[1] + v.getHeight() / 2);
    }

    public static Point getPosMiddle(View v) {
        return new Point((int) v.getX() + v.getWidth() / 2, (int) v.getY() + v.getHeight() / 2);
    }
}
