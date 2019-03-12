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
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class RideControllerSpec {
  private RideController rideController;
  private ObjectId rideFourId;

  @Before
  public void clearAndPopulateDB() {
    MongoClient mongoClient = new MongoClient();
    MongoDatabase db = mongoClient.getDatabase("test");
    MongoCollection<Document> ridesDocuments = db.getCollection("rides");
    ridesDocuments.drop();
    List<Document> testRides = new ArrayList<>();
    testRides.add(Document.parse("{\n" +
      "                    driver: \"Chris\",\n" +
      "                    riders: [\"Tabitha\", \"Boris\"],\n" +
      "                    route: [\"Morris\",\n\"Minneapolis\"],\n" +
      "                    roundTrip: false" +
      "                    dateTime: \"Sun Jun 24 2012 09:27:19 GMT+0000 (UTC)\"\n" +
      "                    notes: \"I am allergic to cats.  I would prefer if we did not stop until we reach our destination\"\n" +
      "                }"));
    testRides.add(Document.parse("{\n" +
      "                    driver: \"Topher\",\n" +
      "                    riders: [\"\"],\n" +
      "                    route: [\"Morris\", \"Big Lake\"],\n" +
      "                    roundTrip: true" +
      "                    dateTime: \"Sun Jun 24 2012 09:27:19 GMT+0000 (UTC)\"\n" +
      "                    notes: \"I listen to exclusively smooth jazz.\"\n" +
      "                }"));
    testRides.add(Document.parse("{\n" +
      "                    driver: \"Sandy\",\n" +
      "                    riders: [\"Beatrice\", \"Paul\"],\n" +
      "                    route: [\"Morris\", \"Chicago\"],\n" +
      "                    roundTrip: false" +
      "                    dateTime: \"Sun Jun 24 2012 09:27:19 GMT+0000 (UTC)\"\n" +
      "                    notes: \"I am willing to drop people off along the way\"\n" +
      "                }"));

    rideFourId = new ObjectId();
    List<String> routeList = Arrays.asList("Fargo", "Morris");
    List<String> riderList = Arrays.asList("Albert");
    BasicDBObject sam = new BasicDBObject("_id", rideFourId);
    sam = sam.append("driver", "Sam")
      .append("riders", riderList)
      .append("route", routeList)
      .append("roundTrip", false)
      .append("dateTime", "2019-03-10")
      .append("notes", "My name is Sam and I will drive you to Morris.");


    ridesDocuments.insertMany(testRides);
    ridesDocuments.insertOne(Document.parse(sam.toJson()));

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

  //Gets Driver returns as a string
  private static String getDriver(BsonValue val) {
    BsonDocument doc = val.asDocument();
    return ((BsonString) doc.get("driver")).getValue();
  }

  @Test
  public void getAllRides() {
    Map<String, String[]> emptyMap = new HashMap<>();
    String jsonResult = rideController.getRides(emptyMap);
    BsonArray docs = parseJsonArray(jsonResult);

    assertEquals("Should be 4 rides", 4, docs.size());
    List<String> drivers = docs
      .stream()
      .map(RideControllerSpec::getDriver)
      .sorted()
      .collect(Collectors.toList());
    List<String> expectedDrivers = Arrays.asList("Chris", "Sam", "Sandy", "Topher");
    assertEquals("Drivers should match", expectedDrivers, drivers);
  }

  @Test
  public void getRideByDriver() {
    Map<String, String[]> argMap = new HashMap<>();
    argMap.put("driver", new String[]{"Topher"});
    String jsonResult = rideController.getRides(argMap);
    BsonArray docs = parseJsonArray(jsonResult);

    assertEquals("Should be 1 user", 1, docs.size());
    List<String> drivers = docs
      .stream()
      .map(RideControllerSpec::getDriver)
      .sorted()
      .collect(Collectors.toList());
    List<String> expectedDrivers = Arrays.asList("Topher");
    assertEquals("Drivers should match", expectedDrivers, drivers);
  }

  @Test
  public void getSamById() {
    String jsonResult = rideController.getRide(rideFourId.toHexString());
    Document sam = Document.parse(jsonResult);
    assertEquals("Driver should match", "Sam", sam.get("driver"));
    String noJsonResult = rideController.getRide(new ObjectId().toString());
    assertNull("No driver should match", noJsonResult);
  }

  @Test
  public void addRideTest() {
    List<String> routeList = Arrays.asList("Minneapolis", "Morris");
    List<String> riderList = Arrays.asList("George", "Samantha");
    String newId = rideController.addNewRide("Brian", riderList, routeList, false, "Sun Jun 24 2012 09:27:19 GMT+0000 (UTC)", "Some notes about the trip.");

    assertNotNull("Add new ride should return true when ride is added,", newId);
    Map<String, String[]> argMap = new HashMap<>();
    argMap.put("driver", new String[]{"Brian"});
    String jsonResult = rideController.getRides(argMap);
    BsonArray docs = parseJsonArray(jsonResult);

    List<String> driver = docs
      .stream()
      .map(RideControllerSpec::getDriver)
      .sorted()
      .collect(Collectors.toList());
    assertEquals("Should return driver of new ride", "Brian", driver.get(0));
  }
}
