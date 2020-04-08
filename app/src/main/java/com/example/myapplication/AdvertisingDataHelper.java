package com.example.myapplication;

import androidx.core.util.Pair;

public class AdvertisingDataHelper {
    private static String header = "AAA";
    private static int sizeAsignatura = 4;
    private static int sizePresentacion = 20;


    public static String generateDeviceName (String idAsignatura, String idPresentacion){
        return header+idAsignatura+idPresentacion;
    }

    public static Pair <String,String> recoverIds (String deviceName){
        if (deviceName == null) {return null;}
        if (deviceName.length()!= header.length()+sizePresentacion+sizeAsignatura) {return null;}
        String extractedheader = deviceName.substring(0,header.length());
        if (!extractedheader.equals(header)){return null;}
        String idAsignatura = deviceName.substring(header.length(),6);
        String idPresentacion = deviceName.substring(9,17);
        return new Pair<>(idAsignatura,idPresentacion);
    }
}
