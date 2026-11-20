package com.example.rutasegura;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistroActivity extends AppCompatActivity implements SensorEventListener {

    private EditText etTitulo, etDescripcion;
    private Spinner spCategoria;
    private TextView tvFechaHora, tvSensorLuz, tvUbicacion;
    private Button btnGuardarReporte, btnActualizarUbicacion;

    private FirebaseFirestore db;
    private FusedLocationProviderClient fusedLocationClient;
    private double latitudActual = 0.0;
    private double longitudActual = 0.0;
    private boolean ubicacionObtenida = false;

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private float valorLuzActual = 0.0f;

    private String fechaHoraActual;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    obtenerUbicacionGPS();
                } else {
                    ubicacionObtenida = false;
                    tvUbicacion.setText("Ubicación GPS: Permiso denegado por el usuario.");
                    Toast.makeText(this, "Se requiere permiso de ubicación para registrar las coordenadas del reporte.", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etTitulo = findViewById(R.id.etTitulo);
        etDescripcion = findViewById(R.id.etDescripcion);
        spCategoria = findViewById(R.id.spCategoria);
        tvFechaHora = findViewById(R.id.tvFechaHora);
        tvSensorLuz = findViewById(R.id.tvSensorLuz);
        tvUbicacion = findViewById(R.id.tvUbicacion);
        btnGuardarReporte = findViewById(R.id.btnGuardarReporte);
        btnActualizarUbicacion = findViewById(R.id.btnActualizarUbicacion);

        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        String[] categorias = {"Luminaria apagada", "Vereda dañada", "Obstáculo en vía", "Señalética caída", "Otro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categorias);
        spCategoria.setAdapter(adapter);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        fechaHoraActual = sdf.format(new Date());
        tvFechaHora.setText("Fecha y hora: " + fechaHoraActual);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }
        if (lightSensor == null) {
            tvSensorLuz.setText("Sensor de luminosidad: No disponible en este dispositivo.");
        }

        verificarPermisoUbicacion();

        btnActualizarUbicacion.setOnClickListener(v -> verificarPermisoUbicacion());
        btnGuardarReporte.setOnClickListener(v -> validarYGuardarReporte());
    }

    private void verificarPermisoUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacionGPS();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void obtenerUbicacionGPS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        tvUbicacion.setText("Ubicación GPS: obteniendo coordenadas...");
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        latitudActual = location.getLatitude();
                        longitudActual = location.getLongitude();
                        ubicacionObtenida = true;
                        tvUbicacion.setText(String.format(Locale.US, "Ubicación GPS: %.5f, %.5f", latitudActual, longitudActual));
                    } else {
                        ubicacionObtenida = false;
                        tvUbicacion.setText("Ubicación GPS: no disponible (active el GPS del dispositivo).");
                        Toast.makeText(this, "No se pudo obtener la ubicación. Verifique que el GPS esté encendido.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    ubicacionObtenida = false;
                    tvUbicacion.setText("Ubicación GPS: error al consultar servicio.");
                    Toast.makeText(this, "Error de ubicación: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void validarYGuardarReporte() {
        String titulo = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String categoria = spCategoria.getSelectedItem().toString();

        if (TextUtils.isEmpty(titulo)) {
            etTitulo.setError("El título es obligatorio");
            etTitulo.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(descripcion)) {
            etDescripcion.setError("La descripción es obligatoria");
            etDescripcion.requestFocus();
            return;
        }
        if (!ubicacionObtenida) {
            Toast.makeText(this, "Debe obtener una ubicación GPS válida antes de guardar.", Toast.LENGTH_LONG).show();
            return;
        }

        btnGuardarReporte.setEnabled(false);
        btnGuardarReporte.setText("Guardando...");

        Reporte nuevoReporte = new Reporte(
                titulo,
                categoria,
                descripcion,
                fechaHoraActual,
                "Pendiente",
                latitudActual,
                longitudActual,
                valorLuzActual
        );

        db.collection("reportes")
                .add(nuevoReporte)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Reporte guardado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnGuardarReporte.setEnabled(true);
                    btnGuardarReporte.setText("Guardar Reporte en Firestore");
                    Toast.makeText(this, "Error de conexión con Firestore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null && lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            valorLuzActual = event.values[0];
            tvSensorLuz.setText(String.format(Locale.US, "Sensor de luminosidad: %.1f lx", valorLuzActual));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}