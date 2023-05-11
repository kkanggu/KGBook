package kkanggu.KGBook.common.aws;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import kkanggu.KGBook.common.Keys;

@SpringBootTest
class ImageControllerTest {
	private final ImageController imageController;
	private final AmazonS3Client amazonS3Client;
	private final String bucket;

	@Autowired
	public ImageControllerTest(ImageController imageController,
							   AmazonS3Client amazonS3Client,
							   Keys keys) {
		this.imageController = imageController;
		this.amazonS3Client = amazonS3Client;
		this.bucket = keys.getS3Bucket();
	}

	@Test
	@DisplayName("S3에 이미지 업로드")
	void uploadImageTest() {
		// given
		String originImageUrl = "https://shopping-phinf.pstatic.net/main_3249079/32490791688.20221230074134.jpg";
		ListObjectsV2Request listObjectsV2RequestBefore = new ListObjectsV2Request()
				.withBucketName(bucket)
				.withPrefix("book-image/");
		int initialFileCount = amazonS3Client.listObjectsV2(listObjectsV2RequestBefore)
				.getObjectSummaries()
				.size();

		// when
		String s3ImageUrl = imageController.uploadImage(originImageUrl);

		// then
		ListObjectsV2Request listObjectsV2RequestAfter = new ListObjectsV2Request()
				.withBucketName(bucket)
				.withPrefix("book-image/");
		int uploadedFileCount = amazonS3Client.listObjectsV2(listObjectsV2RequestAfter)
				.getObjectSummaries()
				.size();

		assertThat(uploadedFileCount).isEqualTo(initialFileCount + 1);

		imageController.deleteImage(s3ImageUrl);
	}

	@Test
	@DisplayName("S3에서 이미지 삭제")
	void deleteImageTest() {
		// given
		String originImageUrl = "https://shopping-phinf.pstatic.net/main_3249079/32490791688.20221230074134.jpg";
		ListObjectsV2Request listObjectsV2RequestBefore = new ListObjectsV2Request()
				.withBucketName(bucket)
				.withPrefix("book-image/");
		int initialFileCount = amazonS3Client.listObjectsV2(listObjectsV2RequestBefore)
				.getObjectSummaries()
				.size();
		String s3ImageUrl = imageController.uploadImage(originImageUrl);

		// when
		boolean isDeleted = imageController.deleteImage(s3ImageUrl);
		ListObjectsV2Request listObjectsV2RequestAfter = new ListObjectsV2Request()
				.withBucketName(bucket)
				.withPrefix("book-image/");
		int uploadedFileCount = amazonS3Client.listObjectsV2(listObjectsV2RequestAfter)
				.getObjectSummaries()
				.size();

		// then
		assertThat(isDeleted).isEqualTo(true);
		assertThat(uploadedFileCount).isEqualTo(initialFileCount);
	}
}
