package com.example.controlgastos.ui.ingreso.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.controlgastos.ui.categoria.components.customIcons
import com.example.controlgastos.ui.categoria.components.getPainterByName
import com.example.controlgastos.ui.theme.AppColors

@Composable
fun IconSelectorIngreso(
    selectedIcon: String,
    onIconSelected: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        items(customIcons) { (name, _) ->
            val isSelected = name == selectedIcon

            Surface(
                shape = CircleShape,
                color = if (isSelected) AppColors.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable { onIconSelected(name) }
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = getPainterByName(name),
                        contentDescription = name,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

