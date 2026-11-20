package com.example.rutasegura;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListadoActivity extends AppCompatActivity {

    private RecyclerView rvReportes;
    private TextView tvMensajeLista;
    private ReporteAdapter adapter;
    private List<Reporte> listaReportes;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);

        rvReportes = findViewById(R.id.rvReportes);
        tvMensajeLista = findViewById(R.id.tvMensajeLista);

        db = FirebaseFirestore.getInstance();
        listaReportes = new ArrayList<>();

        rvReportes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReporteAdapter(listaReportes, reporte -> {
            Intent intent = new Intent(ListadoActivity.this, DetalleActivity.class);
            intent.putExtra("reporte", reporte);
            startActivity(intent);
        });
        rvReportes.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarReportesDesdeFirestore();
    }

    private void cargarReportesDesdeFirestore() {
        tvMensajeLista.setVisibility(View.VISIBLE);
        tvMensajeLista.setText("Cargando reportes...");

        db.collection("reportes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaReportes.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Reporte reporte = document.toObject(Reporte.class);
                        reporte.setId(document.getId()); // Guardamos el ID del documento
                        listaReportes.add(reporte);
                    }
                    adapter.notifyDataSetChanged();

                    if (listaReportes.isEmpty()) {
                        tvMensajeLista.setText("No hay reportes registrados aún.");
                    } else {
                        tvMensajeLista.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    tvMensajeLista.setText("Error al cargar datos: " + e.getMessage());
                    Toast.makeText(this, "Error de conexión con Firestore", Toast.LENGTH_SHORT).show();
                });
    }
}