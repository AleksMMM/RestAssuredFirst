package Api;

import io.restassured.http.ContentType;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class ReqTest {
    private final static String URL = "https://reqres.in";

    @Test
    public void checkAvatar() {
        Specification.installSpecification(Specification.requestSpecification(URL), Specification.responseSpecificationOn200());
        List<UserData> users = given()
                .when()
                .contentType(ContentType.JSON)
                .get("/api/users?page=2")
                .then()
                .log().all()
                .extract().body().jsonPath().getList("data", UserData.class);
        int i = 0;
        users.forEach(x -> Assert.assertTrue(x.getAvatar().contains(x.getId().toString())));

        Assert.assertTrue(users.stream().allMatch(x -> x.getEmail().endsWith("@reqres.in")));

        List<String> avatars = users.stream().map(UserData::getAvatar).collect(Collectors.toList());
        List<String> id = users.stream().map(x -> x.getId().toString()).collect(Collectors.toList());

        for (int j = 0; j < avatars.size(); j++) {
            Assert.assertTrue(avatars.get(j).contains(id.get(j)));
        }

    }
    @Test
    public void sucsessRegister() {
        Specification.installSpecification(Specification.requestSpecification(URL), Specification.responseSpecificationOn200());
        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";
        Register user = new Register("eve.holt@reqres.in", "pistol");
        SuccessReg successReg = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(SuccessReg.class);
        Assert.assertNotNull(successReg.getId());
        Assert.assertNotNull(successReg.getToken());

        Assert.assertEquals(token, successReg.getToken());


    }

    @Test
    public void unSuccessUserReg() {
        Specification.installSpecification(Specification.requestSpecification(URL), Specification.responseSpecificationError400());
        Register user = new Register("sydney@fife", "");
        UnSucc unSucc = given()
                .body(user)
                .post("api/register")
                .then().log().all()
                .extract().as(UnSucc.class);
        Assert.assertEquals("Missing password", unSucc.getError());
    }
}
