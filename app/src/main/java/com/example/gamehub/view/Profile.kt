package com.example.gamehub.view

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.gamehub.model.UserModel
import com.example.gamehub.repository.UserRepoImplementation
import com.example.gamehub.repository.commonRepoImpl
import com.example.gamehub.viewModel.CommonViewModel
import com.example.gamehub.viewModel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: ""

    val userViewModel = remember { UserViewModel(UserRepoImplementation()) }
    val commonViewModel = remember { CommonViewModel(commonRepoImpl()) }

    val userData = userViewModel.users.observeAsState(initial = null)

    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }
    var editedPhone by remember { mutableStateOf("") }
    var cloudinaryLink by remember { mutableStateOf("") }

    LaunchedEffect(userData.value) {
        if (userId.isNotEmpty()) {
            userViewModel.getUserById(userId)
        }

        userData.value?.let { user ->
            editedName = user.fullName
            editedPhone = user.phone
            cloudinaryLink = user.dp
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            commonViewModel.uploadImage(context, it) { success, imageUrl ->
                if (success) {
                    cloudinaryLink = imageUrl.toString()
                } else {
                    Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7292B9))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .background(Color(0xFF8BA9E8), CircleShape)
                .clickable(enabled = isEditing) {
                    imagePickerLauncher.launch("image/*")
                },
            contentAlignment = Alignment.Center
        ) {
            if (cloudinaryLink.isEmpty()) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = Color.White.copy(alpha = 0.5f)
                )
            } else {
                AsyncImage(
                    model = cloudinaryLink,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (isEditing) {
            OutlinedTextField(
                value = editedName,
                onValueChange = { editedName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = editedPhone,
                onValueChange = { editedPhone = it },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            ProfileDetailText("Name: ${userData.value?.fullName ?: "Loading..."}")
            ProfileDetailText("Email: ${userData.value?.email ?: "Loading..."}")
            val phoneToShow = if (userData.value?.phone.isNullOrEmpty()) "98xxxxxxxx" else userData.value?.phone
            ProfileDetailText("Phone: $phoneToShow")
        }

        Spacer(modifier = Modifier.height(40.dp))

        ActionButton(
            text = if (isEditing) "Save Changes" else "Edit",
            icon = Icons.Default.Edit,
            onClick = {
                if (isEditing) {
                    val updatedUser = UserModel(
                        id = userId,
                        fullName = editedName,
                        email = userData.value?.email ?: "",
                        phone = editedPhone,
                        dp = cloudinaryLink
                    )

                    userViewModel.editProfile(userId, updatedUser) { success, message ->
                        if (success) {
                            isEditing = false
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    isEditing = true
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ActionButton(
            text = "Delete",
            icon = Icons.Default.Delete,
            color = Color(0xFFE57373),
            onClick = {
                userViewModel.deleteAccount(userId) { success, message ->
                    if (success) {
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ActionButton(
            text = "LogOut",
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            onClick = {
                auth.signOut()
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            }
        )
    }
}

@Composable
fun ProfileDetailText(text: String) {
    Text(
        text = text,
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
        color = Color.Black,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun ActionButton(text: String, icon: ImageVector, color: Color = Color(0xFF8BA9E8), onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.width(220.dp).height(55.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(28.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, fontSize = 18.sp)
        }
    }
}