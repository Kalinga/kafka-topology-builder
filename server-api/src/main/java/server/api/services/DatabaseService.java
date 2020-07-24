package server.api.services;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.purbon.kafka.topology.model.Impl.ProjectImpl;
import com.purbon.kafka.topology.model.Impl.TopicImpl;
import com.purbon.kafka.topology.model.Project;
import com.purbon.kafka.topology.model.Topic;
import io.micronaut.core.annotation.Creator;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.ClassModel;
import org.bson.codecs.pojo.PojoCodecProvider;
import server.api.helpers.OptionalPropertyCodecProvider;
import server.api.model.TopologyDeco;

public class DatabaseService {

  private CodecRegistry pojoCodecRegistry;

  @Inject
  private MongoClient mongoClient;

  @Creator
  public DatabaseService(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
    this.pojoCodecRegistry = null;
  }

  private void configureCodecRegistry() {
    if (pojoCodecRegistry != null) {
      return;
    }

    ClassModel<Project> projectModel = ClassModel.builder(Project.class).enableDiscriminator(true)
        .build();
    ClassModel<ProjectImpl> projectImplModel = ClassModel.builder(ProjectImpl.class)
        .enableDiscriminator(true).build();

    ClassModel<Topic> topicModel = ClassModel.builder(Topic.class).enableDiscriminator(true)
        .build();
    ClassModel<TopicImpl> topicImplModel = ClassModel.builder(TopicImpl.class)
        .enableDiscriminator(true).build();

    CodecProvider defaultPojoCodecProvider = PojoCodecProvider.builder()
        .register(
            "com.purbon.kafka.topology.model.Impl",
            "com.purbon.kafka.topology.model",
            "com.purbon.kafka.topology.model.users",
            "server.api.models")
        .register(new OptionalPropertyCodecProvider())
        .automatic(true)
        .build();

    PojoCodecProvider mappedPojoCodecProvider = PojoCodecProvider.builder()
        .register(projectModel, projectImplModel)
        .register(topicModel, topicImplModel)
        .register(new OptionalPropertyCodecProvider())
        .build();

    pojoCodecRegistry =
        fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(mappedPojoCodecProvider, defaultPojoCodecProvider));
  }

  public TopologyDeco updateByField(String field, String value, TopologyDeco object) {

    getCollection()
        .replaceOne(eq(field, value), object);

    return object;
  }

  public TopologyDeco findByField(String field, String value) {
    FindIterable resultsIterator = getCollection()
        .find(eq(field, value));
    List<TopologyDeco> results = new ArrayList<>();
    resultsIterator.into(results);
    return results.get(0);
  }

  public TopologyDeco store(TopologyDeco topology) {

    getCollection()
        .insertOne(topology);
    return topology;
  }

  public List<TopologyDeco> all() {
    List<TopologyDeco> payload = new ArrayList<>();
    getCollection()
        .find().into(payload);
    return payload;
  }

  private MongoCollection getCollection() {
    return getDatabase()
        .getCollection("topologies", TopologyDeco.class);
  }

  private MongoDatabase getDatabase() {
    configureCodecRegistry();
    return mongoClient
        .getDatabase("kafka")
        .withCodecRegistry(pojoCodecRegistry);
  }

}
