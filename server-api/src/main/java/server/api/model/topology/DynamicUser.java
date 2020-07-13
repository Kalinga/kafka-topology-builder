package server.api.model.topology;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class DynamicUser extends User {

  private Map<String, List<String>> topics;

  public static final String READ_TOPICS = "read";
  public static final String WRITE_TOPICS = "write";

  public DynamicUser() {
    this("", new HashMap<>());
  }

  public DynamicUser(String principal, HashMap<String, List<String>> topics) {
    super(principal);
    this.topics = topics;
  }

  public Map<String, List<String>> getTopics() {
    return topics;
  }

  public void setTopics(Map<String, List<String>> topics) {
    this.topics = topics;
  }
}
