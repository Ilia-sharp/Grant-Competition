package academy.tochkavhoda.competition.api.dto.responses;

import academy.tochkavhoda.competition.application.results.ApplicationResult;

import java.util.Objects;

public class ApplicationResponse {
    public int id;
    public String title;
    public String description;
    public java.util.List<String> directions;
    public long requestedSum;
    public String companyName;

    public ApplicationResponse() {
    }

    public ApplicationResponse(int id, String title, String description, java.util.List<String> directions,
                               long requestedSum, String companyName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.directions = directions;
        this.requestedSum = requestedSum;
        this.companyName = companyName;
    }

    public static ApplicationResponse from(ApplicationResult result) {
        return new ApplicationResponse(result.id, result.title, result.description, result.directions,
                result.requestedSum, result.companyName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationResponse that = (ApplicationResponse) o;
        return id == that.id
                && requestedSum == that.requestedSum
                && Objects.equals(title, that.title)
                && Objects.equals(description, that.description)
                && Objects.equals(directions, that.directions)
                && Objects.equals(companyName, that.companyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, directions, requestedSum, companyName);
    }
}
