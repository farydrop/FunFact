package com.example.funfact.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View.VISIBLE
import android.webkit.CookieManager
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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
    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetJavaScriptEnabled", "CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.url_default_value)
        getValueFromFireBaseRemoteConfig()
        setContentView(binding.root)

        setSaveUrl()
        getSaveUrl()


        if (isSharedSaved()) {
            if (isInternetAvailable()) {
                val sharedPreference = getSharedPreferences("application", Context.MODE_PRIVATE)
                val urlText = remoteConfig.getString("url")
                val text = sharedPreference.getString("url", urlText)
                if (urlText.isEmpty() || checkIsEmu()) {
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    getWebView(text!!)
                }
            } else {
                startActivity(Intent(this, NoNetworkActivity::class.java))
                finish()
            }
        } else {
            setSaveUrl()
        }

    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) binding.webView.goBack()
        else super.onBackPressed()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun getWebView(url: String){
        with(binding.webView) {
            webViewClient = WebViewClient()
            loadUrl(url)
            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
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
    }

    private fun getValueFromFireBaseRemoteConfig() {
        remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val urlText = remoteConfig.getString("url")
                //setSaveUrl(urlText)
            } else {
                Toast.makeText(
                    this,
                    "Fetch failed",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    private fun setSaveUrl() {
        val urlText = remoteConfig.getString("url")
        val sharedPreference = getSharedPreferences("application", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("url", urlText)
        editor.apply()
    }

    private fun getSaveUrl(): String? {
        val sharedPreference = getSharedPreferences("application", Context.MODE_PRIVATE)
        val urlText = remoteConfig.getString("url")
        return sharedPreference.getString("url", urlText)
    }

    private fun isSharedSaved(): Boolean {
        val sharedPref = getSharedPreferences("application", Context.MODE_PRIVATE)
        return sharedPref.contains("url")
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    private fun checkIsEmu(): Boolean {
        if (BuildConfig.DEBUG) return false // when developer use this build on emulator
        val phoneModel = Build.MODEL
        val buildProduct = Build.PRODUCT
        val buildHardware = Build.HARDWARE
        val brand: String = Build.BRAND
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
        result = result or (brand.startsWith("generic") && Build.DEVICE.startsWith("generic"))
        if (result) return true
        result = result or ("google_sdk" == buildProduct)
        return result
    }
}