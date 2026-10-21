package academy.tochkavhoda.competition.api.dto.responses;

import academy.tochkavhoda.competition.application.results.ParticipantResult;

import java.util.Objects;

public class ParticipantResponse {
    public String firstName;
    public String lastName;
    public String companyName;

    public ParticipantResponse() {
    }

    public ParticipantResponse(String firstName, String lastName, String companyName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.companyName = companyName;
    }

    public static ParticipantResponse from(ParticipantResult result) {
        return new ParticipantResponse(result.firstName, result.lastName, result.companyName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipantResponse that = (ParticipantResponse) o;
        return Objects.equals(firstName, that.firstName)
                && Objects.equals(lastName, that.lastName)
                && Objects.equals(companyName, that.companyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, companyName);
    }
}
