package com.example.strayanimaltracker

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class UserFragment : Fragment() {

    private var imageView: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_user, container, false)

        imageView = v.findViewById(R.id.imagem_fragment)

        return v
    }

    fun setImagem(imagem: Bitmap) {
        imageView?.setImageBitmap(imagem)
    }

}
