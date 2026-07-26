package com.mitclass.hrleave.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mitclass.hrleave.R

/** Placeholder for a nav destination whose real screen lands in a later phase. */
@Composable
fun ComingSoonScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = stringResource(R.string.coming_soon_message, title), style = MaterialTheme.typography.bodyLarge)
    }
}
