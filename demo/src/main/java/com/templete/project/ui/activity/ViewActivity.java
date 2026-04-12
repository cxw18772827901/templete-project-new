package com.templete.project.ui.activity;

import android.annotation.SuppressLint;

import com.greendao.db.util.GsonUtil;
import com.lib.base.glide.GlideUtil;
import com.lib.base.rxjava.RxUtils;
import com.lib.base.ui.activity.BaseActivity;
import com.module.a.bean.LoginBean;
import com.module.a.http.HttpUtil;
import com.templete.project.R;
import com.templete.project.databinding.AndratingBarActivityBinding;
import com.trello.rxlifecycle4.android.ActivityEvent;

import io.reactivex.rxjava3.core.Single;

/**
 * PackageName  com.templete.project.ui
 * ProjectName  TempleteProject-java
 * Date         2022/1/4.
 *
 * @author xwchen
 */
@SuppressLint("CheckResult")
public class ViewActivity extends BaseActivity<AndratingBarActivityBinding> {
    public static final String TAG = "AndRatingBarActivity";

    @Override
    protected AndratingBarActivityBinding viewBinding() {
        return AndratingBarActivityBinding.inflate(getLayoutInflater());
    }

    @Override
    public void inits() {
        setTitleStr("AndratingBar");
    }

    @Override
    public void initView() {
        mViewBinding.tv.setOnClickListener(v -> {
            mViewBinding.btn.setChecked(!mViewBinding.btn.isChecked());
            mViewBinding.btn.setEnabled(!mViewBinding.btn.isEnabled());
        });
        mViewBinding.dragContainer.setDragListener(() -> logD(TAG, "has drag"));
    }

    @Override
    public void initEvent() {

    }

    @Override
    public void initData() {
        String url1 = "http://images0.zaijiawan.com/nanjing/huazhuangdaquan/2/5-1.jpg@!16app_video_list";
        String url2 = "http://images0.zaijiawan.com/nanjing/huazhuangdaquan/2/5-2.jpg@!16app_video_list";
        String url3 = "http://images0.zaijiawan.com/nanjing/huazhuangdaquan/2/5-3.jpg@!16app_video_list";

        GlideUtil.loadPic(this, url1, mViewBinding.iv);
        GlideUtil.loadPic(this, url1, mViewBinding.iv1);
        GlideUtil.loadPicCircle(this, url2, mViewBinding.iv2);
        GlideUtil.loadPicRound(this, url3, mViewBinding.iv3, (int) getDimen(R.dimen.x40));
//        GlideUtil.loadPicRound(this, url2, mViewBinding
//                .ratioIv, (int) getResources().getDimension(R.dimen.x40));

        HttpUtil
                .getInstance()
                .login("18557532484", "000000")
                /******************************************************************************************/
                .compose(RxUtils::toSimpleSingle)
                .compose(RxUtils.toSimpleSingle())//这两个一样
                .doOnSubscribe(this::addDisposable) //自动处理生命周期方式,以下两种是框架处理方式
                .compose(bindUntilEvent(ActivityEvent.DESTROY))// 框架自动处理生命周期方式一
                .compose(RxUtils.bindUntilEvent(this))// 对上面封装
                .compose(RxUtils.bindUntilEvent(this, ActivityEvent.DESTROY))// 对上面封装
                .compose(bindToLifecycle())// 框架自动处理生命周期方式二,注意此种方式绑定的生命周期是onResume()/onPause()
                .compose(RxUtils.bindToLifecycle(this))// 对上面封装
                .compose(RxUtils.withDialogSingle(this, null, false))
                /******************************************************************************************/
                .compose(RxUtils.toSimpleSingle(this, null, false))//包含上面所有,包括线程调度,绑定生命周期,显示loading
                .subscribe(loginBean -> logD(TAG, "data message=" + GsonUtil.toJson(loginBean)), throwable -> logD(TAG, "data message=" + throwable));


        Single<LoginBean> login1 = HttpUtil
                .getInstance()
                .login("18557532484", "000000");
        Single<LoginBean> login2 = HttpUtil
                .getInstance()
                .login("18557532484", "000000");
        Single<LoginBean> login3 = HttpUtil
                .getInstance()
                .login("18557532484", "000000");
        Single
                .zip(login1, login2, login3, (loginBean, loginBean2, loginBean3) -> {
                    // todo
                    return null;
                })
                .compose(RxUtils.toSimpleSingle())
                .doOnSubscribe(disposable -> {

                })
                .subscribe(o -> {

                }, throwable -> {

                });

    }
}
