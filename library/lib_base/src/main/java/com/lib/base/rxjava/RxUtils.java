package com.lib.base.rxjava;

import android.content.Context;
import android.view.View;

import com.jakewharton.rxbinding4.view.RxView;
import com.lib.base.ui.dialog.base.BaseDialog;
import com.lib.base.ui.dialog.base.WaitDialog;
import com.lib.base.util.DebugUtil;
import com.lib.base.util.encrypt.base.TextUtils;
import com.trello.rxlifecycle4.LifecycleTransformer;
import com.trello.rxlifecycle4.android.ActivityEvent;
import com.trello.rxlifecycle4.android.FragmentEvent;
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle4.components.support.RxFragment;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleSource;
import io.reactivex.rxjava3.core.SingleTransformer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * rxjava和rxview工具类
 * Created by xwchen on 19-4-29.
 */

public class RxUtils {
    public static final String TAG = "RxUtils";

    //调用RxUtils::toSimpleSingle
    public static <T> SingleSource<T> toSimpleSingle(@NonNull Single<T> upstream) {
        return upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> SingleTransformer<T, T> toSimpleSingle() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> SingleTransformer<T, T> toSimpleSingle(RxAppCompatActivity activity) {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(activity.bindUntilEvent(ActivityEvent.DESTROY));
    }

    public static <T> SingleTransformer<T, T> toSimpleSingle(RxAppCompatActivity activity, String msg, boolean endTaskWhenDialogDismiss) {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(activity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(withDialogSingle(activity, msg, endTaskWhenDialogDismiss));
    }

    public static <T> SingleTransformer<T, T> toSimpleSingle(RxFragment fragment) {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(fragment.bindUntilEvent(FragmentEvent.DESTROY));
    }

    public static <T> SingleTransformer<T, T> toSimpleSingle(RxFragment fragment, String msg, boolean endTaskWhenDialogDismiss) {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(fragment.bindUntilEvent(FragmentEvent.DESTROY))
                .compose(withDialogSingle(fragment.requireContext(), msg, endTaskWhenDialogDismiss));
    }

    public static <T> SingleSource<T> toSimpleSingleIo(@NonNull Single<T> upstream) {
        return upstream
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    public static <T> ObservableSource<T> toSimpleObservable(@NonNull Observable<T> upstream) {
        return upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<T, T> toSimpleObservable() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<T, T> toSimpleObservable(RxAppCompatActivity activity) {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(activity.bindUntilEvent(ActivityEvent.DESTROY));
    }

    public static <T> ObservableTransformer<T, T> toSimpleObservable(RxAppCompatActivity activity, String msg, boolean endTaskWhenDialogDismiss) {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(activity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(withDialogObservable(activity, msg, endTaskWhenDialogDismiss));
    }

    public static <T> ObservableTransformer<T, T> toSimpleObservable(RxFragment fragment) {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(fragment.bindUntilEvent(FragmentEvent.DESTROY));
    }

    public static <T> ObservableTransformer<T, T> toSimpleObservable(RxFragment fragment, String msg, boolean endTaskWhenDialogDismiss) {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(fragment.bindUntilEvent(FragmentEvent.DESTROY))
                .compose(withDialogObservable(fragment.requireContext(), msg, endTaskWhenDialogDismiss));
    }

    public static <T> ObservableSource<T> toSimpleObservableIo(@NonNull Observable<T> upstream) {
        return upstream.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    public static <T> Flowable<T> toSimpleFlowable(@NonNull Flowable<T> upstream) {
        return upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Flowable<T> toSimpleFlowableIo(@NonNull Flowable<T> upstream) {
        return upstream.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    public static <T> LifecycleTransformer<T> bindToLifecycle(RxAppCompatActivity activity) {
        return activity.bindToLifecycle();
    }

    public static <T> LifecycleTransformer<T> bindUntilEvent(RxAppCompatActivity activity) {
        return bindUntilEvent(activity, ActivityEvent.DESTROY);
    }

    public static <T> LifecycleTransformer<T> bindUntilEvent(RxAppCompatActivity activity, ActivityEvent event) {
        return activity.bindUntilEvent(event);
    }

    /**
     * 连续点击次数大于atLeastCount次才触发
     *
     * @param view
     * @param atLeastCount
     * @param listener
     */
    public static void setClickCountAtLeast(View view, int atLeastCount, RxClickListener listener) {
//        Observable observable = RxView.clicks(view).share();
//        observable
//                .buffer(observable.debounce(300, TimeUnit.MILLISECONDS))
//                .map((Function<List, Integer>) List::size)
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnNext((Consumer<Integer>) size -> {
//                    Log.d(TAG, "size=" + size);
//                    if (size >= atLeastCount && listener != null) {
//                        listener.click(view);
//                    }
//                })
//                .subscribe();

        //另一种写法
        RxView
                .clicks(view)
                .share()
                .compose(RxUtils::buffer)
                .map(List::size)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(size -> {
                    DebugUtil.logD(TAG, "size=" + size);
                    if (size >= atLeastCount && listener != null) {
                        listener.click(view);
                    }
                })
                .subscribe();
    }

    private static <T> Observable<List<T>> buffer(Observable<T> upstream) {
        return upstream.buffer(upstream.debounce(300, TimeUnit.MILLISECONDS));
    }

    /**
     * 防止view连续点击，2.5s内仅第一次会触发,安卓连击的解决方案之一
     *
     * @param view
     * @param listener
     */
    public static void throwFirstClick(View view, RxClickListener listener) {
        RxView
                .clicks(view)
                .throttleFirst(2500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(o -> {
                    if (listener != null) {
                        listener.click(view);
                    }
                })
                .subscribe();
    }

    /**
     * 连续点击结束后才触发,安卓连击的解决方案之一
     *
     * @param view
     * @param listener
     */
    public static void throwEndClick(View view, RxClickListener listener) {
        RxView
                .clicks(view)
                .share()
                .compose(RxUtils::buffer)
                .map(List::size)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(size -> {
                    DebugUtil.logD(TAG, "size=" + size);
                    if (listener != null) {
                        listener.click(view);
                    }
                })
                .subscribe();
    }

    /**
     * 有进度Schedulers:任务结束会取消dialog.
     * 注意dialog消失时候是否有取消任务的需求.
     *
     * @param activityContext          context
     * @param msg                      msg
     * @param endTaskWhenDialogDismiss dialog dismiss是否取消任务
     * @param <T>                      T
     * @return T
     */

    public static <T> SingleTransformer<T, T> withDialogSingle(Context activityContext, String msg, boolean endTaskWhenDialogDismiss) {
        BaseDialog dialog = new WaitDialog.Builder(activityContext)
                .setMessage(!TextUtils.isEmpty(msg) ? msg : "请稍后…")
                .create();
        return upstream -> upstream
                .delay(200, TimeUnit.MILLISECONDS)
                .doOnSubscribe(disposable -> {
                    if (endTaskWhenDialogDismiss) {
                        dialog.addOnDismissListener(dialog1 -> disposable.dispose());
                    }
                    dialog.show();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(dialog::dismiss)
                .doOnDispose(dialog::dismiss);
    }

    /**
     * 有进度Schedulers:任务结束会取消dialog.
     * 注意dialog消失时候是否有取消任务的需求.
     *
     * @param activityContext          context
     * @param msg                      msg
     * @param endTaskWhenDialogDismiss dialog dismiss是否取消任务
     * @param <T>                      T
     * @return T
     */

    public static <T> ObservableTransformer<T, T> withDialogObservable(Context activityContext, String msg, boolean endTaskWhenDialogDismiss) {
        BaseDialog dialog = new WaitDialog.Builder(activityContext)
                .setMessage(!TextUtils.isEmpty(msg) ? msg : "请稍后…")
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .create();
        return upstream -> upstream
                .delay(200, TimeUnit.MILLISECONDS)
                .doOnSubscribe(disposable -> {
                    if (endTaskWhenDialogDismiss) {
                        dialog.addOnDismissListener(dialog1 -> disposable.dispose());
                    }
                    dialog.show();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(dialog::dismiss)
                .doOnDispose(dialog::dismiss);
    }

    public interface RxClickListener {
        void click(View view);
    }

}
