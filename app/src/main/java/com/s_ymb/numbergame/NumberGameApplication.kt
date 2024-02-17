package com.s_ymb.numbergame

import android.app.Application
import com.s_ymb.numbergame.data.AppContainer
import com.s_ymb.numbergame.data.AppDataContainer

class NumberGameApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }

}