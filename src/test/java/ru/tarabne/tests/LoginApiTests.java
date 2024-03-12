package ru.tarabne.tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginApiTests extends TestBase {

    @Test
    @DisplayName("Проверка корректности данных у пользователя по id")
    void checkUserInfoByIdTest() {
        Response response = given()
                .log().uri()
                .log().method()
            .when()
                .get("/users/12")
            .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/user-response-schema.json"))
                .extract().response();
        assertThat(response.path("data.id"), is(12));
        assertThat(response.path("data.first_name"), is("Rachel"));
        assertThat(response.path("data.last_name"), is("Howell"));

    }

    @Test
    @DisplayName("Получение несуществующего пользователя")
    void getNonexistentUserTest() {
        given()
                .log().uri()
                .log().method()
            .when()
                .get("/users/333")
            .then()
                .log().status()
                .log().body()
                .statusCode(404);
    }

    @Test
    @DisplayName("Проверка регистрации с заполнением email и пароля")
    void successfulRegistrationTest() {
        String login = "mikhail.petrovich@reqres.in";
        String password = "пароль";
        Response response = given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(JSON)
                .body("{ \"email\": \"" + login + "\", \"password\": \"" + password + "\" }")
            .when()
                .post("/api/register")
            .then()
                .log().status()
                .log().body()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/success-registration-schema.json"))
                .extract().response();
        assertThat(response.path("email"), equalTo(login));
        assertThat(response.path("password"), equalTo(password));
        assertThat(response.path("id"), notNullValue());
        assertThat(response.path("createdAt"), notNullValue());
    }

    @Test
    @DisplayName("Обновление пользователя целиком")
    void fullUserUpdateTest() {
        String name = "morpheus111";
        String job = "zion resident111";
        Response response = given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(JSON)
                .body("{ \"name\": \"" + name + "\", \"job\": \"" + job + "\" }")
            .when()
                .put("/users/2")
            .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/success-update-schema.json"))
                .extract().response();
        assertThat(response.path("name"), equalTo(name));
        assertThat(response.path("job"), equalTo(job));
        assertThat(response.path("updatedAt"), notNullValue());
    }

    @Test
    @DisplayName("Частичное обновление пользователя")
    void partialUserUpdateTest() {
        String job = "zion resident111";
        Response response = given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(JSON)
                .body("{ \"job\": \"" + job + "\" }")
                .when()
                .patch("/users/2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/success-update-schema.json"))
                .extract().response();
        assertThat(response.path("job"), equalTo(job));
        assertThat(response.path("updatedAt"), notNullValue());
    }

    @Test
    @DisplayName("Проверка названия цвета по id в списке цветов")
    void checkNameInListByIdTest() {
        Response response = given()
                .log().uri()
                .log().method()
            .when()
                .get("/unknown")
            .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/unknown-response-schema.json"))
                .extract().response();
        assertEquals("tigerlily", response.path("data.find{it.id == 5}.name"));
    }
}
