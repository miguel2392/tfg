package com.example.myapplication.calificaciones;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.myapplication.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CalificacionesListView extends FrameLayout {

    private ProgressBar loader;
    private TextView textView;
    private CalificacionesAdapter adapter;

    public CalificacionesListView(@NonNull final Context context) {
        super(context);
        initialize(context);
    }

    public CalificacionesListView(@NonNull final Context context,
                                  @Nullable final AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public CalificacionesListView(@NonNull final Context context,
                                  @Nullable final AttributeSet attrs,
                                  final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {

        LayoutInflater.from(context).inflate(R.layout.calificaciones_list_view_layout, this);
        loader = findViewById(R.id.loader);
        textView = findViewById(R.id.n_votes_text_view);
        RecyclerView recyclerView = findViewById(R.id.calificaciones_rv);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL
                , false));
        adapter = new CalificacionesAdapter();
        recyclerView.setAdapter(adapter);
    }

    public double getMedia() {
        return adapter.getMedia();
    }

    public void showLoader() {
        loader.setVisibility(VISIBLE);
    }

    public void hideLoader() {
        loader.setVisibility(GONE);
    }

    public void setCalificaciones(List<Calificacion> calificaciones) {

        textView.setText("Votos totales: " + calificaciones.size());
        adapter.setCalificaciones(calificaciones);
    }
}
