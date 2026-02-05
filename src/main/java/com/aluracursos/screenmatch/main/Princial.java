package com.aluracursos.screenmatch.main;

import com.aluracursos.screenmatch.model.DatosEpisodio;
import com.aluracursos.screenmatch.model.DatosSerie;
import com.aluracursos.screenmatch.model.DatosTemporada;
import com.aluracursos.screenmatch.service.ConsumoApi;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Princial {

    private Scanner teclado = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=f39d2af5";
    private ConvierteDatos conversor = new ConvierteDatos();

    public void muestraMenu() {
        System.out.println();
        System.out.print("Por favor escriba el nombre de la serie que desea buscar: ");
        var nombreSerie = teclado.nextLine();

        // Buscar los datos generales de la serie
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") +  API_KEY);
        System.out.println();
        System.out.println(json);
        var datosSerie = conversor.obtenerDatos(json, DatosSerie.class);
        System.out.println(datosSerie);

        // Buscar los datos de todas las temporadas
        List<DatosTemporada> temporadas = new ArrayList<>();
        for (int i = 1; i <= datosSerie.totalDeTemporadas(); i++) {
            json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + "&Season=" + i + API_KEY);
            var datosTemporadas = conversor.obtenerDatos(json, DatosTemporada.class);
            temporadas.add(datosTemporadas);
        }
        System.out.println();
        temporadas.forEach(System.out::println);

        // Mostrar solo el título de los episodios para las temporadas
//        System.out.println();
//        for (int i = 0; i < datosSerie.totalDeTemporadas(); i++) {
//            System.out.println();
//            List<DatosEpisodio> episodiosTemporadas = temporadas.get(i).episodios();
//            for (int j = 0; j < episodiosTemporadas.size(); j++) {
//                System.out.println(episodiosTemporadas.get(j).titulo());
//            }
//        }

        // Mostrar solo el título de los episodios para las temporadas -> con funciones lambdas
        System.out.println();
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

    }

}
