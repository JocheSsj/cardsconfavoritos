package com.example.menuapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class InsertActivity extends AppCompatActivity {

    private EditText editNombre, editDireccion, editTipo, editDescripcion, editTelefono, editFavorito, editUrlEstablecimiento, editLogo;
    private Button btnInsertar;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        // Referencias a los EditText y Button
        editNombre = findViewById(R.id.editNombre);
        editDireccion = findViewById(R.id.editDireccion);
        editTipo = findViewById(R.id.editTipo);
        editDescripcion = findViewById(R.id.editDescripcion);
        editTelefono = findViewById(R.id.editTelefono);
        editFavorito = findViewById(R.id.editFavorito);
        editUrlEstablecimiento = findViewById(R.id.editUrlEstablecimiento);
        editLogo = findViewById(R.id.editLogo);
        btnInsertar = findViewById(R.id.btnInsertar);

        // Instancia de DbHelper
        dbHelper = new DbHelper(this);

        // Acción al hacer clic en el botón de insertar
        btnInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores de los EditText
                String nombre = editNombre.getText().toString();
                String direccion = editDireccion.getText().toString();
                String tipo = editTipo.getText().toString();
                String descripcion = editDescripcion.getText().toString();
                String telefono = editTelefono.getText().toString();
                int favorito = Integer.parseInt(editFavorito.getText().toString());
                String urlEstablecimiento = editUrlEstablecimiento.getText().toString();
                String logo = editLogo.getText().toString();

                // Insertar en la base de datos
                long id = dbHelper.insertarEstablecimiento(nombre, direccion, tipo, descripcion, telefono, favorito, urlEstablecimiento, logo);

                if (id > 0) {
                    Toast.makeText(InsertActivity.this, "Establecimiento insertado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(InsertActivity.this, "Error al insertar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}