package com.security.monitor.utils

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

object RootChecker {
    
    fun isRootAvailable(): Boolean {
        return checkRootMethod1() || checkRootMethod2()
    }
    
    private fun checkRootMethod1(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su"
        )
        
        return paths.any { File(it).exists() }
    }
    
    private fun checkRootMethod2(): Boolean {
        return try {
            Runtime.getRuntime().exec("su").let {
                it.outputStream.close()
                it.waitFor() == 0
            }
        } catch (e: Exception) {
            false
        }
    }
    
    fun hasRootPermission(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su -c id")
            val output = process.inputStream.bufferedReader().readText()
            output.contains("uid=0")
        } catch (e: Exception) {
            false
        }
    }
}
