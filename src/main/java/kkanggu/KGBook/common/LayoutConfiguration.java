package kkanggu.KGBook.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;

@Configuration
public class LayoutConfiguration {
	@Bean
	public LayoutDialect layoutDialect() {
		return new LayoutDialect();
	}
}
