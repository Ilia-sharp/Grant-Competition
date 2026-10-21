package academy.tochkavhoda.competition.api.dto.requests;

import java.util.List;

public class RegisterExpertRequest {
    public String firstName;
    public String lastName;
    public List<String> directions;
    public String login;
    public String password;

    public RegisterExpertRequest() {
    }

    public RegisterExpertRequest(String firstName, String lastName, List<String> directions,
                                 String login, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.directions = directions;
        this.login = login;
        this.password = password;
    }
}
