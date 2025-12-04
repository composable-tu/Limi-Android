package personal.limi.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AllInclusive
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.darkokoa.pangu.Pangu
import personal.limi.R
import personal.limi.ui.MainViewModel
import personal.limi.ui.components.preference.PreferenceGroup
import personal.limi.ui.components.preference.navigation
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
@Preview
fun HomeScreen(viewModel: MainViewModel = viewModel { MainViewModel() }) {
    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val density = LocalDensity.current
    val fabVisible by remember {
        derivedStateOf {
            listState.firstVisibleItemScrollOffset <= with(density) { 120.dp.toPx() }.toInt()
        }
    }
    val focusRequester = FocusRequester()
    val context = LocalContext.current
    val layoutDirection = LocalLayoutDirection.current
    val historyList by viewModel.historyListStateFlow.collectAsState()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateStartPadding(layoutDirection),
                    end = innerPadding.calculateEndPadding(layoutDirection),
                    top = innerPadding.calculateTopPadding(),
                )
        ) {
            LazyColumn(
                state = listState, modifier = Modifier.fillMaxSize()
            ) {
                item {
                    // 按日期分组显示历史记录
                    val groupedHistory = historyList.groupBy {
                        // 提取日期部分 (YYYY-MM-DD)
                        it.datetime.substringBefore("T")
                    }

                    val context = LocalContext.current
                    val locale = context.resources.configuration.locales[0]

                    groupedHistory.forEach { (date, histories) ->
                        val localDate = LocalDate.parse(date)
                        val formatter =
                            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
                        val formattedDate = Pangu.spacingText(localDate.format(formatter))

                        PreferenceGroup(title = formattedDate) {
                            histories.forEach { history ->
                                val timePart =
                                    history.datetime.substringAfter("T").substringBeforeLast(".")
                                navigation(
                                    title = timePart,
                                    summary = history.processedUrl,
                                    onClick = { /* TODO: 处理点击事件 */ })
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                }
            }

            val fromText = stringResource(R.string.from_text)
            val scanQRCode = stringResource(R.string.scan_qrcode)

            val items = listOf(
                Icons.Outlined.DriveFileRenameOutline to fromText,
                Icons.Outlined.QrCodeScanner to scanQRCode,
                Icons.Outlined.AllInclusive to "TODO......"
            )

            var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }

            BackHandler(fabMenuExpanded) { fabMenuExpanded = false }

            FloatingActionButtonMenu(
                modifier = Modifier.align(Alignment.BottomEnd),
                expanded = fabMenuExpanded,
                button = {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                            if (fabMenuExpanded) TooltipAnchorPosition.Start else TooltipAnchorPosition.Above
                        ),
                        tooltip = { PlainTooltip { Text("Toggle menu") } },
                        state = rememberTooltipState(),
                    ) {
                        ToggleFloatingActionButton(
                            modifier = Modifier
                                .semantics {
                                    traversalIndex = -1f
                                    stateDescription =
                                        if (fabMenuExpanded) "Expanded" else "Collapsed"
                                    contentDescription = "Toggle menu"
                                }
                                .animateFloatingActionButton(
                                    visible = fabVisible || fabMenuExpanded,
                                    alignment = Alignment.BottomEnd,
                                )
                                .focusRequester(focusRequester),
                            checked = fabMenuExpanded,
                            onCheckedChange = { fabMenuExpanded = !fabMenuExpanded },
                        ) {
                            val imageVector by remember {
                                derivedStateOf {
                                    if (checkedProgress > 0.5f) Icons.Filled.Close else Icons.Filled.Add
                                }
                            }
                            Icon(
                                painter = rememberVectorPainter(imageVector),
                                contentDescription = null,
                                modifier = Modifier.animateIcon({ checkedProgress }),
                            )
                        }
                    }
                },
            ) {
                items.forEachIndexed { i, item ->
                    FloatingActionButtonMenuItem(
                        modifier = Modifier
                            .semantics {
                                isTraversalGroup = true
                                if (i == items.size - 1) customActions = listOf(
                                    CustomAccessibilityAction(
                                        label = "Close menu",
                                        action = {
                                            fabMenuExpanded = false
                                            true
                                        },
                                    )
                                )
                            }
                            .then(if (i == 0) Modifier.onKeyEvent {
                                if (it.type == KeyEventType.KeyDown && (it.key == Key.DirectionUp || (it.isShiftPressed && it.key == Key.Tab))) {
                                    focusRequester.requestFocus()
                                    return@onKeyEvent true
                                }
                                return@onKeyEvent false
                            } else Modifier),
                        onClick = {
                            fabMenuExpanded = false
                            when (item.second) {
                                fromText -> viewModel.startSharePanel(context)
                                scanQRCode -> viewModel.startScanQRCode(context)
                            }
                        },
                        icon = { Icon(item.first, contentDescription = null) },
                        text = { Text(text = item.second) },
                    )
                }
            }
        }
    }
}