package com.example.project_graduation.presentation.admin.user_management

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.project_graduation.domain.model.User
import com.example.project_graduation.domain.model.UserRole
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//// Data model
//data class User(
//    val userId: Int,
//    val username: String,
//    val email: String,
//    val password: String,
//    val phone: String?,
//    val createdAt: String,
//    val role: String = "USER" // USER, ADMIN
//)

@Composable
fun UsersManagementContent(
    viewModel: UsersManagementViewModel
) {
//    var users by remember { mutableStateOf(getSampleUsers()) }
//    var showAddDialog by remember { mutableStateOf(false) }
//    var editingUser by remember { mutableStateOf<User?>(null) }
//    var deletingUser by remember { mutableStateOf<User?>(null) }
//    var viewingUser by remember { mutableStateOf<User?>(null) }
//    var searchQuery by remember { mutableStateOf("") }
//    var filterRole by remember { mutableStateOf<String?>(null) }

    val users by viewModel.users.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<User?>(null) }
    var deletingUser by remember { mutableStateOf<User?>(null) }
    var viewingUser by remember { mutableStateOf<User?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var filterRole by remember { mutableStateOf<UserRole?>(null) }

    // Load users when screen opens
    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    val filteredUsers = users.filter {
        val matchesSearch = it.username.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true) ||
                (it.phone?.contains(searchQuery, ignoreCase = true) == true)
        val matchesRole = filterRole == null || it.role == filterRole
        matchesSearch && matchesRole
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // Header with Add Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Users Management",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
//                    "${users.size} users registered (${users.count { it.role == "ADMIN" }} admins)",
                    "${users.size} users registered (${users.count { it.role == UserRole.ADMIN }} admins)",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1976D2)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add User")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search users...", fontSize = 14.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color(0xFF1976D2),
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filterRole == null,
                onClick = { filterRole = null },
                label = { Text("All (${users.size})") }
            )
            FilterChip(
//                selected = filterRole == "USER",
//                onClick = { filterRole = "USER" },
//                label = { Text("Users (${users.count { it.role == "USER" }})") },
                selected = filterRole == UserRole.USER,
                onClick = { filterRole = UserRole.USER },
                label = { Text("Users (${users.count { it.role == UserRole.USER }})") },

                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            )
            FilterChip(
//                selected = filterRole == "ADMIN",
//                onClick = { filterRole = "ADMIN" },
//                label = { Text("Admins (${users.count { it.role == "ADMIN" }})") },
                selected = filterRole == UserRole.ADMIN,
                onClick = { filterRole = UserRole.ADMIN },
                label = { Text("Admins (${users.count { it.role == UserRole.ADMIN }})") },

                leadingIcon = {
                    Icon(
                        Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Users List
        if (filteredUsers.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.PersonOff,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No users found",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredUsers) { user ->
                    UserCard(
                        user = user,
                        onView = { viewingUser = user },
                        onEdit = { editingUser = user },
                        onDelete = { deletingUser = user }
                    )
                }
            }
        }
    }

//    // Add/Edit Dialog
//    if (showAddDialog || editingUser != null) {
//        UserFormDialog(
//            user = editingUser,
//            onDismiss = {
//                showAddDialog = false
//                editingUser = null
//            },
//            onSave = { newUser ->
//                if (editingUser != null) {
//                    users = users.map { if (it.userId == newUser.userId) newUser else it }
//                } else {
//                    users = users + newUser.copy(
//                        userId = users.maxOfOrNull { it.userId }?.plus(1) ?: 1,
//                        createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
//                    )
//                }
//                showAddDialog = false
//                editingUser = null
//            }
//        )
//    }
//
//    // View Details Dialog
//    if (viewingUser != null) {
//        UserDetailsDialog(
//            user = viewingUser!!,
//            onDismiss = { viewingUser = null }
//        )
//    }
//
//    // Delete Confirmation Dialog
//    if (deletingUser != null) {
//        AlertDialog(
//            onDismissRequest = { deletingUser = null },
//            icon = {
//                Icon(
//                    Icons.Default.Warning,
//                    contentDescription = null,
//                    tint = Color(0xFFF44336),
//                    modifier = Modifier.size(48.dp)
//                )
//            },
//            title = {
//                Text("Delete User?", fontWeight = FontWeight.Bold)
//            },
//            text = {
//                Text("Are you sure you want to delete user \"${deletingUser!!.username}\"? This will also delete all their bookings and cannot be undone.")
//            },
//            confirmButton = {
//                Button(
//                    onClick = {
//                        users = users.filter { it.userId != deletingUser!!.userId }
//                        deletingUser = null
//                    },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color(0xFFF44336)
//                    )
//                ) {
//                    Text("Delete")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { deletingUser = null }) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }
    // Add/Edit Dialog (Coming Soon)
    if (showAddDialog || editingUser != null) {
        UserFormDialog(
            user = editingUser,
            onDismiss = {
                showAddDialog = false
                editingUser = null
            },
            onSave = { newUser ->
                // TODO: Implement API call for create/update
                showAddDialog = false
                editingUser = null
            }
        )
    }

    // View Details Dialog
    if (viewingUser != null) {
        UserDetailsDialog(
            user = viewingUser!!,
            onDismiss = { viewingUser = null }
        )
    }

    // Delete Confirmation Dialog (Coming Soon)
    if (deletingUser != null) {
        AlertDialog(
            onDismissRequest = { deletingUser = null },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFF44336),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text("Delete User?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Are you sure you want to delete user \"${deletingUser!!.username}\"? This will also delete all their bookings and cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        // TODO: Implement API call for delete
                        deletingUser = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingUser = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun UserCard(
    user: User,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        if (user.role == UserRole.ADMIN) Color(0xFFE3F2FD) else Color(0xFFE8F5E9)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (user.role == UserRole.ADMIN) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                    contentDescription = null,
                    tint = if (user.role == UserRole.ADMIN) Color(0xFF1976D2) else Color(0xFF4CAF50),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // User Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        user.username,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    if (user.role == UserRole.ADMIN) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF1976D2).copy(alpha = 0.1f)
                        ) {
                            Text(
                                "ADMIN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1976D2),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        user.email,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (user.phone != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            user.phone,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Action Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = onView,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Visibility,
                        contentDescription = "View",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun UserFormDialog(
    user: User?,
    onDismiss: () -> Unit,
    onSave: (User) -> Unit
) {
    var username by remember { mutableStateOf(user?.username ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var password by remember { mutableStateOf(if (user == null) "" else "••••••••") }
    var phone by remember { mutableStateOf(user?.phone ?: "") }
    var role by remember { mutableStateOf(user?.role ?: "USER") }
    var showPassword by remember { mutableStateOf(false) }

    var usernameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (user == null) "Add New User" else "Edit User",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            usernameError = false
                        },
                        label = { Text("Username *") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null)
                        },
                        isError = usernameError,
                        supportingText = {
                            if (usernameError) Text("Username is required")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = false
                        },
                        label = { Text("Email *") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null)
                        },
                        isError = emailError,
                        supportingText = {
                            if (emailError) Text("Valid email is required")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = false
                        },
                        label = { Text(if (user == null) "Password *" else "Password (leave blank to keep)") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null)
                        },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password"
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = passwordError,
                        supportingText = {
                            if (passwordError) Text("Password is required (min 6 characters)")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Role",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FilterChip(
                            selected = role == "USER",
                            onClick = { role = "USER" },
                            label = { Text("User") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                        FilterChip(
                            selected = role == "ADMIN",
                            onClick = { role = "ADMIN" },
                            label = { Text("Admin") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                if (username.isBlank()) {
                                    usernameError = true
                                    return@Button
                                }
                                if (email.isBlank() || !email.contains("@")) {
                                    emailError = true
                                    return@Button
                                }
                                if (user == null && (password.isBlank() || password.length < 6)) {
                                    passwordError = true
                                    return@Button
                                }
                                onDismiss()
//                                val newUser = User(
//                                    userId = user?.userId ?: 0,
//                                    username = username,
//                                    email = email,
//                                    password = if (password == "••••••••") user!!.password else password,
//                                    phone = phone.ifBlank { null },
//                                    createdAt = user?.createdAt ?: "",
//                                    role = role
//                                )
//                                onSave(newUser)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1976D2)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                if (user == null) Icons.Default.PersonAdd else Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (user == null) "Add" else "Save")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserDetailsDialog(
    user: User,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "User Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            if (user.role == UserRole.ADMIN) Color(0xFFE3F2FD) else Color(0xFFE8F5E9)
                        )
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (user.role == UserRole.ADMIN) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                        contentDescription = null,
                        tint = if (user.role == UserRole.ADMIN) Color(0xFF1976D2) else Color(0xFF4CAF50),
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                DetailRow(Icons.Default.Badge, "User ID", "#${user.userId}")
                DetailRow(Icons.Default.Person, "Username", user.username)
                DetailRow(Icons.Default.Email, "Email", user.email)
                DetailRow(Icons.Default.Phone, "Phone", user.phone ?: "Not provided")
                DetailRow(Icons.Default.Shield, "Role", user.role.name)
                DetailRow(Icons.Default.CalendarToday, "Joined", user.createdAt)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFF1976D2),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                label,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

//// Sample Data
//fun getSampleUsers(): List<User> {
//    return listOf(
//        User(
//            userId = 1,
//            username = "admin",
//            email = "admin@hotel.com",
//            password = "admin123",
//            phone = "+1-555-0001",
//            createdAt = "2024-01-15 10:00:00",
//            role = "ADMIN"
//        ),
//        User(
//            userId = 2,
//            username = "john_doe",
//            email = "john@example.com",
//            password = "password123",
//            phone = "+1-555-0101",
//            createdAt = "2025-12-20 15:25:10",
//            role = "USER"
//        ),
//        User(
//            userId = 3,
//            username = "maria_garcia",
//            email = "maria.garcia@example.com",
//            password = "password123",
//            phone = null,
//            createdAt = "2025-12-20 16:55:11",
//            role = "USER"
//        ),
//        User(
//            userId = 4,
//            username = "chen_wei",
//            email = "chen.wei@gmail.com",
//            password = "password123",
//            phone = "+86-138-0013-8000",
//            createdAt = "2025-12-18 19:38:59",
//            role = "USER"
//        ),
//        User(
//            userId = 5,
//            username = "support_admin",
//            email = "support@hotel.com",
//            password = "admin456",
//            phone = "+1-555-0002",
//            createdAt = "2024-02-01 09:00:00",
//            role = "ADMIN"
//        )
//    )
//}
//
//// ============= PREVIEWS =============
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun UsersManagementPreview() {
//    MaterialTheme {
//        Surface(color = Color(0xFFF5F5F5)) {
//            UsersManagementContent()
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun UserCardPreview() {
//    MaterialTheme {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            UserCard(
//                user = User(
//                    userId = 1,
//                    username = "admin",
//                    email = "admin@hotel.com",
//                    password = "admin123",
//                    phone = "+1-555-0001",
//                    createdAt = "2024-01-15 10:00:00",
//                    role = "ADMIN"
//                ),
//                onView = {},
//                onEdit = {},
//                onDelete = {}
//            )
//            UserCard(
//                user = User(
//                    userId = 2,
//                    username = "john_doe",
//                    email = "john@example.com",
//                    password = "password123",
//                    phone = "+1-555-0101",
//                    createdAt = "2025-12-20 15:25:10",
//                    role = "USER"
//                ),
//                onView = {},
//                onEdit = {},
//                onDelete = {}
//            )
//        }
//    }
//}