package com.vibescage.matrixcoderain.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibescage.matrixcoderain.R
import com.vibescage.matrixcoderain.data.MatrixPalette

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PaletteSelector(
    selectedPalette: MatrixPalette,
    onSelectPalette: (MatrixPalette) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.sec_palette_title),
            style = MaterialTheme.typography.titleMedium,
            color = Color(selectedPalette.primaryColor),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MatrixPalette.entries.forEach { palette ->
                val isSelected = palette == selectedPalette
                val paletteColor = Color(palette.primaryColor)

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) paletteColor.copy(alpha = 0.2f) else Color(0x22FFFFFF))
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) paletteColor else Color(0x44FFFFFF),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onSelectPalette(palette) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(paletteColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(palette.displayNameRes),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) Color.White else Color(0xCCFFFFFF),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
