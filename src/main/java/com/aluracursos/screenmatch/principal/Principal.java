package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.DatosSerie;
import com.aluracursos.screenmatch.model.DatosTemporada;
import com.aluracursos.screenmatch.model.Serie;
import com.aluracursos.screenmatch.repository.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoApi;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private Scanner teclado = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=f39d2af5";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSeries = new ArrayList<>();
    private SerieRepository repositorio;

    public Principal(SerieRepository repository) {
        this.repositorio = repository;
    }

    public void muestraMenu() {

        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar series
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    
                    0 - Salir
                    """
            ;
            System.out.println("\n==================== MENÚ ====================");
            System.out.println(menu);
            System.out.print("Ingrese una opción: ");
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    System.out.println("==============================================");
                    System.out.println("\n====================== 1. ====================");
                    buscarSerieWeb();
                    System.out.println("==============================================");
                    break;
                case 2:
                    System.out.println("==============================================");
                    System.out.println("\n====================== 2. ====================");
                    buscarEpisodioPorSerie();
                    System.out.println("==============================================");
                    break;
                case 3:
                    System.out.println("==============================================");
                    System.out.println("\n====================== 3. ====================");
                    mostrarSeriesBuscadas();
                    System.out.println("==============================================");
                    break;
                case 0:
                    System.out.println("==============================================");
                    System.out.println("\n==============================================");
                    System.out.println("Cerrando la aplicación...");
                    System.out.println("==============================================");
                    break;
                default:
                    System.out.println("==============================================");
                    System.out.println("\n==============================================");
                    System.out.println("Opción inválida!");
                    System.out.println("==============================================");
            }
        }

    }

    private DatosSerie getDatosSerie() {

        System.out.print("Por favor escriba el nombre de la serie que desea buscar: ");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        System.out.println(json);
        DatosSerie datosSerie = conversor.obtenerDatos(json, DatosSerie.class);
        return datosSerie;

    }

    private void buscarEpisodioPorSerie() {

        DatosSerie datosSerie = getDatosSerie();
        List<DatosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= datosSerie.totalDeTemporadas(); i++) {
            var json = consumoApi.obtenerDatos(URL_BASE + datosSerie.titulo().replace(" ", "+") + API_KEY);
            DatosTemporada datosTemporada = conversor.obtenerDatos(json, DatosTemporada.class);
            temporadas.add(datosTemporada);
        }

        temporadas.forEach(System.out::println);

    }

    private void buscarSerieWeb() {

        DatosSerie datos = getDatosSerie();
        //datosSeries.add(datos);
        Serie serie = new Serie(datos);
        repositorio.save(serie);
        System.out.println(datos);

    }

    private void mostrarSeriesBuscadas() {

        List<Serie> listaSeries = new ArrayList<>();
        listaSeries = datosSeries.stream()
                .map(s -> new Serie(s))
                .collect(Collectors.toList())
        ;

        listaSeries.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
        ;

    }

}
