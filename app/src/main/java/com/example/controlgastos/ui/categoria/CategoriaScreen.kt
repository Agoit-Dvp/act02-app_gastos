package com.example.controlgastos.ui.categoria

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlgastos.R
import com.example.controlgastos.data.model.Categoria
import com.example.controlgastos.ui.categoria.components.AddCategoriaSheet
import com.example.controlgastos.ui.categoria.components.EditCategoriaSheet
import com.example.controlgastos.ui.categoria.components.getPainterByName
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CategoriaScreen(planId: String, viewModel: CategoriaViewModel = viewModel()) {
    var esIngreso by remember { mutableStateOf(true) }
    val categorias by viewModel.categorias.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val usuarioId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()


    var showAddSheet by remember { mutableStateOf(false) }
    var categoriaSeleccionada by remember { mutableStateOf<Categoria?>(null) } //Controlar categoria seleccionada
    val sheetState = rememberModalBottomSheetState()

    // Cargar categorías al cambiar de tipo
    LaunchedEffect(planId, esIngreso) {
        viewModel.cargarCategorias(planId = planId, esIngreso = esIngreso)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categorías") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = esIngreso,
                    onClick = { esIngreso = true },
                    label = { Text("Ingresos") }
                )
                FilterChip(
                    selected = !esIngreso,
                    onClick = { esIngreso = false },
                    label = { Text("Gastos") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(categorias) { categoria ->
                        CategoriaItem(
                            categoria = categoria,
                            onEditar = { categoriaSeleccionada = it }
                        )
                    }

                    // Botón "+"
                    item {
                        CategoriaAddButton {
                            showAddSheet = true
                        }
                    }
                }
            }
        }
    }

// Add Sheet
    if (showAddSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddSheet = false },
            sheetState = sheetState
        ) {
            AddCategoriaSheet(
                esIngreso = esIngreso,
                usuarioId = usuarioId,
                planId = planId,
                viewModel = viewModel,
                onCategoriaCreada = {
                    showAddSheet = false
                    viewModel.cargarCategorias(planId, esIngreso)
                }
            )
        }
    }

    // Edit Sheet
    if (categoriaSeleccionada != null) {
        ModalBottomSheet(
            onDismissRequest = { categoriaSeleccionada = null },
            sheetState = sheetState
        ) {
            EditCategoriaSheet(
                categoria = categoriaSeleccionada!!,
                viewModel = viewModel,
                onCategoriaActualizada = {
                    categoriaSeleccionada = null
                    viewModel.cargarCategorias(planId, esIngreso)
                },
                onCategoriaEliminada = {
                    categoriaSeleccionada = null
                    viewModel.cargarCategorias(planId, esIngreso)
                }
            )
        }
    }
}

@Composable
fun CategoriaItem(categoria: Categoria, onEditar: (Categoria) -> Unit) {
    Surface(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable { onEditar(categoria) },
        tonalElevation = 1.dp,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = getPainterByName(categoria.iconName),
                contentDescription = categoria.nombre,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = categoria.nombre,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
fun CategoriaAddButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Agregar categoría",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
