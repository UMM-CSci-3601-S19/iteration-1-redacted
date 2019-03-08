package umm3601.user;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Filters.eq;

public class UserController {

  private final MongoCollection<Document> userCollection;

  public UserController(MongoDatabase database) {
    userCollection = database.getCollection("users");
  }

  public String getUser(String id) {
    FindIterable<Document> jsonUsers
      = userCollection
      .find(eq("_id", new ObjectId(id)));

    Iterator<Document> iterator = jsonUsers.iterator();
    if (iterator.hasNext()) {
      Document user = iterator.next();
      return user.toJson();
    } else {
      // We didn't find the desired user
      return null;
    }
  }

  public String getUsers(Map<String, String[]> queryParams) {

    Document filterDoc = new Document();

    if (queryParams.containsKey("name")) {
      String targetContent = (queryParams.get("name")[0]);
      Document contentRegQuery = new Document();
      contentRegQuery.append("$regex", targetContent);
      contentRegQuery.append("$options", "i");
      filterDoc = filterDoc.append("name", contentRegQuery);
    }

    // Not Sure if we want to be able to filter by any of these
    // Possibly need a vehicle filter in the future
//
//    if (queryParams.containsKey("phone")) {
//      String targetContent = (queryParams.get("phone")[0]);
//      Document contentRegQuery = new Document();
//      contentRegQuery.append("$regex", targetContent);
//      contentRegQuery.append("$options", "i");
//      filterDoc = filterDoc.append("phone", contentRegQuery);
//    }
//
//    if (queryParams.containsKey("email")) {
//      String targetContent = (queryParams.get("email")[0]);
//      Document contentRegQuery = new Document();
//      contentRegQuery.append("$regex", targetContent);
//      contentRegQuery.append("$options", "i");
//      filterDoc = filterDoc.append("email", contentRegQuery);
//    }

    //FindIterable comes from mongo, Document comes from Gson
    FindIterable<Document> matchingUsers = userCollection.find(filterDoc);

    return serializeIterable(matchingUsers);
  }

  private String serializeIterable(Iterable<Document> documents) {
    return StreamSupport.stream(documents.spliterator(), false)
      .map(Document::toJson)
      .collect(Collectors.joining(", ", "[", "]"));
  }

  public String addNewUser(String name, String vehicle, List<String> phone, String email) {

    Document newUser = new Document();
    newUser.append("name", name);
    newUser.append("vehicle", vehicle);
    newUser.append("phone", phone);
    newUser.append("email", email);

    try {
      userCollection.insertOne(newUser);
      ObjectId id = newUser.getObjectId("_id");
      System.err.println("Successfully added new user [_id=" + id + ", name=" + name + ", vehicle=" + vehicle + " phone=" + phone + " email=" + email + ']');
      return id.toHexString();
    } catch (MongoException me) {
      me.printStackTrace();
      return null;
    }
  }
}
