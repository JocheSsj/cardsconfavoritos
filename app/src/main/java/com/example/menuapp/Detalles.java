package com.example.menuapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Detalles extends AppCompatActivity {

    private TextView tvNombre, tvDireccion, tvTipo, tvDescripcion;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();

        // Obtener referencias a los TextView
        tvNombre = findViewById(R.id.tvDetalleNombre);
        tvDireccion = findViewById(R.id.tvDetalleDireccion);
        tvTipo = findViewById(R.id.tvDetalleTipo);
        tvDescripcion = findViewById(R.id.tvDetalleDescripcion);

        // Obtener el nombre enviado desde el intent
        String nombre = getIntent().getStringExtra("nombre");

        // Consultar datos desde Firebase según el nombre
        cargarDatosDesdeFirebase(nombre);
    }

    private void cargarDatosDesdeFirebase(String nombre) {
        db.collection("Establecimientos")
                .whereEqualTo("nombre", nombre)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Obtener el primer documento que coincide
                        for (QueryDocumentSnapshot documento : queryDocumentSnapshots) {
                            // Mostrar los datos en los TextView
                            tvNombre.setText(documento.getString("nombre"));
                            tvDireccion.setText(documento.getString("direccion"));
                            tvTipo.setText(documento.getString("idTipoEsta"));
                            tvDescripcion.setText(documento.getString("descripcion")); // Asegúrate de que la clave sea correcta
                            break; // Salir del bucle tras procesar el primer documento
                        }
                    } else {
                        tvNombre.setText("No se encontraron datos.");
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejo de errores
                    tvNombre.setText("Error al cargar los datos");
                });
    }
}
