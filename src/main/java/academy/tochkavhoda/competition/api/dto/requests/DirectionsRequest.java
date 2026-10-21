package academy.tochkavhoda.competition.api.dto.requests;

import java.util.List;

public class DirectionsRequest {
    public List<String> directions;

    public DirectionsRequest() {
    }

    public DirectionsRequest(List<String> directions) {
        this.directions = directions;
    }
}
