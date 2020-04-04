package com.example.myapplication.calificaciones;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.myapplication.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CalificacionItemView extends FrameLayout {
    private TextView name;
    private TextView nota;

    public CalificacionItemView(@NonNull final Context context) {
        super(context);
        initialize(context);
    }

    public CalificacionItemView(@NonNull final Context context,
                                @Nullable final AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public CalificacionItemView(@NonNull final Context context,
                                @Nullable final AttributeSet attrs,
                                final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        LayoutInflater.from(context).inflate(R.layout.calificaciobn_item_view_layout, this);
        name = findViewById(R.id.nota_name);
        nota = findViewById(R.id.nota_value);
    }

    public void bindData(Calificacion calificacion) {
        name.setText(calificacion.nombreAlumno);
        nota.setText(String.valueOf(calificacion.nota));
    }
}
