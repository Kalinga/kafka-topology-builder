package com.purbon.kafka.topology.actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.purbon.kafka.topology.AccessControlProvider;
import com.purbon.kafka.topology.model.users.KStream;
import com.purbon.kafka.topology.utils.JSON;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetAclsForKStreams extends BaseAccessControlAction {

  private final AccessControlProvider controlProvider;
  private final KStream app;
  private final String topicPrefix;

  public SetAclsForKStreams(
      AccessControlProvider controlProvider, KStream app, String topicPrefix) {
    super();
    this.controlProvider = controlProvider;
    this.app = app;
    this.topicPrefix = topicPrefix;
  }

  @Override
  public void run() throws IOException {
    List<String> readTopics = app.getTopics().get(KStream.READ_TOPICS);
    List<String> writeTopics = app.getTopics().get(KStream.WRITE_TOPICS);

    bindings =
        controlProvider.setAclsForStreamsApp(
            app.getPrincipal(), topicPrefix, readTopics, writeTopics);
  }

  @Override
  public String toString() {
    Map<String, Object> map = new HashMap<>();
    map.put("Operation", getClass().getName());
    map.put("Principal", app.getPrincipal());
    map.put("Topic", topicPrefix);

    try {
      return JSON.asPrettyString(map);
    } catch (JsonProcessingException e) {
      return "";
    }
  }
}
