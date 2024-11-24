package com.example.menuapp;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLngBounds.Builder boundsBuilder;
    private DatabaseReference databaseReference;
    private AutoCompleteTextView autoCompleteTextView;
    private List<String> nombresEstablecimientos; // Lista para autocompletar nombres

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Inicializar Firebase y elementos
        databaseReference = FirebaseDatabase.getInstance().getReference("Establecimientos");
        nombresEstablecimientos = new ArrayList<>();

        // Configurar el fragmento del mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Configurar el AutoCompleteTextView
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        configurarBarraBusqueda();
        cargarNombresParaAutoCompletar(); // Cargar nombres de Firebase para autocompletar
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        boundsBuilder = new LatLngBounds.Builder();

        // Verificar datos del Intent
        Intent intent = getIntent();
        double latitud = intent.getDoubleExtra("latitud", 0);
        double longitud = intent.getDoubleExtra("longitud", 0);
        String nombre = intent.getStringExtra("nombre");
        String direccion = intent.getStringExtra("direccion");
        String idTipoEsta = intent.getStringExtra("idTipoEsta");

        if (latitud != 0 && longitud != 0) {
            agregarMarcadorUnico(latitud, longitud, nombre, direccion, idTipoEsta);
        } else {
            cargarTodosLosMarcadores();
        }
    }

    private void agregarMarcadorUnico(double latitud, double longitud, String nombre, String direccion, String idTipoEsta) {
    }

    private void configurarBarraBusqueda() {
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String nombreSeleccionado = (String) parent.getItemAtPosition(position);
            buscarYMostrarLugar(nombreSeleccionado);
        });
    }

    private void buscarYMostrarLugar(String nombre) {
        databaseReference.orderByChild("nombre").equalTo(nombre).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mMap.clear(); // Limpiar el mapa antes de mostrar el lugar
                    boundsBuilder = new LatLngBounds.Builder(); // Reiniciar boundsBuilder

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        double latitud = snapshot.child("latitud").getValue(Double.class);
                        double longitud = snapshot.child("longitud").getValue(Double.class);
                        String direccion = snapshot.child("direccion").getValue(String.class);
                        String idTipoEsta = snapshot.child("idTipoEsta").getValue(String.class);

                        agregarMarcadorPersonalizado(latitud, longitud, nombre, direccion, idTipoEsta);

                        // Incluir el marcador en los límites
                        boundsBuilder.include(new LatLng(latitud, longitud));
                    }

                    // Ajustar la cámara para mostrar el marcador único
                    ajustarCamaraATodosLosMarcadores();
                } else {
                    Toast.makeText(MapsActivity.this, "No se encontró el lugar.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MapsActivity.this, "Error al buscar el lugar.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarNombresParaAutoCompletar() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nombresEstablecimientos.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    if (nombre != null) {
                        nombresEstablecimientos.add(nombre);
                    }
                }

                // Configurar el adaptador para el AutoCompleteTextView
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MapsActivity.this,
                        android.R.layout.simple_dropdown_item_1line, nombresEstablecimientos);
                autoCompleteTextView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error al cargar nombres: " + databaseError.getMessage());
            }
        });
    }

    private void cargarTodosLosMarcadores() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMap.clear(); // Limpiar el mapa antes de cargar nuevos marcadores
                boundsBuilder = new LatLngBounds.Builder(); // Reiniciar boundsBuilder
                boolean hayMarcadores = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    double latitud = snapshot.child("latitud").getValue(Double.class);
                    double longitud = snapshot.child("longitud").getValue(Double.class);
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    String direccion = snapshot.child("direccion").getValue(String.class);
                    String idTipoEsta = snapshot.child("idTipoEsta").getValue(String.class);

                    agregarMarcadorPersonalizado(latitud, longitud, nombre, direccion, idTipoEsta);
                    boundsBuilder.include(new LatLng(latitud, longitud));
                    hayMarcadores = true;
                }

                if (hayMarcadores) {
                    ajustarCamaraATodosLosMarcadores();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error al cargar datos: " + databaseError.getMessage());
            }
        });
    }

    private void agregarMarcadorPersonalizado(double latitud, double longitud, String nombre, String direccion, String idTipoEsta) {
        LatLng ubicacion = new LatLng(latitud, longitud);
        int imagenResourceId = seleccionarIcono(idTipoEsta);

        BitmapDescriptor iconoPersonalizado = BitmapDescriptorFactory.fromBitmap(
                redimensionarImagen(imagenResourceId, 100, 100)
        );

        mMap.addMarker(new MarkerOptions()
                .position(ubicacion)
                .title(nombre)
                .snippet("Dirección: " + direccion)
                .icon(iconoPersonalizado));
    }

    private void ajustarCamaraATodosLosMarcadores() {
        if (boundsBuilder != null) {
            LatLngBounds bounds = boundsBuilder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100)); // Margen de 100px
        }
    }

    private Bitmap redimensionarImagen(int resourceId, int ancho, int alto) {
        Bitmap imagenOriginal = BitmapFactory.decodeResource(getResources(), resourceId);
        return Bitmap.createScaledBitmap(imagenOriginal, ancho, alto, false);
    }

    private int seleccionarIcono(String idTipoEsta) {
        if ("Universidad".equalsIgnoreCase(idTipoEsta)) {
            return R.drawable.unimarket;
        } else if ("Instituto".equalsIgnoreCase(idTipoEsta)) {
            return R.drawable.institutomarket;
        } else if ("Colegio".equalsIgnoreCase(idTipoEsta)) {
            return R.drawable.colegiomarket;
        }
        return R.drawable.iconouni;
    }
}
