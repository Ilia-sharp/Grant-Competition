package academy.tochkavhoda.competition.api.dto.responses;

import academy.tochkavhoda.competition.application.results.ApplicationIdResult;

import java.util.Objects;

public class ApplicationIdResponse {
    public int id;

    public ApplicationIdResponse() {
    }

    public ApplicationIdResponse(int id) {
        this.id = id;
    }

    public static ApplicationIdResponse from(ApplicationIdResult result) {
        return new ApplicationIdResponse(result.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id == ((ApplicationIdResponse) o).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
