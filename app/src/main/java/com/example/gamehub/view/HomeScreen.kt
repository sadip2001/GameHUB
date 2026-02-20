package com.example.gamehub.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.gamehub.model.ProductModel
import com.example.gamehub.repository.ProductRepoImplementation
import com.example.gamehub.viewModel.ProductViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable

fun HomeScreen() {
    val productViewModel = remember { ProductViewModel(ProductRepoImplementation()) }
    val productsState = productViewModel.products.observeAsState(initial = emptyList())
    var selectedProduct by remember { mutableStateOf<ProductModel?>(null) }

    LaunchedEffect(Unit) {
        productViewModel.getAllProducts()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Available Gears", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(productsState.value ?: emptyList()) { product ->
                ProductCard(product) {
                    selectedProduct = product // Opens Dialog
                }
            }
        }
    }

    // Detail Dialog
    if (selectedProduct != null) {
        ProductDetailDialog(
            product = selectedProduct!!,
            onDismiss = { selectedProduct = null },
            productViewModel = productViewModel
        )
    }
}

@Composable
fun ProductCard(product: ProductModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = product.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "Rs. ${product.price}", color = Color.Gray)
                Text(text = product.category, fontSize = 12.sp, color = Color(0xFF135BEC))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailDialog(
    product: ProductModel,
    onDismiss: () -> Unit,
    productViewModel: ProductViewModel
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val isOwner = product.owner == currentUserId

    // Edit States
    var isEditing by remember { mutableStateOf(false) }
    var editedTitle by remember { mutableStateOf(product.title) }
    var editedDesc by remember { mutableStateOf(product.description) }
    var editedPrice by remember { mutableStateOf(product.price) }
    var editedQty by remember { mutableStateOf(product.quantity) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = Color.Gray) }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isEditing) "Edit Gear" else "Gear Details",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold
                )
                if (isOwner) {
                    IconButton(onClick = {
                        if (isEditing) {
                            // Logic to Save
                            val updatedModel = product.copy(
                                title = editedTitle,
                                description = editedDesc,
                                price = editedPrice,
                                quantity = editedQty
                            )
                            productViewModel.updateProduct(product.id, updatedModel) { success, _ ->
                                if (success) isEditing = false
                            }
                        } else {
                            isEditing = true
                        }
                    }) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = null,
                            tint = if (isEditing) Color(0xFF4CAF50) else Color(0xFF135BEC)
                        )
                    }
                    if (!isEditing) {
                        IconButton(onClick = {
                            productViewModel.deleteProduct(product.id) { success, _ ->
                                if (success) onDismiss()
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                        }
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                EditableInfoField(label = "Title", value = editedTitle, isEditing = isEditing) { editedTitle = it }
                EditableInfoField(label = "Price (Rs.)", value = editedPrice, isEditing = isEditing, isNumber = true) { editedPrice = it }
                EditableInfoField(label = "Quantity", value = editedQty, isEditing = isEditing, isNumber = true) { editedQty = it }
                EditableInfoField(label = "Description", value = editedDesc, isEditing = isEditing, singleLine = false) { editedDesc = it }

                // Read-only info (Sport/Category usually stay fixed or use dropdowns)
                if (!isEditing) {
                    Text(text = "Sport: ${product.sport}", fontSize = 14.sp, color = Color.Gray)
                    Text(text = "Category: ${product.category}", fontSize = 14.sp, color = Color.Gray)
                }
            }
        }
    )
}

@Composable
fun EditableInfoField(
    label: String,
    value: String,
    isEditing: Boolean,
    isNumber: Boolean = false,
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit
) {
    if (isEditing) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isNumber) KeyboardType.Number else KeyboardType.Text
            )
        )
    } else {
        Column {
            Text(text = label, fontSize = 12.sp, color = Color(0xFF135BEC), fontWeight = FontWeight.Bold)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Divider(modifier = Modifier.padding(top = 4.dp), thickness = 0.5.dp, color = Color.LightGray)
        }
    }
}