package com.android.gy.appmanager.viewmodel

import android.annotation.SuppressLint
import android.content.pm.PackageInfo
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.gy.appmanager.App
import com.android.gy.appmanager.constant.GlobalConstant
import com.android.gy.appmanager.util.TaskUtil


/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/6/20 9:49
  * @Version:        1.0
  * @Description:    
 */
class MainViewModel : BaseViewModel() {
    companion object {
        private const val TAG = "MainViewModel"
    }

    private val packAgeDataList = MutableLiveData<ArrayList<PackageInfo>>()

    fun getPackageDataList(): MutableLiveData<ArrayList<PackageInfo>> {
        return packAgeDataList
    }

    fun filterApp(searchKey: String, dataList: ArrayList<PackageInfo>) {
        val appList = ArrayList<PackageInfo>()
        if (!TextUtils.isEmpty(searchKey)) {
            TaskUtil.runOnThread {
                for (i in 0 until dataList.size - 1) {
                    val packageInfo = GlobalConstant.appInfo[i]
                    val packageName = packageInfo.packageName
                    val label = packageInfo.applicationInfo.loadLabel(App.app.packageManager)
                    val name = packageInfo.applicationInfo.name
                    if (packageName.contains(searchKey)
                        || label.contains(searchKey)
                        || (name != null && name.contains(searchKey)
                                )
                    ) {
                        appList.add(packageInfo)
                    }
                }
                Log.i(TAG, "filterApp: searchKey=$searchKey,appList=$appList")
                packAgeDataList.postValue(appList)
            }
        } else {
            appList.addAll(dataList)
            Log.i(TAG, "filterApp: searchKey=$searchKey,appList=$appList")
            packAgeDataList.postValue(appList)
        }
    }

    override fun clear() {

    }
}