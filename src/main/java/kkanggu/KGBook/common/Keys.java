package kkanggu.KGBook.common;

import java.io.InputStream;
import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.Yaml;
import lombok.Getter;

@Configuration
@Getter
public class Keys {
	private final String naverClientId;
	private final String naverClientSecret;
	private final String awsAccessKey;
	private final String awsSecretKey;
	private final String s3Bucket;

	public Keys() {
		InputStream inputStream = this.getClass()
				.getClassLoader()
				.getResourceAsStream("keys.yml");
		Yaml yaml = new Yaml();
		Map<String, Object> yamlMap = yaml.load(inputStream);

		this.naverClientId = (String) yamlMap.get("X-NAVER-CLIENT-ID");
		this.naverClientSecret = (String) yamlMap.get("X-NAVER-CLIENT-SECRET");
		this.awsAccessKey = (String) yamlMap.get("AWS-CREDENTIAL-ACCESS");
		this.awsSecretKey = (String) yamlMap.get("AWS-CREDENTIAL-SECRET");
		this.s3Bucket = (String) yamlMap.get("AWS-S3-BUCKET");
	}
}
