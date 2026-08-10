package personal.limi.ui.components.oss

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

// --- M3 Expressive Constants ---
private val OuterCornerRadius = 24.dp // 外部大圆角
private val InnerCornerRadius = 8.dp // 内部微圆角 (连接处)
private val ItemSpacing = 6.dp // Item 之间的物理间隙
private val GroupHorizontalPadding = 16.dp
private val GroupVerticalPadding = 8.dp
private val TitleStartPadding = 16.dp
private val TitleBottomPadding = 8.dp

// 定义 Item 在组内的位置，用于决定形状
private enum class ItemPosition {
  Single,
  Top,
  Middle,
  Bottom,
}

// 根据位置获取 Shape
private fun getShapeForPosition(position: ItemPosition): Shape {
  return when (position) {
    ItemPosition.Single -> RoundedCornerShape(OuterCornerRadius)
    ItemPosition.Top ->
      RoundedCornerShape(
        topStart = OuterCornerRadius,
        topEnd = OuterCornerRadius,
        bottomStart = InnerCornerRadius,
        bottomEnd = InnerCornerRadius,
      )
    ItemPosition.Middle -> RoundedCornerShape(InnerCornerRadius)
    ItemPosition.Bottom ->
      RoundedCornerShape(
        topStart = InnerCornerRadius,
        topEnd = InnerCornerRadius,
        bottomStart = OuterCornerRadius,
        bottomEnd = OuterCornerRadius,
      )
  }
}

private fun positionFor(index: Int, size: Int): ItemPosition =
  when {
    size <= 1 -> ItemPosition.Single
    index == 0 -> ItemPosition.Top
    index == size - 1 -> ItemPosition.Bottom
    else -> ItemPosition.Middle
  }

/** 懒加载分组 DSL 作用域。 */
class OssLazyGroupScope {
  internal val items = mutableListOf<@Composable LazyItemScope.(Shape) -> Unit>()

  /** 添加一个分组项，[content] 的 shape 参数由分组位置自动计算。 */
  fun item(content: @Composable LazyItemScope.(Shape) -> Unit) {
    items.add(content)
  }
}

/**
 * 在 [LazyListScope] 上渲染一个 M3 Expressive 风格的分组。
 *
 * shape 计算、项间距、标题与分组水平内边距全部封装在内部，调用方只需提供 [content]。 每个分组项作为独立的 LazyColumn item，保持真正的懒加载。
 *
 * 注意：分组项的左右内边距由本扩展负担，调用方无需再额外加水平 padding。
 */
fun LazyListScope.ossLazyGroup(
  title: String? = null,
  content: OssLazyGroupScope.() -> Unit,
) {
  val scope = OssLazyGroupScope().apply(content)
  val size = scope.items.size
  if (size == 0) return

  // 分组顶部留白，与旧 Column 的 vertical padding 对齐
  item { Spacer(modifier = Modifier.height(GroupVerticalPadding)) }

  if (title != null) {
    item {
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier =
          Modifier.fillMaxWidth()
            .padding(
              start = GroupHorizontalPadding + TitleStartPadding,
              bottom = TitleBottomPadding,
            ),
      )
    }
  }

  scope.items.forEachIndexed { index, itemContent ->
    item {
      Box(modifier = Modifier.padding(horizontal = GroupHorizontalPadding)) {
        itemContent(getShapeForPosition(positionFor(index, size)))
      }
    }
    if (index < size - 1) {
      item { Spacer(modifier = Modifier.height(ItemSpacing)) }
    }
  }
  // 分组底部留白，与旧 Column 的 vertical padding 对齐
  item { Spacer(modifier = Modifier.height(GroupVerticalPadding)) }
}
