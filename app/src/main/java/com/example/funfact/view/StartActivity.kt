package com.example.funfact.view

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.funfact.BuildConfig
import com.example.funfact.R
import com.example.funfact.databinding.ActivityStartBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import java.util.*

class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding

    //private val firebaseRemoteConfig: FirebaseRemoteConfig
    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)

        //val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.url_default_value)
        getValueFromFireBaseRemoteConfig()
        checkIsEmu()

        setContentView(binding.root)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun getValueFromFireBaseRemoteConfig() {
        remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                //val updated = task.result
                val urlText = remoteConfig.getString("url")
                Log.d(TAG, "Config params updated: $urlText")
                Toast.makeText(
                    this,
                    "Fetch and activate succeeded",
                    Toast.LENGTH_SHORT,
                ).show()
                if (urlText.isNotEmpty()) {
                    with(binding.webView) {
                        webViewClient = WebViewClient()
                        loadUrl("$urlText")
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.javaScriptCanOpenWindowsAutomatically = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        settings.databaseEnabled = true
                        settings.setSupportZoom(true)
                        settings.allowFileAccess = true
                        settings.allowContentAccess = true
                    }
                } else {
                    startActivity(Intent(this,MainActivity::class.java))
                }
            } else {
                Toast.makeText(
                    this,
                    "Fetch failed",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    private fun checkIsEmu(): Boolean {
        if (BuildConfig.DEBUG) return false // when developer use this build on emulator
        val phoneModel = Build.MODEL
        val buildProduct = Build.PRODUCT
        val buildHardware = Build.HARDWARE
        val brand: String = Build.BRAND;
        var result = (Build.FINGERPRINT.startsWith("generic")
                    || phoneModel.contains("google_sdk")
                    || phoneModel.lowercase(Locale.getDefault()).contains("droid4x")
                    || phoneModel.contains("Emulator")
                    || phoneModel.contains("Android SDK built for x86")
                    || Build.MANUFACTURER.contains("Genymotion")
                    || buildHardware == "goldfish"
                    || Build.BRAND.contains("google")
                    || buildHardware == "vbox86"
                    || buildProduct == "sdk"
                    || buildProduct == "google_sdk"
                    || buildProduct == "sdk_x86"
                    || buildProduct == "vbox86p"
                    || Build.BOARD.lowercase(Locale.getDefault()).contains("nox")
                    || Build.BOOTLOADER.lowercase(Locale.getDefault()).contains("nox")
                    || buildHardware.lowercase(Locale.getDefault()).contains("nox")
                    || buildProduct.lowercase(Locale.getDefault()).contains("nox"))

        if (result) return true
        result = result or (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
        if (result) return true
        result = result or ("google_sdk" == buildProduct)
        return result
    }


    override fun onBackPressed() {
        if (binding.webView.canGoBack()) binding.webView.goBack()
        else super.onBackPressed()
    }
}