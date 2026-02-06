package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.DatosEpisodio;
import com.aluracursos.screenmatch.model.DatosSerie;
import com.aluracursos.screenmatch.model.DatosTemporada;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.service.ConsumoApi;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

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
//        System.out.println();
//        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        // Convertir toda la información a una lista del tipo DatosEpisodio
        List<DatosEpisodio> datosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                        .collect(Collectors.toList())
        ;

        // Top 5 episodios
        System.out.println();
        System.out.println("Top 5 episodios:");
        datosEpisodios.stream()
                .filter(e -> !e.evaluacion().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("Primer filtro (N/A): " + e))
                .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())
//                .peek(e -> System.out.println("Segundo filtro ordena (M>m): " + e))
                .map(e -> e.titulo().toUpperCase())
//                .peek(e -> System.out.println("Tercer filtro mayúsculas (m>M): " + e))
                .limit(5)
                .forEach(System.out::println)
        ;

        // Convertir los datos a una lista del tipo Episodio
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d)))
                .collect(Collectors.toList())
        ;
        System.out.println();
        episodios.forEach(System.out::println);

        // Buscar episodios a partir de X año
        System.out.print("\nPor favor indica el año desde el cual deseas ver los episodios: ");
        var anio = teclado.nextInt();
        teclado.nextLine();

        LocalDate fechaBusqueda = LocalDate.of(anio, 1, 1);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        episodios.stream()
                .filter(e -> e.getFechaLanzamiento() != null && e.getFechaLanzamiento().isAfter(fechaBusqueda))
                .forEach(e -> System.out.println(
                        "Temporada: " + e.getTemporada() +
                                ", Episodio: " + e.getTitulo() +
                                ", Fecha de lanzamiento: " + e.getFechaLanzamiento().format(dtf)
                ))
        ;

        // Buscar episodios por parte del título
        System.out.print("\nPor favor escriba el título del episodio que desea ver: ");
        var parteTitulo = teclado.nextLine();
        Optional<Episodio> episodioBuscado = episodios.stream()
                .filter(e -> e.getTitulo().toLowerCase().contains(parteTitulo.toLowerCase()))
                .findFirst()
        ;
        if (episodioBuscado.isPresent()) {
            System.out.println("Episodio encontrado!");
            System.out.println("Los datos son: " + episodioBuscado.get());
        } else {
            System.out.println("Episodio NO encontrado!");
        }

        Map<Integer, Double> evaluacionesPorTemporada = episodios.stream()
                .filter(e -> e.getEvaluacion() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada, Collectors.averagingDouble(Episodio::getEvaluacion)))
        ;
        System.out.println();
        System.out.println(evaluacionesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getEvaluacion() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getEvaluacion))
        ;
        System.out.println();
        System.out.println("Media de las evaluaciones: " + est.getAverage());
        System.out.println("Episodio mejor evaluado: " + est.getMax());
        System.out.println("Episodio peor evaluado: " +  est.getMin());

    }

}
