package com.example.axiom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.axiom.model.request.RegisterRequest

class ProfileAdapter(val entities: MutableList<RegisterRequest>) :
    RecyclerView.Adapter<ProfileAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //        private val nameTextView: TextView = itemView.findViewById(R.id.profile_name)
        fun bind(entity: RegisterRequest) {
            //            blogTitle.text = entity.email
            itemView.findViewById<TextView>(R.id.blog_title).text = "Money Is Good?"
        }
    }

    fun addEntity(entity: RegisterRequest) {
        entities.add(entity)
        notifyItemInserted(entities.size - 1)
    }

    fun removeEntity(entity: RegisterRequest) {
        val index = entities.indexOfFirst { it.id == entity.id }
        if (index != -1) {
            entities.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun updateEntity(entity: RegisterRequest) {
        val index = entities.indexOfFirst { it.id == entity.id }
        if (index != -1) {
            entities[index] = entity
            notifyItemChanged(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_blog, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(entities[position])
    }

    override fun getItemCount(): Int {
        return entities.size
    }
}
