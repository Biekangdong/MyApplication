package com.dinghe.servicetest.fragment;


import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * @ClassName BaseFragment
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/5/17 22:21
 * @Version 1.0
 * @UpdateDate 2023/5/17 22:21
 * @UpdateRemark 更新说明
 */
public class BaseFragment extends Fragment {

    //跳转Fragment
    public void startToFragment(Context context, int container, Fragment newFragment) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(container, newFragment);
        //添加回退栈
        transaction.addToBackStack(context.getClass().getName());
        transaction.commit();
    }

    //返回上一个页面
    public void popBackFragment(){
        getActivity().getFragmentManager().popBackStack();
    }

    //返回到第一个Fragment,或者回退栈中某个Fragment之上的所有Fragment
    public void popAllFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.popBackStackImmediate(
                getActivity().getClass().getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
