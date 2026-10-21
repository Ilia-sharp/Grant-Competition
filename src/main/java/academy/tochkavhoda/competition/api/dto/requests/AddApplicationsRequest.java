package academy.tochkavhoda.competition.api.dto.requests;

import java.util.List;

public class AddApplicationsRequest {
    public List<AddApplicationRequest> applications;

    public AddApplicationsRequest() {
    }

    public AddApplicationsRequest(List<AddApplicationRequest> applications) {
        this.applications = applications;
    }
}
