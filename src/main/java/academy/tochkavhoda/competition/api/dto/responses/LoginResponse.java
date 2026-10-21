package academy.tochkavhoda.competition.api.dto.responses;

import academy.tochkavhoda.competition.application.results.LoginResult;

import java.util.Objects;

public class LoginResponse {
    public String token;

    public LoginResponse() {
    }

    public LoginResponse(String token) {
        this.token = token;
    }

    public static LoginResponse from(LoginResult result) {
        return new LoginResponse(result.token);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(token, ((LoginResponse) o).token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }
}
