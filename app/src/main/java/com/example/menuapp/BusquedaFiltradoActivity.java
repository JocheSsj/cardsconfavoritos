package com.example.menuapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BusquedaFiltradoActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private List<String> resultados = new ArrayList<>();
    private ArrayAdapter<String> adapter, autoCompleteAdapter;
    private Spinner spinnerFiltros;
    private Set<String> sugerencias = new HashSet<>(); // Usamos Set para evitar duplicados
    private List<LatLng> ubicacionesFiltradas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_filtrado);

        // Inicializar Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Elementos UI
        AutoCompleteTextView autoCompleteBuscar = findViewById(R.id.autoCompleteTextView2);
        Button btnFiltrar = findViewById(R.id.btnFiltrar);
        ListView listViewResultados = findViewById(R.id.listViewResultados);
        spinnerFiltros = findViewById(R.id.spinnerFiltros);

        // Configurar Spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.filtros_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltros.setAdapter(spinnerAdapter);

        // Configurar adaptador de ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, resultados);
        listViewResultados.setAdapter(adapter);

        // Configurar adaptador para AutoCompleteTextView
        autoCompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>(sugerencias));
        autoCompleteBuscar.setAdapter(autoCompleteAdapter);

        // Configurar AutoCompleteTextView para filtrar resultados en tiempo real
        autoCompleteBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarDatos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Configurar el botón de filtrado
        btnFiltrar.setOnClickListener(v -> {
            String selectedFilter = spinnerFiltros.getSelectedItem().toString();
            String query = autoCompleteBuscar.getText().toString();
            filtrarPorTipo(selectedFilter, query);
        });

        cargarDatos();
    }

    private void cargarDatos() {
        // Cargar becas y agregarlas a resultados y sugerencias
        databaseReference.child("Becas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot becaSnapshot : snapshot.getChildren()) {
                    String nombre = becaSnapshot.child("nombre").getValue(String.class);
                    if (nombre != null) {
                        sugerencias.add(nombre); // Agregar a las sugerencias
                        resultados.add("Beca: " + nombre);
                    }
                }
                cargarCarreras(); // Cargar carreras después de las becas
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(BusquedaFiltradoActivity.this, "Error al cargar becas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarCarreras() {
        // Cargar carreras y agregarlas a resultados y sugerencias
        databaseReference.child("Carreras").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot carreraSnapshot : snapshot.getChildren()) {
                    String nombre = carreraSnapshot.child("nombre").getValue(String.class);
                    if (nombre != null) {
                        sugerencias.add(nombre); // Agregar a las sugerencias
                        resultados.add("Carrera: " + nombre);
                    }
                }
                autoCompleteAdapter.clear();
                autoCompleteAdapter.addAll(sugerencias); // Actualizar el adaptador de AutoCompleteTextView
                autoCompleteAdapter.notifyDataSetChanged();
                adapter.notifyDataSetChanged(); // Actualizar el ListView con resultados
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(BusquedaFiltradoActivity.this, "Error al cargar carreras", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filtrarDatos(String texto) {
        List<String> filteredResults = new ArrayList<>();
        for (String item : resultados) {
            if (item.toLowerCase().contains(texto.toLowerCase())) {
                filteredResults.add(item);
            }
        }
        updateListView(filteredResults);
    }

    private void filtrarPorTipo(String tipo, String texto) {
        List<String> filteredResults = new ArrayList<>();

        if (tipo.equals("Becas")) {
            databaseReference.child("Becas").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot becaSnapshot : snapshot.getChildren()) {
                        String nombre = becaSnapshot.child("nombre").getValue(String.class);
                        String descripcion = becaSnapshot.child("descripcion").getValue(String.class);
                        List<String> establecimientosIds = (List<String>) becaSnapshot.child("idEstablecimientos").getValue();

                        if (nombre != null && descripcion != null && (nombre.contains(texto) || descripcion.contains(texto))) {
                            StringBuilder resultadoBeca = new StringBuilder();
                            resultadoBeca.append("Beca: ").append(nombre).append("\n");
                            resultadoBeca.append("Descripción: ").append(descripcion).append("\n");
                            resultadoBeca.append("Establecimientos:\n");

                            if (establecimientosIds != null) {
                                for (String establecimientoId : establecimientosIds) {
                                    databaseReference.child("Establecimientos").child(establecimientoId)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot establecimientoSnapshot) {
                                                    String nombreEstablecimiento = establecimientoSnapshot.child("nombre").getValue(String.class);
                                                    if (nombreEstablecimiento != null) {
                                                        resultadoBeca.append("- ").append(nombreEstablecimiento).append("\n");
                                                    }
                                                    if (establecimientoId.equals(establecimientosIds.get(establecimientosIds.size() - 1))) {
                                                        filteredResults.add(resultadoBeca.toString());
                                                        updateListView(filteredResults);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError error) {
                                                    Toast.makeText(BusquedaFiltradoActivity.this, "Error al cargar establecimientos", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } else {
                                resultadoBeca.append("No hay establecimientos asociados.\n");
                                filteredResults.add(resultadoBeca.toString());
                            }
                        }
                    }
                    updateListView(filteredResults);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(BusquedaFiltradoActivity.this, "Error al cargar becas", Toast.LENGTH_SHORT).show();
                }
            });
        }else if (tipo.equals("Carreras")) {
            // Filtrar las carreras
            databaseReference.child("Carreras").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    filteredResults.clear();

                    for (DataSnapshot carreraSnapshot : snapshot.getChildren()) {
                        String nombre = carreraSnapshot.child("nombre").getValue(String.class);
                        List<String> establecimientosIds = (List<String>) carreraSnapshot.child("idEstablecimientos").getValue();

                        if (nombre != null && nombre.contains(texto)) {
                            StringBuilder resultadoCarrera = new StringBuilder();
                            resultadoCarrera.append("Carrera: ").append(nombre).append("\n");
                            resultadoCarrera.append("Establecimientos:\n");

                            // Verificar si hay IDs de establecimientos asociados
                            if (establecimientosIds != null) {
                                for (String establecimientoId : establecimientosIds) {
                                    // Consultar cada establecimiento para obtener su nombre
                                    databaseReference.child("Establecimientos").child(establecimientoId)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot establecimientoSnapshot) {
                                                    String nombreEstablecimiento = establecimientoSnapshot.child("nombre").getValue(String.class);
                                                    if (nombreEstablecimiento != null) {
                                                        resultadoCarrera.append("- ").append(nombreEstablecimiento).append("\n");
                                                    }
                                                    // Cuando se procesan todos los establecimientos, añadir el resultado
                                                    if (establecimientoId.equals(establecimientosIds.get(establecimientosIds.size() - 1))) {
                                                        filteredResults.add(resultadoCarrera.toString());
                                                        updateListView(filteredResults);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError error) {
                                                    Toast.makeText(BusquedaFiltradoActivity.this, "Error al cargar establecimientos", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } else {
                                resultadoCarrera.append("No hay establecimientos asociados.\n");
                                filteredResults.add(resultadoCarrera.toString());
                            }
                        }
                    }
                    // Actualizar el ListView después de procesar las carreras
                    updateListView(filteredResults);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(BusquedaFiltradoActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateListView(List<String> filteredResults) {
        adapter.clear();
        adapter.addAll(filteredResults);
        adapter.notifyDataSetChanged();
    }
}