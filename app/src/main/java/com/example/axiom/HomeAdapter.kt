package com.example.axiom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.axiom.model.request.RegisterRequest

// Internal so it can be accessed by other things in same module
class HomeAdapter(internal var entities: MutableList<RegisterRequest>) :
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(registerRequest: RegisterRequest) {
            itemView.findViewById<TextView>(R.id.publishInfoTv).text = registerRequest.firstName
            itemView.findViewById<TextView>(R.id.descriptionTv).text = registerRequest.username
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

    //Row blog
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.row_blog, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(entities[position])
    }

    override fun getItemCount(): Int {
        return entities.size
    }
}
