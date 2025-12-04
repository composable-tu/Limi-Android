package personal.limi.ui

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import personal.limi.LimiApplication
import personal.limi.data.model.LimiHistoryEntity
import personal.limi.logic.RuleIds
import personal.limi.ui.share_panel.SharePanelActivity
import personal.limi.utils.asState
import personal.limi.utils.datastore.DataStorePreferences
import personal.limi.utils.room.LimiHistoryDao
import personal.limi.utils.startPlayBarcodeScanner

class MainViewModel() : ViewModel() {
    val dao: LimiHistoryDao = LimiApplication.database.getLimiHistoryDao()

    val historyListStateFlow: StateFlow<List<LimiHistoryEntity>> =
        dao.getAllAsFlowSortedByDatetimeDesc().asState(viewModelScope, emptyList())

    val isCommonParamsRuleEnabled = DataStorePreferences.getBooleanFlow(RuleIds.COMMON_PARAMS, true)
        .asState(viewModelScope, true)
    val isUTMParamsRuleEnabled =
        DataStorePreferences.getBooleanFlow(RuleIds.UTM_PARAMS, true).asState(viewModelScope, true)
    val isUTMParamsEnhancedRuleEnabled =
        DataStorePreferences.getBooleanFlow(RuleIds.UTM_PARAMS_ENHANCED, false)
            .asState(viewModelScope, false)
    val isBilibiliRuleEnabled =
        DataStorePreferences.getBooleanFlow(RuleIds.BILIBILI, true).asState(viewModelScope, true)

    fun setCommonParamsRuleEnabled(bool: Boolean) =
        DataStorePreferences.putBooleanSync(RuleIds.COMMON_PARAMS, bool)

    fun setUTMParamsRuleEnabled(bool: Boolean) =
        DataStorePreferences.putBooleanSync(RuleIds.UTM_PARAMS, bool)

    fun setUTMParamsEnhancedRuleEnabled(bool: Boolean) =
        DataStorePreferences.putBooleanSync(RuleIds.UTM_PARAMS_ENHANCED, bool)

    fun setBilibiliRuleEnabled(bool: Boolean) =
        DataStorePreferences.putBooleanSync(RuleIds.BILIBILI, bool)

    fun startSharePanel(context: Context) {
        val intent = Intent(context, SharePanelActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
    }

    fun startScanQRCode(context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val result = withContext(Dispatchers.IO) { startPlayBarcodeScanner(context) }
                if (result.isNotEmpty()) {
                    val intent = Intent(context, SharePanelActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        putExtra(Intent.EXTRA_TEXT, result.joinToString("\n"))
                    }
                    context.startActivity(intent)
                } else throw Exception("扫码结果为空")
            } catch (_: Exception) {

            }
        }
    }
}