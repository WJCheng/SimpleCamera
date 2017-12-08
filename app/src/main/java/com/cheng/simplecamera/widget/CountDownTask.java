package com.cheng.simplecamera.widget;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

/**
 *
 * Created by cheng on 2017/12/8.
 */

public class CountDownTask extends AsyncTask<Integer, Integer, Void> {
    private ProgressBar mProgressBar;

    public CountDownTask(ProgressBar pb){
        this.mProgressBar = pb;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Integer... integers) {
        int max = integers[0] * 10;
        int count = 0;
        while(count <= max){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
            publishProgress(count);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mProgressBar.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mProgressBar.setVisibility(View.GONE);
    }
}
