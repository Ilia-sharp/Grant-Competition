package academy.tochkavhoda.competition.api.dto.responses;

import academy.tochkavhoda.competition.application.results.GrantResult;

import java.util.Objects;

public class GrantResponse {
    public int applicationId;
    public String title;
    public String companyName;
    public double averageRating;
    public long grantSum;

    public GrantResponse() {
    }

    public GrantResponse(int applicationId, String title, String companyName, double averageRating, long grantSum) {
        this.applicationId = applicationId;
        this.title = title;
        this.companyName = companyName;
        this.averageRating = averageRating;
        this.grantSum = grantSum;
    }

    public static GrantResponse from(GrantResult result) {
        return new GrantResponse(result.applicationId, result.title, result.companyName,
                result.averageRating, result.grantSum);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GrantResponse that = (GrantResponse) o;
        return applicationId == that.applicationId
                && Double.compare(averageRating, that.averageRating) == 0
                && grantSum == that.grantSum
                && Objects.equals(title, that.title)
                && Objects.equals(companyName, that.companyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(applicationId, title, companyName, averageRating, grantSum);
    }
}
