package com.example.menuapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BecasActivity extends AppCompatActivity {

    private ListView lvBecas;
    private DatabaseReference db;
    private List<String> becasList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_becas);

        // Inicializar Firebase Realtime Database
        db = FirebaseDatabase.getInstance().getReference("Becas");

        // Referencias de UI
        lvBecas = findViewById(R.id.lvBecas);

        // Inicializar la lista
        becasList = new ArrayList<>();

        // Obtener el ID del establecimiento enviado desde Detalles.java
        String idEstablecimiento = getIntent().getStringExtra("idEstablecimiento");

        if (idEstablecimiento == null || idEstablecimiento.isEmpty()) {
            Log.e("BecasActivity", "El idEstablecimiento es nulo o está vacío. Cerrando actividad.");
            finish();
            return;
        }

        Log.d("BecasActivity", "ID del Establecimiento recibido: " + idEstablecimiento);

        // Cargar las becas relacionadas
        cargarBecasDesdeFirebase(idEstablecimiento);
    }

    private void cargarBecasDesdeFirebase(String idEstablecimiento) {
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot becaSnapshot : snapshot.getChildren()) {
                        Iterable<DataSnapshot> idEstablecimientosSnapshot = becaSnapshot.child("idEstablecimientos").getChildren();
                        boolean pertenece = false;

                        for (DataSnapshot idSnap : idEstablecimientosSnapshot) {
                            String idRelacionado = idSnap.getValue(String.class);
                            if (idEstablecimiento.equals(idRelacionado)) {
                                pertenece = true;
                                break;
                            }
                        }

                        if (pertenece) {
                            // Concatenar los datos de la beca
                            String nombreBeca = becaSnapshot.child("nombre").getValue(String.class);
                            String condiciones = becaSnapshot.child("condiciones").getValue(String.class);
                            String descripcion = becaSnapshot.child("descripcion").getValue(String.class);

                            String becaInfo = "Nombre: " + nombreBeca + "\n" +
                                    "Condiciones: " + condiciones + "\n" +
                                    "Descripcion: " + descripcion;

                            becasList.add(becaInfo);
                        }
                    }

                    if (becasList.isEmpty()) {
                        becasList.add("No se encontraron becas disponibles.");
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(BecasActivity.this,
                            android.R.layout.simple_list_item_1, becasList);
                    lvBecas.setAdapter(adapter);
                } else {
                    Log.d("BecasActivity", "No se encontraron becas en Firebase.");
                    becasList.add("No se encontraron becas disponibles.");
                    lvBecas.setAdapter(new ArrayAdapter<>(BecasActivity.this,
                            android.R.layout.simple_list_item_1, becasList));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BecasActivity", "Error al cargar las becas: " + error.getMessage(), error.toException());
            }
        });
    }
}
