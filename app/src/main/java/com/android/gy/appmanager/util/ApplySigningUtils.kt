package com.android.gy.appmanager.util

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/6/19 15:04
  * @Version:        1.0
  * @Description:    
 */
object ApplySigningUtils {
    /**
     * 获取应用签名
     *
     * @param context
     * @param packageName
     * @return
     */
    fun getRawSignatureStr(context: Context, packageName: String?): String? {
        try {
            val signs: Array<Signature>? = getRawSignature(context, packageName)
            return getSignValidString(signs!![0].toByteArray())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getRawSignature(context: Context, packageName: String?): Array<Signature>? {
        if (packageName.isNullOrEmpty()) {
            return null
        }
        try {
            val info =
                context.packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            if (info != null) {
                return info.signatures
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun getSignValidString(paramArrayOfByte: ByteArray): String? {
        val localMessageDigest = MessageDigest.getInstance("MD5")
        localMessageDigest.update(paramArrayOfByte)
        return toHexString(localMessageDigest.digest())
    }

    private fun toHexString(paramArrayOfByte: ByteArray?): String? {
        if (paramArrayOfByte == null) {
            return null
        }
        val localStringBuilder = StringBuilder(2 * paramArrayOfByte.size)
        var i = 0
        while (true) {
            if (i >= paramArrayOfByte.size) {
                return localStringBuilder.toString()
            }
            var str = (0xFF and paramArrayOfByte[i].toInt()).toString(16)
            if (str.length == 1) {
                str = "0$str"
            }
            localStringBuilder.append(str)
            i++
        }
    }

    fun sHA1(context: Context, packageName: String?): String? {
        try {
            val info = packageName?.let {
                context.packageManager.getPackageInfo(
                    it,
                    PackageManager.GET_SIGNATURES
                )
            }
            val cert = info?.signatures?.get(0)?.toByteArray()
            val md = MessageDigest.getInstance("SHA1")
            val publicKey = cert?.let { md.digest(it) }
            val hexString = java.lang.StringBuilder()
            if (publicKey != null) {
                for (b in publicKey) {
                    val appendString = Integer.toHexString(0xFF and b.toInt())
                        .uppercase()
                    if (appendString.length == 1) hexString.append("0")
                    hexString.append(appendString)
                    hexString.append(":")
                }
            }
            val result = hexString.toString()
            return result.substring(0, result.length - 1)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return null
    }
}