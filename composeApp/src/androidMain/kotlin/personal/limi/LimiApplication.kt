package personal.limi

import android.app.Application
import personal.limi.utils.datastore.DataStorePreferences

class LimiApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        DataStorePreferences.apply {
            applicationContext.initialize()
        }
    }
}