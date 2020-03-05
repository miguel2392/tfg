package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AsignaturaItemView extends FrameLayout {

    private TextView name;
    private TextView alumnos;

    private Asignatura asignatura;
    public AsignaturaItemView(@NonNull Context context) {
        super(context);
        initialize(context);
    }

    public AsignaturaItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public AsignaturaItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        LayoutInflater.from(context).inflate(R.layout.asignatura_item_layout, this);
        name = findViewById(R.id.asignatura_name);
        alumnos = findViewById(R.id.n_alumnos);
        findViewById(R.id.click_container).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "on click", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindData(Asignatura asignatura) {
        this.asignatura = asignatura;
        name.setText(asignatura.name);
        alumnos.setText(asignatura.getNumberOfAlumnos() + " alumnos");
    }
}