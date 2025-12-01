package personal.limi.ui.share_panel

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import personal.limi.R
import personal.limi.utils.textCopyThenPost

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
@Preview
fun SharePanel(
    viewModel: SharePanelViewModel = viewModel{ SharePanelViewModel() }, onActivityClose: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val urlListScrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        showBottomSheet = true
    }

    LaunchedEffect(showBottomSheet) {
        if (!showBottomSheet) onActivityClose()
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            }, sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                )
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    onClick = { Unit },
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                            contentDescription = stringResource(R.string.edit_original_text),
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = stringResource(R.string.edit_original_text),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(start = 4.dp),
                        )
                    }
                }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                    onClick = { Unit },
                ) {
                    if (viewModel.isProcessing) Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.Center
                    ) { LoadingIndicator() } else Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    ) {
                        if (!viewModel.isProcessing && !viewModel.isEmpty) {
                            Text(
                                text = stringResource(R.string.processing_results),
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(bottom = 4.dp),
                            )
                            Text(
                                text = viewModel.processedText ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier,
                            )
                        } else if (viewModel.isEmpty) Text(
                            text = stringResource(R.string.input_string_is_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                if (!viewModel.isProcessing && viewModel.processedUrlList.isNotEmpty()) Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 12.dp)
                        .horizontalScroll(urlListScrollState),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.size(width = 8.dp, height = 0.dp))
                    if (viewModel.processedUrlList.size > 1) for (url in viewModel.processedUrlList) CompositionLocalProvider(
                        LocalMinimumInteractiveComponentSize provides 0.dp
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                            onClick = {
                                context.textCopyThenPost(url)
                            },
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ContentCopy,
                                    contentDescription = url,
                                    modifier = Modifier
                                        .padding(end = 4.dp)
                                        .size(ButtonDefaults.IconSize)
                                )
                                Text(
                                    text = url,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 4.dp),
                                )
                            }
                        }
                    } else if (viewModel.processedUrlList.size == 1) CompositionLocalProvider(
                        LocalMinimumInteractiveComponentSize provides 0.dp
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                            onClick = {
                                context.textCopyThenPost(viewModel.processedUrlList.first())
                            },
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ContentCopy,
                                    contentDescription = viewModel.processedUrlList.first(),
                                    modifier = Modifier
                                        .padding(end = 4.dp)
                                        .size(ButtonDefaults.IconSize)
                                )
                                Text(
                                    text = viewModel.processedUrlList.first(),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 4.dp),
                                )
                            }
                        }
                    }
                    Spacer(Modifier.size(width = 8.dp, height = 0.dp))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        onClick = { viewModel.shareText(context) },
                        enabled = isButtonEnabled(viewModel),
                        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = stringResource(R.string.share),
                            modifier = Modifier.size(
                                ButtonDefaults.IconSize
                            )
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(stringResource(R.string.share))
                    }
                    FilledTonalButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        onClick = { viewModel.copyText(context) },
                        enabled = isButtonEnabled(viewModel),
                        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = stringResource(R.string.copy),
                            modifier = Modifier.size(
                                ButtonDefaults.IconSize
                            )
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(stringResource(R.string.copy))
                    }
                }
            }
        }
    }
}

@Composable
private fun isButtonEnabled(viewModel: SharePanelViewModel): Boolean {
    return !viewModel.isEmpty && !viewModel.isProcessing && !viewModel.isNotHasUrls && !viewModel.isError
}