package server.api.services.Impl;

import io.micronaut.core.annotation.Creator;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import server.api.model.TopologyCatalog;
import server.api.model.TopologyDeco;
import server.api.services.DatabaseService;
import server.api.services.TopologyService;

@Singleton
public class TopologyServiceImpl implements TopologyService {

  @Inject
  private TopologyCatalog catalog;

  @Inject
  private DatabaseService databaseService;

  @Creator
  public TopologyServiceImpl(DatabaseService databaseService) {
    this.catalog = new TopologyCatalog();
    this.databaseService = databaseService;
  }

  @Override
  public TopologyDeco create(String team) {
    if (catalog.exist(team)) {
      return catalog.getByTeam(team);
    }

    TopologyDeco topology = new TopologyDeco();
    topology.setTeam(team);
    catalog.addTopology(topology);

    return databaseService.store(topology);
  }

  @Override
  public TopologyDeco update(TopologyDeco topology) {

    databaseService
        .updateByField("team", topology.getTeam(), topology);

    return topology;
  }

  @Override
  public TopologyDeco findByTeam(String team) {
    return databaseService
        .findByField("team", team);
  }

  @Override
  public List<TopologyDeco> all() {
    return databaseService.all();
  }
}
