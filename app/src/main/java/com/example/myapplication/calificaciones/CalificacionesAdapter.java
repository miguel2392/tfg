package com.example.myapplication.calificaciones;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class CalificacionesAdapter extends RecyclerView.Adapter<CalificacionesAdapter.ViewHolder> {

    private List<Calificacion> calificaciones;

    public CalificacionesAdapter() {
        calificaciones = new ArrayList<>();
    }

    public void setCalificaciones(List<Calificacion> calificaciones) {
        CalificacionesDiffUtil calificacionesDiffUtil = new CalificacionesDiffUtil(this.calificaciones, calificaciones);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(calificacionesDiffUtil);
        this.calificaciones.clear();
        this.calificaciones.addAll(calificaciones);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        CalificacionItemView itemView = new CalificacionItemView(parent.getContext());
        itemView.setLayoutParams(layoutParams);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.calificacionItemView.bindData(calificaciones.get(position));
    }

    @Override
    public int getItemCount() {
        return calificaciones.size();
    }

    public double getMedia() {
        int nItems = calificaciones.size();
        if (nItems == 0) {
            return -1;
        } else {
            long sum = 0;
            for (Calificacion calificacion : calificaciones) {
                sum += calificacion.nota;
            }

            return sum / ((double) nItems);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CalificacionItemView calificacionItemView;

        ViewHolder(final CalificacionItemView calificacionItemView) {
            super(calificacionItemView);
            this.calificacionItemView = calificacionItemView;
        }
    }

    class CalificacionesDiffUtil extends DiffUtil.Callback {
        private final List<Calificacion> oldList;
        private final List<Calificacion> newList;

        public CalificacionesDiffUtil(final List<Calificacion> oldList,
                                      final List<Calificacion> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(final int oldItemPosition, final int newItemPosition) {
            return oldList.get(oldItemPosition).nombreAlumno.equals(newList.get(newItemPosition).nombreAlumno);
        }

        @Override
        public boolean areContentsTheSame(final int oldItemPosition, final int newItemPosition) {
            return oldList.get(oldItemPosition).nota == newList.get(newItemPosition).nota;
        }
    }
}
