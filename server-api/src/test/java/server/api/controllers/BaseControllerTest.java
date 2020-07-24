package server.api.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.purbon.kafka.topology.model.Impl.ProjectImpl;
import com.purbon.kafka.topology.model.Project;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import javax.inject.Inject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import server.api.MongoContainer;
import server.api.model.TopologyDeco;
import server.api.services.TopologyService;

public class BaseControllerTest {

  public static MongoContainer mongo = new MongoContainer();

  protected static String accessToken = "";

  @BeforeAll
  public static void setup() {
    mongo.start();
  }

  @AfterAll
  public static void down() {
    mongo.stop();
  }

  @Inject
  EmbeddedServer server;

  @Inject
  @Client("/")
  HttpClient client;

  @Inject
  TopologyService service;

  protected TopologyDeco createTopology(String team) {
    return service.create(team);
  }

  protected TopologyDeco addProject(String team, String projectName) {
    TopologyDeco topology = service.findByTeam(team);

    Project project = new ProjectImpl();
    project.setName(projectName);
    topology.addProject(project);

    service.update(topology);
    return topology;
  }

  protected String authenticate() {

    UsernamePasswordCredentials creds = new UsernamePasswordCredentials("sherlock", "password");
    HttpRequest request = HttpRequest.POST("/login", creds);

    HttpResponse<BearerAccessRefreshToken> response = client
        .toBlocking()
        .exchange(request, BearerAccessRefreshToken.class);

    assertEquals(HttpStatus.OK, response.getStatus());

    BearerAccessRefreshToken token = response.getBody().get();

   return token.getAccessToken();

  }
}
