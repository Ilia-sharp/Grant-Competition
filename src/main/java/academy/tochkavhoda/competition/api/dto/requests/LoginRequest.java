package academy.tochkavhoda.competition.api.dto.requests;

public class LoginRequest {
    public String login;
    public String password;

    public LoginRequest() {
    }

    public LoginRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
