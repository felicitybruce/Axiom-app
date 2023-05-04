package com.example.axiom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.axiom.model.request.RegisterRequest

class ProfileAdapter(internal var entities: MutableList<RegisterRequest>) :
    RecyclerView.Adapter<ProfileAdapter.ViewHolder>() {

    // TODO: update datbasse to this, rn its just placeholder
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(registerRequest: RegisterRequest) {
            itemView.findViewById<TextView>(R.id.blog_description).text = registerRequest.lastName
            itemView.findViewById<ImageView>(R.id.blog_image)

        }
    }

    fun addEntity(entity: RegisterRequest) {
        entities.add(entity)
        notifyItemInserted(entities.size - 1)
    }

    fun removeEntity(entity: RegisterRequest) {
        val index = entities.indexOf(entity)
        if (index != -1) {
            entities.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun updateEntity(entity: RegisterRequest) {
        val index = entities.indexOf(entity)
        if (index != -1) {
            entities[index] = entity
            notifyItemChanged(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileAdapter.ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.profile_blog, parent, false)
        return ProfileAdapter.ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProfileAdapter.ViewHolder, position: Int) {
        holder.bind(entities[position])
    }







    override fun getItemCount(): Int {
        return entities.size
    }
}