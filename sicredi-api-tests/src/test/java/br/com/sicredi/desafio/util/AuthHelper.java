package br.com.sicredi.desafio.util;

import br.com.sicredi.desafio.model.LoginRequest;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * Centraliza a obtenção de credenciais e tokens válidos para os testes
 * que dependem de autenticação (GET /auth/products), evitando duplicar
 * essa lógica em cada classe de teste e evitando depender de um
 * usuário fixo "hardcoded": as credenciais são obtidas dinamicamente
 * a partir de GET /users, conforme orientado na própria documentação
 * do desafio ("os campos importantes para o negócio são username e
 * password").
 */
public final class AuthHelper {

    private AuthHelper() {
    }

    public static Credentials obterCredenciaisValidas() {
        Response usersResponse = given()
                .when()
                .get("/users")
                .then()
                .extract().response();

        String username = usersResponse.jsonPath().getString("users[0].username");
        String password = usersResponse.jsonPath().getString("users[0].password");
        return new Credentials(username, password);
    }

    /**
     * Efetua login com credenciais válidas e retorna um token pronto
     * para uso no header {@code Authorization: Bearer <token>}.
     * <p>
     * A documentação do desafio descreve o token no campo {@code token},
     * mas a API pública atual (dummyjson.com) expõe esse valor no campo
     * {@code accessToken} — ver detalhes na seção "Bugs / Divergências"
     * do README. Por isso ambos os campos são verificados aqui, o que
     * torna a suíte resiliente a essa divergência de contrato.
     */
    public static String obterTokenValido() {
        Credentials credenciais = obterCredenciaisValidas();
        LoginRequest loginRequest = new LoginRequest(credenciais.username(), credenciais.password());

        Response response = given()
                .body(loginRequest)
                .when()
                .post("/auth/login")
                .then()
                .extract().response();

        String token = response.jsonPath().getString("token");
        if (token == null || token.isBlank()) {
            token = response.jsonPath().getString("accessToken");
        }
        return token;
    }

    public record Credentials(String username, String password) {
    }
}
