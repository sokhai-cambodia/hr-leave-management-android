package com.mitclass.hrleave.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mitclass.hrleave.R

// STYLE_GUIDE.md: "Poppins — use the actual bundled/downloadable Poppins font family, not a
// system font that merely looks similar." Bundled as res/font/*.ttf (no network/Play Services
// dependency needed at runtime).
private val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold),
)

private val MaterialDefaults = Typography()

// STYLE_GUIDE.md typography rules: app bar title bold, buttons/labels semibold, body regular.
// Keeps Material3's default sizes/line-heights, swaps in Poppins at the prescribed weights.
val HrLeaveTypography = Typography(
    displayLarge = MaterialDefaults.displayLarge.copy(fontFamily = Poppins, fontWeight = FontWeight.Bold),
    displayMedium = MaterialDefaults.displayMedium.copy(fontFamily = Poppins, fontWeight = FontWeight.Bold),
    displaySmall = MaterialDefaults.displaySmall.copy(fontFamily = Poppins, fontWeight = FontWeight.Bold),
    headlineLarge = MaterialDefaults.headlineLarge.copy(fontFamily = Poppins, fontWeight = FontWeight.Bold),
    headlineMedium = MaterialDefaults.headlineMedium.copy(fontFamily = Poppins, fontWeight = FontWeight.Bold),
    headlineSmall = MaterialDefaults.headlineSmall.copy(fontFamily = Poppins, fontWeight = FontWeight.Bold),
    titleLarge = MaterialDefaults.titleLarge.copy(fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 22.sp),
    titleMedium = MaterialDefaults.titleMedium.copy(fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    titleSmall = MaterialDefaults.titleSmall.copy(fontFamily = Poppins, fontWeight = FontWeight.SemiBold),
    bodyLarge = MaterialDefaults.bodyLarge.copy(fontFamily = Poppins, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = MaterialDefaults.bodyMedium.copy(fontFamily = Poppins, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall = MaterialDefaults.bodySmall.copy(fontFamily = Poppins, fontWeight = FontWeight.Normal),
    labelLarge = MaterialDefaults.labelLarge.copy(fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
    labelMedium = MaterialDefaults.labelMedium.copy(fontFamily = Poppins, fontWeight = FontWeight.SemiBold),
    labelSmall = MaterialDefaults.labelSmall.copy(fontFamily = Poppins, fontWeight = FontWeight.SemiBold),
)
