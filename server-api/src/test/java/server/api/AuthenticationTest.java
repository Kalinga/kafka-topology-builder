package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.test.annotation.MockBean;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;
import server.api.services.DatabaseService;
import server.api.services.TopologyService;

@MicronautTest
public class AuthenticationTest {

  @Inject
  TopologyService topologyService;

  @Inject
  @Client("/")
  HttpClient client;

  @Test
  public void testSecuredURLWithoutAuth() {
    try {
      client
          .toBlocking()
          .exchange(HttpRequest.GET("/topologies"));
      assertFalse(true, "Authentication exception HttpClientResponseException not raised ");
    } catch (HttpClientResponseException e) {
      assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
    }
  }

  @Test
  public void testSecuredURLWithAuth() {

    UsernamePasswordCredentials creds = new UsernamePasswordCredentials("sherlock", "password");
    HttpRequest request = HttpRequest.POST("/login", creds);

    HttpResponse<BearerAccessRefreshToken> response = client
        .toBlocking()
        .exchange(request, BearerAccessRefreshToken.class);

    assertEquals(HttpStatus.OK, response.getStatus());

    BearerAccessRefreshToken token = response.getBody().get();

    assertEquals("sherlock", token.getUsername());

    String accessToken = token.getAccessToken();
    HttpRequest requestWithAuthorization = HttpRequest.GET("/topologies")
        .accept(MediaType.APPLICATION_JSON)
        .bearerAuth(accessToken);

    HttpResponse<String> responseWithAuth = client
        .toBlocking()
        .exchange(requestWithAuthorization, String.class);

    assertEquals(HttpStatus.OK, responseWithAuth.getStatus());

  }

  @MockBean(TopologyService.class)
  TopologyService topologyService() {
    return mock(TopologyService.class);
  }


  @MockBean(DatabaseService.class)
  DatabaseService databaseService() {
    return mock(DatabaseService.class);
  }
}
