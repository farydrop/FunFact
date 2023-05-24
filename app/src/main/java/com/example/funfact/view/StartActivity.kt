package com.example.funfact.view

import android.accessibilityservice.MagnificationConfig
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.icu.util.ULocale
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.funfact.BuildConfig
import com.example.funfact.R
import com.example.funfact.databinding.ActivityStartBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.*
import com.google.firebase.remoteconfig.ktx.configUpdates
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class StartActivity : AppCompatActivity() {

    private var mUploadMessage: ValueCallback<Uri?>? = null
    private var mCapturedImageURI: Uri? = null
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    private var mCameraPhotoPath: String? = null

    private lateinit var binding: ActivityStartBinding
    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetJavaScriptEnabled", "CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        /*val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.url_default_value)*/
        //getValueFromFireBaseRemoteConfig()
        //updateRemoteConfig()
        setContentView(binding.root)

        val config = FirebaseRemoteConfig.getInstance()
        config.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(3600L)
                .build()
        )
        config.addOnConfigUpdateListener(object : ConfigUpdateListener {
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
        config.fetchAndActivate().addOnCompleteListener(object : OnCompleteListener<Boolean?> {
            override fun onComplete(@NonNull task: Task<Boolean?>) {
                if (task.isSuccessful) {
                    val find: String = config.getString("url")
                    saveUrl(find)
                    getUrl(find)
                    if (!isSharedSaved()) {
                        //getValueFromFireBaseRemoteConfig()
                        if (find.isEmpty() || checkIsEmu()) {
                            startActivity(Intent(this@StartActivity, MainActivity::class.java))
                        } else {
                            saveUrl(find)
                            getUrl(find)
                            binding.webView.webViewClient = WebViewClient()
                            binding.webView.webChromeClient = ChromeClient()
                            val webSettings = binding.webView.settings
                            webSettings.apply {
                                javaScriptEnabled = true
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                domStorageEnabled = true
                                databaseEnabled = true
                                setSupportZoom(true)
                                allowFileAccess = true
                                allowContentAccess = true
                            }

                            if (savedInstanceState != null) {
                                binding.webView.restoreState(savedInstanceState)
                            } else {
                                binding.webView.loadUrl(find)
                            }

                            binding.webView.settings.apply {
                                domStorageEnabled = true
                                javaScriptCanOpenWindowsAutomatically = true
                            }

                            val cookieManager = CookieManager.getInstance()
                            cookieManager.setAcceptCookie(true)
                        }
                    } else {
                        if (isInternetAvailable()) {
                            //getValueFromFireBaseRemoteConfig()
                            binding.webView.webViewClient = WebViewClient()
                            binding.webView.webChromeClient = ChromeClient()
                            val webSettings = binding.webView.settings
                            webSettings.apply {
                                javaScriptEnabled = true
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                domStorageEnabled = true
                                databaseEnabled = true
                                setSupportZoom(true)
                                allowFileAccess = true
                                allowContentAccess = true
                            }

                            if (savedInstanceState != null) {
                                binding.webView.restoreState(savedInstanceState)
                            } else {
                                binding.webView.loadUrl(find)
                            }

                            binding.webView.settings.apply {
                                domStorageEnabled = true
                                javaScriptCanOpenWindowsAutomatically = true
                            }

                            val cookieManager = CookieManager.getInstance()
                            cookieManager.setAcceptCookie(true)
                        } else {
                            startActivity(Intent(this@StartActivity, NoNetworkActivity::class.java))
                            finish()
                        }
                    }

                }
            }
        })


        //showWebView()

        /*val config = FirebaseRemoteConfig.getInstance()
        val configSettings: FirebaseRemoteConfigSettings = Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        config.setConfigSettingsAsync(configSettings)
        config.fetchAndActivate().addOnCompleteListener(object : OnCompleteListener<Boolean?>() {
            fun onComplete(@NonNull task: Task<Boolean?>) {
                if (task.isSuccessful()) {
                    val find: String = config.getString("url")
                }
            }
        })*/

    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        )
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    /*private fun showWebViewOrActivity() {
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
    }*/

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        }
    }

    /*@SuppressLint("SetJavaScriptEnabled")
    private fun getWebView(url: String) {

        //webView = findViewById<View>(R.id.webView) as WebView
        binding.webView.webViewClient = WebViewClient()
        binding.webView.webChromeClient = ChromeClient()
        val webSettings = binding.webView.settings
        webSettings.apply {
            javaScriptEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            domStorageEnabled = true
            databaseEnabled = true
            setSupportZoom(true)
            allowFileAccess = true
            allowContentAccess = true
        }

        if (savedInstanceState != null) {
            binding.webView.restoreState(savedInstanceState)
        } else {
            binding.webView.loadUrl(url)
        }

        binding.webView.settings.apply {
            domStorageEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
        }

        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        *//*with(binding.webView) {
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
        }*//*
    }*/

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
        var result =
            (Build.FINGERPRINT.startsWith("generic") || phoneModel.contains("google_sdk") || phoneModel.lowercase(
                Locale.getDefault()
            )
                .contains("droid4x") || phoneModel.contains("Emulator") || phoneModel.contains("Android SDK built for x86") || Build.MANUFACTURER.contains(
                "Genymotion"
            ) || buildHardware == "goldfish" || Build.BRAND.contains("google") || buildHardware == "vbox86" || buildProduct == "sdk" || buildProduct == "google_sdk" || buildProduct == "sdk_x86" || buildProduct == "vbox86p" || Build.BOARD.lowercase(
                Locale.getDefault()
            ).contains("nox") || Build.BOOTLOADER.lowercase(Locale.getDefault())
                .contains("nox") || buildHardware.lowercase(Locale.getDefault())
                .contains("nox") || buildProduct.lowercase(Locale.getDefault()).contains("nox"))

        if (result) return true
        result = result or (brand.startsWith("generic") && Build.DEVICE.startsWith("generic"))
        if (result) return true
        result = result or ("google_sdk" == buildProduct)
        return result
    }

    inner class ChromeClient : WebChromeClient() {
        // For Android 5.0
        override fun onShowFileChooser(
            view: WebView,
            filePath: ValueCallback<Array<Uri>>,
            fileChooserParams: FileChooserParams
        ): Boolean {
            // Double check that we don't have any existing callbacks
            if (mFilePathCallback != null) {
                mFilePathCallback!!.onReceiveValue(null)
            }
            mFilePathCallback = filePath
            var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent!!.resolveActivity(packageManager) != null) {
                // Create the File where the photo should go
                var photoFile: File? = null
                try {
                    photoFile = createImageFile()
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Log.e("ErrorCreatingFile", "Unable to create Image File", ex)
                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.absolutePath
                    takePictureIntent.putExtra(
                        MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile)
                    )
                } else {
                    takePictureIntent = null
                }
            }
            val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
            contentSelectionIntent.type = "image/*"
            val intentArray: Array<Intent?>
            intentArray = takePictureIntent?.let { arrayOf(it) } ?: arrayOfNulls(0)
            val chooserIntent = Intent(Intent.ACTION_CHOOSER)
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE)
            return true
        }

        // openFileChooser for Android 3.0+
        // openFileChooser for Android < 3.0
        @JvmOverloads
        fun openFileChooser(uploadMsg: ValueCallback<Uri?>?, acceptType: String? = "") {
            mUploadMessage = uploadMsg
            // Create AndroidExampleFolder at sdcard
            // Create AndroidExampleFolder at sdcard
            val imageStorageDir = File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                ), "AndroidExampleFolder"
            )
            if (!imageStorageDir.exists()) {
                // Create AndroidExampleFolder at sdcard
                imageStorageDir.mkdirs()
            }

            // Create camera captured image file path and name
            val file = File(
                imageStorageDir.toString() + File.separator + "IMG_"
                        + System.currentTimeMillis().toString() + ".jpg"
            )
            mCapturedImageURI = Uri.fromFile(file)

            // Camera capture image intent
            val captureIntent = Intent(
                MediaStore.ACTION_IMAGE_CAPTURE
            )
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI)
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = "image/*"

            // Create file chooser intent
            val chooserIntent = Intent.createChooser(i, "Image Chooser")

            // Set camera intent to file chooser
            chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS, arrayOf<Parcelable>(captureIntent)
            )

            // On select image call onActivityResult method of activity
            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE)
        }

        //openFileChooser for other Android versions
        fun openFileChooser(
            uploadMsg: ValueCallback<Uri?>?,
            acceptType: String?,
            capture: String?
        ) {
            openFileChooser(uploadMsg, acceptType)
        }
    }

    @Deprecated("Deprecated in Java")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data)
                return
            }
            var results: Array<Uri>? = null

            // Check that the response is a good one
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = arrayOf(Uri.parse(mCameraPhotoPath))
                    }
                } else {
                    val dataString = data.dataString
                    if (dataString != null) {
                        results = arrayOf(Uri.parse(dataString))
                    }
                }
            }
            mFilePathCallback!!.onReceiveValue(results)
            mFilePathCallback = null
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data)
                return
            }
            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == mUploadMessage) {
                    return
                }
                var result: Uri? = null
                try {
                    result = if (resultCode != RESULT_OK) {
                        null
                    } else {

                        // retrieve from the private variable if the intent is null
                        if (data == null) mCapturedImageURI else data.data
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        applicationContext, "activity :$e",
                        Toast.LENGTH_LONG
                    ).show()
                }
                mUploadMessage!!.onReceiveValue(result)
                mUploadMessage = null
            }
        }
        return
    }

    companion object {
        private const val INPUT_FILE_REQUEST_CODE = 1
        private const val FILECHOOSER_RESULTCODE = 1
    }
}