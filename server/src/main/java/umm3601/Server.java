package umm3601;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import spark.Request;
import spark.Response;
import umm3601.ride.RideController;
import umm3601.ride.RideRequestHandler;
import spark.Route;
import spark.utils.IOUtils;
import umm3601.user.UserController;
import umm3601.user.UserRequestHandler;
import umm3601.vehicle.VehicleController;
import umm3601.vehicle.VehicleRequestHandler;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

import java.io.InputStream;

public class Server {
  private static final String userDatabaseName = "dev";
  private static final String vehicleDatabaseName = "dev";
  private static final String rideDatabaseName = "dev";
  private static final int serverPort = 4567;

  public static void main(String[] args) {

    MongoClient mongoClient = new MongoClient();
    MongoDatabase userDatabase = mongoClient.getDatabase(userDatabaseName);
    MongoDatabase vehicleDatabase = mongoClient.getDatabase(vehicleDatabaseName);
    MongoDatabase rideDatabase = mongoClient.getDatabase(rideDatabaseName);

    UserController userController = new UserController(userDatabase);
    UserRequestHandler userRequestHandler = new UserRequestHandler(userController);

    VehicleController vehicleController = new VehicleController(vehicleDatabase);
    VehicleRequestHandler vehicleRequestHandler = new VehicleRequestHandler(vehicleController);

    RideController rideController = new RideController(rideDatabase);
    RideRequestHandler rideRequestHandler = new RideRequestHandler(rideController);

    //Configure Spark
    port(serverPort);
    enableDebugScreen();

    // Specify where assets like images will be "stored"
    staticFiles.location("/public");

    options("/*", (request, response) -> {

      String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
      if (accessControlRequestHeaders != null) {
        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
      }

      String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
      if (accessControlRequestMethod != null) {
        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
      }

      return "OK";
    });

    before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

    // Redirects for the "home" page
    redirect.get("", "/");

    Route clientRoute = (req, res) -> {
	InputStream stream = Server.class.getResourceAsStream("/public/index.html");
	return IOUtils.toString(stream);
    };

    get("/", clientRoute);

    /// User Endpoints ///////////////////////////
    /////////////////////////////////////////////

    //List users, filtered using query parameters

    get("api/users", userRequestHandler::getUsers);
    get("api/users/:id", userRequestHandler::getUserJSON);
    post("api/users/new", userRequestHandler::addNewUser);

    get("api/vehicles", vehicleRequestHandler::getVehicles);
    get("api/vehicles/:id", vehicleRequestHandler::getVehicleJSON);
    post("api/vehicles/new", vehicleRequestHandler::addNewVehicle);

    get("api/rides", rideRequestHandler::getRides);
    get("api/rides/:id", rideRequestHandler::getRideJSON);
    post("api/rides/new", rideRequestHandler::addNewRide);

    // An example of throwing an unhandled exception so you can see how the
    // Java Spark debugger displays errors like this.
    get("api/error", (req, res) -> {
      throw new RuntimeException("A demonstration error");
    });

    // Called after each request to insert the GZIP header into the response.
    // This causes the response to be compressed _if_ the client specified
    // in their request that they can accept compressed responses.
    // There's a similar "before" method that can be used to modify requests
    // before they they're processed by things like `get`.
    after("*", Server::addGzipHeader);

    get("/*", clientRoute);

    // Handle "404" file not found requests:
    notFound((req, res) -> {
      res.type("text");
      res.status(404);
      return "Sorry, we couldn't find that!";
    });
  }

  // Enable GZIP for all responses
  private static void addGzipHeader(Request request, Response response) {
    response.header("Content-Encoding", "gzip");
  }
}
