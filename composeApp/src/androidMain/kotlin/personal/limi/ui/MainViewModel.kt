package personal.limi.ui

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import personal.limi.ui.share_panel.SharePanelActivity

class MainViewModel : ViewModel() {
    fun startSharePanel(context: Context) {
        val intent = Intent(context, SharePanelActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
    }
}