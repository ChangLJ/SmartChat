package com.angrykiwi.smartchat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.angrykiwi.smartchat.databinding.ActivityMainBinding
import com.angrykiwi.smartchat.model.ChatGPTRequest
import com.angrykiwi.smartchat.model.ChatGPTResponse
import com.angrykiwi.smartchat.model.MessageX
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val apiKey = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MobileAds.initialize(
            this
        ) { initializationStatus ->
            val map = initializationStatus.adapterStatusMap
            for ((key, adapterStatus) in map) {
                val state = adapterStatus.initializationState
                Log.d(
                    "AdView", "key = " + key                            + ", state = " + state.name
                )
            }
        }
        val adview = findViewById<AdView>(R.id.adView)
        val adRequest: AdRequest = AdRequest.Builder().build()
        adview.loadAd(adRequest)

        binding.btnSubmit.setOnClickListener {
            send2ChatGPT()
        }
        binding.btnCopy.setOnClickListener {
            copyToClipBoard(binding.textContent.text.toString())
        }
        binding.btnShare.setOnClickListener {
            shareText(binding.textContent.text.toString())
        }
    }

    /**
     * 送出Message到ChatGPT
     */
    private fun send2ChatGPT() {
        hideKeyboard()
        binding.progress.visibility = View.VISIBLE
        binding.textContent.text = ""
        binding.scrollView.visibility = View.INVISIBLE
        binding.btnSubmit.isEnabled = false
        val contentList = mutableListOf<MessageX>()
        contentList.add(MessageX(binding.editQuestion.text.toString()))
        val body = ChatGPTRequest(contentList)
        val api = ApiHelper(this).getApi()
        val call: Call<ChatGPTResponse?>? = api!!.completions(apiKey, body)
        call?.enqueue(object : Callback<ChatGPTResponse?> {
            override fun onResponse(
                call: Call<ChatGPTResponse?>,
                response: Response<ChatGPTResponse?>
            ) {
                // 連線成功
                binding.progress.visibility = View.GONE
                binding.btnSubmit.isEnabled = true
                if (response.body() != null) {
                    binding.textContent.text = response.body()!!.choices[0].message.content
                    binding.btnCopy.visibility = View.VISIBLE
                    binding.btnShare.visibility = View.VISIBLE
                } else
                    binding.textContent.text = getString(R.string.msg_chatgpt_fail)
                binding.scrollView.visibility = View.VISIBLE
            }

            override fun onFailure(call: Call<ChatGPTResponse?>, t: Throwable) {
                // 連線失敗
                binding.progress.visibility = View.GONE
                binding.btnSubmit.isEnabled = true
                if (t.message.toString() == "timeout")
                    binding.textContent.text = getString(R.string.msg_timeout)
                else
                    binding.textContent.text =
                        getString(R.string.msg_connect_fail) + t!!.message.toString()
                binding.scrollView.visibility = View.VISIBLE
            }
        })
    }

    /**
     * 隱藏軟體鍵盤
     */
    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * 複製到剪貼簿
     */
    private fun copyToClipBoard(msg: String) {
        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("SmartChat", msg)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, getString(R.string.copy_success), Toast.LENGTH_SHORT).show()
    }

    /**
     * Share to other applications
     */
    private fun shareText(msg: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, msg)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}