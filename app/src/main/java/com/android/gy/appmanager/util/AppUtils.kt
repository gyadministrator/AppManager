package com.android.gy.appmanager.util

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.Resources
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.android.gy.appmanager.constant.GlobalConstant
import com.android.gy.appmanager.viewmodel.BaseViewModel
import java.io.*


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/2/2 16:50
  * @Version:        1.0
  * @Description:    
 */
object AppUtils {
    private const val TAG = "AppUtils"

    fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels//屏幕宽度
    }

    fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels//屏幕高度
    }

    @SuppressLint("QueryPermissionsNeeded", "ObsoleteSdkInt")
    fun getAppInfo(context: Context) {
        val packageManager = context.packageManager
        val queryIntentActivities: MutableList<ResolveInfo>
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        queryIntentActivities =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            } else {
                packageManager.queryIntentActivities(intent, 0)
            }
        /* queryIntentActivities.forEach {

         }*/
    }

    fun getPackageName(context: Context): String? {
        val packageName: String?
        val manager: PackageManager = context.packageManager
        packageName = try {
            val info: PackageInfo = manager.getPackageInfo(context.packageName, 0)
            info.packageName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ""
        }
        return packageName
    }

    fun launchApp(context: Context, packageName: String?) {
        Log.i(TAG, "launchApp: packageName=$packageName")
        try {
            //通过包名启动
            val packageManager = context.packageManager
            val intent = packageName.let {
                it?.let { it1 ->
                    packageManager.getLaunchIntentForPackage(
                        it1
                    )
                }
            }
            Log.i(TAG, "launchApp: " + intent.toString())
            if (intent != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "该APP未安装", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i(TAG, "launchApp: " + e.message)
            Toast.makeText(context, "启动失败: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun runApp(context: Context, packageName: String) {
        val packageInfo: PackageInfo
        try {
            val packageManager = context.packageManager
            packageInfo = packageManager.getPackageInfo(packageName, 0)
            val resolveIntent = Intent(Intent.ACTION_MAIN, null)
            resolveIntent.setPackage(packageInfo.packageName)
            val appList = packageManager.queryIntentActivities(resolveIntent, 0)
            val resolveInfo = appList.iterator().next()
            if (resolveInfo != null) {
                val className = resolveInfo.activityInfo.name
                val intent = Intent(Intent.ACTION_MAIN)
                val componentName =
                    ComponentName(resolveInfo.activityInfo.packageName, className)
                intent.component = componentName
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "启动失败: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }


    fun getApkPath(pkgName: String, context: Context): String? {
        val pm: PackageManager = context.packageManager
        val pi: ApplicationInfo?
        return try {
            pi = pm.getApplicationInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES)
            pi.sourceDir
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    fun getPackageVersionName(context: Context, pkgName: String?): String? {
        return try {
            val manager = context.packageManager
            val info = manager.getPackageInfo(pkgName!!, 0) //PackageManager.GET_CONFIGURATIONS
            info.versionName
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            null
        }
    }


    fun getPackageVersionCode(context: Context, pkgName: String?): Int {
        return try {
            val manager = context.packageManager
            val info = manager.getPackageInfo(pkgName!!, 0)
            info.versionCode
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            -1
        }
    }

    fun <T : BaseViewModel> getViewModel(owner: ViewModelStoreOwner, clazz: Class<T>): T {
        return ViewModelProvider(owner)[clazz]
    }
}