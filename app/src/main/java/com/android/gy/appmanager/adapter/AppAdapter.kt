package com.android.gy.appmanager.adapter

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.gy.appmanager.R
import com.android.gy.appmanager.databinding.LayoutAppItemBinding
import com.android.gy.appmanager.util.AppUtils
import com.android.gy.appmanager.util.ApplySigningUtils
import java.io.File
import java.math.BigDecimal

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/2/2 16:08
  * @Version:        1.0
  * @Description:    
 */
class AppAdapter(
    context: Context,
    appList: List<PackageInfo>,
    onAppItemListener: OnAppItemListener
) :
    RecyclerView.Adapter<AppAdapter.ViewHolder>(), View.OnClickListener, View.OnLongClickListener {
    private var appList: List<PackageInfo>
    private var context: Context
    private var onAppItemListener: OnAppItemListener?
    private var isScrolling = false

    init {
        this.appList = appList
        this.context = context
        this.onAppItemListener = onAppItemListener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivIcon: ImageView
        var tvName: TextView
        var tvCopy: TextView
        var tvPackage: TextView
        var tvSize: TextView
        var tvSign: TextView
        var tvStore: TextView
        var tvProcess: TextView
        var tvVersion: TextView
        var tvSha1: TextView
        var llContent: LinearLayout

        init {
            val binding = LayoutAppItemBinding.bind(itemView)
            this.ivIcon = binding.ivIcon
            this.tvName = binding.tvName
            this.tvCopy = binding.tvCopy
            this.tvPackage = binding.tvPackage
            this.tvSize = binding.tvSize
            this.tvSign = binding.tvSign
            this.tvStore = binding.tvStore
            this.tvProcess = binding.tvProcess
            this.tvVersion = binding.tvVersion
            this.tvSha1 = binding.tvSha1
            this.llContent = binding.llContent
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_app_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val packageInfo = appList[position]
        val applicationInfo = packageInfo.applicationInfo
        if (applicationInfo != null) {
            if (!isScrolling) {
                val icon = applicationInfo.loadIcon(context.packageManager)
                if (icon != null) {
                    holder.ivIcon.setImageDrawable(icon)
                }
            }
            val label = applicationInfo.loadLabel(context.packageManager)
            if (!TextUtils.isEmpty(label)) {
                holder.tvName.text = label
            } else {
                holder.tvName.text = applicationInfo.name
            }
        }
        holder.tvPackage.text = "包名: ${applicationInfo.packageName}"

        val sourceDir = applicationInfo.sourceDir
        val length = File(sourceDir).length()
        val formatSize = getFormatSize(length.toDouble())
        holder.tvSize.text = "大小: $formatSize"

        val stringBuilder = StringBuilder()
        //判断是否安装在外存
        val flags = applicationInfo.flags
        //判断是否是系统应用
        if ((flags and ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
            stringBuilder.append("系统应用")
        } else {
            stringBuilder.append("非系统应用")
        }
        if ((flags and ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
            stringBuilder.append("\t\t")
            stringBuilder.append("外置存储空间")
        } else {
            stringBuilder.append("\t\t")
            stringBuilder.append("内置存储空间")
        }
        holder.tvStore.text = stringBuilder.toString()

        holder.tvProcess.text = "进程: ${applicationInfo.processName}"

        val signatureStr =
            ApplySigningUtils.getRawSignatureStr(holder.tvSign.context, applicationInfo.packageName)
        holder.tvSign.text = "签名: $signatureStr"

        val packageVersionCode =
            AppUtils.getPackageVersionCode(holder.tvVersion.context, applicationInfo.packageName)
        val packageVersionName =
            AppUtils.getPackageVersionName(holder.tvVersion.context, applicationInfo.packageName)
        holder.tvVersion.text = "version: $packageVersionName \t\tcode: $packageVersionCode"

        val sHA1 = ApplySigningUtils.sHA1(holder.tvSha1.context, applicationInfo.packageName)
        holder.tvSha1.text = "SHA1: $sHA1"

        stringBuilder.clear()
        stringBuilder.append(applicationInfo.packageName)
        stringBuilder.append("\t\t")
        stringBuilder.append(signatureStr)
        stringBuilder.append("\t\t")
        stringBuilder.append(sHA1)
        stringBuilder.append("\t\t")

        holder.tvCopy.setOnClickListener {
            copyAppInfoToClipboard(it.context, stringBuilder.toString())
        }
        holder.llContent.tag = position
        holder.llContent.setOnClickListener(this)
        holder.llContent.setOnLongClickListener(this)
    }

    private fun copyAppInfoToClipboard(context: Context, info: String) {
        val clipboard: ClipboardManager =
            context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("app_info_label", info)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "app信息已复制到剪切板", Toast.LENGTH_SHORT).show()
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return String
     */
    private fun getFormatSize(size: Double): String {
        val kiloByte = size / 1024
        if (kiloByte < 1) {
            return size.toString() + "B"
        }
        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(kiloByte.toString())
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                .toPlainString() + "KB"
        }
        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(megaByte.toString())
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                .toPlainString() + "MB"
        }
        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(gigaByte.toString())
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                .toPlainString() + "GB"
        }
        val result4 = BigDecimal(teraBytes)
        return (result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB")
    }

    override fun getItemCount(): Int {
        return appList.let { appList.size }
    }

    interface OnAppItemListener {
        fun onClickAppItemListener(packageInfo: PackageInfo)
        fun onLongClickAppItemListener(view: View, packageInfo: PackageInfo)
    }

    override fun onClick(p0: View?) {
        val tag = p0?.tag as Int
        val packageInfo = appList[tag]
        onAppItemListener?.onClickAppItemListener(packageInfo)
    }

    override fun onLongClick(p0: View?): Boolean {
        val tag = p0?.tag as Int
        val packageInfo = appList[tag]
        onAppItemListener?.onLongClickAppItemListener(p0, packageInfo)
        return true
    }

    fun setScrolling(scrolling: Boolean) {
        isScrolling = scrolling
    }
}