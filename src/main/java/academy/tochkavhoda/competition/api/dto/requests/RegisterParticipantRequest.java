package academy.tochkavhoda.competition.api.dto.requests;

public class RegisterParticipantRequest {
    public String firstName;
    public String lastName;
    public String companyName;
    public String login;
    public String password;

    public RegisterParticipantRequest() {
    }

    public RegisterParticipantRequest(String firstName, String lastName, String companyName,
                                      String login, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.companyName = companyName;
        this.login = login;
        this.password = password;
    }
}
