package com.android.gy.appmanager.util

import android.content.pm.PackageInfo

/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/2/6 10:26
  * @Version:        1.0
  * @Description:    
 */
class PageUtils {
    companion object {
        fun loadPageData(
            list: List<PackageInfo>?,
            page: Int,
            pageSize: Int
        ): ArrayList<PackageInfo> {
            val listData: ArrayList<PackageInfo> = ArrayList()
            if (list.isNullOrEmpty() || page < 0 || pageSize < 0) return listData
            val size = list.size
            val total: Int = if (size % pageSize == 0) {
                size / pageSize
            } else {
                size / pageSize + 1
            }
            if (page > total) return listData
            val startIndex: Int = (page - 1) * pageSize
            val endIndex: Int = page * pageSize - 1
            //original：第一个参数为要拷贝的数组对象
            //from：第二个参数为拷贝的开始位置（包含）
            //to：第三个参数为拷贝的结束位置（不包含）
            //val range = list.toTypedArray().copyOfRange(startIndex, endIndex + 1)
            //filterList.addAll(range.asList())
            val subList = if (page == total) {
                //最后一页
                list.subList(startIndex, size)
            } else {
                list.subList(startIndex, endIndex + 1)
            }
            listData.addAll(subList)
            return listData
        }
    }
}