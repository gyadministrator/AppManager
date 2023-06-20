package com.android.gy.appmanager.activity.base

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.android.gy.appmanager.R
import com.android.gy.appmanager.common.IBaseCommon
import com.android.gy.appmanager.manager.UiModeManager
import com.android.gy.appmanager.viewmodel.BaseViewModel
import com.gyf.immersionbar.ImmersionBar

/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/16 14:45
  * @Version:        1.0
  * @Description:
 */
abstract class BaseActivity<V : ViewBinding, M : BaseViewModel> : AppCompatActivity(),
    IBaseCommon<V, M> {
    protected lateinit var mBinding: V
    protected lateinit var mViewModel: M
    protected lateinit var mActivity: AppCompatActivity

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT//竖屏
        setBarState()
        UiModeManager.onUiModeChange(this)
        mActivity = this
        mBinding = getViewBinding()
        setContentView(mBinding.root)
        initView()
        initEvent()
        mViewModel = getViewModel()
        initData()
        onNotifyDataChanged()
    }

    private fun setBarState() {
        val mImmersionBar = ImmersionBar.with(this)
        mImmersionBar.statusBarColor(R.color.main_bg_color)
        mImmersionBar.statusBarDarkFont(true)
        mImmersionBar.navigationBarColor(R.color.main_bg_color)
        //解决软键盘与底部输入框冲突问题
        mImmersionBar.keyboardEnable(false)
        mImmersionBar.init()
    }

    override fun onDestroy() {
        super.onDestroy()
        onClear()
    }
}