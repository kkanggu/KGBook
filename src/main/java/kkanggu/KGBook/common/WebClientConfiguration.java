package kkanggu.KGBook.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {
	private final Keys keys;

	public WebClientConfiguration(Keys keys) {
		this.keys = keys;
	}

	@Bean
	public WebClient webClient() {
		String naverBookApiUrl = "https://openapi.naver.com/v1/search/book.json";

		return WebClient.builder()
				.baseUrl(naverBookApiUrl)
				.defaultHeader("X-Naver-Client-Id", keys.getNaverClientId())
				.defaultHeader("X-Naver-Client-Secret", keys.getNaverClientSecret())
				.build();
	}
}
