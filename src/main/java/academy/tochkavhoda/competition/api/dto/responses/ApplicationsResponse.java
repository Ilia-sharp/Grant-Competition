package academy.tochkavhoda.competition.api.dto.responses;

import academy.tochkavhoda.competition.application.results.ApplicationsResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ApplicationsResponse {
    public List<ApplicationResponse> applications;

    public ApplicationsResponse() {
    }

    public ApplicationsResponse(List<ApplicationResponse> applications) {
        this.applications = applications;
    }

    public static ApplicationsResponse from(ApplicationsResult result) {
        List<ApplicationResponse> items = new ArrayList<>();
        for (academy.tochkavhoda.competition.application.results.ApplicationResult application
                : result.applications) {
            items.add(ApplicationResponse.from(application));
        }
        return new ApplicationsResponse(items);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(applications, ((ApplicationsResponse) o).applications);
    }

    @Override
    public int hashCode() {
        return Objects.hash(applications);
    }
}
