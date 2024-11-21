package com.example.menuapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UpdateActivity extends AppCompatActivity {

    private EditText editBuscarId, editNombre, editDireccion, editTipo, editDescripcion, editTelefono, editFavorito, editUrlEstablecimiento, editLogo;
    private Button btnBuscar, btnModificar;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        // Inicializar vistas
        editBuscarId = findViewById(R.id.editBuscarId);
        editNombre = findViewById(R.id.editNombre);
        editDireccion = findViewById(R.id.editDireccion);
        editTipo = findViewById(R.id.editTipo);
        editDescripcion = findViewById(R.id.editDescripcion);
        editTelefono = findViewById(R.id.editTelefono);
        editFavorito = findViewById(R.id.editFavorito);
        editUrlEstablecimiento = findViewById(R.id.editUrlEstablecimiento);
        editLogo = findViewById(R.id.editLogo);

        btnModificar = findViewById(R.id.btnModificar);

        dbHelper = new DbHelper(this);


        // Acción del botón Modificar
        btnModificar.setOnClickListener(view -> {
            int id = Integer.parseInt(editBuscarId.getText().toString());
            String nombre = editNombre.getText().toString();
            String direccion = editDireccion.getText().toString();
            String tipo = editTipo.getText().toString();
            String descripcion = editDescripcion.getText().toString();
            String telefono = editTelefono.getText().toString();
            int favorito = Integer.parseInt(editFavorito.getText().toString());
            String urlEstablecimiento = editUrlEstablecimiento.getText().toString();
            String logo = editLogo.getText().toString();

            int rowsAffected = dbHelper.modificarEstablecimiento(id, nombre, direccion, tipo, descripcion, telefono, favorito, urlEstablecimiento, logo);
            if (rowsAffected > 0) {
                Toast.makeText(UpdateActivity.this, "Establecimiento modificado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(UpdateActivity.this, "Error al modificar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
