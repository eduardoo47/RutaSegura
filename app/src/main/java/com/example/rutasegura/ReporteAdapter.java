package com.example.rutasegura;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ReporteAdapter extends RecyclerView.Adapter<ReporteAdapter.ReporteViewHolder> {

    public interface OnReporteClickListener {
        void onReporteClick(Reporte reporte);
    }

    private final List<Reporte> listaReportes;
    private final OnReporteClickListener listener;

    public ReporteAdapter(List<Reporte> listaReportes, OnReporteClickListener listener) {
        this.listaReportes = listaReportes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReporteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reporte, parent, false);
        return new ReporteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReporteViewHolder holder, int position) {
        Reporte reporte = listaReportes.get(position);
        holder.tvTitulo.setText(reporte.getTitulo());
        holder.tvCategoria.setText("Categoría: " + reporte.getCategoria());
        holder.tvFecha.setText(reporte.getFechaHora());
        holder.tvEstado.setText(reporte.getEstado());

        holder.itemView.setOnClickListener(v -> listener.onReporteClick(reporte));
    }

    @Override
    public int getItemCount() {
        return listaReportes.size();
    }

    static class ReporteViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvCategoria, tvFecha, tvEstado;

        public ReporteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvItemTitulo);
            tvCategoria = itemView.findViewById(R.id.tvItemCategoria);
            tvFecha = itemView.findViewById(R.id.tvItemFecha);
            tvEstado = itemView.findViewById(R.id.tvItemEstado);
        }
    }
}