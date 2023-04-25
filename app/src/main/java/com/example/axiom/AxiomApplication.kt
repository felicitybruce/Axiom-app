package com.example.axiom

import android.app.Application

class AxiomApplication : Application() {
    //calling getDatabase instantiates the db
    // pass in context
    //database is lazily created when you first need/access
    // the reference (rather than when the app starts).
    val database: UserRoomDatabase by lazy {UserRoomDatabase.getDatabase(this)}
}
