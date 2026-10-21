package academy.tochkavhoda.competition.api.dto.responses;

import academy.tochkavhoda.competition.application.results.RatedApplicationsResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RatedApplicationsResponse {
    public List<RatedApplicationResponse> applications;

    public RatedApplicationsResponse() {
    }

    public RatedApplicationsResponse(List<RatedApplicationResponse> applications) {
        this.applications = applications;
    }

    public static RatedApplicationsResponse from(RatedApplicationsResult result) {
        List<RatedApplicationResponse> items = new ArrayList<>();
        for (academy.tochkavhoda.competition.application.results.RatedApplicationResult rated
                : result.applications) {
            items.add(RatedApplicationResponse.from(rated));
        }
        return new RatedApplicationsResponse(items);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(applications, ((RatedApplicationsResponse) o).applications);
    }

    @Override
    public int hashCode() {
        return Objects.hash(applications);
    }
}
