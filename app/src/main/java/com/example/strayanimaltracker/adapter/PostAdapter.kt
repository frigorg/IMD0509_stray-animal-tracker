package com.example.strayanimaltracker.adapter

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.strayanimaltracker.R
import com.example.strayanimaltracker.entity.Post
import kotlinx.android.synthetic.main.item_tarefa.view.*

class PostAdapter (
    public var posts: List<Post>):RecyclerView.Adapter<PostAdapter.VH>(){



    override fun getItemCount(): Int = posts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_tarefa,parent, false)

        val vh = VH(v)


        return vh
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val post = posts[position]

        holder.txtTitle.text = post.nome
        holder.txtDescricao.text = post.sexo
        holder.id.text= post.idUsuario


    }

    class  VH(itemView: View): RecyclerView.ViewHolder(itemView){

        val id:TextView=itemView.txtId
        val txtTitle: TextView = itemView.txtTitulo
        val txtDescricao:TextView =itemView.txtDescricao


    }

}