//package com.example.project_graduation.presentation.edit
//
//import android.net.Uri
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.fadeOut
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.text.input.VisualTransformation
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import coil.compose.AsyncImage
//import com.example.project_graduation.data.remote.ApiConfig
//import com.example.project_graduation.presentation.profile.EditProfileUiState
//import com.example.project_graduation.presentation.profile.ProfileViewModel
//
//// ─────────────────────────────────────────────
//// Data class — điều chỉnh cho khớp với User model thực tế
//// ─────────────────────────────────────────────
//
//
//// ─────────────────────────────────────────────
//// Main Screen
//// ─────────────────────────────────────────────
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun EditProfileScreen(
//    viewModel: ProfileViewModel,          // dùng ProfileViewModel có sẵn
//    onNavigateBack: () -> Unit
//) {
//    // TODO: thay bằng uiState thực tế từ ProfileViewModel
//    // val uiState by viewModel.editProfileUiState.collectAsState()
//    val uiState = remember { mutableStateOf(EditProfileUiState()) }
//
//    var fullName     by remember { mutableStateOf(uiState.value.username) }
//    var email        by remember { mutableStateOf(uiState.value.email) }
//    var phone        by remember { mutableStateOf(uiState.value.phone) }
//    var avatarUri    by remember { mutableStateOf<Uri?>(null) }
//
//    // Password fields
//    var currentPassword    by remember { mutableStateOf("") }
//    var newPassword        by remember { mutableStateOf("") }
//    var confirmPassword    by remember { mutableStateOf("") }
//    var showCurrentPw      by remember { mutableStateOf(false) }
//    var showNewPw          by remember { mutableStateOf(false) }
//    var showConfirmPw      by remember { mutableStateOf(false) }
//    var changePasswordMode by remember { mutableStateOf(false) }
//
//    // Validation errors
//    var fullNameError       by remember { mutableStateOf<String?>(null) }
//    var emailError          by remember { mutableStateOf<String?>(null) }
//    var phoneError          by remember { mutableStateOf<String?>(null) }
//    var currentPasswordError by remember { mutableStateOf<String?>(null) }
//    var newPasswordError    by remember { mutableStateOf<String?>(null) }
//    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
//
//    val scrollState = rememberScrollState()
//    val snackbarHostState = remember { SnackbarHostState() }
//
//    // Image picker
//    val imagePickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        uri?.let { avatarUri = it }
//    }
//
//    // Snackbar khi có success/error
//    LaunchedEffect(uiState.value.successMessage) {
//        uiState.value.successMessage?.let {
//            snackbarHostState.showSnackbar(it)
//        }
//    }
//    LaunchedEffect(uiState.value.errorMessage) {
//        uiState.value.errorMessage?.let {
//            snackbarHostState.showSnackbar(it)
//        }
//    }
//
//    fun validate(): Boolean {
//        var valid = true
//        fullNameError = if (fullName.isBlank()) "Full name is required" else null
//        emailError = when {
//            email.isBlank() -> "Email is required"
//            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
//            else -> null
//        }
//        phoneError = when {
//            phone.isBlank() -> null // phone optional
//            phone.length < 9 -> "Phone number too short"
//            else -> null
//        }
//        if (changePasswordMode) {
//            currentPasswordError = if (currentPassword.isBlank()) "Current password is required" else null
//            newPasswordError = when {
//                newPassword.isBlank() -> "New password is required"
//                newPassword.length < 6 -> "Minimum 6 characters"
//                else -> null
//            }
//            confirmPasswordError = when {
//                confirmPassword.isBlank() -> "Please confirm your password"
//                confirmPassword != newPassword -> "Passwords do not match"
//                else -> null
//            }
//            if (currentPasswordError != null || newPasswordError != null || confirmPasswordError != null) valid = false
//        }
//        if (fullNameError != null || emailError != null || phoneError != null) valid = false
//        return valid
//    }
//
//    Scaffold(
//        snackbarHost = { SnackbarHost(snackbarHostState) },
//        containerColor = Color(0xFFF8F9FA),
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        "Edit Profile",
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 20.sp
//                    )
//                },
//                navigationIcon = {
//                    IconButton(onClick = onNavigateBack) {
//                        Icon(
//                            Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "Back"
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color.White,
//                    titleContentColor = Color.Black,
//                    navigationIconContentColor = Color.Black
//                )
//            )
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .verticalScroll(scrollState)
//        ) {
//            // ── Avatar Section ──────────────────────────────
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(Color.White)
//                    .padding(vertical = 28.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Box(contentAlignment = Alignment.BottomEnd) {
//                        // Avatar
//                        Box(
//                            modifier = Modifier
//                                .size(100.dp)
//                                .clip(CircleShape)
//                                .border(3.dp, Color(0xFF2196F3), CircleShape)
//                                .background(Color(0xFFE3F2FD)),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            if (avatarUri != null) {
//                                AsyncImage(
//                                    model = avatarUri,
//                                    contentDescription = "Avatar",
//                                    modifier = Modifier.fillMaxSize(),
//                                    contentScale = ContentScale.Crop
//                                )
//                            }
////                            else if (uiState.value.avatarUrl != null) {
////                                AsyncImage(
////                                    model = "${ApiConfig.BASE_URL}${uiState.value.avatarUrl}",
////                                    contentDescription = "Avatar",
////                                    modifier = Modifier.fillMaxSize(),
////                                    contentScale = ContentScale.Crop
////                                )
////                            }
//                            else {
//                                Icon(
//                                    imageVector = Icons.Default.Person,
//                                    contentDescription = null,
//                                    modifier = Modifier.size(56.dp),
//                                    tint = Color(0xFF2196F3)
//                                )
//                            }
//                        }
//
//                        // Camera button
//                        Box(
//                            modifier = Modifier
//                                .size(32.dp)
//                                .clip(CircleShape)
//                                .background(Color(0xFF2196F3))
//                                .clickable { imagePickerLauncher.launch("image/*") },
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.CameraAlt,
//                                contentDescription = "Change photo",
//                                tint = Color.White,
//                                modifier = Modifier.size(18.dp)
//                            )
//                        }
//                    }
//
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text(
//                        text = "Tap to change photo",
//                        fontSize = 12.sp,
//                        color = Color.Gray
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // ── Personal Info Section ───────────────────────
//            SectionCard(title = "Personal Information") {
//                ProfileTextField(
//                    value = fullName,
//                    onValueChange = {
//                        fullName = it
//                        fullNameError = null
//                    },
//                    label = "Full Name",
//                    icon = Icons.Default.Person,
//                    errorMessage = fullNameError
//                )
//
//                Spacer(modifier = Modifier.height(14.dp))
//
//                ProfileTextField(
//                    value = email,
//                    onValueChange = {
//                        email = it
//                        emailError = null
//                    },
//                    label = "Email",
//                    icon = Icons.Default.Email,
//                    errorMessage = emailError
//                )
//
//                Spacer(modifier = Modifier.height(14.dp))
//
//                ProfileTextField(
//                    value = phone,
//                    onValueChange = {
//                        phone = it
//                        phoneError = null
//                    },
//                    label = "Phone Number",
//                    icon = Icons.Default.Phone,
//                    errorMessage = phoneError
//                )
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // ── Change Password Section ─────────────────────
//            SectionCard(title = "Security") {
//                // Toggle row
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable { changePasswordMode = !changePasswordMode }
//                        .padding(vertical = 4.dp),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Icon(
//                            imageVector = Icons.Default.Lock,
//                            contentDescription = null,
//                            tint = Color(0xFF2196F3),
//                            modifier = Modifier.size(20.dp)
//                        )
//                        Spacer(modifier = Modifier.width(12.dp))
//                        Column {
//                            Text(
//                                "Change Password",
//                                fontSize = 15.sp,
//                                fontWeight = FontWeight.Medium,
//                                color = Color.Black
//                            )
//                            Text(
//                                if (changePasswordMode) "Tap to cancel" else "Tap to update your password",
//                                fontSize = 12.sp,
//                                color = Color.Gray
//                            )
//                        }
//                    }
//                    Switch(
//                        checked = changePasswordMode,
//                        onCheckedChange = {
//                            changePasswordMode = it
//                            if (!it) {
//                                currentPassword = ""
//                                newPassword = ""
//                                confirmPassword = ""
//                                currentPasswordError = null
//                                newPasswordError = null
//                                confirmPasswordError = null
//                            }
//                        },
//                        colors = SwitchDefaults.colors(
//                            checkedThumbColor = Color.White,
//                            checkedTrackColor = Color(0xFF2196F3)
//                        )
//                    )
//                }
//
//                AnimatedVisibility(
//                    visible = changePasswordMode,
//                    enter = fadeIn(),
//                    exit = fadeOut()
//                ) {
//                    Column {
//                        Spacer(modifier = Modifier.height(16.dp))
//
//                        HorizontalDivider(color = Color(0xFFF0F0F0))
//
//                        Spacer(modifier = Modifier.height(16.dp))
//
//                        // Current Password
//                        PasswordTextField(
//                            value = currentPassword,
//                            onValueChange = {
//                                currentPassword = it
//                                currentPasswordError = null
//                            },
//                            label = "Current Password",
//                            showPassword = showCurrentPw,
//                            onToggleShow = { showCurrentPw = !showCurrentPw },
//                            errorMessage = currentPasswordError
//                        )
//
//                        Spacer(modifier = Modifier.height(14.dp))
//
//                        // New Password
//                        PasswordTextField(
//                            value = newPassword,
//                            onValueChange = {
//                                newPassword = it
//                                newPasswordError = null
//                            },
//                            label = "New Password",
//                            showPassword = showNewPw,
//                            onToggleShow = { showNewPw = !showNewPw },
//                            errorMessage = newPasswordError
//                        )
//
//                        // Password strength indicator
//                        if (newPassword.isNotEmpty()) {
//                            Spacer(modifier = Modifier.height(6.dp))
//                            PasswordStrengthBar(password = newPassword)
//                        }
//
//                        Spacer(modifier = Modifier.height(14.dp))
//
//                        // Confirm Password
//                        PasswordTextField(
//                            value = confirmPassword,
//                            onValueChange = {
//                                confirmPassword = it
//                                confirmPasswordError = null
//                            },
//                            label = "Confirm New Password",
//                            showPassword = showConfirmPw,
//                            onToggleShow = { showConfirmPw = !showConfirmPw },
//                            errorMessage = confirmPasswordError,
//                            trailingValidation = confirmPassword.isNotEmpty() && confirmPassword == newPassword
//                        )
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(28.dp))
//
//            // ── Save Button ─────────────────────────────────
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 20.dp)
//            ) {
//                Button(
//                    onClick = {
//                        if (validate()) {
//                            // TODO: gọi viewModel.updateProfile(...)
//                            // viewModel.updateProfile(
//                            //     fullName = fullName,
//                            //     email = email,
//                            //     phone = phone,
//                            //     avatarUri = avatarUri,
//                            //     currentPassword = if (changePasswordMode) currentPassword else null,
//                            //     newPassword = if (changePasswordMode) newPassword else null
//                            // )
//                        }
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(52.dp),
//                    shape = RoundedCornerShape(14.dp),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color(0xFF2196F3)
//                    ),
//                    enabled = !uiState.value.isSaving
//                ) {
//                    if (uiState.value.isSaving) {
//                        CircularProgressIndicator(
//                            modifier = Modifier.size(22.dp),
//                            color = Color.White,
//                            strokeWidth = 2.dp
//                        )
//                    } else {
//                        Icon(
//                            Icons.Default.Save,
//                            contentDescription = null,
//                            modifier = Modifier.size(20.dp)
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text(
//                            "Save Changes",
//                            fontSize = 16.sp,
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(32.dp))
//        }
//    }
//}
//
//// ─────────────────────────────────────────────
//// Reusable Components
//// ─────────────────────────────────────────────
//
//@Composable
//private fun SectionCard(
//    title: String,
//    content: @Composable ColumnScope.() -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp),
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        elevation = CardDefaults.cardElevation(2.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(20.dp)
//        ) {
//            Text(
//                text = title,
//                fontSize = 14.sp,
//                fontWeight = FontWeight.SemiBold,
//                color = Color(0xFF2196F3),
//                modifier = Modifier.padding(bottom = 16.dp)
//            )
//            content()
//        }
//    }
//}
//
//@Composable
//private fun ProfileTextField(
//    value: String,
//    onValueChange: (String) -> Unit,
//    label: String,
//    icon: ImageVector,
//    errorMessage: String? = null
//) {
//    Column {
//        OutlinedTextField(
//            value = value,
//            onValueChange = onValueChange,
//            label = { Text(label, fontSize = 14.sp) },
//            leadingIcon = {
//                Icon(
//                    imageVector = icon,
//                    contentDescription = null,
//                    tint = if (errorMessage != null) Color(0xFFE53935) else Color(0xFF2196F3),
//                    modifier = Modifier.size(20.dp)
//                )
//            },
//            isError = errorMessage != null,
//            modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(12.dp),
//            singleLine = true,
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedBorderColor = Color(0xFF2196F3),
//                unfocusedBorderColor = Color(0xFFE0E0E0),
//                errorBorderColor = Color(0xFFE53935),
//                focusedLabelColor = Color(0xFF2196F3),
//                cursorColor = Color(0xFF2196F3)
//            )
//        )
//        if (errorMessage != null) {
//            Text(
//                text = errorMessage,
//                color = Color(0xFFE53935),
//                fontSize = 11.sp,
//                modifier = Modifier.padding(start = 14.dp, top = 4.dp)
//            )
//        }
//    }
//}
//
//@Composable
//private fun PasswordTextField(
//    value: String,
//    onValueChange: (String) -> Unit,
//    label: String,
//    showPassword: Boolean,
//    onToggleShow: () -> Unit,
//    errorMessage: String? = null,
//    trailingValidation: Boolean = false
//) {
//    Column {
//        OutlinedTextField(
//            value = value,
//            onValueChange = onValueChange,
//            label = { Text(label, fontSize = 14.sp) },
//            leadingIcon = {
//                Icon(
//                    imageVector = Icons.Default.Lock,
//                    contentDescription = null,
//                    tint = if (errorMessage != null) Color(0xFFE53935) else Color(0xFF2196F3),
//                    modifier = Modifier.size(20.dp)
//                )
//            },
//            trailingIcon = {
//                if (trailingValidation) {
//                    Icon(
//                        Icons.Default.CheckCircle,
//                        contentDescription = null,
//                        tint = Color(0xFF4CAF50),
//                        modifier = Modifier.size(20.dp)
//                    )
//                } else {
//                    IconButton(onClick = onToggleShow) {
//                        Icon(
//                            imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
//                            contentDescription = if (showPassword) "Hide password" else "Show password",
//                            tint = Color.Gray,
//                            modifier = Modifier.size(20.dp)
//                        )
//                    }
//                }
//            },
//            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
//            isError = errorMessage != null,
//            modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(12.dp),
//            singleLine = true,
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedBorderColor = Color(0xFF2196F3),
//                unfocusedBorderColor = Color(0xFFE0E0E0),
//                errorBorderColor = Color(0xFFE53935),
//                focusedLabelColor = Color(0xFF2196F3),
//                cursorColor = Color(0xFF2196F3)
//            )
//        )
//        if (errorMessage != null) {
//            Text(
//                text = errorMessage,
//                color = Color(0xFFE53935),
//                fontSize = 11.sp,
//                modifier = Modifier.padding(start = 14.dp, top = 4.dp)
//            )
//        }
//    }
//}
//
//@Composable
//private fun PasswordStrengthBar(password: String) {
//    val strength = when {
//        password.length < 6 -> 1
//        password.length < 8 -> 2
//        password.any { it.isDigit() } && password.any { it.isLetter() } && password.length >= 8 -> 4
//        password.length >= 8 -> 3
//        else -> 2
//    }
//    val (label, color) = when (strength) {
//        1 -> "Weak" to Color(0xFFE53935)
//        2 -> "Fair" to Color(0xFFFF9800)
//        3 -> "Good" to Color(0xFF2196F3)
//        4 -> "Strong" to Color(0xFF4CAF50)
//        else -> "Weak" to Color(0xFFE53935)
//    }
//
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.spacedBy(4.dp),
//        modifier = Modifier.padding(start = 2.dp)
//    ) {
//        repeat(4) { index ->
//            Box(
//                modifier = Modifier
//                    .height(4.dp)
//                    .weight(1f)
//                    .clip(RoundedCornerShape(2.dp))
//                    .background(if (index < strength) color else Color(0xFFE0E0E0))
//            )
//        }
//        Spacer(modifier = Modifier.width(4.dp))
//        Text(
//            text = label,
//            fontSize = 11.sp,
//            color = color,
//            fontWeight = FontWeight.Medium
//        )
//    }
//}

package com.example.project_graduation.presentation.edit

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.project_graduation.presentation.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    // ── Collect state thật từ ViewModel ──────────────────────────────────────
    val profileState by viewModel.state.collectAsState()
    val editState    by viewModel.editState.collectAsState()

    // ── Pre-fill khi mở màn hình ─────────────────────────────────────────────
    LaunchedEffect(Unit) {
        viewModel.initEditProfile()
    }

    // ── Local form fields ─────────────────────────────────────────────────────
    var username  by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var phone     by remember { mutableStateOf("") }
    var avatarUri by remember { mutableStateOf<Uri?>(null) }

    // Sync 1 lần sau khi initEditProfile() điền xong data vào editState
    var hasInitialized by remember { mutableStateOf(false) }
    LaunchedEffect(editState) {
        if (!hasInitialized && editState.username.isNotEmpty()) {
            username       = editState.username
            email          = editState.email
            phone          = editState.phone
            hasInitialized = true
        }
    }

    // ── Password ──────────────────────────────────────────────────────────────
    var currentPassword    by remember { mutableStateOf("") }
    var newPassword        by remember { mutableStateOf("") }
    var confirmPassword    by remember { mutableStateOf("") }
    var showCurrentPw      by remember { mutableStateOf(false) }
    var showNewPw          by remember { mutableStateOf(false) }
    var showConfirmPw      by remember { mutableStateOf(false) }
    var changePasswordMode by remember { mutableStateOf(false) }

    // ── Validation errors ─────────────────────────────────────────────────────
    var usernameError        by remember { mutableStateOf<String?>(null) }
    var emailError           by remember { mutableStateOf<String?>(null) }
    var currentPasswordError by remember { mutableStateOf<String?>(null) }
    var newPasswordError     by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val scrollState       = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { avatarUri = it } }

    // ── Snackbar ──────────────────────────────────────────────────────────────
    LaunchedEffect(editState.successMessage) {
        editState.successMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.clearEditMessages()
//            onNavigateBack()
        }
    }
    LaunchedEffect(editState.errorMessage) {
        editState.errorMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Long)
            viewModel.clearEditMessages()
        }
    }

    // ── Validation ────────────────────────────────────────────────────────────
    fun validate(): Boolean {
        var valid = true
        usernameError = if (username.isBlank()) "Username is required" else null
        emailError = when {
            email.isBlank() -> "Email is required"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
            else -> null
        }
        if (changePasswordMode) {
            currentPasswordError = if (currentPassword.isBlank()) "Current password is required" else null
            newPasswordError = when {
                newPassword.isBlank()   -> "New password is required"
                newPassword.length < 6  -> "Minimum 6 characters"
                else                    -> null
            }
            confirmPasswordError = when {
                confirmPassword.isBlank()          -> "Please confirm your password"
                confirmPassword != newPassword      -> "Passwords do not match"
                else                               -> null
            }
            if (currentPasswordError != null || newPasswordError != null || confirmPasswordError != null) valid = false
        }
        if (usernameError != null || emailError != null) valid = false
        return valid
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {

            // ── Avatar Section ────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(3.dp, Color(0xFF2196F3), CircleShape)
                                .background(Color(0xFF667eea)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (avatarUri != null) {
                                AsyncImage(
                                    model = avatarUri,
                                    contentDescription = "Avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = (profileState.user?.username ?: "?")
                                        .take(1).uppercase(),
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2196F3))
                                .border(2.dp, Color.White, CircleShape)
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Change photo",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = profileState.user?.username ?: "",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = profileState.user?.email ?: "",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    profileState.user?.phone?.takeIf { it.isNotBlank() }?.let {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = it, fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Personal Information ──────────────────────────────────────────
            SectionCard(title = "Personal Information") {
                if (editState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF2196F3))
                    }
                } else {
                    ProfileTextField(
                        value = username,
                        onValueChange = { username = it; usernameError = null },
                        label = "Username",
                        icon = Icons.Default.Person,
                        errorMessage = usernameError
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    ProfileTextField(
                        value = email,
                        onValueChange = { email = it; emailError = null },
                        label = "Email",
                        icon = Icons.Default.Email,
                        errorMessage = emailError
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    ProfileTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = "Phone Number (optional)",
                        icon = Icons.Default.Phone,
                        errorMessage = null
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Security ──────────────────────────────────────────────────────
            SectionCard(title = "Security") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { changePasswordMode = !changePasswordMode }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Change Password",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                            Text(
                                if (changePasswordMode) "Tap to cancel"
                                else "Tap to update your password",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    Switch(
                        checked = changePasswordMode,
                        onCheckedChange = {
                            changePasswordMode = it
                            if (!it) {
                                currentPassword = ""; newPassword = ""; confirmPassword = ""
                                currentPasswordError = null; newPasswordError = null; confirmPasswordError = null
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF2196F3)
                        )
                    )
                }

                AnimatedVisibility(visible = changePasswordMode, enter = fadeIn(), exit = fadeOut()) {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFFF0F0F0))
                        Spacer(modifier = Modifier.height(16.dp))
                        PasswordTextField(
                            value = currentPassword,
                            onValueChange = { currentPassword = it; currentPasswordError = null },
                            label = "Current Password",
                            showPassword = showCurrentPw,
                            onToggleShow = { showCurrentPw = !showCurrentPw },
                            errorMessage = currentPasswordError
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        PasswordTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it; newPasswordError = null },
                            label = "New Password",
                            showPassword = showNewPw,
                            onToggleShow = { showNewPw = !showNewPw },
                            errorMessage = newPasswordError
                        )
                        if (newPassword.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            PasswordStrengthBar(password = newPassword)
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        PasswordTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it; confirmPasswordError = null },
                            label = "Confirm New Password",
                            showPassword = showConfirmPw,
                            onToggleShow = { showConfirmPw = !showConfirmPw },
                            errorMessage = confirmPasswordError,
                            trailingValidation = confirmPassword.isNotEmpty() && confirmPassword == newPassword
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Save Button ───────────────────────────────────────────────────
            Button(
                onClick = {
                    if (validate()) {
                        viewModel.updateProfile(
                            username = username,
                            email = email,
                            phone = phone.takeIf { it.isNotBlank() },
                            currentPassword = if (changePasswordMode) currentPassword else null,
                            newPassword = if (changePasswordMode) newPassword else null
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                enabled = !editState.isSaving
            ) {
                if (editState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Reusable Components
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2196F3),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
        }
    }
}

@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    errorMessage: String? = null
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (errorMessage != null) Color(0xFFE53935) else Color(0xFF2196F3),
                    modifier = Modifier.size(20.dp)
                )
            },
            isError = errorMessage != null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                errorBorderColor = Color(0xFFE53935),
                focusedLabelColor = Color(0xFF2196F3),
                cursorColor = Color(0xFF2196F3)
            )
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color(0xFFE53935),
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 14.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    showPassword: Boolean,
    onToggleShow: () -> Unit,
    errorMessage: String? = null,
    trailingValidation: Boolean = false
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (errorMessage != null) Color(0xFFE53935) else Color(0xFF2196F3),
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (trailingValidation) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    IconButton(onClick = onToggleShow) {
                        Icon(
                            imageVector = if (showPassword) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            visualTransformation = if (showPassword) VisualTransformation.None
            else PasswordVisualTransformation(),
            isError = errorMessage != null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                errorBorderColor = Color(0xFFE53935),
                focusedLabelColor = Color(0xFF2196F3),
                cursorColor = Color(0xFF2196F3)
            )
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color(0xFFE53935),
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 14.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun PasswordStrengthBar(password: String) {
    val strength = when {
        password.length < 6 -> 1
        password.length < 8 -> 2
        password.any { it.isDigit() } && password.any { it.isLetter() } && password.length >= 8 -> 4
        password.length >= 8 -> 3
        else -> 2
    }
    val (label, color) = when (strength) {
        1    -> "Weak"   to Color(0xFFE53935)
        2    -> "Fair"   to Color(0xFFFF9800)
        3    -> "Good"   to Color(0xFF2196F3)
        4    -> "Strong" to Color(0xFF4CAF50)
        else -> "Weak"   to Color(0xFFE53935)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(start = 2.dp)
    ) {
        repeat(4) { index ->
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .weight(1f)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (index < strength) color else Color(0xFFE0E0E0))
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 11.sp, color = color, fontWeight = FontWeight.Medium)
    }
}