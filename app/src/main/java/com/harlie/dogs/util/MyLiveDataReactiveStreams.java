package com.harlie.dogs.util;

/*
 *  This is a modified version of androidx.lifecycle.LiveDataReactiveStreams
 *  The original LiveDataReactiveStreams object can't handle RxJava error conditions.
 *  Now errors are emitted as RxErrorEvent objects using the GreenRobot EventBus.
 */

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.github.ajalt.timberkt.Timber;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Adapts {@link LiveData} input and output to the ReactiveStreams spec.
 */
@SuppressWarnings("ALL")
public final class MyLiveDataReactiveStreams {
    private final static String _tag = "LEE: <" + MyLiveDataReactiveStreams.class.getSimpleName() + ">";

    private MyLiveDataReactiveStreams() {
    }

    /**
     * Adapts the given {@link LiveData} stream to a ReactiveStreams {@link Publisher}.
     *
     * <p>
     * By using a good publisher implementation such as RxJava 2.x Flowables, most consumers will
     * be able to let the library deal with backpressure using operators and not need to worry about
     * ever manually calling {@link Subscription#request}.
     *
     * <p>
     * On subscription to the publisher, the observer will attach to the given {@link LiveData}.
     * Once {@link Subscription#request} is called on the subscription object, an observer will be
     * connected to the data stream. Calling request(Long.MAX_VALUE) is equivalent to creating an
     * unbounded stream with no backpressure. If request with a finite count reaches 0, the observer
     * will buffer the latest item and emit it to the subscriber when data is again requested. Any
     * other items emitted during the time there was no backpressure requested will be dropped.
     */
    @NonNull
    public static <T> Publisher<T> toPublisher(
            @NonNull LifecycleOwner lifecycle, @NonNull LiveData<T> liveData) {

        return new MyLiveDataReactiveStreams.LiveDataPublisher<>(lifecycle, liveData);
    }

    @SuppressWarnings("Convert2Diamond")
    private static final class LiveDataPublisher<T> implements Publisher<T> {
        final LifecycleOwner mLifecycle;
        final LiveData<T> mLiveData;

        LiveDataPublisher(LifecycleOwner lifecycle, LiveData<T> liveData) {
            this.mLifecycle = lifecycle;
            this.mLiveData = liveData;
        }

        @Override
        public void subscribe(Subscriber<? super T> subscriber) {
            subscriber.onSubscribe(new MyLiveDataReactiveStreams.LiveDataPublisher.LiveDataSubscription<T>(subscriber, mLifecycle, mLiveData));
        }

        @SuppressWarnings("Convert2Lambda")
        static final class LiveDataSubscription<T> implements Subscription, Observer<T> {
            final Subscriber<? super T> mSubscriber;
            final LifecycleOwner mLifecycle;
            final LiveData<T> mLiveData;

            volatile boolean mCanceled;
            // used on main thread only
            boolean mObserving;
            long mRequested;
            // used on main thread only
            @Nullable
            T mLatest;

            LiveDataSubscription(final Subscriber<? super T> subscriber,
                                 final LifecycleOwner lifecycle, final LiveData<T> liveData) {
                this.mSubscriber = subscriber;
                this.mLifecycle = lifecycle;
                this.mLiveData = liveData;
            }

            @Override
            public void onChanged(@Nullable T t) {
                if (mCanceled) {
                    return;
                }
                if (mRequested > 0) {
                    mLatest = null;
                    mSubscriber.onNext(t);
                    if (mRequested != Long.MAX_VALUE) {
                        mRequested--;
                    }
                } else {
                    mLatest = t;
                }
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void request(final long n) {
                if (mCanceled) {
                    return;
                }
                ArchTaskExecutor.getInstance().executeOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCanceled) {
                            return;
                        }
                        if (n <= 0L) {
                            mCanceled = true;
                            if (mObserving) {
                                mLiveData.removeObserver(MyLiveDataReactiveStreams.LiveDataPublisher.LiveDataSubscription.this);
                                mObserving = false;
                            }
                            mLatest = null;
                            mSubscriber.onError(
                                    new IllegalArgumentException("Non-positive request"));
                            return;
                        }

                        // Prevent overflow.
                        mRequested = mRequested + n >= mRequested
                                ? mRequested + n : Long.MAX_VALUE;
                        if (!mObserving) {
                            mObserving = true;
                            mLiveData.observe(mLifecycle, MyLiveDataReactiveStreams.LiveDataPublisher.LiveDataSubscription.this);
                        } else if (mLatest != null) {
                            onChanged(mLatest);
                            mLatest = null;
                        }
                    }
                });
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void cancel() {
                if (mCanceled) {
                    return;
                }
                mCanceled = true;
                ArchTaskExecutor.getInstance().executeOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mObserving) {
                            mLiveData.removeObserver(MyLiveDataReactiveStreams.LiveDataPublisher.LiveDataSubscription.this);
                            mObserving = false;
                        }
                        mLatest = null;
                    }
                });
            }
        }
    }

    /**
     * Creates an observable {@link LiveData} stream from a ReactiveStreams {@link Publisher}}.
     *
     * <p>
     * When the LiveData becomes active, it subscribes to the emissions from the Publisher.
     *
     * <p>
     * When the LiveData becomes inactive, the subscription is cleared.
     * LiveData holds the last value emitted by the Publisher when the LiveData was active.
     * <p>
     * Therefore, in the case of a hot RxJava Observable, when a new LiveData {@link Observer} is
     * added, it will automatically notify with the last value held in LiveData,
     * which might not be the last value emitted by the Publisher.
     * <p>
     * Note that LiveData does NOT handle errors and it expects that errors are treated as states
     * in the data that's held. In case of an error being emitted by the publisher, an error will
     * be propagated to the main thread and the app will crash.
     *
     * @param <T> The type of data hold by this instance.
     */
    @NonNull
    public static <T> LiveData<T> fromPublisher(@NonNull Publisher<T> publisher) {
        return new MyLiveDataReactiveStreams.PublisherLiveData<>(publisher);
    }

    /**
     * Defines a {@link LiveData} object that wraps a {@link Publisher}.
     *
     * <p>
     * When the LiveData becomes active, it subscribes to the emissions from the Publisher.
     *
     * <p>
     * When the LiveData becomes inactive, the subscription is cleared.
     * LiveData holds the last value emitted by the Publisher when the LiveData was active.
     * <p>
     * Therefore, in the case of a hot RxJava Observable, when a new LiveData {@link Observer} is
     * added, it will automatically notify with the last value held in LiveData,
     * which might not be the last value emitted by the Publisher.
     *
     * <p>
     * Note that LiveData does NOT handle errors and it expects that errors are treated as states
     * in the data that's held. In case of an error being emitted by the publisher, an error will
     * be propagated to the main thread and the app will crash.
     *
     * @param <T> The type of data hold by this instance.
     */
    @SuppressWarnings("rawtypes")
    private static class PublisherLiveData<T> extends LiveData<T> {
        private final Publisher<T> mPublisher;
        final AtomicReference<MyLiveDataReactiveStreams.PublisherLiveData.LiveDataSubscriber> mSubscriber;

        PublisherLiveData(@NonNull Publisher<T> publisher) {
            mPublisher = publisher;
            mSubscriber = new AtomicReference<>();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onActive() {
            super.onActive();
            MyLiveDataReactiveStreams.PublisherLiveData.LiveDataSubscriber s = new MyLiveDataReactiveStreams.PublisherLiveData.LiveDataSubscriber();
            mSubscriber.set(s);
            //noinspection unchecked
            mPublisher.subscribe(s);
        }

        @Override
        protected void onInactive() {
            super.onInactive();
            MyLiveDataReactiveStreams.PublisherLiveData.LiveDataSubscriber s = mSubscriber.getAndSet(null);
            if (s != null) {
                s.cancelSubscription();
            }
        }

        @SuppressWarnings("Convert2Lambda")
        final class LiveDataSubscriber extends AtomicReference<Subscription>
                implements Subscriber<T> {

            @Override
            public void onSubscribe(Subscription s) {
                if (compareAndSet(null, s)) {
                    s.request(Long.MAX_VALUE);
                } else {
                    s.cancel();
                }
            }

            @Override
            public void onNext(T item) {
                postValue(item);
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onError(final Throwable ex) {
                mSubscriber.compareAndSet(this, null);

                ArchTaskExecutor.getInstance().executeOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        //NOTE: Errors are be handled upstream
                        Timber.tag(_tag).e("LiveData does not handle errors. Errors from publishers are handled upstream via EventBus. error: " + ex);
                        RxErrorEvent rx_event = new RxErrorEvent(ex.toString());
                        rx_event.post();
                    }
                });
            }

            @Override
            public void onComplete() {
                mSubscriber.compareAndSet(this, null);
            }

            public void cancelSubscription() {
                Subscription s = get();
                if (s != null) {
                    s.cancel();
                }
            }
        }
    }
}

