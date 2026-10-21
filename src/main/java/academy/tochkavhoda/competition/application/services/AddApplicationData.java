package academy.tochkavhoda.competition.application.services;

import java.util.List;

public class AddApplicationData {
    public final String title;
    public final String description;
    public final List<String> directions;
    public final Long requestedSum;

    public AddApplicationData(String title, String description, List<String> directions, Long requestedSum) {
        this.title = title;
        this.description = description;
        this.directions = directions;
        this.requestedSum = requestedSum;
    }
}
