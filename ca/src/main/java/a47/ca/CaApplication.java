package a47.ca;

import a47.ca.keyManager.KeyManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CaApplication {

	public static void main(String[] args) {
		KeyManager keyManager;
		SpringApplication.run(CaApplication.class, args);
	}
}
