package com.android.gy.appmanager

import android.app.Application


/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/6/20 9:22
  * @Version:        1.0
  * @Description:    
 */
class App : Application() {

    companion object {
        lateinit var app: App
    }

    init {
        app = this
    }

    override fun onCreate() {
        super.onCreate()
    }
}