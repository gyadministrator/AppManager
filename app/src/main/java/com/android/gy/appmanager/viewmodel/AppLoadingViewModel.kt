package com.android.gy.appmanager.viewmodel

import android.annotation.SuppressLint
import android.content.Context
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
        val installedPackages = packageManager.getInstalledPackages(0)
        GlobalConstant.appInfo.clear()
        GlobalConstant.appInfo.addAll(installedPackages)
        for (i in 0 until GlobalConstant.appInfo.size - 1) {
            val packageInfo = GlobalConstant.appInfo[i]
            val packageName = packageInfo.packageName
            if (TextUtils.equals(packageName, AppUtils.getPackageName(context))) {
                GlobalConstant.appInfo.remove(packageInfo)
            }
        }
        isFinish.postValue(true)
    }

    override fun clear() {

    }
}