package academy.tochkavhoda.competition.application.results;

public class GrantResult {
    public final int applicationId;
    public final String title;
    public final String companyName;
    public final double averageRating;
    public final long grantSum;

    public GrantResult(int applicationId, String title, String companyName, double averageRating, long grantSum) {
        this.applicationId = applicationId;
        this.title = title;
        this.companyName = companyName;
        this.averageRating = averageRating;
        this.grantSum = grantSum;
    }
}
