package kkanggu.KGBook.integration.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import kkanggu.KGBook.common.Keys;
import kkanggu.KGBook.common.aws.ImageController;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class ImageControllerIntegrationTest {
	@Autowired
	private ImageController imageController;

	@Autowired
	private AmazonS3Client amazonS3Client;

	@Autowired
	private Keys keys;

	private String bucket;

	@BeforeEach
	void init() {
		bucket = keys.getS3Bucket();
	}

	@Test
	@DisplayName("S3에 이미지 업로드 성공")
	void uploadImageOk() {
		String originImageUrl = "https://shopping-phinf.pstatic.net/main_3249079/32490791688.20221230074134.jpg";
		ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request()
				.withBucketName(bucket)
				.withPrefix("book-image/");
		int initialFileCount = amazonS3Client.listObjectsV2(listObjectsV2Request)
				.getObjectSummaries()
				.size();

		String s3ImageUrl = imageController.uploadImage(originImageUrl);

		int uploadedFileCount = amazonS3Client.listObjectsV2(listObjectsV2Request)
				.getObjectSummaries()
				.size();
		assertAll(
				() -> assertThat(uploadedFileCount).isEqualTo(initialFileCount + 1),
				() -> assertThat(s3ImageUrl).isNotNull()
		);
		imageController.deleteImage(s3ImageUrl);
	}

	@Test
	@DisplayName("S3에 이미지 업로드 실패, 잘못된 URL")
	void uploadImageFail() {
		String originImageUrl = "https://wrong-URL/wrong.jpg";

		String s3ImageUrl = imageController.uploadImage(originImageUrl);

		assertAll(
				() -> assertThat(s3ImageUrl).isNull()
		);
	}

	// 이미지가 없더라도 URL이 올바르다면 삭제 성공 처리가 되는 상황
	@Test
	@DisplayName("S3에서 이미지 삭제 성공")
	void deleteImageOk() {
		String originImageUrl = "https://shopping-phinf.pstatic.net/main_3249079/32490791688.20221230074134.jpg";
		String s3ImageUrl = imageController.uploadImage(originImageUrl);
		ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request()
				.withBucketName(bucket)
				.withPrefix("book-image/");
		int initialFileCount = amazonS3Client.listObjectsV2(listObjectsV2Request)
				.getObjectSummaries()
				.size();

		boolean isDeleted = imageController.deleteImage(s3ImageUrl);
		int uploadedFileCount = amazonS3Client.listObjectsV2(listObjectsV2Request)
				.getObjectSummaries()
				.size();
		assertAll(
				() -> assertThat(isDeleted).isTrue(),
				() -> assertThat(uploadedFileCount).isEqualTo(initialFileCount - 1)
		);
	}
}
