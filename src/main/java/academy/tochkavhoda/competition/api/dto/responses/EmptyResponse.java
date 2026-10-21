package academy.tochkavhoda.competition.api.dto.responses;

import academy.tochkavhoda.competition.application.results.EmptyResult;

public class EmptyResponse {
    public static EmptyResponse from(EmptyResult result) {
        return new EmptyResponse();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof EmptyResponse;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
