package com.example.axiom

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.axiom.model.request.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AxiomViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AxiomRepository = AxiomRepository(application)

    val allEntities: LiveData<List<RegisterRequest>> = repository.allEntities

//    fun insert(registerRequest: RegisterRequest) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.insert(registerRequest)
//        }
//    }



    fun update(registerRequest: RegisterRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(registerRequest)
        }
    }

    fun delete(registerRequest: RegisterRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(registerRequest)
        }
    }
}

