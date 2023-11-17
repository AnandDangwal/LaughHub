package com.example.memeshare

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.memeshare.databinding.ActivityMainBinding
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private var currentImageUrl : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val memeImage : ImageView = binding.ivMemes
        val nextButton : Button = binding.btnNext
        val shareMeme : ImageView? = binding.ivShareMeme
        loadMeme(memeImage)

        nextButton.setOnClickListener {
            loadMeme(memeImage)
        }

        //Share Memes.
        shareMeme?.setOnClickListener{
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"

            intent.putExtra(Intent.EXTRA_TEXT,"Hyy, Checkout this new meme : $currentImageUrl")
            val chooser = Intent.createChooser(intent,"Share this meme .. ")
            startActivity(chooser)

        }
    }

    private fun loadMeme(memeImage : ImageView){
        binding.pbLoading.visibility = View.VISIBLE

        // Instantiate the RequestQueue.
//        val queue = Volley.newRequestQueue(this)
        val url = "https://api.memegen.link/images"


        // Request a string response from the provided URL.
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                // Get a random element from the JSON array.
                val jsonArray = response.getJSONArray()
                val randomIndex = (Math.random() * jsonArray.length()).toInt()
                val jsonElement = jsonArray.getJSONObject(randomIndex)

                // Get the URL of the meme image.
                currentImageUrl = jsonElement.getString("url")

                // Load the meme image URL into the ImageView.
                Glide.with(this).load(currentImageUrl).listener(object : RequestListener<Drawable>{

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.pbLoading.visibility = View.GONE
                        Toast.makeText(this@MainActivity,"Loading Failed!",Toast.LENGTH_LONG).show()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.pbLoading.visibility = View.GONE
                        return false
                    }

                }).into(memeImage)
            },
            {
                Toast.makeText(this,"Can't get the response",Toast.LENGTH_LONG).show()
            })

        // Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest)
    }
    private fun JSONArray.getJSONArray(): JSONArray {
        return this
    }
}




