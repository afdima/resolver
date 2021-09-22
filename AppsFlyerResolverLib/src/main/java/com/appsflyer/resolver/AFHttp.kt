package com.appsflyer.resolver

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.*
import java.net.CookieHandler
import java.net.CookieManager
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

object AFHttp : Application.ActivityLifecycleCallbacks {
    var debug = false
    private var coroutineScope: CoroutineScope? = null
    private var didInit = false


    init {
        val cookieManager = CookieManager()
        CookieHandler.setDefault(cookieManager)
    }

    fun init(context: Context) {
        (context.applicationContext as Application).registerActivityLifecycleCallbacks(this)
        didInit = true
    }


    fun resolveDeepLinkValue(
        url: String?,
        maxRedirections: Int = 10,
        listener: AFResolverListener
    ) {
        if (!didInit) {
            afDebugLog("The SDK must be initialized with context first")
            return
        }
        if (coroutineScope == null) {
            coroutineScope = CoroutineScope(Dispatchers.IO)
        }
        if (url == null) {
            return listener.onDeepLinkValueResolved(null)
        }

        coroutineScope!!.launch(Dispatchers.IO) {
            afDebugLog("resolving $url")
            if (url.isValidURL()) {
                val redirects = ArrayList<String>().apply {
                    add(url)
                }

                var res: AFHttpResponse? = null
                for (i in 0 until maxRedirections) {
                    // resolve current URL - check for redirection
                    res = resolve(redirects.last())
                    res.redirected?.let { // if redirected to another URL
                        redirects.add(it)
                    } ?: break
                }
                val returnValue = if (res?.error == null) {
                    afDebugLog("found link: ${redirects.last()}")
                    redirects.last()
                } else null

                withContext(Dispatchers.Main) {
                    listener.onDeepLinkValueResolved(returnValue)
                }
            } else {
                withContext(Dispatchers.Main) {
                    listener.onDeepLinkValueResolved(url)
                }
            }
        }

    }

    private fun resolve(uri: String): AFHttpResponse {
        val res = AFHttpResponse()
        try {
            (URL(uri).openConnection() as HttpURLConnection).run {
                instanceFollowRedirects = false
                readTimeout = TimeUnit.SECONDS.toMillis(2).toInt()
                connectTimeout = TimeUnit.SECONDS.toMillis(2).toInt()

                val responseCode = responseCode
                res.status = responseCode
                if (responseCode in 300..308) {
                    // redirect
                    res.redirected = getHeaderField("Location")
                    afDebugLog("redirecting to ${res.redirected}")

                }
                disconnect()
            }
        } catch (e: Throwable) {
            res.error = e.localizedMessage
            afErrorLog(e.message, e)
        }

        return res
    }

    private fun afDebugLog(msg: String) {
        if (debug)
            Log.d("AppsFlyer_Resolver", msg)
    }

    private fun afErrorLog(msg: String?, e: Throwable) {
        if (msg != null)
            Log.d("AppsFlyer_Resolver", msg)
        e.printStackTrace()
    }

    private fun String.isValidURL(): Boolean {
        val regex =
            Regex("^(http|https)://(\\w+:{0,1}\\w*@)?(\\S+)(:[0-9]+)?(/|/([\\w#!:.?+=&%@!\\-/]))?")
        return regex.matches(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
        coroutineScope?.cancel()
        coroutineScope = null
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}