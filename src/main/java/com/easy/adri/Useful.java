package com.easy.adri;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Useful {

    public static void playSound(Context context, int soundId) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("unique", 0);

        if (sharedPreferences.getBoolean("sound_enabled", true)) {
            final MediaPlayer mp = MediaPlayer.create(context, soundId);
            mp.setVolume(1, 1);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mp.reset();
                    mp.release();
                }
            });
            mp.start();
        }
    }

    public static void animateChillRotate(View view) {
        animateChillRotate(view, 1);
    }

    public static void animateChillRotate(View view, int direction) {

        RotateAnimation anim = new RotateAnimation(3 * direction, 6 * direction, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setInterpolator(new CycleInterpolator(1));
        anim.setDuration(5000);
        view.startAnimation(anim);
    }

    public static JSONObject loadJSONFromInternet(String urlStr) {

        try {
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            return new JSONObject(IOUtils.toString(con.getInputStream(), "utf-8"));

        } catch (IOException | JSONException e) {
            Log.e("Useful", "An error occured downloading JSON" + e.toString());
            return null;
        }
    }

    public static String loadAssetString(String filename, Context context) {

        String str = null;
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            str = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return str;
    }

    public static String loadJSONFromAsset(String filename, Context context) {
        return loadAssetString(filename, context);
    }

    public static Drawable drawableFromAsset(String path, Context context) {

        Drawable d = null;

        try {
            InputStream ims = context.getAssets().open(path);
            d = Drawable.createFromStream(ims, null);
            ims.close();
        } catch (IOException ex) {
            Log.d("Useful", "Impossible to load drawable (" + ex.toString() + ")");
        }

        return d;
    }

    public static void saveIntPref(Context context, String key, int value) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("unique", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }


    public static void animatePopsUp(View view, int direction, final Runnable complete) {

        float ss, se;

        if (direction > 0) {
            ss = 0;
            se = 1;
        } else {
            ss = 1;
            se = 0;
        }


        ScaleAnimation anim = new ScaleAnimation(ss, se, ss, se,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new DecelerateInterpolator(1));
        anim.setDuration(200);
        anim.setFillAfter(true);
        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                if (complete != null)
                    complete.run();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
        view.startAnimation(anim);
        view.animate().alpha(direction > 0 ? 1 : 0).setDuration(300).start();
    }

    public static int[] getLocationCenter(View view) {

        int[] result = new int[2];
        view.getLocationOnScreen(result);
        result[0] += view.getWidth() / 2;
        result[1] += view.getHeight() / 2;

        return result;
    }

    public static int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static String getLocale() {

        String locale = Locale.getDefault().getLanguage().split("[-_]+")[0];
        String localeFull = Locale.getDefault().toString();

        return localeFull.equals("en_GB") ? "en_GB" : locale;
    }

    public static DisplayMetrics getDisplayMetrics(Activity activity) {

        DisplayMetrics displayMetrics = new DisplayMetrics();

        activity.getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        return displayMetrics;
    }

    public static int getScreenWidth(Activity activity) {
        return getDisplayMetrics(activity).widthPixels;
    }

    public static int getScreenHeight(Activity activity) {
        return getDisplayMetrics(activity).heightPixels;
    }

    public static int getScreenHeightDp(Activity activity) {
        DisplayMetrics displayMetrics = getDisplayMetrics(activity);

        float density = displayMetrics.density;
        return (int)(displayMetrics.heightPixels / density);
    }

    public static Bitmap takeScreenshotBitmap(Activity activity) {

        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);
        return bitmap;
    }


    public static void takeScreenshot(Activity activity, String fileName) {

        Bitmap bm = takeScreenshotBitmap(activity);

        String dirPath = activity.getFilesDir().getPath() + "/screens/";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            if (!dir.mkdir())
                Log.e("adrien", "cannot mkdir");
        }

            try {
                File file = new File(dirPath, fileName);
                FileOutputStream fOut = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                Log.e("adrien", e.toString());
            }
        Log.d("Useful", "Screenshot taken at " + dir.getAbsolutePath());
        Toast.makeText(activity, "Screenshot taken", Toast.LENGTH_SHORT).show();
    }

    public static void rateApp(Context context) {

        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    public static void hideStatusBar(Activity activity) {

        // make the game fullscreen
        View decorView = activity.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public interface tryAction {
        public boolean tryIt();
    }

    /*
    ** try something and make it slower with time
     */
    public static class Repeater {
        private boolean mStop = false;
        public void stop() {
            mStop = true;
        }
    }

    public static Repeater tryUntilSuccess(final JavaHelpers.Callback cb, final tryAction tryFunc) {
        return tryUntilSuccess(cb, tryFunc, 300);
    }

    private static void tryRec(final JavaHelpers.Callback cb, final tryAction tryFunc, final int tryTime, final Repeater repeater) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (repeater.mStop) return;

                if (!tryFunc.tryIt())
                    tryRec(cb, tryFunc, tryTime, repeater);
                else cb.call();
            }
        }, tryTime + 300);
    }

    public static Repeater tryUntilSuccess(final JavaHelpers.Callback cb, final tryAction tryFunc, int tryTime) {

        final Repeater repeater = new Repeater();

        tryRec(cb, tryFunc, tryTime, repeater);
        return repeater;
    }

    static public void onUIThread(final JavaHelpers.Callback cb) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                cb.call();
            }
        });
    }

    static public void sleep(long duration) {
        try {
            Thread.sleep(duration);
        } catch (Exception e) {}
    }

    static public <T> T randomly(T[] array) {
        Random r = new Random();
        r.setSeed((new Date()).getSeconds());
        return array[r.nextInt(array.length)];
    }
}
