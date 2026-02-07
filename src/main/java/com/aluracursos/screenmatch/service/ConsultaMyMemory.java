package com.aluracursos.screenmatch.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class ConsultaMyMemory {

    // API de MyMemory (Translated.net)

    public static String obtenerTraduccion(String texto) {
        // Configuración de la URL base
        // langpair indica traducir del Inglés (en) al Español (es)
        String urlBase = "https://api.mymemory.translated.net/get?q=";
        String langPair = "&langpair=en%7Ces";

        try {
            // 1. CODIFICACIÓN para GET:
            // Conversión de espacios y caracteres especiales a formato URL (ej: "Breaking Bad" -> "Breaking%20Bad")
            String textoCodificado = URLEncoder.encode(texto, StandardCharsets.UTF_8);

            // Construcción de la URL final
            String urlFinal = urlBase + textoCodificado + langPair;

            // 2. CREACIÓN DEL CLIENTE
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlFinal))
                    .GET()
                    .build();

            // 3. ENVÍO Y RESPUESTA
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // 4. PARSEO DEL JSON
                // La estructura es: { "responseData": { "translatedText": "Texto Traducido" } }
                JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
                return jsonObject.getAsJsonObject("responseData")
                        .get("translatedText").getAsString();
            } else {
                System.out.println("Error en la API de traducción: " + response.statusCode());
                return texto;
            }

        } catch (Exception e) {
            // System.err.println("Error al conectar con MyMemory: " + e.getMessage());
            return texto; // Si falla, devuelve el texto original
        }
    }
}