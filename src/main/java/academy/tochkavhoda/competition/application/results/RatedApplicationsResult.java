package academy.tochkavhoda.competition.application.results;

import java.util.List;

public class RatedApplicationsResult {
    public final List<RatedApplicationResult> applications;

    public RatedApplicationsResult(List<RatedApplicationResult> applications) {
        this.applications = applications;
    }
}
