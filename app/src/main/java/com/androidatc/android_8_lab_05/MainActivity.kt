package com.androidatc.android_8_lab_05

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import okhttp3.*
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()

    private lateinit var imgView: ImageView
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val pizzaUrl = "http://192.168.1.3:4000/api/listAll?username=neu&&password=neupass"
        val tempUrl = "https://192.168.1.3:4000/imgpizza2.png"
        var request = Request.Builder().url(pizzaUrl).build()


        var responseStr: String? = null
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle this
                Log.e("MainActivity", e.message)

            }

            override fun onResponse(call: Call, response: Response) {
                // Handle this
                responseStr = response.body()?.string()
                Log.v("MainActivity", responseStr);
                Log.v("neu", "on response method")

            }
        })

        Toast.makeText(applicationContext, responseStr, Toast.LENGTH_SHORT).show()

        imgView = findViewById(R.id.imageView)
        Thread(Runnable {
            try {
                var url: URL = URL(tempUrl)
                val conn = url.openConnection() as HttpURLConnection
                conn.setDoInput(true)
                conn.connect()


                var input: InputStream = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(input)
                input.close()
                Log.v("neu", "read the image")
                mHandler.sendEmptyMessage(SET_IMAGE)
            } catch (e : Exception) {
                Log.e("neu error", e.toString())
            }
        }).start()
    }

    private val SET_IMAGE = 1
    private val mHandler: Handler = Handler() {
        msg: Message -> handleMessage(msg)
    }

    private fun handleMessage(msg: Message) : Boolean {
        if (msg.what == SET_IMAGE) {
            imgView.setImageBitmap(bitmap)
            return true
        }
        return false
    }

    fun onPlaceOrderButtonClicked(view: View) {
        var option1 = 0
        var option2 = 0
        var option3 = 0
        var option4 = 0

        when {
            radioGroup.smallpizza.isChecked -> option4 = 1
            radioGroup.mediumpizza.isChecked -> option4 = 2
            radioGroup.largepizza.isChecked -> option4 = 3
        }

        if (OnionsCheckBox.isChecked) {
            option1 = 1
        }
        if (OlivesCheckBox.isChecked) {
            option2 = 1
        }
        if (TomatoesCheckBox.isChecked) {
            option3 = 1
        }

        val priceUrl =
                "http://192.168.1.3:4000/api/cost/?option1=%d&option2=%d&option3=%d&option4=%d".format(
                        option1,
                        option2,
                        option3,
                        option4
                )
        Toast.makeText(applicationContext, priceUrl, Toast.LENGTH_SHORT).show()

        var client = OkHttpClient()
        var request = Request.Builder().url(priceUrl).build()
        var responseStr: String? = null
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MainActivity", e.message)

            }

            override fun onResponse(call: Call, response: Response) {
                responseStr = response.body()?.string()
                this@MainActivity.runOnUiThread(java.lang.Runnable {
                    Toast.makeText(applicationContext, "Price: " + responseStr, Toast.LENGTH_SHORT)
                            .show()

                })
            }
        })
        Toast.makeText(applicationContext, responseStr, Toast.LENGTH_SHORT).show()
    }
}
