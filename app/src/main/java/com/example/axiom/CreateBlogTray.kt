package com.example.axiom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.axiom.databinding.FragmentCreateBlogTrayListDialogBinding
import com.example.axiom.databinding.FragmentCreateBlogTrayListDialogItemBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// TODO: Customize parameter argument names
const val ARG_ITEM_COUNT = "item_count"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    CreateBlogTray.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */
class CreateBlogTray : BottomSheetDialogFragment() {

    private var _binding: FragmentCreateBlogTrayListDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCreateBlogTrayListDialogBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the RecyclerView to display a list of items
        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.adapter = arguments?.getInt(ARG_ITEM_COUNT)?.let { ItemAdapter(it) }
    }


    private inner class ViewHolder internal constructor(binding: FragmentCreateBlogTrayListDialogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

//        internal val text: TextView = binding.text
        val text: TextView = itemView.findViewById(R.id.headerText)
        val createBlogXCTA: ImageView = itemView.findViewById(R.id.createBlogXCTA)
        val createBlogHeader: TextView = itemView.findViewById(R.id.createBlogHeader)
        val createBlogText: EditText = itemView.findViewById(R.id.createBlogText)
        val createBlogContentHeader: TextView = itemView.findViewById(R.id.createBlogContentHeader)
        val createBlogDescription: EditText = itemView.findViewById(R.id.createBlogDescription)
        val createBlogCTA: Button = itemView.findViewById(R.id.createBlogCTA)

    }

    private inner class ItemAdapter internal constructor(private val mItemCount: Int) :
        RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            return ViewHolder(
                FragmentCreateBlogTrayListDialogItemBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.createBlogHeader.text = "Create Blog Post"
            holder.createBlogText.setText("")

            holder.createBlogText.hint = "Enter Blog Title"
            holder.createBlogContentHeader.text = "Blog Content"

            holder.createBlogDescription.hint = " Blah blah blah"
            holder.createBlogCTA.text = "Post"

        }

        override fun getItemCount(): Int {
            return 1
        }
    }

    companion object {

        // TODO: Customize parameters
        fun newInstance(itemCount: Int): CreateBlogTray =
            CreateBlogTray().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ITEM_COUNT, itemCount)
                }
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}