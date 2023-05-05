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
    private lateinit var adapter: PostListAdapter
    private lateinit var fab: FloatingActionButton

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

        // Global: Get buttons
        fab = view.findViewById(R.id.createBlogFAB)

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
//                    val action = HomeFragmentDirections.act()
//                    this.findNavController().navigate(action)
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
//                    val activity = requireActivity() as AppCompatActivity
//                    FragmentUtils.navigateScreen(activity, EditUserFragment())


                    val action = HomeFragmentDirections.actionHomeFragmentToEditUserFragment()
                    this.findNavController().navigate(action)
                    true
                }
                else -> false

            }
        }
    }
}
