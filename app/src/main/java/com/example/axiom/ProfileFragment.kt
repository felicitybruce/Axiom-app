package com.example.axiom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView


class ProfileFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProfileAdapter
    private lateinit var profileName: TextView // declare the variable

    private lateinit var constraintView: ConstraintLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Get a reference to the ViewModel
        val viewModel = ViewModelProvider(this)[EditUserViewModel::class.java]

        // Observe the LiveData from the ViewModel and update the UI accordingly
        viewModel.allEntities.observe(viewLifecycleOwner) { entities ->
            // Loop through each entity and update the corresponding TextViews
            for (entity in entities) {
                view.findViewById<TextView>(R.id.profile_name).text = entity.firstName + " " + entity.lastName
                view.findViewById<TextView>(R.id.profile_bio).text = "bio stuff"
            }
        }
        return view
    }
}
