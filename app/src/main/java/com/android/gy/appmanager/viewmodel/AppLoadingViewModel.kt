package com.android.gy.appmanager.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.android.gy.appmanager.constant.GlobalConstant
import com.android.gy.appmanager.util.AppUtils


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/6/20 9:49
  * @Version:        1.0
  * @Description:    
 */
class AppLoadingViewModel : BaseViewModel() {
    private val isFinish = MutableLiveData<Boolean>()

    fun getIsFinish(): MutableLiveData<Boolean> {
        return isFinish
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun getAppInfo(context: Context) {
        val packageManager = context.packageManager
        //val installedPackages = packageManager.getInstalledPackages(0)
        //获取到所有安装了的应用程序的信息，包括那些卸载了的，但没有清除数据的应用程序
        val installedPackages =
            packageManager.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES or PackageManager.GET_ACTIVITIES)
        GlobalConstant.appInfo.clear()
        for (i in 0 until installedPackages.size - 1) {
            val packageInfo = installedPackages[i]
            if (packageInfo.activities == null || packageInfo.activities.isEmpty()) {
                //没有activity ,表示不能打开
                continue
            }
            val queryActIntent = Intent(Intent.ACTION_MAIN, null)
            queryActIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            queryActIntent.setPackage(packageInfo.packageName)
            val resolveInfoList = packageManager.queryIntentActivities(queryActIntent, 0)
            if (resolveInfoList.isEmpty()) {
                continue
            }
            val resolveInfo = resolveInfoList.iterator().next() ?: continue
            resolveInfo.activityInfo.name ?: continue

            val packageName = packageInfo.packageName
            if (TextUtils.equals(packageName, AppUtils.getPackageName(context))) {
                continue
            }
            GlobalConstant.appInfo.add(packageInfo)
        }
        isFinish.postValue(true)
    }

    override fun clear() {

    }
}