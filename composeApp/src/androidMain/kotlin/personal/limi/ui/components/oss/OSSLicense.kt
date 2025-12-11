package personal.limi.ui.components.oss

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Developer
import com.mikepenz.aboutlibraries.entity.Funding
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.entity.Organization
import com.mikepenz.aboutlibraries.entity.Scm
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import personal.limi.utils.openUrl

@Composable
fun BaseLicenseCell(
    shape: Shape,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Surface(
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier.fillMaxWidth(),
        onClick = { if (enabled) onClick?.invoke() },
        enabled = (onClick != null) && enabled
    ) {
        Column(modifier = Modifier.padding(16.dp)) { content() }
    }
}

/**
 * 开源许可证项，使用PreferenceGroup风格
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LicenseItem(
    shape: Shape, library: Library, onClick: () -> Unit
) {
    val context = LocalContext.current
    val (uniqueId: String, artifactVersion: String?, name: String, description: String?, _: String?, developers: ImmutableList<Developer>, _: Organization?, _: Scm?, licenses: ImmutableSet<License>, _: ImmutableSet<Funding>, _: String?) = library
    BaseLicenseCell(shape = shape, onClick = onClick) {
        // 库名称
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        // 包名
        Text(
            text = uniqueId,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // 作者信息
        if (developers.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                developers.forEachIndexed { index, developer ->
                    if (!developer.organisationUrl.isNullOrBlank() && !developer.name.isNullOrBlank()) Text(
                        text = developer.name ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textDecoration = TextDecoration.Underline
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                            onClick = {
                                context.openUrl(developer.organisationUrl!!)
                            })
                    ) else Text(
                        text = developer.name ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (index < developers.size - 1) Text(
                        text = ", ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // 描述信息
        if (!description.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 版本和许可证信息
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            // 版本信息
            if (!artifactVersion.isNullOrEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ), shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (artifactVersion.firstOrNull()
                                ?.isDigit() == true
                        ) "v$artifactVersion" else artifactVersion,
                        modifier = Modifier.padding(8.dp, 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 许可证信息
            licenses.forEach { license ->
                Card(
                    colors = CardDefaults.cardColors(
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ), shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp, 3.dp),
                        text = license.name,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

data class DeveloperInfo(
    val name: String, val url: String?
)

data class LicenseInfo(
    val name: String, val url: String?
)

