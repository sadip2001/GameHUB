package com.example.gamehub.view

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.gamehub.model.ProductModel
import com.example.gamehub.repository.ProductRepoImplementation
import com.example.gamehub.repository.commonRepoImpl
import com.example.gamehub.viewModel.CommonViewModel
import com.example.gamehub.viewModel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProduct(onProductAdded: () -> Unit) {
    val context = LocalContext.current
    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    val commonViewModel = remember { CommonViewModel(commonRepoImpl()) }

    val productViewModel = remember { ProductViewModel(ProductRepoImplementation()) }

    // Form States
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var cloudinaryLink by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }

    // Dropdown States
    val sports = listOf("Football", "Cricket", "Badminton", "Futsal", "Mobile Games")
    val categories = listOf("Game Equipment", "Footwear", "Body Wears")

    var sportExpanded by remember { mutableStateOf(false) }
    var selectedSport by remember { mutableStateOf(sports[0]) }

    var categoryExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            commonViewModel.uploadImage(context, it) { success, imageUrl ->
                if (success) cloudinaryLink = imageUrl.toString()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Add New Gear", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        // Image Selection Rectangle
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray.copy(alpha = 0.3f))
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (cloudinaryLink.isEmpty()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(48.dp))
                    Text("Tap to add image", color = Color.Gray)
                }
            } else {
                AsyncImage(
                    model = cloudinaryLink,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Title and Description
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Product Title") }, modifier = Modifier.fillMaxWidth())

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        // Sport Selection Dropdown
        ExposedDropdownMenuBox(
            expanded = sportExpanded,
            onExpandedChange = { sportExpanded = !sportExpanded }
        ) {
            OutlinedTextField(
                value = selectedSport,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Sport") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sportExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = sportExpanded, onDismissRequest = { sportExpanded = false }) {
                sports.forEach { sport ->
                    DropdownMenuItem(text = { Text(sport) }, onClick = {
                        selectedSport = sport
                        sportExpanded = false
                    })
                }
            }
        }

        // Category Selection Dropdown
        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                categories.forEach { cat ->
                    DropdownMenuItem(text = { Text(cat) }, onClick = {
                        selectedCategory = cat
                        categoryExpanded = false
                    })
                }
            }
        }

        // Price and Quantity Row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Qty") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Button(
            onClick = {
                if (title.isEmpty() || price.isEmpty() || cloudinaryLink.isEmpty()) {
                    Toast.makeText(context, "Please fill all fields and upload an image", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                val currentUserId = auth.currentUser?.uid

                val product = ProductModel(
                    title = title,
                    owner = currentUserId ?: "",
                    description = description,
                    sport = selectedSport,
                    category = selectedCategory,
                    price = price,
                    quantity = quantity,
                    imageUrl = cloudinaryLink
                )

                productViewModel.addProduct(product) { success, message ->
                    if (success) {
                        Toast.makeText(context, "Product Uploaded!", Toast.LENGTH_SHORT).show()
                        onProductAdded()
                        title = ""; description = ""; price = ""; quantity = ""; cloudinaryLink = ""
                    } else {
                        Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !isUploading // Disable button while image is uploading
        ) {
            if (isUploading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("UPLOAD PRODUCT", fontWeight = FontWeight.Bold)
            }
        }
    }
}