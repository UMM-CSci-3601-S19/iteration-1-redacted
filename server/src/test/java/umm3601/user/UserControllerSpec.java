package umm3601.user;

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

public class UserControllerSpec {
  private UserController userController;
  private ObjectId samsId;

  @Before
  public void clearAndPopulateDB() {
    MongoClient mongoClient = new MongoClient();
    MongoDatabase db = mongoClient.getDatabase("test");
    MongoCollection<Document> userDocuments = db.getCollection("users");
    userDocuments.drop();
    List<Document> testUsers = new ArrayList<>();
    testUsers.add(Document.parse("{\n" +
      "                    name: \"Chris\",\n" +
      "                    vehicle: \"Honda Civic\",\n" +
      "                    phone: [\"(123) 456 7890\",\n\"(234) 567 8901\"],\n" +
      "                    email: \"chris@this.that\"\n" +
      "                }"));
    testUsers.add(Document.parse("{\n" +
      "                    name: \"Pat\",\n" +
      "                    vehicle: \"Honda Accord\",\n" +
      "                    phone: [\"(345) 678 9012\",\n\"(456) 789 0123\"],\n" +
      "                    email: \"chris@this.that\"\n" +
      "                }"));
    testUsers.add(Document.parse("{\n" +
      "                    name: \"Jamie\",\n" +
      "                    vehicle: \"Honda Odyssey\",\n" +
      "                    phone: [\"(456) 789 7890\",\n\"(567) 890 1234\"],\n" +
      "                    email: \"chris@this.that\"\n" +
      "                }"));

    samsId = new ObjectId();
    List<String> samPhone = Arrays.asList("(789) 012 3456", "(890) 123 4567");
    BasicDBObject sam = new BasicDBObject("_id", samsId);
    sam = sam.append("name", "Sam")
      .append("vehicle", "Honda Civic")
      .append("phone", samPhone)
      .append("email", "sam@frogs.com");


    userDocuments.insertMany(testUsers);
    userDocuments.insertOne(Document.parse(sam.toJson()));

    userController = new UserController(db);
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
  private static String getName(BsonValue val) {
    BsonDocument doc = val.asDocument();
    return ((BsonString) doc.get("name")).getValue();
  }

  @Test
  public void getAllUsers() {
    Map<String, String[]> emptyMap = new HashMap<>();
    String jsonResult = userController.getUsers(emptyMap);
    BsonArray docs = parseJsonArray(jsonResult);

    assertEquals("Should be 4 users", 4, docs.size());
    List<String> names = docs
      .stream()
      .map(UserControllerSpec::getName)
      .sorted()
      .collect(Collectors.toList());
    List<String> expectedNames = Arrays.asList("Chris", "Jamie", "Pat", "Sam");
    assertEquals("Names should match", expectedNames, names);
  }

  @Test
  public void getUserByName() {
    Map<String, String[]> argMap = new HashMap<>();
    argMap.put("name", new String[]{"Chris"});
    String jsonResult = userController.getUsers(argMap);
    BsonArray docs = parseJsonArray(jsonResult);

    assertEquals("Should be 1 user", 1, docs.size());
    List<String> names = docs
      .stream()
      .map(UserControllerSpec::getName)
      .sorted()
      .collect(Collectors.toList());
    List<String> expectedNames = Arrays.asList("Chris");
    assertEquals("Names should match", expectedNames, names);
  }

  @Test
  public void getSamById() {
    String jsonResult = userController.getUser(samsId.toHexString());
    Document sam = Document.parse(jsonResult);
    assertEquals("Name should match", "Sam", sam.get("name"));
    String noJsonResult = userController.getUser(new ObjectId().toString());
    assertNull("No name should match", noJsonResult);
  }

  @Test
  public void addUserTest() {
    List<String> phoneNumbers = Arrays.asList("(808) 404 5005", "(735) 101 1337");
    String newId = userController.addNewUser("Brian", "Brian's Vehicle", phoneNumbers, "brian@yahoo.com");

    assertNotNull("Add new user should return true when user is added,", newId);
    Map<String, String[]> argMap = new HashMap<>();
    argMap.put("vehicle", new String[]{"Brian's Vehicle"});
    String jsonResult = userController.getUsers(argMap);
    BsonArray docs = parseJsonArray(jsonResult);

    List<String> name = docs
      .stream()
      .map(UserControllerSpec::getName)
      .sorted()
      .collect(Collectors.toList());
    assertEquals("Should return name of new user", "Brian", name.get(0));
  }
}
