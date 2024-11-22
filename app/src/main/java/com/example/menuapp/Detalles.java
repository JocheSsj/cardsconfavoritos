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
    private Button btnVerUbicacion, btnVerCarreras, btnVerBecas;
    private DatabaseReference db;
    private double latitud, longitud;
    private String direccion, nombre, idTipoEsta, idEstablecimiento, descripcion,telefono,tipoeducacion,horario,correo,link;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);

        // Inicializar Firebase Realtime Database
        db = FirebaseDatabase.getInstance().getReference("Establecimientos");

        // Referencias de UI
        tvDetalles = findViewById(R.id.tvDetalles);
        btnVerUbicacion = findViewById(R.id.btnVerUbicacion);
        btnVerCarreras = findViewById(R.id.btnVerCarreras);
        btnVerBecas = findViewById(R.id.btnVerBecas);

        // Obtener el ID enviado desde la actividad anterior
        idEstablecimiento = getIntent().getStringExtra("id");

        if (idEstablecimiento == null || idEstablecimiento.isEmpty()) {
            Log.e("Detalles", "El idEstablecimiento es nulo o está vacío. Cerrando actividad.");
            finish();
            return;
        }

        Log.d("Detalles", "ID del Establecimiento recibido: " + idEstablecimiento);

        // Cargar los datos del establecimiento
        cargarDatosDesdeFirebase(idEstablecimiento);

        // Configurar el botón "Ver Ubicación"
        btnVerUbicacion.setOnClickListener(v -> {
            if (latitud != 0 && longitud != 0 && direccion != null && nombre != null) {
                Log.d("Detalles", "Datos de ubicación enviados: Lat=" + latitud + ", Lng=" + longitud);
                Intent intent = new Intent(Detalles.this, MapsActivity.class);
                intent.putExtra("latitud", latitud);
                intent.putExtra("longitud", longitud);
                intent.putExtra("nombre", nombre);
                intent.putExtra("direccion", direccion);
                intent.putExtra("idTipoEsta", idTipoEsta); // Pasa el idTipoEsta al mapa
                startActivity(intent);
            } else {
                Log.e("Detalles", "Datos incompletos: latitud=" + latitud + ", longitud=" + longitud + ", direccion=" + direccion);
            }
        });

        // Configurar el botón "Ver Carreras"
        btnVerCarreras.setOnClickListener(v -> {
            Log.d("Detalles", "Abriendo CarrerasActivity con idEstablecimiento: " + idEstablecimiento);
            Intent intent = new Intent(Detalles.this, CarrerasActivity.class);
            intent.putExtra("idEstablecimiento", idEstablecimiento); // Pasa el ID del establecimiento
            startActivity(intent);
        });
        btnVerBecas.setOnClickListener(v -> {
            Intent intent = new Intent(Detalles.this, BecasActivity.class);
            intent.putExtra("idEstablecimiento", idEstablecimiento); // Pasa el ID del establecimiento
            startActivity(intent);
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
                    descripcion = snapshot.child("descripcion").getValue(String.class);
                    idTipoEsta = snapshot.child("idTipoEsta").getValue(String.class);
                    correo = snapshot.child("email").getValue(String.class);
                    tipoeducacion = snapshot.child("idTipoEdu").getValue(String.class);
                    horario = snapshot.child("horario").getValue(String.class);
                    link = snapshot.child("url").getValue(String.class);
                    telefono = snapshot.child("telefono").getValue(String.class);
                    latitud = snapshot.child("latitud").getValue(Double.class);
                    longitud = snapshot.child("longitud").getValue(Double.class);

                    // Mostrar los datos en el TextView
                    Log.d("Detalles", "Datos cargados: Nombre=" + nombre + ", Dirección=" + direccion+", Tipo=" + idTipoEsta+", Descripcion=" + descripcion+", Correo=" + correo+", Telefono=" + telefono+", Mas Informacion=" + link+", Atencion=" + horario);
                    tvDetalles.setText("" + nombre + "\nDirección: " + direccion+ "\nDescripcion: " + descripcion+ "\nTipo: " + idTipoEsta+"\nCorreo: " + correo+"\nTelefono: " + telefono+"\nSitio Web: " + link+"\nAtencion: " + horario);
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
