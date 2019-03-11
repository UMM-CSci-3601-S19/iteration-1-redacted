package umm3601.ride;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.*;
import org.bson.codecs.*;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.json.JsonReader;
import org.bson.types.ObjectId;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RideControllerSpec {
  private RideController rideController;
  private ObjectId samsId;

  @Before
  public void clearAndPopulateDB() {
    MongoClient mongoClient = new MongoClient();
    MongoDatabase db = mongoClient.getDatabase("test");
    MongoCollection<Document> rideDocuments = db.getCollection("rides");
    rideDocuments.drop();
    List<Document> testRides = new ArrayList<>();
    testRides.add(Document.parse("{\n" +
      "                    driver: \"Luisa Griffith\",\n" +
      "                    roundTrip: false,\n" +
      "                    dateTime: \"Sun Aug 25 2002 08:03:03 GMT+0000 (UTC)\",\n" +
      "                    route: [\"Rockbridge\",\"Wawona\"]\n" +
      "                    notes: \"These are notes\",\n" +
      "                }"));

    samsId = new ObjectId();
    List<String> samRoute = Arrays.asList("Here", "There");
    BasicDBObject sam = new BasicDBObject("_id", samsId);
    sam = sam.append("driver", "Sam")
      .append("roundTrip", true)
      .append("route", samRoute)
      .append("dateTime", "Sun Oct 21 1990 19:09:39 GMT+0000 (UTC)")
      .append("notes","Est dolore ipsum cillum culpa sint dolor aliquip.");


    rideDocuments.insertMany(testRides);
    rideDocuments.insertOne(Document.parse(sam.toJson()));

    rideController = new RideController(db);
  }

  private BsonArray parseJsonArray(String json) {
    final CodecRegistry codecRegistry
      = CodecRegistries.fromProviders(Arrays.asList(
      new ValueCodecProvider(),
      new BsonValueCodecProvider(),
      new DocumentCodecProvider()));

    JsonReader reader = new JsonReader(json);
    BsonArrayCodec arrayReader = new BsonArrayCodec(codecRegistry);

    return arrayReader.decode(reader, DecoderContext.builder().build());
  }

  //Gets Name returns as a string
  private static String getDriver(BsonValue val) {
    BsonDocument doc = val.asDocument();
    return ((BsonString) doc.get("driver")).getValue();
  }
}
