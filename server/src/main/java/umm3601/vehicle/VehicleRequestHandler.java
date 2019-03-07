package umm3601.vehicle;

import org.bson.Document;
import spark.Request;
import spark.Response;

public class VehicleRequestHandler {

  private final VehicleController vehicleController;

  public VehicleRequestHandler(VehicleController vehicleController) {
    this.vehicleController = vehicleController;
  }

  public String getVehicleJSON(Request req, Response res) {
    res.type("application/json");
    String id = req.params("id");
    String vehicle;
    try {
      vehicle = vehicleController.getVehicle(id);
    } catch (IllegalArgumentException e) {
      res.status(400);
      res.body("The requested vehicle id " + id + " wasn't a legal Mongo Object ID.\n" +
        "See 'https://docs.mongodb.com/manual/reference/method/ObjectId/' for more info.");
      return "";
    }
    if (vehicle != null) {
      return vehicle;
    } else {
      res.status(404);
      res.body("The requested vehicle with id " + id + " was not found");
      return "";
    }
  }

  public String getVehicles(Request req, Response res) {
    res.type("application/json");
    return vehicleController.getVehicles(req.queryMap().toMap());
  }

  public String addNewVehicle(Request req, Response res) {
    res.type("application/json");

    Document newVehicle = Document.parse(req.body());

    String makeModel = newVehicle.getString("makeModel");
    int year = newVehicle.getInteger("year");
    String color = newVehicle.getString("color");
    int mpg = newVehicle.getInteger("mpg");
    String notes = newVehicle.getString("notes");

    System.err.println("Adding new vehicle [makeModel=" + makeModel + ", year=" + year + " color=" + color + " mpg=" + mpg + " notes=" + notes + ']');
    return vehicleController.addNewVehicle(makeModel, year, color, mpg, notes);
  }
}
