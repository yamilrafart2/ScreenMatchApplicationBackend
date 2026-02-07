package com.aluracursos.screenmatch.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ConsultaGemini {

    public static String obtenerTraduccion(String texto) {
        String apiKey = "API_KEY_AQUÍ";

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

        HttpClient client = HttpClient.newHttpClient();

        // Prompt simple para evitar filtros de seguridad
        String jsonBody = """
                {
                  "contents": [{
                    "parts":[{"text": "Translate to Spanish: %s"}]
                  }]
                }
                """.formatted(texto);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Imprime el estado para debug
            // System.out.println("Intento con Gemini 2.0 Standard - Status: " + response.statusCode());

            if (response.statusCode() == 200) {
                JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
                return jsonObject.getAsJsonArray("candidates")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("content")
                        .getAsJsonArray("parts")
                        .get(0).getAsJsonObject()
                        .get("text").getAsString().trim();
            } else {
                // Si falla, imprime el cuerpo del error
                System.err.println("Fallo API: " + response.body());
                return texto;
            }
        } catch (Exception e) {
            System.err.println("Error crítico: " + e.getMessage());
            return texto;
        }
    }
}