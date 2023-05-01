package com.example.axiom

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.axiom.model.request.RegisterRequest

class AxiomRepository(application: Application) {
    private val userDao: UserDao = UserRoomDatabase.getDatabase(application).userDao()
    val allEntities: LiveData<List<RegisterRequest>> = userDao.getUsers().asLiveData()

    suspend fun insert(registerRequest: RegisterRequest) {
        userDao.register(registerRequest)
    }

    suspend fun update(registerRequest: RegisterRequest) {
        userDao.update(registerRequest)
    }

    suspend fun delete(registerRequest: RegisterRequest) {
        userDao.delete(registerRequest)
    }
}
