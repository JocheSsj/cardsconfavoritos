package com.example.menuapp;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
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
    private List<String> nombresEstablecimientos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        databaseReference = FirebaseDatabase.getInstance().getReference("Establecimientos");

        nombresEstablecimientos = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);

        }

        // Configurar el botón de salir
        ImageButton btnSalirMapa = findViewById(R.id.btnSalirMapa);
        btnSalirMapa.setOnClickListener(v -> finish()); // Cierra la actividad
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        configurarBarraBusqueda();
        cargarNombresParaAutoCompletar();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        boundsBuilder = new LatLngBounds.Builder();

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
        LatLng ubicacionSeleccionada = new LatLng(latitud, longitud);
        int imagenResourceId = seleccionarIcono(idTipoEsta);

        BitmapDescriptor iconoPersonalizado = BitmapDescriptorFactory.fromBitmap(
                redimensionarImagen(imagenResourceId, 100, 100)
        );

        mMap.addMarker(new MarkerOptions()
                .position(ubicacionSeleccionada)
                .title(nombre)
                .snippet("Dirección: " + direccion)
                .icon(iconoPersonalizado));

        // Centrar el mapa en este marcador específico
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionSeleccionada, 17));
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
                    mMap.clear();
                    boundsBuilder = new LatLngBounds.Builder();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        double latitud = snapshot.child("latitud").getValue(Double.class);
                        double longitud = snapshot.child("longitud").getValue(Double.class);
                        String direccion = snapshot.child("direccion").getValue(String.class);
                        String idTipoEsta = snapshot.child("idTipoEsta").getValue(String.class);

                        agregarMarcadorPersonalizado(latitud, longitud, nombre, direccion, idTipoEsta);
                        boundsBuilder.include(new LatLng(latitud, longitud));
                    }

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
                mMap.clear();
                boundsBuilder = new LatLngBounds.Builder();
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
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
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
    @Override
    public void onBackPressed() {
        if (mMap != null) {
            if (boundsBuilder == null || boundsBuilder.build().equals(mMap.getProjection().getVisibleRegion().latLngBounds)) {
                super.onBackPressed();
            } else {
                cargarTodosLosMarcadores();
            }
        } else {
            super.onBackPressed();
        }
    }

}
