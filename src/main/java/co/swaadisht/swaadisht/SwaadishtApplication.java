package co.swaadisht.swaadisht;

import co.swaadisht.swaadisht.config.GoogleDistanceMatrixProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(GoogleDistanceMatrixProperties.class)
public class SwaadishtApplication {


	public static void main(String[] args) {
		SpringApplication.run(SwaadishtApplication.class, args);
	}

}
