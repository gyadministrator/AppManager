package com.android.gy.appmanager.activity

import android.annotation.SuppressLint
import android.content.pm.PackageInfo
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.gy.appmanager.activity.base.BaseActivity
import com.android.gy.appmanager.adapter.AppAdapter
import com.android.gy.appmanager.constant.GlobalConstant
import com.android.gy.appmanager.databinding.ActivityMainBinding
import com.android.gy.appmanager.util.AppUtils
import com.android.gy.appmanager.util.PageUtils
import com.android.gy.appmanager.view.ClearEditText
import com.android.gy.appmanager.viewmodel.MainViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import kotlin.system.exitProcess

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), OnRefreshListener,
    OnLoadMoreListener, AppAdapter.OnAppItemListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var etSearch: ClearEditText
    private lateinit var adapter: AppAdapter
    private lateinit var refreshLayout: SmartRefreshLayout
    private var page: Int = 1
    private var appList: ArrayList<PackageInfo> = ArrayList()
    private var searchKey = ""
    private var firstTime: Long = 0

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun initView() {
        etSearch = mBinding.etSearch
        recyclerView = mBinding.recycler
        refreshLayout = mBinding.refreshLayout
        refreshLayout.setOnRefreshListener(this)
        refreshLayout.setOnLoadMoreListener(this)
        refreshLayout.setEnableLoadMore(false)
    }

    override fun initData() {
        adapter = AppAdapter(this, appList, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        loadData()
    }

    private fun loadData() {
        val pageData =
            PageUtils.loadPageData(
                GlobalConstant.appInfo,
                page,
                GlobalConstant.PAGE_SIZE
            )
        Log.i(TAG, "loadData: page=$page,pageData=$pageData")
        if (TextUtils.isEmpty(searchKey)) {
            mViewModel.filterApp(searchKey, pageData)
        } else {
            mViewModel.filterApp(searchKey, GlobalConstant.appInfo)
        }
    }

    override fun initEvent() {
        etSearch.addListener(object : ClearEditText.OnEditTextListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onEditTextClear() {
                searchKey = ""
                appList.clear()
                page = 1
                loadData()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onTextChange(text: String) {
                searchKey = text
                appList.clear()
                mViewModel.filterApp(text, GlobalConstant.appInfo)
            }
        })
    }

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): MainViewModel {
        return AppUtils.getViewModel(this, MainViewModel::class.java)
    }

    override fun onClear() {

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onNotifyDataChanged() {
        mViewModel.getPackageDataList().observe(this) {
            refreshLayout.setEnableLoadMore(TextUtils.isEmpty(searchKey))
            appList.addAll(it)
            adapter.notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRefresh(refreshLayout: RefreshLayout) {
        page = 1
        appList.clear()
        loadData()
        refreshLayout.setNoMoreData(false)
        refreshLayout.finishRefresh()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        ++page
        loadData()
        refreshLayout.finishLoadMore()
        if (appList.size == GlobalConstant.appInfo.size) {
            refreshLayout.setNoMoreData(true)
        }
    }

    override fun onClickAppItemListener(packageInfo: PackageInfo) {
        AppUtils.launchApp(this, packageInfo.packageName)
    }

    override fun onLongClickAppItemListener(view: View, packageInfo: PackageInfo) {

    }


    @Deprecated(
        "Deprecated in Java", ReplaceWith(
            "super.onBackPressed()",
            "com.android.gy.appmanager.activity.base.BaseActivity"
        )
    )
    override fun onBackPressed() {
        val secondTime = System.currentTimeMillis()
        if (secondTime - firstTime > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show()
            firstTime = secondTime
        } else {
            finish()
            exitProcess(0)
        }
    }
}