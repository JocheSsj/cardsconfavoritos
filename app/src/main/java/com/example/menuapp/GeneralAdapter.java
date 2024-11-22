package com.example.menuapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GeneralAdapter extends RecyclerView.Adapter<GeneralAdapter.ViewHolder> {

    private List<Establecimiento> establecimientoList;
    private DatabaseReference databaseReference;
    private Context context;

    public GeneralAdapter() {
        this.context = context;
        this.establecimientoList = new ArrayList<>();
        this.databaseReference = FirebaseDatabase.getInstance().getReference("Establecimientos");
        cargarDatosDesdeFirebase();
    }
    private void cargarDatosDesdeFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                establecimientoList.clear(); // Limpiar lista antes de agregar nuevos datos
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Establecimiento establecimiento = dataSnapshot.getValue(Establecimiento.class); // Convertir nodo en objeto Establecimiento
                    if (establecimiento != null) { // Verificar que no sea nulo
                        establecimiento.setId(dataSnapshot.getKey()); // Asignar la clave única al objeto
                        establecimientoList.add(establecimiento); // Agregar a la lista
                    }
                }
                notifyDataSetChanged(); // Notificar cambios al RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores si es necesario
            }
        });

    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_favoritos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Establecimiento establecimiento = establecimientoList.get(position);

        // Configurar los datos
        holder.tvNombre.setText(establecimiento.getNombre());
        holder.tvDireccion.setText(establecimiento.getDireccion());
        holder.tvDescripcion.setText(establecimiento.getDescripcion());

        // Cargar imagen usando Glide

        // Configurar el botón favorito
        holder.btnFavorito.setImageResource(
                establecimiento.getFavorito() == 1
                        ? R.drawable.baseline_star_rate_24
                        : R.drawable.vaciastar_24
        );

        holder.btnFavorito.setOnClickListener(v -> {
            boolean esFavorito = establecimiento.getFavorito() == 1;
            establecimiento.setFavorito(esFavorito ? 0 : 1);
            holder.btnFavorito.setImageResource(
                    esFavorito ? R.drawable.vaciastar_24 : R.drawable.baseline_star_rate_24
            );

            // Actualizar en Firebase
            databaseReference.child(establecimiento.getId()).child("favorito").setValue(establecimiento.getFavorito());
        });
        holder.itemView.findViewById(R.id.btnmas).setOnClickListener(v -> {
            String idEstablecimiento = establecimiento.getId(); // Usar la clave única

            // Iniciar la actividad Detalles y enviar el ID del establecimiento
            Intent intent = new Intent(holder.itemView.getContext(), Detalles.class);
            intent.putExtra("id", idEstablecimiento); // Enviar la clave única del establecimiento
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return establecimientoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDireccion, tvDescripcion;
        ImageView ivImagen;
        ImageButton btnFavorito;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            btnFavorito = itemView.findViewById(R.id.btnFavorito);
        }
    }
}
