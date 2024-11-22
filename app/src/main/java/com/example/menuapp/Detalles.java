package com.example.menuapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Detalles extends AppCompatActivity {

    private TextView tvDetalles;
    private Button btnVerUbicacion;
    private DatabaseReference db;
    private double latitud, longitud;
    private String direccion, nombre;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);

        // Inicializar Firebase Realtime Database
        db = FirebaseDatabase.getInstance().getReference("Establecimientos");

        // Referencias de UI
        tvDetalles = findViewById(R.id.tvDetalles);
        btnVerUbicacion = findViewById(R.id.btnVerUbicacion);

        // Obtener el ID enviado desde el intent
        String id = getIntent().getStringExtra("id");

        // Cargar los datos del establecimiento
        cargarDatosDesdeFirebase(id);

        // Configurar el botón "Ver Ubicación"
        btnVerUbicacion.setOnClickListener(v -> {
            if (latitud != 0 && longitud != 0 && direccion != null && nombre != null) {
                Intent intent = new Intent(Detalles.this, MapsActivity.class);
                intent.putExtra("latitud", latitud);
                intent.putExtra("longitud", longitud);
                intent.putExtra("nombre", nombre);
                intent.putExtra("direccion", direccion);
                startActivity(intent);
            } else {
                Log.e("Detalles", "Datos incompletos: latitud=" + latitud + ", longitud=" + longitud + ", direccion=" + direccion);
            }
        });
    }

    private void cargarDatosDesdeFirebase(String id) {
        Log.d("Detalles", "Buscando datos para el ID: " + id);

        db.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Recuperar datos del establecimiento
                    nombre = snapshot.child("nombre").getValue(String.class);
                    direccion = snapshot.child("direccion").getValue(String.class);
                    latitud = snapshot.child("latitud").getValue(Double.class);
                    longitud = snapshot.child("longitud").getValue(Double.class);

                    // Mostrar los datos en el TextView
                    tvDetalles.setText("Nombre: " + nombre + "\nDirección: " + direccion);
                } else {
                    Log.d("Detalles", "No se encontró un establecimiento con el ID: " + id);
                    tvDetalles.setText("No se encontraron datos.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Detalles", "Error al cargar los datos: " + error.getMessage(), error.toException());
                tvDetalles.setText("Error al cargar los datos.");
            }
        });
    }
}
