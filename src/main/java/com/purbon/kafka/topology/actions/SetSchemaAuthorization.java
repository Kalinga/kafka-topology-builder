package com.purbon.kafka.topology.actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.purbon.kafka.topology.AccessControlProvider;
import com.purbon.kafka.topology.model.users.Schemas;
import com.purbon.kafka.topology.utils.JSON;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SetSchemaAuthorization extends BaseAccessControlAction {

  private final AccessControlProvider controlProvider;
  private final Schemas schemaAuthorization;

  public SetSchemaAuthorization(
      AccessControlProvider controlProvider, Schemas schemaAuthorization) {
    super();
    this.controlProvider = controlProvider;
    this.schemaAuthorization = schemaAuthorization;
  }

  @Override
  public void run() throws IOException {

    bindings =
        controlProvider.setSchemaAuthorization(
            schemaAuthorization.getPrincipal(), schemaAuthorization.getSubjects());
  }

  @Override
  public String toString() {
    Map<String, Object> map = new HashMap<>();
    map.put("Operation", getClass().getName());
    map.put("Principal", schemaAuthorization.getPrincipal());
    map.put("Subjects", schemaAuthorization.getSubjects());

    try {
      return JSON.asPrettyString(map);
    } catch (JsonProcessingException e) {
      return "";
    }
  }
}
