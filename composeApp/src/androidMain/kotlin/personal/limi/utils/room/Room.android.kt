package personal.limi.utils.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

const val limiRoomDB = "limi_room.db"

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(limiRoomDB)
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}