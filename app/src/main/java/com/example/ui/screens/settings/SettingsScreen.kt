package com.example.ui.screens.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.engine.BackupValidationResult
import com.example.ui.components.IceButton
import com.example.ui.components.IceOutlineButton
import com.example.ui.components.LuxuryCard
import com.example.ui.components.PersianTopBar
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CautionYellow
import com.example.ui.theme.FrostWhite
import com.example.ui.theme.GlassCardBorder
import com.example.ui.theme.GlassCardBorderSubtle
import com.example.ui.theme.IceBlueContainer
import com.example.ui.theme.IceBlueLight
import com.example.ui.theme.IceBluePrimary
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.Obsidian700
import com.example.ui.theme.Obsidian900
import com.example.ui.theme.Obsidian950
import com.example.ui.theme.SafeGreen
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    reduceMotionEnabled: Boolean,
    onToggleReduceMotion: (Boolean) -> Unit,
    onExportBackup: suspend () -> String,
    onValidateBackup: (String) -> BackupValidationResult,
    onRestoreBackup: suspend (String) -> Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var exportedJson by remember { mutableStateOf<String?>(null) }
    var restoreInputJson by remember { mutableStateOf("") }
    var restoreValidationResult by remember { mutableStateOf<BackupValidationResult?>(null) }
    var showRestorePreviewDialog by remember { mutableStateOf(false) }
    var restoreStatusText by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Obsidian950)
            .padding(bottom = 80.dp)
    ) {
        PersianTopBar(
            title = "تنظیمات و پشتیبان‌گیری",
            subtitle = "مدیریت داده‌ها، روان‌سازی و حریم خصوصی",
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Backup & Restore Section
            Text(
                text = "پشتیبان‌گیری و بازیابی داده‌ها (Backup / Restore)",
                style = MaterialTheme.typography.titleMedium,
                color = FrostWhite,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            LuxuryCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "خروجی کامل تمام پروفایل‌ها، جلسات تمرینی، ست‌ها، رکوردهای شخصی، مهارت‌ها و وضعیت ریکاوری با فرمت استاندارد kalisten-backup-v1.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedSlate
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        IceButton(
                            text = "تولید فایل پشتیبان",
                            onClick = {
                                coroutineScope.launch {
                                    val json = onExportBackup()
                                    exportedJson = json
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Kalisten Backup", json)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "پشتیبان در حافظه کپی شد", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_backup_btn")
                        )
                    }

                    if (exportedJson != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Obsidian900)
                                .border(1.dp, SafeGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "نسخه پشتیبان آماده شد و در کلیپ‌بورد کپی گردید. (حجم: ${exportedJson?.length} کاراکتر)",
                                style = MaterialTheme.typography.labelSmall,
                                color = SafeGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "بازیابی اطلاعات از فایل پشتیبان:",
                        style = MaterialTheme.typography.titleSmall,
                        color = FrostWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = restoreInputJson,
                        onValueChange = {
                            restoreInputJson = it
                            restoreValidationResult = null
                            restoreStatusText = null
                        },
                        placeholder = { Text("متن پشتیبان JSON را اینجا جای‌گذاری کنید...", color = MutedSlate) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .testTag("restore_json_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = FrostWhite,
                            unfocusedTextColor = FrostWhite,
                            focusedBorderColor = IceBluePrimary,
                            unfocusedBorderColor = Obsidian700
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    IceOutlineButton(
                        text = "اعتبارسنجی و پیش‌نمایش بازیابی",
                        onClick = {
                            val result = onValidateBackup(restoreInputJson)
                            restoreValidationResult = result
                            if (result.isValid) {
                                showRestorePreviewDialog = true
                            } else {
                                restoreStatusText = result.errorMessage
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("validate_restore_btn")
                    )

                    if (restoreStatusText != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = restoreStatusText ?: "",
                            style = MaterialTheme.typography.labelSmall,
                            color = AlertRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Performance & Motion Section
            Text(
                text = "عملکرد و بهینه‌سازی بصری",
                style = MaterialTheme.typography.titleMedium,
                color = FrostWhite,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            LuxuryCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "کاهش جلوه‌های حرکتی (Reduce Motion)",
                            style = MaterialTheme.typography.titleMedium,
                            color = FrostWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "غیرفعال کردن پارالکس و ترنزیشن‌های انیمیشنی جهت مصرف کمتر باتری و سازگاری با دستگاه‌های ضعیف‌تر.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSlate
                        )
                    }

                    Switch(
                        checked = reduceMotionEnabled,
                        onCheckedChange = onToggleReduceMotion,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Obsidian950,
                            checkedTrackColor = IceBluePrimary
                        ),
                        modifier = Modifier.testTag("reduce_motion_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Offline & Architecture Architecture Card
            LuxuryCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Obsidian900
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = IceBluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "معماری آفلاین و استقلال داده‌ها",
                            style = MaterialTheme.typography.titleMedium,
                            color = FrostWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• پایگاه داده کاملاً محلی Room در دستگاه شما\n• عدم نیاز به هوش مصنوعی سرورمحور یا اتصال اینترنت برای محاسبه بار تمرین\n• موتور اضافه‌بار تدریجی و تفکیک پلاتو بر پایه متغیرهای بیومکانیکی قطعی\n• حفظ کامل تاریخچه نسخه‌ها با قابلیت Rollback",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedSlate,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }

    // Restore Preview & Confirmation Dialog
    if (showRestorePreviewDialog && restoreValidationResult != null) {
        val result = restoreValidationResult!!
        AlertDialog(
            onDismissRequest = { showRestorePreviewDialog = false },
            title = {
                Text(
                    text = "پیش‌نمایش و تایید بازیابی",
                    style = MaterialTheme.typography.titleLarge,
                    color = FrostWhite,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "فایل پشتیبان با موفقیت اعتبارسنجی شد:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FrostWhite
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = result.previewSummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = IceBlueLight
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "آیا مایل به درج و تلفیق داده‌های این پشتیبان در پایگاه داده هستید؟",
                        style = MaterialTheme.typography.bodySmall,
                        color = CautionYellow
                    )
                }
            },
            confirmButton = {
                IceButton(
                    text = "تایید و بازیابی اطلاعات",
                    onClick = {
                        coroutineScope.launch {
                            val success = onRestoreBackup(restoreInputJson)
                            if (success) {
                                restoreStatusText = "بازیابی اطلاعات با موفقیت انجام شد."
                                Toast.makeText(context, "اطلاعات بازیابی شد", Toast.LENGTH_SHORT).show()
                                showRestorePreviewDialog = false
                            } else {
                                restoreStatusText = "خطا در فرآیند بازیابی داده‌ها."
                            }
                        }
                    },
                    modifier = Modifier.padding(horizontal = 4.dp),
                    testTag = "confirm_restore_btn"
                )
            },
            dismissButton = {
                TextButton(onClick = { showRestorePreviewDialog = false }) {
                    Text("انصراف", color = MutedSlate)
                }
            },
            containerColor = Obsidian900
        )
    }
}
