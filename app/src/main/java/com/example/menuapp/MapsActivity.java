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
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.menuapp.databinding.ActivityMapsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps); // Asegúrate de usar el nombre correcto del layout

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

        // Obtener datos enviados desde Detalles.java
        Intent intent = getIntent();
        double latitud = intent.getDoubleExtra("latitud", 0);
        double longitud = intent.getDoubleExtra("longitud", 0);
        String nombre = intent.getStringExtra("nombre");
        String direccion = intent.getStringExtra("direccion");
        String idTipoEsta = intent.getStringExtra("idTipoEsta"); // Recibe el idTipoEsta

        // Crear la ubicación seleccionada
        LatLng ubicacionSeleccionada = new LatLng(latitud, longitud);

        // Seleccionar la imagen del marcador según idTipoEsta
        int imagenResourceId = R.drawable.iconouni; // Marcador predeterminado
        if ("Universidad".equalsIgnoreCase(idTipoEsta)) {
            imagenResourceId = R.drawable.unimarket; // Imagen para Universidad
        } else if ("Instituto".equalsIgnoreCase(idTipoEsta)) {
            imagenResourceId = R.drawable.institutomarket; // Imagen para Instituto
        } else if ("Colegio".equalsIgnoreCase(idTipoEsta)) {
            imagenResourceId = R.drawable.colegiomarket; // Imagen para Colegio
        }

        // Crear un BitmapDescriptor personalizado desde la imagen seleccionada
        BitmapDescriptor iconoPersonalizado = BitmapDescriptorFactory.fromBitmap(
                redimensionarImagen(imagenResourceId, 100, 100)
        );

        // Agregar un marcador único para el establecimiento seleccionado
        mMap.addMarker(new MarkerOptions()
                .position(ubicacionSeleccionada)
                .title(nombre)
                .snippet("Dirección: " + direccion)
                .icon(iconoPersonalizado)); // Usar el icono personalizado

        // Centrar el mapa en el marcador y hacer zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionSeleccionada, 17));
    }

    /**
     * Método para redimensionar una imagen desde drawable.
     *
     * @param resourceId ID del recurso drawable
     * @param ancho Nuevo ancho del bitmap
     * @param alto Nuevo alto del bitmap
     * @return Bitmap redimensionado
     */
    private Bitmap redimensionarImagen(int resourceId, int ancho, int alto) {
        Bitmap imagenOriginal = BitmapFactory.decodeResource(getResources(), resourceId);
        return Bitmap.createScaledBitmap(imagenOriginal, ancho, alto, false);
    }



}
