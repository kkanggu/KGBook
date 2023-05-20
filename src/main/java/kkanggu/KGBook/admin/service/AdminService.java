package kkanggu.KGBook.admin.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.XML;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kkanggu.KGBook.admin.dto.ApiBookDto;
import kkanggu.KGBook.book.controller.BookController;
import kkanggu.KGBook.book.dto.RenderBookDto;
import kkanggu.KGBook.book.entity.BookEntity;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminService {
	private final BookController bookController;
	private final WebClient webClient;
	private final ObjectMapper objectMapper;

	public AdminService(BookController bookController,
						WebClient webClient,
						ObjectMapper objectMapper) {
		this.bookController = bookController;
		this.webClient = webClient;
		this.objectMapper = objectMapper;
	}

	/**
	 * Currently WebClient execute with Block, Synchronous.
	 * This must be fixed
	 */
	public ResponseEntity<String> fetchBookFromNaverApi(String keyword, boolean searchRecent) {
		String naverBookApiUrl = "https://openapi.naver.com/v1/search/book_adv.xml?d_catg=280&d_titl=" + keyword;
		if (searchRecent) {
			naverBookApiUrl += "&sort=date";
		}

		return webClient.get()
				.uri(naverBookApiUrl)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(String.class)
				.block();
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

	public List<BookEntity> convertApiBookDtoToBookEntity(List<ApiBookDto> apiBookDtos) {
		return apiBookDtos.stream()
				.map(apiBookDto -> {
					BookEntity book = new BookEntity();
					book.setIsbn(apiBookDto.getIsbn());
					book.setTitle(apiBookDto.getTitle());
					book.setAuthor(apiBookDto.getAuthor());
					book.setPublisher(apiBookDto.getPublisher());
					book.setDescription(apiBookDto.getDescription());
					book.setOriginImageUrl(apiBookDto.getImage());

					DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
					LocalDate date = LocalDate.parse(apiBookDto.getPubdate(), inputFormatter);
					book.setPublishDate(date);
					book.setCreateDate(LocalDate.now());

					return book;
				})
				.collect(Collectors.toList());
	}

	public void saveBooks(List<BookEntity> books) {
		books.forEach(bookController::saveBook);
	}

	public List<RenderBookDto> findAll() {
		List<BookEntity> books = bookController.findAll();

		return books.stream().map(book -> {
					RenderBookDto renderBookDto = new RenderBookDto();
					renderBookDto.setIsbn(book.getIsbn());
					renderBookDto.setTitle(book.getTitle());
					renderBookDto.setAuthor(book.getAuthor());
					renderBookDto.setPublisher(book.getPublisher());
					renderBookDto.setPublishDate(book.getPublishDate());
					renderBookDto.setDescription(book.getDescription());
					renderBookDto.setImageUrl(book.getS3ImageUrl());

					return renderBookDto;
				})
				.toList();
	}
}
