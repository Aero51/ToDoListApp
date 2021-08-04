package com.codinginflow.mvvmtodo.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.codinginflow.mvvmtodo.data.PreferencesManager
import com.codinginflow.mvvmtodo.data.SortOrder
import com.codinginflow.mvvmtodo.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class TasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    //val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
    //val hideCompleted = MutableStateFlow(false)

    val preferencesFlow = preferencesManager.preferencesFlow

    private val tasksFlow = combine(
        searchQuery,
        preferencesFlow
        //sortOrder,
        //hideCompleted
    //) { query, sortOrder, hideCompleted ->
    ) { query, filterPreferences ->
        //Triple(query, sortOrder, hideCompleted)
        Pair(query, filterPreferences)
   // }.flatMapLatest {(query, sortOrder, hideCompleted) ->
    }.flatMapLatest {(query,filterPreferences) ->
            //taskDao.getTasks(query, sortOrder, hideCompleted)
        taskDao.getTasks(query, filterPreferences.sortOrder,filterPreferences.hideCompleted)
        }

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    val tasks = tasksFlow.asLiveData()

}

