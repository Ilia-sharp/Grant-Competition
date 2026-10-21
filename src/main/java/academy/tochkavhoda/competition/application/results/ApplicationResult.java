package academy.tochkavhoda.competition.application.results;

import java.util.List;

public class ApplicationResult {
    public final int id;
    public final String title;
    public final String description;
    public final List<String> directions;
    public final long requestedSum;
    public final String companyName;

    public ApplicationResult(int id, String title, String description, List<String> directions,
                             long requestedSum, String companyName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.directions = directions;
        this.requestedSum = requestedSum;
        this.companyName = companyName;
    }
}
