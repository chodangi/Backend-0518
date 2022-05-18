package MCcrew.Coinportal;

import MCcrew.Coinportal.game.GameService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class CoinPortalApplication  {


	public static void main(String[] args) {
		SpringApplication.run(CoinPortalApplication.class, args);
		// GameService.gameTimer(); // 게임 시작

		// 메모리 사용량 출력
		long heapSize = Runtime.getRuntime().totalMemory();
		System.out.println("HEAP Size(M) : "+ heapSize / (1024*1024) + " MB");
	}
}
