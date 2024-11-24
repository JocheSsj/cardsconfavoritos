package com.example.menuapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

public class ColegiosAdapter extends RecyclerView.Adapter<ColegiosAdapter.ViewHolder> {

    private List<Establecimiento> establecimientos;

    public ColegiosAdapter() {
        this.establecimientos = new ArrayList<>();
        cargarDatosFirebase(); // Cargar datos desde Firebase Realtime Database
    }

    // Cargar datos desde Firebase Realtime Database
    private void cargarDatosFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Establecimientos");

        // Consultar solo los establecimientos con tipo "Colegio"
        databaseReference.orderByChild("idTipoEsta").equalTo("Colegio")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        establecimientos.clear(); // Limpiar la lista para evitar duplicados
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Establecimiento establecimiento = data.getValue(Establecimiento.class);
                            if (establecimiento != null) {
                                establecimiento.setId(data.getKey()); // Establecer la clave única
                                establecimientos.add(establecimiento);
                            }
                        }
                        notifyDataSetChanged(); // Notificar al RecyclerView que los datos han cambiado
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_establecimiento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Establecimiento establecimiento = establecimientos.get(position);

        // Configurar textos
        holder.tvNombre.setText(establecimiento.getNombre());
        holder.tvDireccion.setText(establecimiento.getDireccion());
        holder.tvTipo.setText(establecimiento.getTipo());
        holder.tvDescripcion.setText(establecimiento.getDescripcion());

        // Configurar el estado inicial del botón favorito
        holder.btnFavorito.setImageResource(
                establecimiento.getFavorito() == 1
                        ? R.drawable.baseline_star_rate_24
                        : R.drawable.vaciastar_24
        );

        // Manejar clic en el botón favorito
        holder.btnFavorito.setOnClickListener(v -> {
            boolean esFavorito = establecimiento.getFavorito() == 1;

            // Cambiar el estado de favorito en el objeto
            establecimiento.setFavorito(esFavorito ? 0 : 1);

            // Actualizar el icono
            holder.btnFavorito.setImageResource(
                    esFavorito ? R.drawable.vaciastar_24 : R.drawable.baseline_star_rate_24
            );

            // Actualizar el cambio en Firebase Realtime Database
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Establecimientos");
            databaseReference.child(establecimiento.getId()).child("favorito").setValue(establecimiento.getFavorito());
        });

        // Manejar clic en el botón "Ver Más"
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
        return establecimientos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDireccion, tvTipo, tvDescripcion;
        ImageButton btnFavorito;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            btnFavorito = itemView.findViewById(R.id.btnFavorito); // Botón de favoritos
        }
    }
}
