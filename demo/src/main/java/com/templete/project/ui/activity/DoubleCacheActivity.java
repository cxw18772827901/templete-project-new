package com.templete.project.ui.activity;

import com.lib.base.bean.User;
import com.lib.base.ui.activity.BaseActivity;
import com.lib.base.util.DoubleCacheUtil;
import com.lib.base.util.GlobalThreadPoolUtil;
import com.templete.project.databinding.DoubleCacheActivityBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PackageName  com.templete.project.ui.activity
 * ProjectName  TempleteProject-java
 * Date         2023/2/28.
 *
 * @author dave
 */

public class DoubleCacheActivity extends BaseActivity<DoubleCacheActivityBinding> {
    @Override
    public void inits() {
        setTitleStr("双缓存");
    }

    @Override
    public void initView() {

    }

    @Override
    public void initEvent() {

    }

    @Override
    public void initData() {
        get();
        put(0);//保存有效期2s
        GlobalThreadPoolUtil.postOnUiThreadDelay(this::get, 4000);//2s后已过期
    }

    private void put(int second) {
        DoubleCacheUtil.put("int", 1, second);

        DoubleCacheUtil.put("double", 1.0, second);

        DoubleCacheUtil.put("string", "123", second);

        DoubleCacheUtil.put("user", new User(18, "张三"), second);

        List<User> users = new ArrayList<>();
        users.add(new User(18, "张三"));
        users.add(new User(20, "李四"));
        DoubleCacheUtil.put("users", users, second);

        Map<String, User> map = new HashMap<>();
        map.put("张三", new User(18, "张三"));
        map.put("李四", new User(20, "李四"));
        DoubleCacheUtil.put("map", map, second);
    }

    private void get() {
        Integer ints = DoubleCacheUtil.get("int", Integer.class);
        logD("DoubleCacheUtil", "int=" + ints);

        Float floats = DoubleCacheUtil.get("double", Float.class);
        logD("DoubleCacheUtil", "floats=" + floats);

        Double doubles = DoubleCacheUtil.get("double", Double.class);
        logD("DoubleCacheUtil", "double=" + doubles);

        String string = DoubleCacheUtil.get("string", String.class);
        logD("DoubleCacheUtil", "string=" + string);

        User user = DoubleCacheUtil.get("user", User.class);
        logD("DoubleCacheUtil", "user=" + user);

        List<User> users = DoubleCacheUtil.getList("users", User.class);
        logD("DoubleCacheUtil", "users=" + users);

        Map<String, User> map = DoubleCacheUtil.getMap("map", String.class, User.class);
        logD("DoubleCacheUtil", "map=" + map);
    }

    @Override
    protected DoubleCacheActivityBinding viewBinding() {
        return DoubleCacheActivityBinding.inflate(getLayoutInflater());
    }
}
