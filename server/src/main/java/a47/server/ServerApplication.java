package a47.server;

import a47.server.service.PingService;
import a47.server.service.RegisterService;
import a47.server.service.ReplicationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ServerApplication.class, args);

		String port = args[1].split("=")[1];
		Constants.SERVER_URL = "https://localhost:" + port;
		Constants.DIRECTORY.FILES_DIRECTORY = "/tmp/servers/server_" + port;

		if (args[0].equals("--primary")) {
			context.getBean(ReplicationService.class).initReplication();
		}
		if (args[0].equals("--secondary")) {
			context.getBean(PingService.class).init();
			context.getBean(RegisterService.class).registerPrimary(port);
		}
	}
}
