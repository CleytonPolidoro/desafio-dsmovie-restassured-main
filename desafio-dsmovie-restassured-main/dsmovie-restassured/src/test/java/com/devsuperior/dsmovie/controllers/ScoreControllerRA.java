package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

import java.util.HashMap;
import java.util.Map;


public class ScoreControllerRA {


	private Map<String, Object> postScoreInstance;
	private String adminUsername, clientUsername;
	private String adminPassword, clientPassword;
	private String adminToken, clientToken, invalidToken;

	@BeforeEach
	public void setUp() throws Exception {
		adminUsername = "maria@gmail.com";
		adminPassword = "123456";
		clientUsername = "alex@gmail.com";
		clientPassword = "123456";
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		invalidToken = clientToken + "XPTO";

		postScoreInstance = new HashMap<>();

		postScoreInstance.put("movieId", 1L);
		postScoreInstance.put("score",4L);

	}

	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {		
		postScoreInstance.put("movieId", 1000);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer "+ adminToken)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(postScoreInstance)
				.when()
				.put("/scores")
				.then()
				.statusCode(404)
				.body("error", equalTo("Recurso não encontrado"));

	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {
		postScoreInstance.put("movieId", null);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer "+ adminToken)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(postScoreInstance)
				.when()
				.put("/scores")
				.then()
				.statusCode(422)
				.body("errors.message[0]", equalTo("Campo requerido"))
				.body("error", equalTo("Dados inválidos"));

	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {
		postScoreInstance.put("score", -4);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer "+ adminToken)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(postScoreInstance)
				.when()
				.put("/scores")
				.then()
				.statusCode(422)
				.body("error", equalTo("Dados inválidos"))
				.body("errors.message[0]", equalTo("Valor mínimo 0"));
	}
}
