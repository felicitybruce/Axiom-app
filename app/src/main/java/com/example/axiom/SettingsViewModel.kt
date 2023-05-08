package com.example.axiom

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.axiom.model.request.RegisterRequest

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AxiomRepository = AxiomRepository(application)

    val allEntities: LiveData<List<RegisterRequest>> = repository.allEntities
}
