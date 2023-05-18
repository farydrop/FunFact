package com.example.funfact.view

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.versionedparcelable.VersionedParcel.ParcelException
import com.example.funfact.BuildConfig
import com.example.funfact.R
import com.example.funfact.databinding.ActivityStartBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
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
        //updateRemoteConfig()

        setContentView(binding.root)

        showWebView()

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showWebView() {
        getValueFromFireBaseRemoteConfig()
        updateRemoteConfig()
        val urlText = remoteConfig.getString("url")
        saveUrl(urlText)
        getUrl(urlText)
        if (!isSharedSaved()) {
            getValueFromFireBaseRemoteConfig()
            if (urlText.isEmpty() || checkIsEmu()) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                saveUrl(urlText)
                getUrl(urlText)
                getWebView(urlText)
            }
        } else {
            if (isInternetAvailable()) {
                getValueFromFireBaseRemoteConfig()
                getWebView(urlText)
            } else {
                startActivity(Intent(this, NoNetworkActivity::class.java))
                finish()
            }
        }
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) binding.webView.goBack()
        else super.onBackPressed()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun getWebView(url: String) {
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
        remoteConfig.fetchAndActivate()
    }

    private fun updateRemoteConfig() {
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Log.d(TAG, "Updated keys: " + configUpdate.updatedKeys);

                if (configUpdate.updatedKeys.contains("welcome_message")) {
                    remoteConfig.activate()
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.w(TAG, "Config update error with code: " + error.code, error)
            }
        })
    }

    private fun saveUrl(url: String) {
        val urlText = remoteConfig.getString("url")
        val sharedPreference = getSharedPreferences("application", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("url", url)
        editor.apply()
    }

    private fun getUrl(url: String): String? {
        val sharedPreference = getSharedPreferences("application", Context.MODE_PRIVATE)
        return sharedPreference.getString("url", url)
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