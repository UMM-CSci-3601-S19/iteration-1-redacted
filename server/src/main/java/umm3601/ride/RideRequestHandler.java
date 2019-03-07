package umm3601.ride;

import org.bson.Document;
import spark.Request;
import spark.Response;

public class RideRequestHandler {

  private final RideController rideController;

  public RideRequestHandler(RideController rideController) {
    this.rideController = rideController;
  }

  public String getRideJSON(Request req, Response res) {
    res.type("application/json");
    String id = req.params("id");
    String ride;
    try {
      ride = rideController.getRide(id);
    } catch (IllegalArgumentException e) {
      res.status(400);
      res.body("The requested ride id " + id + " wasn't a legal Mongo Object ID.\n" +
        "See 'https://docs.mongodb.com/manual/reference/method/ObjectId/' for more info.");
      return "";
    }
    if (ride != null) {
      return ride;
    } else {
      res.status(404);
      res.body("The requested ride with id " + id + " was not found");
      return "";
    }
  }

  public String getRides(Request req, Response res) {
    res.type("application/json");
    return rideController.getRides(req.queryMap().toMap());
  }

//  public String addNewVehicle(Request req, Response res) {
//    res.type("application/json");
//
//    Document newVehicle = Document.parse(req.body());
//
//    String makeModel = newVehicle.getString("makeModel");
//    int year = newVehicle.getInteger("year");
//    String color = newVehicle.getString("color");
//    int mpg = newVehicle.getInteger("mpg");
//    String notes = newVehicle.getString("notes");
//
//    System.err.println("Adding new vehicle [makeModel=" + makeModel + ", year=" + year + " color=" + color + " mpg=" + mpg + " notes=" + notes + ']');
//    return rideController.addNewVehicle(makeModel, year, color, mpg, notes);
//  }
}
