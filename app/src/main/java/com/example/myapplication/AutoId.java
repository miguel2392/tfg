package com.example.myapplication;


import java.util.Random;

public class AutoId {

    private Random rand = new Random();
    private static final String alfabeto = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public String autoId(int idSize) {
            StringBuilder builder = new StringBuilder();
            int maxRandom = alfabeto.length();

            for(int i = 0; i < idSize; ++i) {
            builder.append(alfabeto.charAt(rand.nextInt(maxRandom)));
            }

            return builder.toString();
            }
}
