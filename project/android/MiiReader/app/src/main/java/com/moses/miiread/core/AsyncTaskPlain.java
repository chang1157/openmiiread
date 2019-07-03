package com.moses.miiread.core;

import android.os.AsyncTask;

public class AsyncTaskPlain extends AsyncTask<Void, Void, Void> {

    private Runnable task;

    public AsyncTaskPlain(Runnable runnable) {
        this.task = runnable;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        task.run();
        return null;
    }
}
