package kkanggu.KGBook.admin.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.XML;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kkanggu.KGBook.admin.dto.ApiBookDto;
import kkanggu.KGBook.book.controller.BookController;
import kkanggu.KGBook.book.dto.RenderBookDto;
import kkanggu.KGBook.book.entity.BookEntity;
import kkanggu.KGBook.common.Keys;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminService {
	private final BookController bookController;
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	private final Keys keys;

	public AdminService(BookController bookController,
	                    RestTemplate restTemplate,
	                    ObjectMapper objectMapper,
	                    Keys keys) {
		this.bookController = bookController;
		this.restTemplate = restTemplate;
		this.objectMapper = objectMapper;
		this.keys = keys;
	}

	public ResponseEntity<String> fetchBookFromNaverApi(String keyword, boolean searchRecent) {
		String naverBookApiUrl = "https://openapi.naver.com/v1/search/book_adv.xml?d_catg=280&d_titl=" + keyword;
		if (searchRecent) {
			naverBookApiUrl += "&sort=date";
		}

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("X-Naver-Client-Id", keys.getNaverClientId());
		httpHeaders.set("X-Naver-Client-Secret", keys.getNaverClientSecret());
		HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

		return restTemplate.exchange(naverBookApiUrl, HttpMethod.GET, entity, String.class);
	}

	public List<ApiBookDto> convertXmlToApiBookDto(String booksXml) {
		List<ApiBookDto> apiBookDtos = new ArrayList<>();

		try {
			JSONArray jsonArray = XML.toJSONObject(booksXml)
					.getJSONObject("rss")
					.getJSONObject("channel")
					.getJSONArray("item");
			for (Object object : jsonArray) {
				ApiBookDto apiBookDto = objectMapper.readValue(object.toString(), ApiBookDto.class);
				apiBookDtos.add(apiBookDto);
			}
		} catch (JSONException e) {
			log.warn("Parsing API Failed : {}", booksXml, e);
		} catch (JsonMappingException e) {
			log.warn("API Json Mapping Failed : {}", booksXml, e);
		} catch (JsonProcessingException e) {
			log.warn("API Json Processing Failed : {}", booksXml, e);
			throw new RuntimeException(e);
		}

		return apiBookDtos;
	}

	public void saveBooks(List<RenderBookDto> books) {
		books.stream()
				.map(this::convertRenderBookDtoToBookEntity)
				.forEach(bookController::saveBook);
	}

	public List<RenderBookDto> findAll() {
		List<BookEntity> books = bookController.findAll();

		return books.stream()
				.map(this::convertBookEntityToRenderBookDto)
				.toList();
	}

	public RenderBookDto findByIsbn(long isbn) {
		BookEntity book = bookController.findByIsbn(isbn);

		return null != book ? convertBookEntityToRenderBookDto(book) : null;
	}

	public void updateBook(RenderBookDto renderBookDto) {
		bookController.updateBook(convertRenderBookDtoToBookEntity(renderBookDto));
	}

	public List<RenderBookDto> convertApiBookDtoToRenderBookDto(List<ApiBookDto> apiBookDtos) {
		return apiBookDtos.stream()
				.map(apiBookDto -> {
					DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
					LocalDate date = LocalDate.parse(apiBookDto.getPubdate(), inputFormatter);

					return RenderBookDto.builder()
							.isbn(apiBookDto.getIsbn())
							.title(apiBookDto.getTitle())
							.author(apiBookDto.getAuthor().replace("^", ","))
							.publisher(apiBookDto.getPublisher())
							.originPrice(apiBookDto.getDiscount())
							.publishDate(date)
							.description(apiBookDto.getDescription())
							.imageUrl(apiBookDto.getImage())
							.build();
				})
				.collect(Collectors.toList());
	}

	private RenderBookDto convertBookEntityToRenderBookDto(BookEntity book) {
		return RenderBookDto.builder()
				.isbn(book.getIsbn())
				.title(book.getTitle())
				.author(book.getAuthor())
				.publisher(book.getPublisher())
				.originPrice(book.getOriginPrice())
				.discountPrice(book.getDiscountPrice())
				.discountRate(book.getDiscountRate())
				.discountType(book.getDiscountType())
				.publishDate(book.getPublishDate())
				.description(book.getDescription())
				.imageUrl(book.getS3ImageUrl())
				.build();
	}

	private BookEntity convertRenderBookDtoToBookEntity(RenderBookDto renderBookDto) {
		return BookEntity.builder()
				.isbn(renderBookDto.getIsbn())
				.title(renderBookDto.getTitle())
				.author(renderBookDto.getAuthor())
				.publisher(renderBookDto.getPublisher())
				.originPrice(renderBookDto.getOriginPrice())
				.publishDate(renderBookDto.getPublishDate())
				.createDate(LocalDate.now())
				.description(renderBookDto.getDescription())
				.originImageUrl(renderBookDto.getImageUrl())
				.build();
	}
}
