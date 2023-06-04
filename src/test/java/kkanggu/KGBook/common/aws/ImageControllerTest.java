package kkanggu.KGBook.common.aws;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import kkanggu.KGBook.common.Keys;

@ExtendWith(MockitoExtension.class)
class ImageControllerTest {
	private ImageController imageController;

	@Mock
	private AmazonS3Client amazonS3Client;

	@Mock
	private Keys keys;

	@BeforeEach
	void init() {
		when(keys.getS3Bucket()).thenReturn("unit-test-bucket");
		imageController = new ImageController(amazonS3Client, keys);
	}

	@Test
	@DisplayName("S3에 이미지 업로드 성공")
	void uploadImageOk() {
		String originImageUrl = "https://shopping-phinf.pstatic.net/main_3249079/32490791688.20221230074134.jpg";
		when(amazonS3Client.putObject(any(), anyString(), any(InputStream.class), any(ObjectMetadata.class))).thenReturn(null);

		String s3ImageUrl = imageController.uploadImage(originImageUrl);

		assertAll(
				() -> assertThat(s3ImageUrl).isNotNull()
		);
	}

	@Test
	@DisplayName("S3에 이미지 업로드 실패, 확장자가 없을 경우")
	void uploadImageNoFileExtension() {
		String originImageUrl = "https://shopping-phinf.pstatic.net/null";

		assertAll(
				() -> assertThrows(StringIndexOutOfBoundsException.class, () -> imageController.uploadImage(originImageUrl))
		);
	}

	@Test
	@DisplayName("S3에 이미지 업로드 실패, 이미지가 없을 경우")
	void uploadImageNoImage() {
		String originImageUrl = "https://shopping-phinf.pstatic.net/no-image.jpg";

		String s3ImageUrl = imageController.uploadImage(originImageUrl);

		assertAll(
				() -> assertThat(s3ImageUrl).isNull()
		);
	}

	@Test
	@DisplayName("S3에 이미지 업로드 실패, 모종의 이유")
	void uploadImageUploadFail() {
		String originImageUrl = "https://shopping-phinf.pstatic.net/main_3249079/32490791688.20221230074134.jpg";
		when(amazonS3Client.putObject(any(), anyString(), any(InputStream.class), any(ObjectMetadata.class))).thenThrow(AmazonClientException.class);

		assertAll(
				() -> assertThrows(AmazonClientException.class, () -> imageController.uploadImage(originImageUrl))
		);
	}

	@Test
	@DisplayName("S3에서 이미지 삭제 성공")
	void deleteImageOk() {
		String s3ImageUrl = "https://unit-test-bucket.s3.amazonaws.com/nothing.jpg";
		doNothing().when(amazonS3Client).deleteObject(anyString(), anyString());

		boolean isDeleted = imageController.deleteImage(s3ImageUrl);

		assertAll(
				() -> assertThat(isDeleted).isTrue()
		);
	}

	@Test
	@DisplayName("S3에서 이미지 삭제 실패, 모종의 이유")
	void deleteImageFail() {
		String s3ImageUrl = "https://unit-test-bucket.s3.amazonaws.com/nothing.jpg";
		doThrow(AmazonClientException.class).when(amazonS3Client).deleteObject(anyString(), anyString());

		boolean isDeleted = imageController.deleteImage(s3ImageUrl);

		assertAll(
				() -> assertThat(isDeleted).isFalse()
		);
	}
}
