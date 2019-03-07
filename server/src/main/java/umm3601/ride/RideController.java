package umm3601.ride;

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

public class RideController {

  private final MongoCollection<Document> rideCollection;

  public RideController(MongoDatabase database) {
    rideCollection = database.getCollection("rides");
  }

  public String getRide(String id) {
    FindIterable<Document> jsonRides
      = rideCollection
      .find(eq("_id", new ObjectId(id)));

    Iterator<Document> iterator = jsonRides.iterator();
    if (iterator.hasNext()) {
      Document ride = iterator.next();
      return ride.toJson();
    } else {
      // We didn't find the desired ride
      return null;
    }
  }

  public String getRides(Map<String, String[]> queryParams) {

    Document filterDoc = new Document();

    if (queryParams.containsKey("driver")) {
      String targetContent = (queryParams.get("driver")[0]);
      Document contentRegQuery = new Document();
      contentRegQuery.append("$regex", targetContent);
      contentRegQuery.append("$options", "i");
      filterDoc = filterDoc.append("driver", contentRegQuery);
    }

    if (queryParams.containsKey("riders")) {
      String targetContent = (queryParams.get("riders")[0]);
      Document contentRegQuery = new Document();
      contentRegQuery.append("$regex", targetContent);
      contentRegQuery.append("$options", "i");
      filterDoc = filterDoc.append("riders", contentRegQuery);
    }

    FindIterable<Document> matchingRides = rideCollection.find(filterDoc);

    return serializeIterable(matchingRides);
  }

  private String serializeIterable(Iterable<Document> documents) {
    return StreamSupport.stream(documents.spliterator(), false)
      .map(Document::toJson)
      .collect(Collectors.joining(", ", "[", "]"));
  }

//  public String addNewRide(String makeModel, int year, String color, int mpg, String notes) {
//
//    Document newVehicle = new Document();
//    newVehicle.append("makeModel", makeModel);
//    newVehicle.append("year", year);
//    newVehicle.append("color", color);
//    newVehicle.append("mpg", mpg);
//    newVehicle.append("notes", notes);
//
//    try {
//      rideCollection.insertOne(newVehicle);
//      ObjectId id = newVehicle.getObjectId("_id");
//      System.err.println("Successfully added new vehicle [_id=" + id + ", makeModel=" + makeModel + ", year=" + year + " color=" + color + " mpg =" + mpg + " notes=" + notes + ']');
//      return id.toHexString();
//    } catch (MongoException me) {
//      me.printStackTrace();
//      return null;
//    }
//  }
}
