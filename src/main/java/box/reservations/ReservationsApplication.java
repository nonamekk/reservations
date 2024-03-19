package box.reservations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication()
@EnableScheduling
public class ReservationsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationsApplication.class, args);

	}


}
