package personal.limi

import android.app.Application
import personal.limi.utils.datastore.DataStorePreferences
import personal.limi.utils.room.AppDatabase
import personal.limi.utils.room.getDatabaseBuilder
import personal.limi.utils.room.getRoomDatabase

class LimiApplication : Application() {
    lateinit var database: AppDatabase
    override fun onCreate() {
        super.onCreate()

        DataStorePreferences.apply {
            applicationContext.initialize()
        }
        database = getRoomDatabase(getDatabaseBuilder(this))
    }
}