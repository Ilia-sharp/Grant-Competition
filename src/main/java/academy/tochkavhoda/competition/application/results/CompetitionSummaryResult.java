package academy.tochkavhoda.competition.application.results;

import java.util.List;

public class CompetitionSummaryResult {
    public final long fund;
    public final double threshold;
    public final long distributedSum;
    public final long remainingSum;
    public final List<GrantResult> grants;

    public CompetitionSummaryResult(long fund, double threshold, long distributedSum,
                                    long remainingSum, List<GrantResult> grants) {
        this.fund = fund;
        this.threshold = threshold;
        this.distributedSum = distributedSum;
        this.remainingSum = remainingSum;
        this.grants = grants;
    }
}
