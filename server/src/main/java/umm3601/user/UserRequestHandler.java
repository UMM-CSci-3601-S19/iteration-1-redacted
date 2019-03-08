package umm3601.user;

import org.bson.Document;
import spark.Request;
import spark.Response;

import java.util.List;

public class UserRequestHandler {

  private final UserController userController;

  public UserRequestHandler(UserController userController) {
    this.userController = userController;
  }

  public String getUserJSON(Request req, Response res) {
    res.type("application/json");
    String id = req.params("id");
    String user;
    try {
      user = userController.getUser(id);
    } catch (IllegalArgumentException e) {
      res.status(400);
      res.body("The requested user id " + id + " wasn't a legal Mongo Object ID.\n" +
        "See 'https://docs.mongodb.com/manual/reference/method/ObjectId/' for more info.");
      return "";
    }
    if (user != null) {
      return user;
    } else {
      res.status(404);
      res.body("The requested user with id " + id + " was not found");
      return "";
    }
  }

  public String getUsers(Request req, Response res) {
    res.type("application/json");
    return userController.getUsers(req.queryMap().toMap());
  }

  public String addNewUser(Request req, Response res) {
    res.type("application/json");

    Document newUser = Document.parse(req.body());
    List<String> phoneNumbers = newUser.getList("phone", String.class);

    // Currently obsolete could be useful
//    String[] phone = new String[3];
//    int i = 0;
//
//    for (String numbers : phoneNumbers) {
//      phone[i] = numbers;
//      i++;
//    }

    String name = newUser.getString("name");
    String vehicle = newUser.getString("vehicle");
    String email = newUser.getString("email");

    System.err.println("Adding new user [name=" + name + ", vehicle=" + vehicle + " phone=" + phoneNumbers + " email=" + email + ']');
    return userController.addNewUser(name, vehicle, phoneNumbers, email);
  }
}
