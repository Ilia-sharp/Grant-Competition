package academy.tochkavhoda.competition.api.dto.responses;

import academy.tochkavhoda.competition.application.results.RatedApplicationResult;

import java.util.Objects;

public class RatedApplicationResponse {
    public ApplicationResponse application;
    public int rating;

    public RatedApplicationResponse() {
    }

    public RatedApplicationResponse(ApplicationResponse application, int rating) {
        this.application = application;
        this.rating = rating;
    }

    public static RatedApplicationResponse from(RatedApplicationResult result) {
        return new RatedApplicationResponse(ApplicationResponse.from(result.application), result.rating);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RatedApplicationResponse that = (RatedApplicationResponse) o;
        return rating == that.rating && Objects.equals(application, that.application);
    }

    @Override
    public int hashCode() {
        return Objects.hash(application, rating);
    }
}
