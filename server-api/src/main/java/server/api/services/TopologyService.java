package server.api.services;

import java.util.List;
import server.api.model.TopologyDeco;

public interface TopologyService {

  TopologyDeco create(String team);

  TopologyDeco update(TopologyDeco topology);

  TopologyDeco findByTeam(String team);

  List<TopologyDeco> all();

}
