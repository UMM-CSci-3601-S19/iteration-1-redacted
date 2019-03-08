package umm3601.ride;

import org.bson.Document;
import spark.Request;
import spark.Response;

import java.util.Date;
import java.util.List;

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

  public String addNewRide(Request req, Response res) {
    res.type("application/json");

    Document newRide = Document.parse(req.body());

    String driver = newRide.getString("driver");
    List<String> riders = newRide.getList("riders", String.class);
    List<String> route = newRide.getList("route", String.class);
    Boolean roundTrip = newRide.getBoolean("roundTrip");
    Date dateTime = newRide.getDate("dateTime");
    String notes = newRide.getString("notes");

    System.err.println("Adding new vehicle [driver=" + driver + ", riders=" + riders + " route=" + route + " roundTrip=" + roundTrip + "dateTime=" + dateTime +" notes=" + notes + ']');
    return rideController.addNewRide(driver, riders, route, roundTrip, dateTime, notes);
  }
}
