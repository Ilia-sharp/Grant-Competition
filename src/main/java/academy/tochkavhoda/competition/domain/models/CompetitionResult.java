package academy.tochkavhoda.competition.domain.models;

import java.util.ArrayList;
import java.util.List;

public class CompetitionResult {
    private long fund;
    private double threshold;
    private List<Grant> grants;

    public CompetitionResult(long fund, double threshold, List<Grant> grants) {
        this.fund = fund;
        this.threshold = threshold;
        this.grants = new ArrayList<>(grants);
    }

    public long getFund() {
        return fund;
    }

    public void setFund(long fund) {
        this.fund = fund;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public List<Grant> getGrants() {
        return grants;
    }

    public void setGrants(List<Grant> grants) {
        this.grants = new ArrayList<>(grants);
    }
}
