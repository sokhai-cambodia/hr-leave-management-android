package com.mitclass.hrleave.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// SPEC.md §7 calls for Poppins; using the system default family until a bundled/downloadable
// Poppins font is wired in (tracked as a Phase 12 polish item, not a functional blocker).
private val HrLeaveFontFamily = FontFamily.Default

val HrLeaveTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = HrLeaveFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = HrLeaveFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = HrLeaveFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = HrLeaveFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = HrLeaveFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
    ),
)
