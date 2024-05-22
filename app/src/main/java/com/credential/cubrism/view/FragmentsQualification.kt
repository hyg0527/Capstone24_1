package com.credential.cubrism.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.credential.cubrism.R
import com.credential.cubrism.databinding.FragmentQualificationBinding
import com.credential.cubrism.model.repository.QualificationRepository
import com.credential.cubrism.view.adapter.MajorFieldAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.QualificationViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory

class QualificationFragment : Fragment() {
    private var _binding: FragmentQualificationBinding? = null
    private val binding get() = _binding!!

    private val qualificationViewModel: QualificationViewModel by viewModels { ViewModelFactory(QualificationRepository()) }

    private val majorFieldAdapter = MajorFieldAdapter()
    private lateinit var gridLayoutmanager: GridLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQualificationBinding.inflate(inflater, container, false)

        setupToolbar()
        setupRecyclerView()
        setupView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        (activity as AppCompatActivity).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.qualification_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.search) {
                    val intent = Intent(requireContext(), QualificationSearchActivity::class.java)
                    intent.putExtra("type", "search")
                    startActivity(intent)
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupView() {
        binding.progressIndicator.show()
        qualificationViewModel.getMajorFieldList()
    }

    private fun setupRecyclerView() {
        gridLayoutmanager = GridLayoutManager(context, 3)
        binding.recyclerView.apply {
            layoutManager = gridLayoutmanager
            adapter = majorFieldAdapter
            addItemDecoration(ItemDecoratorDivider(requireContext(), 0, 12, 12, 12, 0, 0, null))
            setHasFixedSize(true)
        }

        majorFieldAdapter.setOnItemClickListener { item, _ ->
            val intent = Intent(requireContext(), QualificationMiddleActivity::class.java)
            intent.putExtra("majorFieldName", item.majorFieldName)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        qualificationViewModel.apply {
            majorFieldList.observe(viewLifecycleOwner) { result ->
                binding.progressIndicator.visibility = View.GONE
                majorFieldAdapter.setItemList(result)
            }

            errorMessage.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    if (!message.lowercase().contains("jwt"))
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}