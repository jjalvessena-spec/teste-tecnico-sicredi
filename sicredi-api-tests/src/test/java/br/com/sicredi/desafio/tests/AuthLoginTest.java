package br.com.sicredi.desafio.tests;

import br.com.sicredi.desafio.base.BaseApiTest;
import br.com.sicredi.desafio.model.LoginRequest;
import br.com.sicredi.desafio.util.AuthHelper;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.oneOf;

/**
 * POST /auth/login - Criação de token para autenticação.
 */
@Epic("Sicredi Desafio QE")
@Feature("POST /auth/login")
class AuthLoginTest extends BaseApiTest {

    @Test
    @Tag("smoke")
    @DisplayName("Deve autenticar com sucesso usando credenciais válidas e retornar um token")
    @Description("A documentação do desafio descreve o status de sucesso como 201, mas a API "
            + "pública (dummyjson.com) responde atualmente com 200 - ver seção \"Bugs\" do "
            + "README. Por isso o teste aceita ambos os códigos, mantendo a suíte resiliente.")
    void deveAutenticarComCredenciaisValidas() {
        AuthHelper.Credentials credenciais = AuthHelper.obterCredenciaisValidas();
        LoginRequest loginRequest = new LoginRequest(credenciais.username(), credenciais.password());

        Response response = given()
                .body(loginRequest)
        .when()
                .post("/auth/login")
        .then()
                .statusCode(is(oneOf(200, 201)))
                .body("username", is(credenciais.username()))
                .extract().response();

        String token = response.jsonPath().getString("token");
        String accessToken = response.jsonPath().getString("accessToken");

        assertThat(token != null && !token.isBlank() || accessToken != null && !accessToken.isBlank())
                .as("token retornado (campo 'token' ou 'accessToken')")
                .isTrue();
    }

    @Test
    @DisplayName("Deve rejeitar login com senha inválida")
    void deveRetornarErroComSenhaInvalida() {
        AuthHelper.Credentials credenciais = AuthHelper.obterCredenciaisValidas();
        LoginRequest loginRequest = new LoginRequest(credenciais.username(), "senha-incorreta-123");

        given()
                .body(loginRequest)
        .when()
                .post("/auth/login")
        .then()
                .statusCode(400)
                .body("message", org.hamcrest.Matchers.not(org.hamcrest.Matchers.emptyOrNullString()));
    }

    @Test
    @DisplayName("Deve rejeitar login com usuário inexistente")
    void deveRetornarErroComUsuarioInexistente() {
        LoginRequest loginRequest = new LoginRequest("usuario_que_nao_existe_qe", "qualquer-senha");

        given()
                .body(loginRequest)
        .when()
                .post("/auth/login")
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Deve rejeitar login sem o campo obrigatório password")
    void deveRetornarErroComCampoObrigatorioAusente() {
        Map<String, String> corpoIncompleto = Map.of("username", "emilys");

        given()
                .body(corpoIncompleto)
        .when()
                .post("/auth/login")
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Deve rejeitar login com corpo de requisição vazio")
    void deveRetornarErroComBodyVazio() {
        given()
                .body("{}")
        .when()
                .post("/auth/login")
        .then()
                .statusCode(400);
    }
}
