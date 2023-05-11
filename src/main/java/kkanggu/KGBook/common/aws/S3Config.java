package kkanggu.KGBook.common.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import kkanggu.KGBook.common.Keys;
import lombok.Getter;

@Configuration
@Getter
public class S3Config {
	private final String accessKey;
	private final String secretKey;
	private final String region;

	public S3Config(@Value("${cloud.aws.region.static}") String region,
	                Keys keys) {
		this.region = region;
		this.accessKey = keys.getAwsAccessKey();
		this.secretKey = keys.getAwsSecretKey();
	}

	@Bean
	public AmazonS3Client amazonS3Client() {
		BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

		return (AmazonS3Client) AmazonS3ClientBuilder.standard()
				.withRegion(region)
				.withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
				.build();
	}
}
