package com.aluracursos.screenmatch;

import com.aluracursos.screenmatch.principal.Principal;
import com.aluracursos.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@Profile("consola")
public class ScreenmatchApplicationConsola implements CommandLineRunner {

	@Autowired
	private SerieRepository repository;

	public static void main(String[] args) {

		new SpringApplicationBuilder(ScreenmatchApplicationConsola.class)
				.web(WebApplicationType.NONE)
				.profiles("consola")
				.run(args);
	}

	@Override
	public void run(String... args) throws Exception {

		Principal principal = new Principal(repository);
		principal.muestraMenu();

	}
}
