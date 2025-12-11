package personal.limi.ui.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import personal.limi.R
import personal.limi.ui.MainViewModel
import personal.limi.ui.components.preference.PreferenceGroup
import personal.limi.ui.components.preference.switch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuleScreen(
    viewModel: MainViewModel = viewModel { MainViewModel() }, titleResId: Int = R.string.rule
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()
    val layoutDirection = LocalLayoutDirection.current
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(titleResId)) },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        },
    ) { innerPadding ->
        val commonParamsRuleTitle = stringResource(R.string.common_params_rule)
        val commonParamsRuleDesc = stringResource(R.string.common_params_rule_desc)
        val commonParamsRuleEnabled by viewModel.isCommonParamsRuleEnabled.collectAsState()
        val utmRuleTitle = stringResource(R.string.utm_rule)
        val utmRuleDesc = stringResource(R.string.utm_rule_desc)
        val utmRuleEnabled by viewModel.isUTMParamsRuleEnabled.collectAsState()
        val utmEnhancedTitle = stringResource(R.string.utm_enhanced_rule)
        val utmEnhancedDesc = stringResource(R.string.utm_enhanced_rule_desc)
        val utmEnhancedEnabled by viewModel.isUTMParamsEnhancedRuleEnabled.collectAsState()
        val commonRulesList = listOf(
            SwitchPreferenceItem(
                title = commonParamsRuleTitle,
                summary = commonParamsRuleDesc,
                checked = commonParamsRuleEnabled,
                onCheckedChange = { bool -> viewModel.setCommonParamsRuleEnabled(bool) }),
            SwitchPreferenceItem(
                title = utmRuleTitle,
                summary = utmRuleDesc,
                checked = utmRuleEnabled,
                onCheckedChange = { bool -> viewModel.setUTMParamsRuleEnabled(bool) }),
            SwitchPreferenceItem(
                title = utmEnhancedTitle,
                summary = utmEnhancedDesc,
                checked = utmEnhancedEnabled,
                onCheckedChange = { bool -> viewModel.setUTMParamsEnhancedRuleEnabled(bool) })
        )
        val bilibiliRuleTitle = stringResource(R.string.bilibili_rule)
        val bilibiliRuleDesc = stringResource(R.string.bilibili_rule_desc)
        val bilibiliRuleEnabled by viewModel.isBilibiliRuleEnabled.collectAsState()

        val exceptionalRulesList = listOf(
            SwitchPreferenceItem(
                title = bilibiliRuleTitle,
                summary = bilibiliRuleDesc,
                checked = bilibiliRuleEnabled,
                onCheckedChange = { bool -> viewModel.setBilibiliRuleEnabled(bool) })
        )
        LazyColumn(
            state = listState, modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateStartPadding(layoutDirection),
                    end = innerPadding.calculateEndPadding(layoutDirection),
                    top = innerPadding.calculateTopPadding(),
                )
        ) {
            item {
                PreferenceGroup(stringResource(R.string.common_rules_group)) {
                    commonRulesList.forEach { item ->
                        switch(
                            title = item.title,
                            summary = item.summary,
                            checked = item.checked,
                            onCheckedChange = item.onCheckedChange
                        )
                    }
                }
            }
            item {
                PreferenceGroup(stringResource(R.string.exceptional_rules_group)) {
                    exceptionalRulesList.forEach { item ->
                        switch(
                            title = item.title,
                            summary = item.summary,
                            checked = item.checked,
                            onCheckedChange = item.onCheckedChange
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

data class SwitchPreferenceItem(
    val title: String,
    val summary: String,
    val checked: Boolean,
    val onCheckedChange: (Boolean) -> Unit
)
