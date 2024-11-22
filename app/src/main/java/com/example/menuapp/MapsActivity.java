package com.example.menuapp;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLngBounds.Builder boundsBuilder; // Para ajustar el zoom a todos los marcadores

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtener el fragmento del mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        boundsBuilder = new LatLngBounds.Builder(); // Inicializar el objeto para los límites

        // Verificar si hay datos del intent para un marcador único
        Intent intent = getIntent();
        double latitud = intent.getDoubleExtra("latitud", 0);
        double longitud = intent.getDoubleExtra("longitud", 0);
        String nombre = intent.getStringExtra("nombre");
        String direccion = intent.getStringExtra("direccion");
        String idTipoEsta = intent.getStringExtra("idTipoEsta");

        if (latitud != 0 && longitud != 0) {
            // Agregar un marcador único y centrar el mapa en él
            agregarMarcadorUnico(latitud, longitud, nombre, direccion, idTipoEsta);
        } else {
            // Cargar todos los marcadores y ajustar la cámara
            cargarTodosLosMarcadores();
        }
    }

    /**
     * Método para agregar un marcador único basado en los datos enviados desde otra actividad.
     */
    private void agregarMarcadorUnico(double latitud, double longitud, String nombre, String direccion, String idTipoEsta) {
        LatLng ubicacionSeleccionada = new LatLng(latitud, longitud);

        // Seleccionar la imagen del marcador según idTipoEsta
        int imagenResourceId = seleccionarIcono(idTipoEsta);

        BitmapDescriptor iconoPersonalizado = BitmapDescriptorFactory.fromBitmap(
                redimensionarImagen(imagenResourceId, 100, 100)
        );

        mMap.addMarker(new MarkerOptions()
                .position(ubicacionSeleccionada)
                .title(nombre)
                .snippet("Dirección: " + direccion)
                .icon(iconoPersonalizado));

        // Centrar el mapa en este marcador
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionSeleccionada, 17));
    }

    /**
     * Método para cargar todos los marcadores desde Firebase.
     */
    private void cargarTodosLosMarcadores() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Establecimientos");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean hayMarcadores = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Obtener datos del establecimiento
                    double latitud = snapshot.child("latitud").getValue(Double.class);
                    double longitud = snapshot.child("longitud").getValue(Double.class);
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    String direccion = snapshot.child("direccion").getValue(String.class);
                    String idTipoEsta = snapshot.child("idTipoEsta").getValue(String.class);

                    // Agregar marcador al mapa
                    agregarMarcador(latitud, longitud, nombre, direccion, idTipoEsta);

                    // Incluir el marcador en los límites
                    boundsBuilder.include(new LatLng(latitud, longitud));
                    hayMarcadores = true;
                }

                // Ajustar la cámara para mostrar todos los marcadores solo si no se centró en un marcador único
                if (hayMarcadores) {
                    LatLngBounds bounds = boundsBuilder.build();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100)); // Margen de 100px
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error al cargar los datos: " + databaseError.getMessage());
            }
        });
    }

    /**
     * Método para agregar un marcador genérico.
     */
    private void agregarMarcador(double latitud, double longitud, String nombre, String direccion, String idTipoEsta) {
        LatLng ubicacion = new LatLng(latitud, longitud);

        // Seleccionar la imagen del marcador según idTipoEsta
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

    /**
     * Método para redimensionar una imagen desde drawable.
     */
    private Bitmap redimensionarImagen(int resourceId, int ancho, int alto) {
        Bitmap imagenOriginal = BitmapFactory.decodeResource(getResources(), resourceId);
        return Bitmap.createScaledBitmap(imagenOriginal, ancho, alto, false);
    }

    /**
     * Método para seleccionar el ícono del marcador según el tipo de establecimiento.
     */
    private int seleccionarIcono(String idTipoEsta) {
        if ("Universidad".equalsIgnoreCase(idTipoEsta)) {
            return R.drawable.unimarket;
        } else if ("Instituto".equalsIgnoreCase(idTipoEsta)) {
            return R.drawable.institutomarket;
        } else if ("Colegio".equalsIgnoreCase(idTipoEsta)) {
            return R.drawable.colegiomarket;
        }
        return R.drawable.iconouni; // Icono predeterminado
    }
}
