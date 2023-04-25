package com.example.axiom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * View Model to keep a reference to the Inventory repository and an up-to-date list of all items.
 *
 */
class InventoryViewModel(private val userDao: UserDao) : ViewModel() {

    /**
     * Inserts the new Item into database.
     */
    fun addNewUser(id: Int, firstName: String, lastName: String, email: String, username: String, password: String, cnfPassword: String) {
        val newUser = getNewUserEntry(id, firstName, lastName, email, username, password, cnfPassword)
        insertUser(newUser)
    }

    /**
     * Launching a new coroutine to insert an item in a non-blocking way
     */
    private fun insertUser(user: User) {
        viewModelScope.launch {
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
     * This will be used to add a new entry to the Inventory database.
     */
    private fun getNewUserEntry(id: Int, firstName: String, lastName: String, email: String, username: String, password: String, cnfPassword: String): User {
        return User(
            id = id,
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
class InventoryViewModelFactory(private val userDao: UserDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(userDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}