package com.codinginflow.mvvmtodo.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.data.SortOrder
import com.codinginflow.mvvmtodo.databinding.FragmentTasksBinding
import com.codinginflow.mvvmtodo.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private val viewModel: TasksViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTasksBinding.bind(view)

        val tasksAdapter = TasksAdapter()
        binding.apply {
            recyclerViewTasks.apply {
                adapter = tasksAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }
        viewModel.tasks.observe(viewLifecycleOwner) {
            tasksAdapter.submitList(it)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_task, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged {
           viewModel.searchQuery.value=it
        }

        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_hide_completed_tasks).isChecked =
                viewModel.preferencesFlow.first().hideCompleted
            //first value and then cancel it vs collect which updates all the time
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
      return when(item.itemId){
          R.id.action_sort_by_name ->{
              //viewModel.sortOrder.value = SortOrder.BY_NAME
              viewModel.onSortOrderSelected(SortOrder.BY_NAME)
              true
          }
          R.id.action_sort_by_date_created ->{
              //viewModel.sortOrder.value = SortOrder.BY_DATE
              viewModel.onSortOrderSelected(SortOrder.BY_DATE)
              true
          }
          R.id.action_hide_completed_tasks ->{
              item.isChecked=!item.isChecked
              //viewModel.hideCompleted.value=item.isChecked
              viewModel.onHideCompletedClick(item.isChecked)
              true
          }
          R.id.action_delete_all_completed_tasks ->{

              true
          }
          else -> super.onOptionsItemSelected(item)
      }
    }
}