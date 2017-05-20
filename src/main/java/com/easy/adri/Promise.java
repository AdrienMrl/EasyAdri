package com.easy.adri;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class Promise<T> {

    public interface Callback<A, B> {
        public B run(A value);
    }

    JavaHelpers.CallBackWithArg<Exception> mOnError;
    Promise mNextPromise;
    Callback mCb;
    boolean uithread = false;
    Activity mActivity;

    public boolean accepted = false;
    public T acceptedValue;
    public Exception rejected;

    public Promise() {}

    public Promise(boolean acc) {
        accepted = acc;
    }

    // TODO: handle all of which thenOnUi is handleing
    public Promise then(Callback cb) {
        mNextPromise = new Promise();
        mNextPromise.mCb = cb;
        if (accepted && mCb == null) {
            mNextPromise.accept(acceptedValue);
        }
        return mNextPromise;
    }

    public Promise thenOnUI(final Activity activity, final Callback cb) {

        Promise theNext = new Promise();
        if (mNextPromise == null)
            mNextPromise = theNext;
        else {

            Promise bottomPromise = mNextPromise;

            while (true) {
                if (bottomPromise.mNextPromise == null) {
                    bottomPromise.mNextPromise = theNext;
                    break;
                }
                bottomPromise = bottomPromise.mNextPromise;
            }
        }

        theNext.uithread = true;
        theNext.mActivity = activity;
        theNext.mCb = cb;
        theNext.rejected = rejected;
        theNext.acceptedValue = acceptedValue;
        if (accepted && mCb == null)
            mNextPromise.accept(acceptedValue);
        return theNext;
    }

    public Promise accept() {
        return accept(null);
    }

    private void acceptOnThread(final T result) {
        try {
            Object res = null;
            if (mCb != null) {
                res = mCb.run(result);
                if (res instanceof Promise) {
                    Promise returnedPromise = (Promise) res;
                    returnedPromise.mNextPromise = mNextPromise;
                    returnedPromise.mOnError = mOnError;
                    mNextPromise = null;
                }
            }
            if (mNextPromise != null)
                mNextPromise.accept(res);
        } catch (Exception e) {
            while (mOnError == null && mNextPromise != null) {
                mOnError = mNextPromise.mOnError;
                mNextPromise = mNextPromise.mNextPromise;
            }
            if (mOnError != null)
                mOnError.call(e);
        }
    }

    public Promise<T> accept(final T result) {

        accepted = true;
        acceptedValue = result;

        if (uithread) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   // Log.d("adrien", "acceptig on ui thread with callback set to " + mNextPromise.mCb);
                    acceptOnThread(result);
                }
            });
        } else {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    acceptOnThread(result);
                }
            });
        }
        return this;
    }

    public Promise delay(final long delay) {

        return then(new Promise.Callback() {
            @Override
            public T run(Object value) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {}
                return null;
            }
        });
    }

    public void reject(Exception err) {

        rejected = err;
        if (mOnError != null)
            mOnError.call(err);
        else if (mNextPromise != null)
            mNextPromise.reject(err);
    }

    // todo allow catch chaining
    public void fail(JavaHelpers.CallBackWithArg<Exception> onErr) {
        if (rejected != null)
            onErr.call(rejected);
        mOnError = onErr;
    }

    @SuppressWarnings("unchecked")
    public Promise<T> all(final List<Promise<T>> promises) {
        if (promises.size() == 0)
            return new Promise();
        return promises.get(0).then(new Callback() {
            @Override
            public Promise run(Object value) {
                return all(promises.subList(1, promises.size()));
            }
        });
    }
}
