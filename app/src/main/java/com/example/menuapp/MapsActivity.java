package com.example.menuapp;

import androidx.fragment.app.FragmentActivity;

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
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtener el SupportMapFragment y notificar cuando el mapa esté listo
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Llama al método para cargar los marcadores desde Firebase
        cargarMarcadoresDesdeFirebase();
    }

    private void cargarMarcadoresDesdeFirebase() {
        // Instancia de Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference locationsRef = database.getReference("Establecimientos");

        // Leer los datos de Firebase
        locationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                    try {
                        // Leer los datos de cada establecimiento
                        double latitude = locationSnapshot.child("latitud").getValue(Double.class);
                        double longitude = locationSnapshot.child("longitud").getValue(Double.class);
                        String name = locationSnapshot.child("nombre").getValue(String.class);
                        String type = locationSnapshot.child("tipo").getValue(String.class); // Tipo del establecimiento

                        // Personalizar el marcador según el tipo
                        BitmapDescriptor icon;
                        if ("universidad".equals(type)) {
                            icon = BitmapDescriptorFactory.fromResource(R.drawable.mar);
                        } else if ("Colegio".equals(type)) {
                            icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_cafe);
                        }else if ("Instituto".equals(type)) {
                            icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_cafe);
                        }
                        else {
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE); // Marcador predeterminado
                        }

                        // Agregar el marcador al mapa
                        LatLng position = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions()
                                .position(position)
                                .title(name)
                                .icon(icon));
                    } catch (Exception e) {
                        Log.e("Firebase", "Error al procesar los datos", e);
                    }
                }

                // Centrar la cámara en el primer punto (opcional)
                if (dataSnapshot.getChildrenCount() > 0) {
                    DataSnapshot firstLocation = dataSnapshot.getChildren().iterator().next();
                    double latitude = firstLocation.child("latitud").getValue(Double.class);
                    double longitude = firstLocation.child("longitud").getValue(Double.class);
                    LatLng firstPosition = new LatLng(latitude, longitude);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstPosition, 13));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error al leer los datos", databaseError.toException());
            }
        });
    }
}
