package academy.tochkavhoda.competition.api.dto.responses;

import academy.tochkavhoda.competition.application.results.ApplicationIdsResult;

import java.util.List;
import java.util.Objects;

public class ApplicationIdsResponse {
    public List<Integer> ids;

    public ApplicationIdsResponse() {
    }

    public ApplicationIdsResponse(List<Integer> ids) {
        this.ids = ids;
    }

    public static ApplicationIdsResponse from(ApplicationIdsResult result) {
        return new ApplicationIdsResponse(result.ids);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(ids, ((ApplicationIdsResponse) o).ids);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ids);
    }
}
