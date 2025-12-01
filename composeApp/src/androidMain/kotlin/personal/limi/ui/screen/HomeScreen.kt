package personal.limi.ui.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import personal.limi.R

@Composable
@Preview
fun HomeScreen(titleResId: Int = R.string.home) {
    Text(text = stringResource(titleResId))
}