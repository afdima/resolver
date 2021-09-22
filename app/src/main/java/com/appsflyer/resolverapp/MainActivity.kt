package com.appsflyer.resolverapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.appsflyer.resolver.AFHttp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AFHttp(this).resolveDeepLinkValue("dfcd") { res ->
            res?.let {
                Log.d("TAG", "onCreate: $it")
            }
        }
    }
}