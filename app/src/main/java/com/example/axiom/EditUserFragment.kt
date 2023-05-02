package com.example.axiom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class EditUserFragment : Fragment() {


    companion object {
        fun newInstance() = EditUserFragment()
    }

    private lateinit var viewModel: EditUserViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_user, container, false)

        // Get a reference to the ViewModel
        val viewModel = ViewModelProvider(this)[EditUserViewModel::class.java]

        // Observe the LiveData from the ViewModel and update the UI accordingly
        viewModel.allEntities.observe(viewLifecycleOwner) { entities ->
            // Loop through each entity and update the corresponding TextViews
            for (entity in entities) {
                view.findViewById<TextView>(R.id.user_firstname).text = entity.firstName
                view.findViewById<TextView>(R.id.user_username).text = entity.username
                view.findViewById<TextView>(R.id.user_password).text = entity.password
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}
