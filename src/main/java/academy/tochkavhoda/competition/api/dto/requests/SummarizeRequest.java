package academy.tochkavhoda.competition.api.dto.requests;

public class SummarizeRequest {
    public Long fund;
    public Double threshold;

    public SummarizeRequest() {
    }

    public SummarizeRequest(Long fund, Double threshold) {
        this.fund = fund;
        this.threshold = threshold;
    }
}
