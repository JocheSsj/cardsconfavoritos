package com.example.menuapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

import com.example.menuapp.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private Button btnCrear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configuración de ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializa Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("PruebaAndroid");

        myRef.setValue("Conexión exitosa").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(MainActivity.this, "Conectado a Firebase", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Error al conectar con Firebase", Toast.LENGTH_LONG).show();
            }
        });

        // Configuración de la barra de herramientas
        setSupportActionBar(binding.appBarMain.toolbar);

        // Redirigir desde textView4 a PuntosActivity
        TextView textView4 = findViewById(R.id.textView4);
        textView4.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        });
        TextView textView5 = findViewById(R.id.textView5);
        textView5.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GeneralVista.class);
            startActivity(intent);
        });

        // Configurar DrawerLayout y NavigationView
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Manejo de opciones de navegación en un solo bloque
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_Insert) {
                Intent intent = new Intent(MainActivity.this, InsertActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_Uni) {
                Intent intent = new Intent(MainActivity.this, Universidades.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_Instituto) {
                Intent intent = new Intent(MainActivity.this, institutovis.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_Colegio) {
                Intent intent = new Intent(MainActivity.this, ColegioVista.class);
                startActivity(intent);
                return true;
            }else if (id == R.id.nav_Modificar) {
                Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
                startActivity(intent);
                return true;
            }else if (id == R.id.nav_Favoritos) {
                Intent intent = new Intent(MainActivity.this, FavoritosVista.class);
                startActivity(intent);
                return true;
            }
            else if (id == R.id.nav_gallery) {
                Intent intent = new Intent(MainActivity.this, GeneralVista.class);
                startActivity(intent);
                return true;
            }

            // Usar el controlador de navegación para manejar otros casos
            return NavigationUI.onNavDestinationSelected(item, navController)
                    || super.onOptionsItemSelected(item);
        });

        // Configurar botón para crear la base de datos


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}