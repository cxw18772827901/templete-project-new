package com.templete.project.ui.activity;

import android.util.Pair;

import com.lib.base.ui.activity.BaseActivity;
import com.templete.project.R;
import com.templete.project.bean.NavBean;
import com.templete.project.databinding.NavsActivityBinding;
import com.templete.project.ui.fragment.ContainerFragment;

import java.util.Arrays;
import java.util.List;

/**
 * 1、根据导航数据自动处理多级viewpager嵌套，正常最多两层viewpager（超过三层后很容易会有上百个子fragment，这是不合理的）；
 * 2、view懒加载，不展示的viewpager不会初始化，最大限度减轻内存压力；
 * 3、http懒加载，不展示的viewpager不会发起网络请求，最大限度减轻服务端压力，不会直接一次发起几十个请求.
 * <p>
 * ProjectName  TempleteProject-java
 * PackageName  com.templete.project.ui.activity
 * Author       Administrator
 * Date         2022/3/11.
 */

public class NavsActivity extends BaseActivity<NavsActivityBinding> {

    private List<NavBean> navs;
    private ContainerFragment containerFragment;

    @Override
    protected NavsActivityBinding viewBinding() {
        return NavsActivityBinding.inflate(getLayoutInflater());
    }

    @Override
    public void inits() {
        setTitleStr("ViewPager 多级导航");
    }

    @Override
    public void initView() {

    }

    @Override
    public void initEvent() {

    }

    @Override
    public void initData() {
        NavBean nav1 = new NavBean("张大三", "data", true, null);
        NavBean nav11 = new NavBean("张一", "data", true, null);
        NavBean nav12 = new NavBean("张二", "data", false, null);
        NavBean nav13 = new NavBean("张三", "data", false, null);
        NavBean nav14 = new NavBean("张四", "data", false, null);
        NavBean nav15 = new NavBean("张五", "data", false, null);
        NavBean nav16 = new NavBean("张六", "data", false, null);
        NavBean nav17 = new NavBean("张七", "data", false, null);
        nav1.navs = Arrays.asList(nav11, nav12, nav13, nav14, nav15, nav16, nav17);

        NavBean nav2 = new NavBean("李大四", "data", false, null);
        NavBean nav21 = new NavBean("李一", "data", true, null);
        NavBean nav22 = new NavBean("李二", "data", false, null);
        NavBean nav23 = new NavBean("李三", "data", false, null);
        NavBean nav24 = new NavBean("李四", "data", false, null);
        NavBean nav25 = new NavBean("李五", "data", false, null);
        NavBean nav26 = new NavBean("李六", "data", false, null);
        NavBean nav27 = new NavBean("李七", "data", false, null);
        nav2.navs = Arrays.asList(nav21, nav22, nav23, nav24, nav25, nav26, nav27);

        NavBean nav3 = new NavBean("王大五", "data", false, null);
        NavBean nav31 = new NavBean("王一", "data", true, null);
        NavBean nav32 = new NavBean("王二", "data", false, null);
        NavBean nav33 = new NavBean("王三", "data", false, null);
        NavBean nav34 = new NavBean("王四", "data", false, null);
        NavBean nav35 = new NavBean("王五", "data", false, null);
        NavBean nav36 = new NavBean("王六", "data", false, null);
        NavBean nav37 = new NavBean("王七", "data", false, null);
        nav3.navs = Arrays.asList(nav31, nav32, nav33, nav34, nav35, nav36, nav37);

        NavBean nav4 = new NavBean("赵大六", "data", false, null);
        NavBean nav41 = new NavBean("赵一", "data", true, null);
        NavBean nav42 = new NavBean("赵二", "data", false, null);
        NavBean nav43 = new NavBean("赵三", "data", false, null);
        NavBean nav44 = new NavBean("赵四", "data", false, null);
        NavBean nav45 = new NavBean("赵五", "data", false, null);
        NavBean nav46 = new NavBean("赵六", "data", false, null);
        NavBean nav47 = new NavBean("赵七", "data", false, null);
        nav4.navs = Arrays.asList(nav41, nav42, nav43, nav44, nav45, nav46, nav47);

        NavBean nav5 = new NavBean("孙大七", "data", false, null);
        NavBean nav51 = new NavBean("孙一", "data", true, null);
        NavBean nav52 = new NavBean("孙二", "data", false, null);
        NavBean nav53 = new NavBean("孙三", "data", false, null);
        NavBean nav54 = new NavBean("孙四", "data", false, null);
        NavBean nav55 = new NavBean("孙五", "data", false, null);
        NavBean nav56 = new NavBean("孙六", "data", false, null);
        NavBean nav57 = new NavBean("孙七", "data", false, null);
        nav5.navs = Arrays.asList(nav51, nav52, nav53, nav54, nav55, nav56, nav57);

        NavBean nav6 = new NavBean("周大八", "data", false, null);
        NavBean nav61 = new NavBean("周一", "data", true, null);
        NavBean nav62 = new NavBean("周二", "data", false, null);
        NavBean nav63 = new NavBean("周三", "data", false, null);
        NavBean nav64 = new NavBean("周四", "data", false, null);
        NavBean nav65 = new NavBean("周五", "data", false, null);
        NavBean nav66 = new NavBean("周六", "data", false, null);
        NavBean nav67 = new NavBean("周七", "data", false, null);
        nav6.navs = Arrays.asList(nav61, nav62, nav63, nav64, nav65, nav66, nav67);

        navs = Arrays.asList(nav1, nav2, nav3, nav4, nav5, nav6);
        for (int i = 0; i < navs.size(); i++) {
            NavBean beanOut = navs.get(i);
            beanOut.indexPair = new Pair<>(-1, i);
            if (beanOut.navs != null && !beanOut.navs.isEmpty()) {
                for (int j = 0; j < beanOut.navs.size(); j++) {
                    NavBean beanIn = beanOut.navs.get(j);
                    beanIn.indexPair = new Pair<>(i, j);
                }
            }
        }

        containerFragment = ContainerFragment.newInstance(navs, false);
        // containerFragment = ContainerFragment.newInstance(navs, true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, containerFragment).commitNow();
    }

    public NavBean getDefaultFirstNav() {
        if (navs == null || navs.isEmpty()) return null;
        // 找当前层选中
        NavBean selected = null;
        for (NavBean nav : navs) {
            if (nav.isSelect) {
                selected = nav;
                break;
            }
        }
        // 如果没有 isSelect，取第一个
        if (selected == null) {
            selected = navs.get(0);
        }
        // 一直往下找
        while (selected.navs != null && !selected.navs.isEmpty()) {
            NavBean next = null;
            for (NavBean child : selected.navs) {
                if (child.isSelect) {
                    next = child;
                    break;
                }
            }
            if (next == null) {
                next = selected.navs.get(0);
            }
            selected = next;
        }
        return selected;
    }
}