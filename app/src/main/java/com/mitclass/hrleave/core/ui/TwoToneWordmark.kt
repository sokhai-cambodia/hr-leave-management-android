package com.mitclass.hrleave.core.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.mitclass.hrleave.core.theme.BrandPrimary

/** The app's two-tone wordmark: "HR" bold primary + " Leave" bold default-color (Task 13.8). */
@Composable
fun TwoToneWordmark(modifier: Modifier = Modifier, fontSize: TextUnit = 34.sp) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = BrandPrimary, fontWeight = FontWeight.Bold)) { append("HR") }
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(" Leave") }
        },
        fontSize = fontSize,
        modifier = modifier,
    )
}
