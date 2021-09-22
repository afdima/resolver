package com.appsflyer.resolver

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UrlResolverTest {
    @Test
    fun testResolveUsingBitlyAndLambda() {
        AFHttp.resolveDeepLinkValue("https://bit.ly/38JtcFq", 5) {
            assertEquals(it, "https://paz.onelink.me/waF3/paz")
        }
    }

    @Test
    fun testResolveMILink() {
        AFHttp.resolveDeepLinkValue(
            "https://mi.gap.com/p/cp/d46c31d50872e4b3/c?mi_u=51635872&EV=GPUSCPATHTSTPC404859" +
                    "3PERST1_WMNNLPSPRP04162021&DI=51635872&CD=GPNC_GPR&cvosrc=email.exacttarget." +
                    "GPUS04162021&EV_Segment1=GPUSGWMET&url=https%3A%2F%2Fmi.gap.com%2Fp%2Frp%2F6" +
                    "d2aa0caedfe8322%2Furl&mi_u=%%email_key%%&EV=%%EV_value%%&DI=%%DI_value%%&CD=" +
                    "%%CD_value%%"
        ) {
            assertEquals(
                it,
                "https://www.gap.com/?mi_u=%25%25email_key%25%25&EV=%25%25EV_value%25%25&D" +
                        "I=%25%25DI_value%25%25&CD=%25%25CD_value%25%25&cvosrc=email.exacttarget." +
                        "GPUS04162021"
            )
            Log.d("testTag", "testResolveMILink: $it")
            println(it)
        }
    }

    @Test
    fun testResolveUsingBitlyAndCallback() {
        AFHttp.resolveDeepLinkValue("https://bit.ly/38JtcFq", 5, AFResolverListener {
            assertEquals(it, "https://paz.onelink.me/waF3/paz")
        })
    }

    @Test
    fun testResolveUsingBitlyAndCallbackObject() {
        val listener = object : AFResolverListener {
            override fun onDeepLinkValueResolved(deepLinkValue: String?) {
                assertEquals(deepLinkValue, "https://paz.onelink.me/waF3/paz")
            }
        }
        AFHttp.resolveDeepLinkValue("https://bit.ly/38JtcFq", 5, listener)
    }

    @Test
    fun testNullURL() {
        AFHttp.resolveDeepLinkValue(null, 5) {
            assertEquals(it, null)
        }
    }

    @Test
    fun testNotaURL() {
        AFHttp.resolveDeepLinkValue("abcd", 5) {
            assertEquals(it, "abcd")
        }
    }

    @Test
    fun testEmptyString() {
        AFHttp.resolveDeepLinkValue("", 5) {
            assertEquals(it, "")
        }
    }

    @Test
    fun testWhenURLisEncoded() {
        AFHttp.resolveDeepLinkValue(
            "https%3A%2F%2Fmi.gap.com%2Fp%2Fcp%2Fd46c31d50872e4b3%2Fc%3Fmi_u%3D51635872%26EV%3DGPUSCPATHTSTPC4048593PERST1_WMNNLPSPRP04162021%26DI%3D51635872%26CD%3DGPNC_GPR%26cvosrc%3Demail.exacttarget.GPUS04162021%26EV_Segment1%3DGPUSGWMET%26url%3Dhttps%253A%252F%252Fmi.gap.com%252Fp%252Frp%252F6d2aa0caedfe8322%252Furl%26mi_u%3D%25%25email_key%25%25%26EV%3D%25%25EV_value%25%25%26DI%3D%25%25DI_value%25%25%26CD%3D%25%25CD_value%25%25",
            5
        ) {
            assertEquals(
                it,
                "https%3A%2F%2Fmi.gap.com%2Fp%2Fcp%2Fd46c31d50872e4b3%2Fc%3Fmi_u%3D51635872%26EV%3DGPUSCPATHTSTPC4048593PERST1_WMNNLPSPRP04162021%26DI%3D51635872%26CD%3DGPNC_GPR%26cvosrc%3Demail.exacttarget.GPUS04162021%26EV_Segment1%3DGPUSGWMET%26url%3Dhttps%253A%252F%252Fmi.gap.com%252Fp%252Frp%252F6d2aa0caedfe8322%252Furl%26mi_u%3D%25%25email_key%25%25%26EV%3D%25%25EV_value%25%25%26DI%3D%25%25DI_value%25%25%26CD%3D%25%25CD_value%25%25"
            )
        }
    }
}