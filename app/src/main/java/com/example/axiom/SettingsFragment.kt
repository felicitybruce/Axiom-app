package com.example.axiom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Get a reference to the ViewModel
        val viewModel = ViewModelProvider(this)[AxiomViewModel::class.java]

        // Observe the LiveData from the ViewModel and update the UI accordingly
        viewModel.allUsers.observe(viewLifecycleOwner) { entities ->
            // Loop through each entity and update the corresponding TextViews
            for (entity in entities) {
                view.findViewById<TextView>(R.id.user_firstname).text = "First name: ${entity.firstName}"
                view.findViewById<TextView>(R.id.user_username).text = "Username: ${entity.username}"
                view.findViewById<TextView>(R.id.user_password).text = "Password: ${entity.password}"
            }
        }
        return view
    }
}
