package umm3601.vehicle;

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

public class VehicleControllerSpec {
  private VehicleController vehicleController;
  private ObjectId vehicleFourId;

  @Before
  public void clearAndPopulateDB() {
    MongoClient mongoClient = new MongoClient();
    MongoDatabase db = mongoClient.getDatabase("test");
    MongoCollection<Document> vehiclesDocuments = db.getCollection("vehicles");
    vehiclesDocuments.drop();
    List<Document> testVehicles = new ArrayList<>();
    testVehicles.add(Document.parse("{\n" +
      "                    makeModel: \"Honda Fit\",\n" +
      "                    year: 1996,\n" +
      "                    color: \"yellow\",\n" +
      "                    mpg: 25" +
      "                    notes: \"No A/C!\"\n" +
      "                }"));
    testVehicles.add(Document.parse("{\n" +
      "                    makeModel: \"Honda Civic\",\n" +
      "                    year: 2015,\n" +
      "                    color: \"black\",\n" +
      "                    mpg: 27" +
      "                    notes: \"Heater does not work!\"\n" +
      "                }"));
    testVehicles.add(Document.parse("{\n" +
      "                    makeModel: \"Ford Fusion\",\n" +
      "                    year: 2005,\n" +
      "                    color: \"pink\",\n" +
      "                    mpg: 20" +
      "                    notes: \"\"\n" +
      "                }"));

    vehicleFourId = new ObjectId();
    BasicDBObject odyssey = new BasicDBObject("_id", vehicleFourId);
    odyssey = odyssey.append("makeModel", "Honda Odyssey")
      .append("year", 2019)
      .append("color", "white")
      .append("mpg", 30)
      .append("notes", "Brand new");

    vehiclesDocuments.insertMany(testVehicles);
    vehiclesDocuments.insertOne(Document.parse(odyssey.toJson()));

    vehicleController = new VehicleController(db);
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

  //Gets makeModel returns as a string
  private static String getMakeModel(BsonValue val) {
    BsonDocument doc = val.asDocument();
    return ((BsonString) doc.get("makeModel")).getValue();
  }

  @Test
  public void getAllVehicles() {
    Map<String, String[]> emptyMap = new HashMap<>();
    String jsonResult = vehicleController.getVehicles(emptyMap);
    BsonArray docs = parseJsonArray(jsonResult);

    assertEquals("Should be 4 rides", 4, docs.size());
    List<String> makeModel = docs
      .stream()
      .map(VehicleControllerSpec::getMakeModel)
      .sorted()
      .collect(Collectors.toList());
    List<String> expectedMakeModel = Arrays.asList("Ford Fusion", "Honda Civic", "Honda Fit", "Honda Odyssey");
    assertEquals("Drivers should match", expectedMakeModel, makeModel);
  }

  @Test
  public void getVehicleByMakeModel() {
    Map<String, String[]> argMap = new HashMap<>();
    argMap.put("makeModel", new String[]{"Honda"});
    String jsonResult = vehicleController.getVehicles(argMap);
    BsonArray docs = parseJsonArray(jsonResult);

    assertEquals("Should be 3 vehicles", 3, docs.size());
    List<String> makeModel = docs
      .stream()
      .map(VehicleControllerSpec::getMakeModel)
      .sorted()
      .collect(Collectors.toList());
    List<String> expectedMakeModel = Arrays.asList("Honda Civic", "Honda Fit", "Honda Odyssey");
    assertEquals("makeModels should match", expectedMakeModel, makeModel);
  }

  @Test
  public void getVehicleByYear() {
    Map<String, String[]> argMap = new HashMap<>();
    argMap.put("year", new String[]{"1996"});
    String jsonResult = vehicleController.getVehicles(argMap);
    BsonArray docs = parseJsonArray(jsonResult);

    assertEquals("Should be 1 vehicle", 1, docs.size());
    List<String> makeModel = docs
      .stream()
      .map(VehicleControllerSpec::getMakeModel)
      .sorted()
      .collect(Collectors.toList());
    List<String> expectedMakeModel = Arrays.asList("Honda Fit");
    assertEquals("makeModels should match", expectedMakeModel, makeModel);
  }

  @Test
  public void getVehicleByColor() {
    Map<String, String[]> argMap = new HashMap<>();
    argMap.put("color", new String[]{"black"});
    String jsonResult = vehicleController.getVehicles(argMap);
    BsonArray docs = parseJsonArray(jsonResult);

    assertEquals("Should be 1 vehicle", 1, docs.size());
    List<String> makeModel = docs
      .stream()
      .map(VehicleControllerSpec::getMakeModel)
      .sorted()
      .collect(Collectors.toList());
    List<String> expectedMakeModel = Arrays.asList("Honda Civic");
    assertEquals("makeModels should match", expectedMakeModel, makeModel);
  }

  @Test
  public void getVehicleByNotes() {
    Map<String, String[]> argMap = new HashMap<>();
    argMap.put("notes", new String[]{"new"});
    String jsonResult = vehicleController.getVehicles(argMap);
    BsonArray docs = parseJsonArray(jsonResult);

    assertEquals("Should be 1 vehicle", 1, docs.size());
    List<String> makeModel = docs
      .stream()
      .map(VehicleControllerSpec::getMakeModel)
      .sorted()
      .collect(Collectors.toList());
    List<String> expectedMakeModel = Arrays.asList("Honda Odyssey");
    assertEquals("makeModels should match", expectedMakeModel, makeModel);
  }

  @Test
  public void getHondaOdysseyById() {
    String jsonResult = vehicleController.getVehicle(vehicleFourId.toHexString());
    Document hondaOdyssey = Document.parse(jsonResult);
    assertEquals("makeModel should match", "Honda Odyssey", hondaOdyssey.get("makeModel"));
    String noJsonResult = vehicleController.getVehicle(new ObjectId().toString());
    assertNull("No makeModel should match", noJsonResult);
  }

  @Test
  public void addVehicleTest() {
    String newId = vehicleController.addNewVehicle("Nissan Skyline GT-R", 2013, "purple", 29, "Some notes about the vehicle.");

    assertNotNull("Add new vehicle should return true when vehicle is added,", newId);
    Map<String, String[]> argMap = new HashMap<>();
    argMap.put("makeModel", new String[]{"Nissan"});
    String jsonResult = vehicleController.getVehicles(argMap);
    BsonArray docs = parseJsonArray(jsonResult);

    List<String> makeModel = docs
      .stream()
      .map(VehicleControllerSpec::getMakeModel)
      .sorted()
      .collect(Collectors.toList());
    assertEquals("Should return makeModel of new vehicle", "Nissan Skyline GT-R", makeModel.get(0));
  }
}
