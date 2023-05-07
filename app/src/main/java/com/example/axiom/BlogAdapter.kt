package com.example.axiom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.axiom.model.request.RegisterRequest

class BlogAdapter(val blogs: List<RegisterRequest>) : RecyclerView.Adapter<BlogAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_blog, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(blogs[position])
    }

    override fun getItemCount(): Int {
        return blogs.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val titleTextView: TextView = itemView.findViewById(R.id.blog_description)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.blog_title)

        fun bind(registerRequest: RegisterRequest) {
//            titleTextView.text = registerRequest.email
            descriptionTextView.text = "This is the hardcoded blog description"
        }
    }
}
