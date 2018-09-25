package com.easy.adri;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;


import java.net.URL;
import java.util.ArrayList;

/**
 * Created by adrienmorel on 17/02/2017.
 */
public class JavaHelpers {

    public interface Callback {
        public void call();
    }

    public interface CallBackWithArg<T> {
        public void call(T arg);
    }

    static public<T> void callbackOnUIThread(final CallBackWithArg<T> cb, final T param) {
        Useful.onUIThread(new Callback() {
            @Override
            public void call() {
                cb.call(param);
            }
        });
    }

    static public void after(int delay, final Callback cb) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cb.call();
            }
        }, delay);
    }

    static public void simpleAsyncTask(final Runnable background, final Runnable foreground) {

        new AsyncTask<Void,Void,Void>() {

            protected Void doInBackground(Void... unused) {
                background.run();
                return null;
            }

            protected void onPostExecute(Void result) {
                foreground.run();
            }
        }.execute();
    }
}
