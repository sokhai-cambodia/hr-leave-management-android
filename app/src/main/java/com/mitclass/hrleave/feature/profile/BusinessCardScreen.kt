package com.mitclass.hrleave.feature.profile

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.theme.BrandPrimary
import com.mitclass.hrleave.core.theme.BrandPrimaryDark
import com.mitclass.hrleave.core.theme.CardCornerRadius
import com.mitclass.hrleave.core.theme.CardElevation
import com.mitclass.hrleave.core.ui.AppButton
import com.mitclass.hrleave.core.ui.AppOutlinedButton
import com.mitclass.hrleave.core.ui.encodeQrCodeBitmap
import com.mitclass.hrleave.data.remote.dto.UserDto
import java.io.File
import java.io.FileOutputStream

private const val QR_SIZE_PX = 720

@Composable
fun BusinessCardScreen(user: UserDto) {
    val context = LocalContext.current
    val displayName = user.fullName?.takeIf { it.isNotBlank() } ?: user.email
    val teamName = user.team?.name ?: "No team assigned"
    val qrContent = remember(user) { buildVCard(displayName, user.email, teamName) }
    val qrBitmap = remember(qrContent) { encodeQrCodeBitmap(qrContent, QR_SIZE_PX) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppSpacing.lg),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CardCornerRadius),
            elevation = CardDefaults.cardElevation(defaultElevation = CardElevation),
        ) {
            Column {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(BrandPrimary, BrandPrimaryDark)))
                        .padding(vertical = AppSpacing.xl),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(color = Color.White, shape = CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = initials(displayName),
                            style = MaterialTheme.typography.headlineSmall,
                            color = BrandPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(Modifier.height(AppSpacing.md))
                    Text(text = displayName, style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(text = teamName, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppSpacing.xl),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "Business card QR code",
                        modifier = Modifier.size(200.dp),
                    )
                    Spacer(Modifier.height(AppSpacing.md))
                    Text(
                        text = "Scan to save contact",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        Spacer(Modifier.height(AppSpacing.lg))
        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
            AppOutlinedButton(
                text = "Save",
                onClick = { saveQrToGallery(context, qrBitmap, displayName) },
                icon = { Icon(Icons.Outlined.Download, contentDescription = null) },
                modifier = Modifier.weight(1f),
            )
            AppButton(
                text = "Share",
                onClick = { shareQrBitmap(context, qrBitmap) },
                icon = { Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = null) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private fun initials(name: String): String {
    val parts = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    return when {
        parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> "?"
    }
}

private fun buildVCard(name: String, email: String, team: String): String =
    "BEGIN:VCARD\nVERSION:3.0\nFN:$name\nEMAIL:$email\nORG:$team\nEND:VCARD"

private fun saveQrToGallery(context: Context, bitmap: Bitmap, displayName: String) {
    val fileName = "HR Leave - $displayName"
    val saved = runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/HR Leave")
            }
            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: error("MediaStore insert failed")
            val out = context.contentResolver.openOutputStream(uri) ?: error("Couldn't open output stream")
            out.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        } else {
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File(dir, "$fileName.png")
            FileOutputStream(file).use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
        }
    }.isSuccess

    Toast.makeText(
        context,
        if (saved) "Saved to Pictures" else "Couldn't save the QR code",
        Toast.LENGTH_SHORT,
    ).show()
}

private fun shareQrBitmap(context: Context, bitmap: Bitmap) {
    val imagesDir = File(context.cacheDir, "images").apply { mkdirs() }
    val file = File(imagesDir, "business_card_qr.png")
    FileOutputStream(file).use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share business card"))
}
