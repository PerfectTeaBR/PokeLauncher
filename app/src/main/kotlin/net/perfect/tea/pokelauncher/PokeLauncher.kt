package net.perfect.tea.pokelauncher

import android.util.Log
import android.app.Application
import android.os.Build

class PokeLauncherApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.i("Debug", "Initializing PokeLauncherApp")
        Log.d("PokeLauncherApp", "onCreate")
    }
}