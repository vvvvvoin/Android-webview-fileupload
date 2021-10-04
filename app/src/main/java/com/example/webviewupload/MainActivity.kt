package com.example.webviewupload

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.webviewupload.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonMainActivityUrl.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java).apply {
                putExtra(YOUR_URL, binding.editMainActivityUrl.text)
            }

            startActivity(intent)
        }
    }

    companion object {
        const val YOUR_URL = "YOUR_URL"
    }
}
