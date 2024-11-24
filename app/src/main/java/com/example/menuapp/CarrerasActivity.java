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

public class CarrerasActivity extends AppCompatActivity {

    private ListView lvCarreras;
    private DatabaseReference db;
    private List<String> carrerasList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carreras);

        // Inicializar Firebase Realtime Database
        db = FirebaseDatabase.getInstance().getReference("Carreras");

        // Referencias de UI
        lvCarreras = findViewById(R.id.lvCarreras);

        // Inicializar la lista
        carrerasList = new ArrayList<>();

        // Obtener el ID del establecimiento enviado desde Detalles.java
        String idEstablecimiento = getIntent().getStringExtra("idEstablecimiento");

        if (idEstablecimiento == null || idEstablecimiento.isEmpty()) {
            Log.e("CarrerasActivity", "El idEstablecimiento es nulo o está vacío. Cerrando actividad.");
            finish();
            return;
        }

        Log.d("CarrerasActivity", "ID del Establecimiento recibido: " + idEstablecimiento);

        // Cargar las carreras relacionadas
        cargarCarrerasDesdeFirebase(idEstablecimiento);
    }
    private void cargarCarrerasDesdeFirebase(String idEstablecimiento) {
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot carreraSnapshot : snapshot.getChildren()) {
                        // Leer el nodo idEstablecimientos como un Iterable
                        Iterable<DataSnapshot> idEstablecimientosSnapshot = carreraSnapshot.child("idEstablecimientos").getChildren();
                        boolean pertenece = false;

                        for (DataSnapshot idSnap : idEstablecimientosSnapshot) {
                            String idRelacionado = idSnap.getValue(String.class);
                            if (idEstablecimiento.equals(idRelacionado)) {
                                pertenece = true;
                                break;
                            }
                        }

                        if (pertenece) {
                            // Recuperar los datos de la carrera
                            String nombreCarrera = carreraSnapshot.child("nombre").getValue(String.class);
                            int arancel = carreraSnapshot.child("arancel").getValue(Integer.class);
                            int matricula = carreraSnapshot.child("matricula").getValue(Integer.class);

                            // Concatenar los datos en una sola cadena para mostrar en la lista
                            String carreraInfo = "Nombre: " + nombreCarrera + "\n" +
                                    "Arancel: $" + arancel + "\n" +
                                    "Matrícula: " + matricula + "\n";

                            carrerasList.add(carreraInfo); // Agregar la cadena a la lista
                        }
                    }

                    // Verificar si se encontraron carreras
                    if (carrerasList.isEmpty()) {
                        carrerasList.add("No se encontraron carreras disponibles.");
                    }

                    // Mostrar las carreras en la ListView
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(CarrerasActivity.this,
                            android.R.layout.simple_list_item_1, carrerasList);
                    lvCarreras.setAdapter(adapter);
                } else {
                    Log.d("CarrerasActivity", "No se encontraron carreras en Firebase.");
                    carrerasList.add("No se encontraron carreras disponibles.");
                    lvCarreras.setAdapter(new ArrayAdapter<>(CarrerasActivity.this,
                            android.R.layout.simple_list_item_1, carrerasList));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CarrerasActivity", "Error al cargar las carreras: " + error.getMessage(), error.toException());
                carrerasList.add("Error al cargar las carreras. Inténtalo de nuevo más tarde.");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(CarrerasActivity.this,
                        android.R.layout.simple_list_item_1, carrerasList);
                lvCarreras.setAdapter(adapter);
            }
        });
    }


}
