package academy.tochkavhoda.competition.api.dto.requests;

import java.util.List;

public class AddApplicationRequest {
    public String title;
    public String description;
    public List<String> directions;
    public Long requestedSum;

    public AddApplicationRequest() {
    }

    public AddApplicationRequest(String title, String description, List<String> directions, Long requestedSum) {
        this.title = title;
        this.description = description;
        this.directions = directions;
        this.requestedSum = requestedSum;
    }
}
