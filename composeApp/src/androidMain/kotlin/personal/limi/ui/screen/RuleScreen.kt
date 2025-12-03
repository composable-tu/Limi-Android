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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import personal.limi.R
import personal.limi.ui.components.preference.PreferenceGroup
import personal.limi.ui.components.preference.switch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun RuleScreen(titleResId: Int = R.string.rule) {
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
                PreferenceGroup("通用规则") {
                    for (i in 1..4) switch(
                        title = "通用规则 $i",
                        summary = "该规则将文本文本文本文本文本文本文本文本文本文本文本文本",
                        checked = i % 2 == 0,
                        onCheckedChange = {})
                }
                PreferenceGroup("特定规则") {
                    for (i in 1..3) switch(
                        title = "Example 规则 $i",
                        summary = "该规则将作用于特定文本文本文本文本文本文本文本文本文本文本文本文本",
                        checked = i % 2 != 0,
                        onCheckedChange = {})
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}