package com.example.myapplication;

import java.util.List;

public class Asignatura {

    final String id;
    final String name;
    final List<String> alumnos;
    final List<String> profesores;

    public Asignatura(String id, String name, List<String> alumnos, List<String> profesores) {
        this.id = id;
        this.name = name;
        this.alumnos = alumnos;
        this.profesores = profesores;
    }

    public int getNumberOfAlumnos() {
        return alumnos.size();
    }
}
