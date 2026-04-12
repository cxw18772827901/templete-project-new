package com.module.a.view.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.lib.base.aroute.ArouteConfig;
import com.lib.base.bean.User;
import com.lib.base.ui.activity.BaseActivity;
import com.module.a.databinding.MaActivityMainBinding;

import io.reactivex.rxjava3.disposables.Disposable;

@Route(path = ArouteConfig.ACTIVITY_MAIN_A/*, group =RouteConfig.GROUP_A*/)
public class MainActivityA extends BaseActivity<MaActivityMainBinding> {
    public static final String TAG = "sssss";
    //    @Autowired
    public int age;
    //    @Autowired
    public String name;
    //    @Autowired
    public User user;
    private Disposable disposable;

    @Override
    public void inits() {
        setTitleStr("module a");
    }

    @Override
    public void initView() {

    }

    @Override
    public void initEvent() {

    }

    @Override
    public void initData() {
    }

    @Override
    public MaActivityMainBinding viewBinding() {
        return MaActivityMainBinding.inflate(getLayoutInflater());
    }
}