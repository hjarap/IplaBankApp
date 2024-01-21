package com.example.iplabank

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.iplabank.data.Solicitud
import com.example.iplabank.ui.AplicacionVM
import com.google.android.gms.location.LocationServices
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IplaBankApp()
        }
    }
}

@Composable
fun IplaBankApp(
    navController: NavHostController = rememberNavController(),
    vmListaDatos: AplicacionVM = viewModel(factory = AplicacionVM.Factory)
) {

    NavHost(
        navController = navController,
        startDestination = "inicio"
    ){
        composable("inicio") {
            PantallaDatos(
                datos = vmListaDatos.solicitudes,
                onAdd = {navController.navigate("form")})
        }
        composable("form") {
            PantallaFormDatos(vmListaDatos, onIdentityCardPhotosCaptured = {

            })
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PantallaDatos(datos: List<Solicitud> = listOf(),
                  onAdd:() -> Unit = {}) {
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("IplaBank")
        TextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Usuario") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        TextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text("Ingresar")
        }
        Button(
            onClick = onAdd,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Solicitar cuenta ")
        }
    }
}
data class IdentityCardPhotos(
    val frontPhotoUri: Uri,
    val backPhotoUri: Uri
)

@Composable
fun PantallaFormDatos(vmListaDatos: AplicacionVM,
                      onIdentityCardPhotosCaptured: (IdentityCardPhotos) -> Unit
) {
    var nombreCompleto by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    val frontIdImage: String? by remember { mutableStateOf(null) }
    val backIdImage: String? by remember { mutableStateOf(null) }
    val frontPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val backPhotoUri by remember { mutableStateOf<Uri?>(null) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Text("Solicitud de Cuenta")
        TextField(
            value = nombreCompleto,
            onValueChange = { nombreCompleto = it },
            label = { Text("Nombre completo") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        TextField(
            value = rut,
            onValueChange = { rut = it },
            label = { Text("RUT") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        TextField(
            value = fechaNacimiento,
            onValueChange = { fechaNacimiento = it },
            label = { Text("Fecha de nacimiento") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        TextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Button(
                onClick = {
                    navController.navigate("camera") {
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                Text("Tomar foto frontal de la cédula")
            }

            Button(
                onClick = {
                    navController.navigate("camera") {
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                Text("Tomar foto trasera de la cédula")
            }

            Button(
                onClick = {
                    if (frontPhotoUri != null && backPhotoUri != null) {
                        val identityCardPhotos = IdentityCardPhotos(frontPhotoUri!!, backPhotoUri!!)
                        onIdentityCardPhotosCaptured(identityCardPhotos)
                    } else {
                        // Manejo de error o mensaje al usuario
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(20.dp)
            ) {
                Text("Guardar fotos de la cédula")
            }

            Button(
                onClick = {
                    val solicitud = Solicitud(
                        nombreCompleto = nombreCompleto,
                        rut = rut,
                        fechaNacimiento = fechaNacimiento,
                        email = email,
                        telefono = telefono,
                        frontIdImage = frontIdImage,
                        backIdImage = backIdImage,
                        fechaSolicitud = LocalDate.now(),
                        latitud = 0.0,
                        longitud = 0.0
                    )
                    vmListaDatos.insertarSolicitud(solicitud)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text("Enviar solicitud")
            }
            UbicacionButton(vmListaDatos = vmListaDatos)
        }
    }
}

@Composable
fun UbicacionButton(vmListaDatos: AplicacionVM) {
    val contexto = LocalContext.current
    var mensaje by rememberSaveable { mutableStateOf("Ubicación: ") }
    val lanzadorPermisos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {
            if (
                it.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)
                ||
                it.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
            ) {
                val locationService = LocationServices.getFusedLocationProviderClient(contexto)
                val repository = UbicacionRepository(locationService)
                repository.conseguirUbicacion(
                    onExito = { ubicacion ->
                        mensaje = "Lat: ${ubicacion.latitude} | Long: ${ubicacion.longitude}"
                        // Guardar la ubicación en la base de datos
                        runBlocking {
                            val solicitud = vmListaDatos.obtenerUltimaSolicitud()
                            if (solicitud != null) {
                                solicitud.latitud = ubicacion.latitude
                                solicitud.longitud = ubicacion.longitude
                                vmListaDatos.actualizarSolicitud(solicitud)
                            }
                        }
                    },
                    onError = {
                        mensaje = "No se pudo conseguir la ubicación."
                        it.message?.let { it1 -> Log.e("IplaBankApp::conseguirUbicacion", it1) }
                    }
                )
            } else {
                mensaje = "Debe otorgar permisos o sino la aplicación...."
            }
        }
    )

    Column() {
        Button(
            onClick = {
                lanzadorPermisos.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text("Recuperar ubicación")
        }
    }
}

val FILENAME_FORMAT = "yyyyMMdd_HHmmss"

@Composable
fun CameraPreviewScreen(
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
    onImageCaptured: (Uri) -> Unit,

    ) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    AndroidView(
        factory = { context ->
            previewView
        },
        update = { view ->
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(view, cameraProvider, lifecycleOwner)
        }
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(
            onClick = {
                val imageBitmap = previewView.bitmap
                val uri = saveImageToPrivateStorage(context, imageBitmap)
                onImageCaptured(uri)
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Capturar Foto")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            previewView.preferredImplementationMode =
                PreviewView.ImplementationMode.TEXTURE_VIEW
        }
    }
}

private fun bindPreview(previewView: PreviewView,
                        cameraProvider: ProcessCameraProvider,
                        lifecycleOwner: LifecycleOwner) {
    val preview = Preview.Builder()
        .build()
        .also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()

    val camera = cameraProvider.bindToLifecycle(
        lifecycleOwner,
        cameraSelector,
        preview
    )
}

private fun saveImageToPrivateStorage(context: Context, bitmap: Bitmap): Uri {
    val imagesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(imagesDir, generateFilename())

    FileOutputStream(imageFile).use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    }

    return FileProvider.getUriForFile(
        context,
        context.packageName + ".fileprovider",
        imageFile
    )
}

private fun generateFilename(): String {
    val timestamp = SimpleDateFormat(FILENAME_FORMAT, Locale.getDefault()).format(Date())
    return "IMG_$timestamp.jpg"
}

