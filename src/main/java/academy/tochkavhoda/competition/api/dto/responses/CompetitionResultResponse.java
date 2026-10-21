package academy.tochkavhoda.competition.api.dto.responses;

import academy.tochkavhoda.competition.application.results.CompetitionSummaryResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CompetitionResultResponse {
    public long fund;
    public double threshold;
    public long distributedSum;
    public long remainingSum;
    public List<GrantResponse> grants;

    public CompetitionResultResponse() {
    }

    public CompetitionResultResponse(long fund, double threshold, long distributedSum,
                                     long remainingSum, List<GrantResponse> grants) {
        this.fund = fund;
        this.threshold = threshold;
        this.distributedSum = distributedSum;
        this.remainingSum = remainingSum;
        this.grants = grants;
    }

    public static CompetitionResultResponse from(CompetitionSummaryResult result) {
        List<GrantResponse> grants = new ArrayList<>();
        for (academy.tochkavhoda.competition.application.results.GrantResult grant : result.grants) {
            grants.add(GrantResponse.from(grant));
        }
        return new CompetitionResultResponse(result.fund, result.threshold, result.distributedSum,
                result.remainingSum, grants);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompetitionResultResponse that = (CompetitionResultResponse) o;
        return fund == that.fund
                && Double.compare(threshold, that.threshold) == 0
                && distributedSum == that.distributedSum
                && remainingSum == that.remainingSum
                && Objects.equals(grants, that.grants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fund, threshold, distributedSum, remainingSum, grants);
    }
}
