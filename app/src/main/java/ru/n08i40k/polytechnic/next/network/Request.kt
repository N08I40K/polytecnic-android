package ru.n08i40k.polytechnic.next.network

import android.annotation.SuppressLint
import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.security.cert.X509Certificate
import java.util.logging.Logger
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class NetworkConnection(ctx: Context) {
    companion object {
        @Volatile
        private var INSTANCE: NetworkConnection? = null

        fun getInstance(ctx: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: NetworkConnection(ctx).also { INSTANCE = it }
        }
    }

    private val sslSocketFactory: SSLSocketFactory by lazy {
        val trustAllCerts =
            arrayOf<TrustManager>(@SuppressLint("CustomX509TrustManager") object :
                X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(
                    chain: Array<X509Certificate>, authType: String
                ) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(
                    chain: Array<X509Certificate>, authType: String
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, null)

        sslContext.socketFactory
    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(ctx.applicationContext, HurlStack(null, sslSocketFactory))
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }
}

open class RequestBase(
    protected val context: Context,
    method: Int,
    url: String?,
    listener: Response.Listener<String>,
    errorListener: Response.ErrorListener?
) : StringRequest(method, NetworkValues.API_HOST + url, listener, errorListener) {
    open fun send() {
        Logger.getLogger("RequestBase").info("Sending request to $url")
        NetworkConnection.getInstance(context).addToRequestQueue(this)
    }

    override fun getHeaders(): MutableMap<String, String> {
        val headers = mutableMapOf<String, String>()
        headers["Content-Type"] = "application/json; charset=utf-8"

        return headers
    }
}
