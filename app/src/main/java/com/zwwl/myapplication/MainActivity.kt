package com.zwwl.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zwwl.myapplication.service.MyAccessibilityService
import com.zwwl.myapplication.utils.AccessibilityOpenUtil
import com.zwwl.myapplication.utils.DisplayUtil

class MainActivity : AppCompatActivity() {
    companion object{
        var screenHeight: Int = 0
        var screenWidth: Int = 0
        var activity: Activity?=null

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activity=this
        val decor = window.decorView
        decor.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                decor.viewTreeObserver.removeOnPreDrawListener(this)
                screenWidth = DisplayUtil.getScreenWidth(this@MainActivity)
                screenHeight = DisplayUtil.getScreenHeight(this@MainActivity)
                return true
            }
        })
        initView()

    }

    private fun initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //如果没有覆盖权限
            if (!Settings.canDrawOverlays(this)) {
                //引导用户去开启权限浮窗权限
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
                return
            }
        }

        if (!AccessibilityOpenUtil.isAccessibilitySettingsOn(this)){
            //没有辅助服务权限
            onOpen()
        }else{
            if (MyAccessibilityService.mService==null){
                Toast.makeText(this,"辅助服务未开启",Toast.LENGTH_LONG).show()
            }
        }
    }
    /**
     * 开启辅助权限
     */
    private fun onOpen() {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: Exception) {
            val intent = Intent(Settings.ACTION_SETTINGS)
            startActivity(intent)
        }
    }


}