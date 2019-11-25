package com.example.strayanimaltracker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.strayanimaltracker.R
import com.example.strayanimaltracker.entity.Post
import kotlinx.android.synthetic.main.item_post.view.*

class PostAdapter(
    var posts: List<Post>,
    var onClickCallback: (Int) -> Unit,
    var onLongClickCallback: (Int) -> Unit
) : RecyclerView.Adapter<PostAdapter.VH>() {

    override fun getItemCount(): Int = posts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_post, parent, false)

        val vh = VH(v)

        vh.itemView.setOnClickListener {
            onClickCallback(vh.adapterPosition)
        }

        vh.itemView.setOnLongClickListener {
            onLongClickCallback(vh.adapterPosition)
            false
        }

        return vh
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val post = posts[position]

        holder.nome.text = post.nome
        holder.sexo.text = post.sexo
        holder.especie.text = post.especie
        holder.data.text = post.data
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.nome_item_post
        val sexo: TextView = itemView.sexo_item_post
        val especie: TextView = itemView.especie_item_post
        val data: TextView = itemView.data_item_post
    }

}