package com.android.gy.appmanager.activity

import android.content.Intent
import android.view.View
import com.android.gy.appmanager.activity.base.BaseActivity
import com.android.gy.appmanager.databinding.ActivityAppLoadingBinding
import com.android.gy.appmanager.util.AppUtils
import com.android.gy.appmanager.util.TaskUtil
import com.android.gy.appmanager.view.CustomLoadingView
import com.android.gy.appmanager.view.LoadingView
import com.android.gy.appmanager.viewmodel.AppLoadingViewModel

class AppLoadingActivity : BaseActivity<ActivityAppLoadingBinding, AppLoadingViewModel>() {
    private lateinit var loadingView: CustomLoadingView

    override fun initView() {
        loadingView = mBinding.loadingView
    }

    override fun initData() {
        TaskUtil.runOnThread { mViewModel.getAppInfo(mActivity) }
    }

    override fun initEvent() {

    }

    override fun getViewBinding(): ActivityAppLoadingBinding {
        return ActivityAppLoadingBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): AppLoadingViewModel {
        return AppUtils.getViewModel(this, AppLoadingViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {
        mViewModel.getIsFinish().observe(this) {
            if (it) {
                TaskUtil.runOnUiThread({
                    loadingView.visibility = View.GONE
                    val intent = Intent(mActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 1000)
            }
        }
    }
}