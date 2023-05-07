package com.example.axiom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ProfileFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProfileAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewProfile)
        adapter = ProfileAdapter(mutableListOf()) // pass the list of entities to the adapter here
        recyclerView.adapter = adapter // attach the adapter to the recyclerView
        val layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.layoutManager = layoutManager

        // Get a reference to the ViewModel
        val viewModel = ViewModelProvider(this)[AxiomViewModel::class.java]
        val profileName = view.findViewById<TextView>(R.id.blog_description)
//        val profileBio = view.findViewById<TextView>(R.id.profile_bio)
        // Observe the LiveData from the ViewModel and update the UI accordingly
        viewModel.allEntities.observe(viewLifecycleOwner) { entities ->
            // Loop through each entity and update the corresponding TextViews
            for (entity in entities) {
//                profileName.text = entity.firstName + " " + entity.lastName
//                profileBio.text = "bio stuff"
                if (!adapter.entities.contains(entity)) {
                    adapter.addEntity(entity)
                } else {
                    adapter.updateEntity(entity)
                }
            }
            val entitiesToRemove = adapter.entities.filter { entity ->
                !entities.contains(entity)
            }
            for (entityToRemove in entitiesToRemove) {
                adapter.removeEntity(entityToRemove)
            }
        }
        return view
    }
}

