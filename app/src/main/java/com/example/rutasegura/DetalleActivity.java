package com.example.rutasegura;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class DetalleActivity extends AppCompatActivity {

    private Reporte reporte;
    private Spinner spDetalleEstado;
    private Button btnActualizarEstado;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        db = FirebaseFirestore.getInstance();
        reporte = (Reporte) getIntent().getSerializableExtra("reporte");

        TextView tvTitulo = findViewById(R.id.tvDetalleTitulo);
        TextView tvCategoria = findViewById(R.id.tvDetalleCategoria);
        TextView tvFecha = findViewById(R.id.tvDetalleFecha);
        TextView tvCoordenadas = findViewById(R.id.tvDetalleCoordenadas);
        TextView tvSensor = findViewById(R.id.tvDetalleSensor);
        TextView tvDescripcion = findViewById(R.id.tvDetalleDescripcion);
        spDetalleEstado = findViewById(R.id.spDetalleEstado);
        btnActualizarEstado = findViewById(R.id.btnActualizarEstado);

        String[] estados = {"Pendiente", "En revisión", "Resuelto"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, estados);
        spDetalleEstado.setAdapter(adapter);

        if (reporte != null) {
            tvTitulo.setText(reporte.getTitulo());
            tvCategoria.setText("Categoría: " + reporte.getCategoria());
            tvFecha.setText("Fecha y hora: " + reporte.getFechaHora());
            tvCoordenadas.setText(String.format(Locale.US, "Ubicación: %.5f, %.5f", reporte.getLatitud(), reporte.getLongitud()));
            tvSensor.setText(String.format(Locale.US, "Luminosidad registrada: %.1f lx", reporte.getValorSensor()));
            tvDescripcion.setText(reporte.getDescripcion());

            // Seleccionar estado actual en el Spinner
            for (int i = 0; i < estados.length; i++) {
                if (estados[i].equalsIgnoreCase(reporte.getEstado())) {
                    spDetalleEstado.setSelection(i);
                    break;
                }
            }
        }

        btnActualizarEstado.setOnClickListener(v -> actualizarEstadoEnFirestore());
    }

    private void actualizarEstadoEnFirestore() {
        if (reporte == null || reporte.getId() == null) {
            Toast.makeText(this, "Error: no se encontró el ID del reporte", Toast.LENGTH_SHORT).show();
            return;
        }

        String nuevoEstado = spDetalleEstado.getSelectedItem().toString();
        btnActualizarEstado.setEnabled(false);

        db.collection("reportes").document(reporte.getId())
                .update("estado", nuevoEstado)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Estado actualizado a: " + nuevoEstado, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnActualizarEstado.setEnabled(true);
                    Toast.makeText(this, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}