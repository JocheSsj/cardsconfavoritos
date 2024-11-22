package com.example.menuapp;

import android.os.Bundle;
import android.util.Log;
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

    private TextView tvDetalles; // TextView para mostrar los resultados
    private DatabaseReference db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);

        // Inicializar Firebase Realtime Database
        db = FirebaseDatabase.getInstance().getReference("Establecimientos");

        // Obtener referencias al TextView
        tvDetalles = findViewById(R.id.tvDetalles);

        // Obtener el ID enviado desde el intent
        String id = getIntent().getStringExtra("id");

        // Consultar datos desde Firebase según el ID
        cargarDatosDesdeFirebase(id);
    }

    private void cargarDatosDesdeFirebase(String id) {
        Log.d("Detalles", "Buscando datos para el ID: " + id);

        db.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Construir los detalles a partir de los datos recuperados
                    StringBuilder detalles = new StringBuilder();

                    detalles.append("Nombre: ").append(snapshot.child("nombre").getValue(String.class)).append("\n");
                    detalles.append("Dirección: ").append(snapshot.child("direccion").getValue(String.class)).append("\n");
                    detalles.append("Tipo de Establecimiento: ").append(snapshot.child("idTipoEsta").getValue(String.class)).append("\n");
                    detalles.append("Descripción: ").append(snapshot.child("descripcion").getValue(String.class)).append("\n");
                    detalles.append("Teléfono: ").append(snapshot.child("telefono").getValue(String.class)).append("\n");

                    // Mostrar los detalles en el TextView
                    tvDetalles.setText(detalles.toString());
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
