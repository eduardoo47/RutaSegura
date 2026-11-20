package com.example.rutasegura;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MapaActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        db = FirebaseFirestore.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        cargarMarcadoresDesdeFirestore();
    }

    private void cargarMarcadoresDesdeFirestore() {
        db.collection("reportes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    LatLng primeraUbicacion = null;

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Reporte reporte = document.toObject(Reporte.class);
                        if (reporte.getLatitud() != 0.0 || reporte.getLongitud() != 0.0) {
                            LatLng posicion = new LatLng(reporte.getLatitud(), reporte.getLongitud());
                            if (primeraUbicacion == null) {
                                primeraUbicacion = posicion;
                            }

                            // Color verde si está resuelto, rojo si está pendiente/en revisión
                            float colorMarcador = "Resuelto".equalsIgnoreCase(reporte.getEstado())
                                    ? BitmapDescriptorFactory.HUE_GREEN
                                    : BitmapDescriptorFactory.HUE_RED;

                            mMap.addMarker(new MarkerOptions()
                                    .position(posicion)
                                    .title(reporte.getTitulo())
                                    .snippet(reporte.getCategoria() + " | Estado: " + reporte.getEstado())
                                    .icon(BitmapDescriptorFactory.defaultMarker(colorMarcador)));
                        }
                    }

                    if (primeraUbicacion != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(primeraUbicacion, 14f));
                    } else {
                        Toast.makeText(this, "No hay reportes con coordenadas válidas para mostrar.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar marcadores: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}