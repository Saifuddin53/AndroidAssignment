package com.myjar.jarassignment.ui.vm

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myjar.jarassignment.createRetrofit
import com.myjar.jarassignment.data.model.ComputerItem
import com.myjar.jarassignment.data.repository.JarRepository
import com.myjar.jarassignment.data.repository.JarRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class JarViewModel : ViewModel() {

    private val _listStringData = MutableStateFlow<List<ComputerItem>>(emptyList())
    val listStringData: StateFlow<List<ComputerItem>>
        get() = _listStringData

    private val repository: JarRepository = JarRepositoryImpl(createRetrofit())

    fun fetchData() {
        viewModelScope.launch {
            try {
                repository.fetchResults().collect { fetchedItems ->
                    Log.d("JarViewModel", "Fetched items: $fetchedItems")
                    _listStringData.value = fetchedItems
                }
            } catch (e: Exception) {
                Log.e("JarViewModel", "Error fetching data: ${e.message}")
            }
        }
    }

    fun searchItems(item: String) {
        if (item.isBlank()) {
            fetchData()
            return
        }
        viewModelScope.launch {
            val filteredItems = _listStringData.value.filter { it.name.contains(item, ignoreCase = true) }
            _listStringData.value = filteredItems
        }
    }
}