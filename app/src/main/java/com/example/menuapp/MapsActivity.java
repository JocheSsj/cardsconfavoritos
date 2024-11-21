package com.example.menuapp;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.menuapp.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Agregar primer marcador con una imagen personalizada
        Bitmap imagenMarcador = BitmapFactory.decodeResource(getResources(), R.drawable.santomarket);
        LatLng SantoTomas = new LatLng(-29.908632, -71.257537);
        MarkerOptions opcionesMarcadorSantoTomas = new MarkerOptions()
                .position(SantoTomas)
                .title("Universidad Santo Tomas")
                .snippet("Ruta 5, Universidad\nUniversidad Santo Tomas inscritos a la gratuitas")
                .icon(BitmapDescriptorFactory.fromBitmap(imagenMarcador));

        mMap.addMarker(opcionesMarcadorSantoTomas);

        // Agregar un segundo marcador
        LatLng Inacap = new LatLng(-29.915682, -71.250084);
        MarkerOptions opcionesMarcadorInacap = new MarkerOptions()
                .position(Inacap)
                .title("INACAP La Serena")
                .snippet("Dirección: Av. Francisco de Aguirre, La Serena")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)); // Marcador azul predeterminado

        mMap.addMarker(opcionesMarcadorInacap);

        // Agregar un tercer marcador
        LatLng Colegio = new LatLng(-29.920492, -71.253876);
        MarkerOptions opcionesMarcadorColegio = new MarkerOptions()
                .position(Colegio)
                .title("Colegio Inglés Católico")
                .snippet("Dirección: Calle Los Arrayanes, La Serena")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)); // Marcador rojo predeterminado

        mMap.addMarker(opcionesMarcadorColegio);

        // Mover la cámara para mostrar el primer marcador
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SantoTomas, 13)); // Zoom inicial para visualizar todos los marcadores
    }
}