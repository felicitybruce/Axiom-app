package com.example.axiom

import androidx.room.*
import com.example.axiom.model.request.RegisterRequest

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun register(registerRequest: RegisterRequest)

    @Update
    suspend fun update(registerRequest: RegisterRequest)

    @Delete
    suspend fun delete(registerRequest: RegisterRequest)

    @Query("SELECT * from user")
    fun getUsers(): List<RegisterRequest>

    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): RegisterRequest

    @Query("SELECT * FROM user WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): RegisterRequest


    @Query("SELECT COUNT(*) FROM user WHERE username = :username AND password = :password")
    fun userExists(username: String, password: String): Int

    @Query("SELECT * FROM user WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): RegisterRequest?

    @Query("SELECT * FROM user WHERE password = :password LIMIT 1")
    suspend fun getUserPassword(password: String): RegisterRequest?

}
