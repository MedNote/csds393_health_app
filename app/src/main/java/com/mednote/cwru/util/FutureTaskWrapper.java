package com.mednote.cwru.util;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


public class FutureTaskWrapper<V> extends FutureTask<V> {

    private boolean successful;
    private final List<OnSuccessListener<? super V>> successListeners;
    private final List<OnFailureListener> failureListeners;

    public FutureTaskWrapper(Callable<V> callable) {
        super(callable);
        successListeners = new ArrayList<>();
        failureListeners = new ArrayList<>();
    }

    public FutureTaskWrapper(Runnable runnable, V result) {
        super(runnable, result);
        successListeners = new ArrayList<>();
        failureListeners = new ArrayList<>();
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    @Override
    protected void done() {
        super.done();
        try {
            V result = this.get();
            setSuccessful(true);
            notifySuccessListeners(result);
        } catch (ExecutionException | InterruptedException e) {
            notifyFailureListeners(e);
            e.printStackTrace();
        }
    }

    private void notifySuccessListeners(V result) {
        for (OnSuccessListener<? super V> listener : this.successListeners) {
            listener.onSuccess(result);
        }
    }

    private void notifyFailureListeners(Exception e) {
        for (OnFailureListener listener : this.failureListeners) {
            listener.onFailure(e);
        }
    }

    public boolean isComplete() {
        return super.isDone();
    }

    public boolean isSuccessful() {
        return this.successful;
    }

    public boolean isCanceled() {
        return super.isCancelled();
    }

    @NonNull
    @NotNull
    public FutureTaskWrapper<V> addOnSuccessListener(@NonNull @NotNull OnSuccessListener<? super V> onSuccessListener) {
        this.successListeners.add(onSuccessListener);
        return this;
    }

    @NonNull
    @NotNull
    public FutureTaskWrapper<V> addOnFailureListener(@NonNull @NotNull OnFailureListener onFailureListener) {
        this.failureListeners.add(onFailureListener);
        return this;
    }
}
