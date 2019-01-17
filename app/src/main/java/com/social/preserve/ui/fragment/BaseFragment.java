package com.social.preserve.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.social.preserve.ui.activity.BaseActivity;
import com.social.preserve.ui.views.CenterLoadingView;

/**
 * Created by pt198 on 08/01/2019.
 */

public class BaseFragment extends Fragment {
    private CenterLoadingView loading;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public CenterLoadingView loading(String title) {
        if(null==loading){
            loading = new CenterLoadingView(getContext());
            loading.setCanceledOnTouchOutside(false);
        }
        if (!this.isDetached()){
            loading.setTitle(title);
            loading.show();
        }
        return loading;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (loading != null && loading.isShowing()) {
                loading.dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void dissLoad()  {
        try {
            if (loading != null && loading.isShowing()) {
                if(!this.isDetached()) {
                    loading.dismiss();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
