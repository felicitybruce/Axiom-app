package com.example.axiom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class HomeFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = PostListAdapter(mutableListOf()) // pass the list of entities to the adapter here

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        // observe the LiveData from the ViewModel and update the adapter accordingly
        val viewModel = ViewModelProvider(this)[AxiomViewModel::class.java]
        viewModel.allEntities.observe(viewLifecycleOwner) { entities ->
            for (entity in entities) {
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
