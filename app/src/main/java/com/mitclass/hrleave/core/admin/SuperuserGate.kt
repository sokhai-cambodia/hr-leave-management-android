package com.mitclass.hrleave.core.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mitclass.hrleave.R

/**
 * Defense in depth for admin routes: the Profile tab's Admin section already hides these entries
 * for non-superusers (Task 13.2/13.7), but a direct navigation (e.g. a stale deep link) must
 * still be rejected here rather than rendering the CRUD screen.
 */
@Composable
fun SuperuserGate(isSuperuser: Boolean, content: @Composable () -> Unit) {
    if (isSuperuser) {
        content()
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.admin_superuser_gate_message),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
