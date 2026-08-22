package com.vibescage.matrixcoderain.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun MessageInjectorSection(
    palette: MatrixPalette,
    onInjectMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val themeColor = Color(palette.primaryColor)

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.sec_message_title),
            style = MaterialTheme.typography.titleMedium,
            color = themeColor,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        text = stringResource(R.string.msg_placeholder),
                        color = Color(0x66FFFFFF),
                        fontSize = 12.sp
                    )
                },
                singleLine = true,
                maxLines = 1,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = themeColor,
                    unfocusedBorderColor = Color(0x44FFFFFF),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = themeColor
                ),
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onInjectMessage(inputText)
                        inputText = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = themeColor,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.btn_send),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Frases predefinidas tipo Easter Egg
        val quotes = listOf(
            stringResource(R.string.chip_wake) to "Wake up, Neo...",
            stringResource(R.string.chip_rabbit) to "Follow the white rabbit.",
            stringResource(R.string.chip_spoon) to "There is no spoon.",
            stringResource(R.string.chip_alert) to "SYSTEM BREACH DETECTED"
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            quotes.forEach { (label, rawText) ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x22FFFFFF))
                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(6.dp))
                        .clickable { onInjectMessage(rawText) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
