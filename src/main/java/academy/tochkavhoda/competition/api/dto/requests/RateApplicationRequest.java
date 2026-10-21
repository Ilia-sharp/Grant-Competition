package academy.tochkavhoda.competition.api.dto.requests;

public class RateApplicationRequest {
    public Integer applicationId;
    public Integer rating;

    public RateApplicationRequest() {
    }

    public RateApplicationRequest(Integer applicationId, Integer rating) {
        this.applicationId = applicationId;
        this.rating = rating;
    }
}
