package com.example.axiom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HomeAdapter
    private lateinit var fab: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        // Get a reference to the ViewModel
        val viewModel = ViewModelProvider(this)[AxiomViewModel::class.java]


        recyclerView = view.findViewById(R.id.recyclerView)
        // Pass the list of entities to the adapter here
        adapter = HomeAdapter(mutableListOf())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        fab = view.findViewById(R.id.createBlogFAB)


        // Observe the LiveData from the ViewModel and update the adapter accordingly
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val bottomAppBar = view.findViewById<BottomAppBar>(R.id.bottomAppBar)
        bottomAppBar.setNavigationOnClickListener {
            // Handle navigation icon press
        }

        fab.setOnClickListener {
            CreateBlogTray.newInstance(30).show(childFragmentManager, "dialog")
        }
        
        bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Profile -> {
                    // Handle search icon press
                    val action = HomeFragmentDirections.actionHomeFragmentToProfileFragment()
                    this.findNavController().navigate(action)
                    true
                }
                R.id.Home -> {
                    // Handle more item (inside overflow menu) press
                    true
                }
                R.id.Settings -> {
                    // Handle more item (inside overflow menu) press
                    val action = HomeFragmentDirections.actionHomeFragmentToEditUserFragment()
                    this.findNavController().navigate(action)
                    true
                }
                else -> false
            }
        }
    }
}
