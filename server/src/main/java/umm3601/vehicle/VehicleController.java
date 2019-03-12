package umm3601.vehicle;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Filters.eq;

public class VehicleController {

  private final MongoCollection<Document> vehicleCollection;

  public VehicleController(MongoDatabase database) {
    vehicleCollection = database.getCollection("vehicles");
  }

  public String getVehicle(String id) {
    FindIterable<Document> jsonVehicles
      = vehicleCollection
      .find(eq("_id", new ObjectId(id)));

    Iterator<Document> iterator = jsonVehicles.iterator();
    if (iterator.hasNext()) {
      Document vehicle = iterator.next();
      return vehicle.toJson();
    } else {
      // We didn't find the desired vehicle
      return null;
    }
  }

  public String getVehicles(Map<String, String[]> queryParams) {

    Document filterDoc = new Document();

    if (queryParams.containsKey("makeModel")) {
      String targetContent = (queryParams.get("makeModel")[0]);
      Document contentRegQuery = new Document();
      contentRegQuery.append("$regex", targetContent);
      contentRegQuery.append("$options", "i");
      filterDoc = filterDoc.append("makeModel", contentRegQuery);
    }

    if (queryParams.containsKey("year")) {
      int targetYear = Integer.parseInt(queryParams.get("year")[0]);
      filterDoc = filterDoc.append("year", targetYear);
    }

    if (queryParams.containsKey("color")) {
      String targetContent = (queryParams.get("color")[0]);
      Document contentRegQuery = new Document();
      contentRegQuery.append("$regex", targetContent);
      contentRegQuery.append("$options", "i");
      filterDoc = filterDoc.append("color", contentRegQuery);
    }

    if (queryParams.containsKey("notes")) {
      String targetContent = (queryParams.get("notes")[0]);
      Document contentRegQuery = new Document();
      contentRegQuery.append("$regex", targetContent);
      contentRegQuery.append("$options", "i");
      filterDoc = filterDoc.append("notes", contentRegQuery);
    }

    FindIterable<Document> matchingVehicles = vehicleCollection.find(filterDoc);

    return serializeIterable(matchingVehicles);
  }

  private String serializeIterable(Iterable<Document> documents) {
    return StreamSupport.stream(documents.spliterator(), false)
      .map(Document::toJson)
      .collect(Collectors.joining(", ", "[", "]"));
  }

  public String addNewVehicle(String makeModel, int year, String color, int mpg, String notes) {

    Document newVehicle = new Document();
    newVehicle.append("makeModel", makeModel);
    newVehicle.append("year", year);
    newVehicle.append("color", color);
    newVehicle.append("mpg", mpg);
    newVehicle.append("notes", notes);

    try {
      vehicleCollection.insertOne(newVehicle);
      ObjectId id = newVehicle.getObjectId("_id");
      System.err.println("Successfully added new vehicle [_id=" + id + ", makeModel=" + makeModel + ", year=" + year + " color=" + color + " mpg =" + mpg + " notes=" + notes + ']');
      return id.toHexString();
    } catch (MongoException me) {
      me.printStackTrace();
      return null;
    }
  }
}

