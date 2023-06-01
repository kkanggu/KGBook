package kkanggu.KGBook.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class JdbcTemplateConfiguration {
	private final DataSourceConfiguration dataSourceConfiguration;

	public JdbcTemplateConfiguration(DataSourceConfiguration dataSourceConfiguration) {
		this.dataSourceConfiguration = dataSourceConfiguration;
	}

	@Bean
	@Profile("local")
	public JdbcTemplate localJdbcTemplate() {
		return new JdbcTemplate(dataSourceConfiguration.localDataSource());
	}
}
