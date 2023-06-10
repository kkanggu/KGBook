package kkanggu.KGBook.common.aws;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

import org.springframework.stereotype.Component;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import kkanggu.KGBook.common.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ImageController {
	private final AmazonS3Client amazonS3Client;
	private final String bucket;

	public ImageController(AmazonS3Client amazonS3Client,
						   Keys keys) {
		this.amazonS3Client = amazonS3Client;
		this.bucket = keys.getS3Bucket();
	}

	public String uploadImage(String originImageUrl) {
		String s3FileName = "";
		try {
			// Set S3 File Name
			URL originUrl = new URL(originImageUrl);
			URLConnection urlConnection = originUrl.openConnection();
			urlConnection.setConnectTimeout(3000);
			urlConnection.setReadTimeout(5000);
			String randomFileName = UUID.randomUUID().toString();
			String fileExt = originUrl.getPath().substring(originUrl.getPath().lastIndexOf("."));
			s3FileName = "book-image/" + randomFileName + fileExt;

			// Set contentType and contentLength
			InputStream inputStream = urlConnection.getInputStream();
			String contentType = urlConnection.getContentType();
			long contentLength = urlConnection.getContentLengthLong();

			// Upload image to S3
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(contentType);
			metadata.setContentLength(contentLength);
			amazonS3Client.putObject(bucket, s3FileName, inputStream, metadata);
		} catch (IOException e) {
			log.warn("Image upload failed : ", e);
			return null;
		}

		String s3ImageUrl = String.format("https://%s.s3.amazonaws.com/%s", bucket, s3FileName);
		return s3ImageUrl;
	}

	public boolean deleteImage(String s3ImageUrl) {
		String key = s3ImageUrl.split("s3.amazonaws.com/")[1];

		try {
			amazonS3Client.deleteObject(bucket, key);
		} catch (Exception e) {
			log.info("Image delete failed : ", e);
			return false;
		}

		return true;
	}
}
