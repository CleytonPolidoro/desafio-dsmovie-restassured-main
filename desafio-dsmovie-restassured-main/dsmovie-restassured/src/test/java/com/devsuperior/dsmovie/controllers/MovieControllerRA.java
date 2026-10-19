package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

import com.devsuperior.dsmovie.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.json.simple.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class MovieControllerRA {

	private String movieTitle;
	private Long existingMovieId, nonExistingMovieId;
	private String adminUsername, clientUsername;
	private String adminPassword, clientPassword;
	private String adminToken, clientToken, invalidToken;

	private Map<String, Object> postMovieInstance;

	@BeforeEach
	public void setUp() throws Exception{
		baseURI = "http://localhost:8080";


		movieTitle = "The Witcher";
		existingMovieId = 5L;
		nonExistingMovieId = 1000L;
		adminUsername = "maria@gmail.com";
		adminPassword = "123456";
		clientUsername = "alex@gmail.com";
		clientPassword = "123456";
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		invalidToken = clientToken + "XPTO";
		postMovieInstance = new HashMap<>();

		postMovieInstance.put("title", "Test movie");
		postMovieInstance.put("image", "https://image.tmdb.org/t/p/w500/5WDKYt2oV5y23qfA2qP7kQ5J3D2.jpg");
	}


	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
		given().
				get("/movies")
				.then()
				.statusCode(200);

	}
	
	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {
		given()
				.get("/movies?title={movieTitle}", movieTitle)
				.then()
				.statusCode(200)
				.body("content.count[0]", is(2))
				.body("content.id[0]", is(1))
				.body("content.image[0]", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"))
				.body("content.score[0]", is(4.5F))
				.body("content.title[0]", equalTo("The Witcher"));

	}
	
	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {
		given()
				.get("movies/{id}", existingMovieId)
				.then()
				.statusCode(200)
				.body("count", is(0))
				.body("id", is(5))
				.body("image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/cinER0ESG0eJ49kXlExM0MEWGxW.jpg"))
				.body("score", is(0.0F))
				.body("title", equalTo("Shang-Chi e a Lenda dos Dez Anéis"));
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {
		given()
				.get("movies/{id}", nonExistingMovieId)
				.then()
				.statusCode(404)
				.body("error", equalTo("Recurso não encontrado"));
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {
		postMovieInstance.put("title", " ");
		JSONObject newMovie = new JSONObject(postMovieInstance);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer "+ adminToken)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(newMovie)
				.when()
				.post("/movies")
				.then()
				.statusCode(422)
				.body("errors.message[0]", equalTo("Campo requerido"));

	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
		JSONObject newMovie = new JSONObject(postMovieInstance);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer "+ clientToken)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(newMovie)
				.when()
				.post("/movies")
				.then()
				.statusCode(403);


	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
		JSONObject newMovie = new JSONObject(postMovieInstance);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer "+ invalidToken)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(newMovie)
				.when()
				.post("/movies")
				.then()
				.statusCode(401);

	}
}
