package com.example.axiom

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * View Model to keep a reference to the Inventory repository and an up-to-date list of all users.
 *
 */
class AxiomViewModel(private val userDao: UserDao) : ViewModel() {
    /**
     * Inserts the new User into database.
     */
    fun addNewUser(firstName: String, lastName: String, email: String, username: String, password: String, cnfPassword: String) {
        val newUser = getNewUserEntry(firstName, lastName, email, username, password, cnfPassword)
        Log.d("test", "insertUser: HITTING ADD ENW USER -  the new user $newUser")

        insertUser(newUser)
    }





    /**
     * Launching a new coroutine to insert an user in a non-blocking way
     */
    private fun insertUser(user: User) {
        viewModelScope.launch {
            Log.d("test", "insertUser: $user")
            userDao.insert(user)
        }
    }




    /**
     * Returns true if the EditTexts are not empty
     */
    fun isEntryValid(firstName: String, lastName: String, email: String, username: String, password: String, cnfPassword: String): Boolean {
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || username.isBlank() || password.isBlank() || cnfPassword.isBlank()) {
            return false
        }
        return true
    }

    /**
     * Returns an instance of the [User] entity class with the user info entered by the user.
     * This will be used to add a new entry to the Axiom database.
     */
    private fun getNewUserEntry(firstName: String, lastName: String, email: String, username: String, password: String, cnfPassword: String): User {
        return User(
            firstName = firstName,
            lastName = lastName,
            email = email,
            username = username,
            password = password,
            cnfPassword = cnfPassword
        )
    }
}

/**
 * Factory class to instantiate the [ViewModel] instance.
 */
class AxiomViewModelFactory(private val userDao: UserDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AxiomViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AxiomViewModel(userDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}