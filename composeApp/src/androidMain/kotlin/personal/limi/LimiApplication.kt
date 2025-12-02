package personal.limi

import android.app.Application
import personal.limi.utils.AppDatabase
import personal.limi.utils.datastore.DataStorePreferences
import personal.limi.utils.getRoomDatabase

class LimiApplication : Application() {
    lateinit var database: AppDatabase
    override fun onCreate() {
        super.onCreate()

        DataStorePreferences.apply {
            applicationContext.initialize()
        }
        database = getRoomDatabase(this)
    }
}