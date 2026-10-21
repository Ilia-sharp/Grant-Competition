package academy.tochkavhoda.competition.application.services;

import academy.tochkavhoda.competition.application.results.CompetitionSummaryResult;
import academy.tochkavhoda.competition.application.results.GrantResult;
import academy.tochkavhoda.competition.domain.Database;
import academy.tochkavhoda.competition.domain.exception.ServerErrorCode;
import academy.tochkavhoda.competition.domain.exception.ServerException;
import academy.tochkavhoda.competition.domain.models.CompetitionResult;
import academy.tochkavhoda.competition.domain.models.Grant;
import academy.tochkavhoda.competition.domain.models.GrantApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static academy.tochkavhoda.competition.application.services.ServiceUtils.getUserByToken;

public class CompetitionService {
    private static final double MIN_THRESHOLD = 0;
    private static final double MAX_THRESHOLD = 5;

    private final Database database = Database.getInstance();
    private final Random random = new Random();

    public CompetitionSummaryResult summarize(String token, Long fund, Double threshold) throws ServerException {
        getUserByToken(database, token);
        validate(fund, threshold);
        if (database.isCompetitionFinished()) {
            throw new ServerException(ServerErrorCode.COMPETITION_FINISHED);
        }
        List<RankedApplication> ranking = rank(database.getAllApplicationsWithRatings());
        long remaining = fund;
        List<Grant> grants = new ArrayList<>();
        for (RankedApplication ranked : ranking) {
            if (ranked.averageRating <= threshold) {
                break;
            }
            long sum = ranked.application.getRequestedSum();
            if (sum <= remaining) {
                grants.add(new Grant(ranked.application, ranked.averageRating, sum));
                remaining -= sum;
            }
        }
        CompetitionResult result = new CompetitionResult(fund, threshold, grants);
        database.saveCompetitionResult(result);
        return toSummary(result);
    }

    public CompetitionSummaryResult getResults(String token) throws ServerException {
        getUserByToken(database, token);
        CompetitionResult result = database.getCompetitionResult();
        if (result == null) {
            throw new ServerException(ServerErrorCode.COMPETITION_NOT_FINISHED);
        }
        return toSummary(result);
    }

    private List<RankedApplication> rank(Map<GrantApplication, List<Integer>> applicationsWithRatings) {
        List<RankedApplication> ranking = new ArrayList<>();
        for (Map.Entry<GrantApplication, List<Integer>> entry : applicationsWithRatings.entrySet()) {
            ranking.add(new RankedApplication(entry.getKey(), average(entry.getValue())));
        }
        Collections.shuffle(ranking, random);
        ranking.sort(Comparator
                .comparingDouble((RankedApplication r) -> r.averageRating).reversed()
                .thenComparingLong(r -> r.application.getRequestedSum()));
        return ranking;
    }

    private static double average(List<Integer> ratings) {
        if (ratings.isEmpty()) {
            return 0;
        }
        long total = 0;
        for (int rating : ratings) {
            total += rating;
        }
        return (double) total / ratings.size();
    }

    private static CompetitionSummaryResult toSummary(CompetitionResult result) {
        List<GrantResult> grants = new ArrayList<>();
        long distributed = 0;
        for (Grant grant : result.getGrants()) {
            GrantApplication application = grant.getApplication();
            grants.add(new GrantResult(application.getId(), application.getTitle(),
                    application.getParticipant().getCompanyName(), grant.getAverageRating(), grant.getSum()));
            distributed += grant.getSum();
        }
        return new CompetitionSummaryResult(result.getFund(), result.getThreshold(), distributed,
                result.getFund() - distributed, grants);
    }

    private static void validate(Long fund, Double threshold) throws ServerException {
        if (fund == null || fund <= 0) {
            throw new ServerException(ServerErrorCode.WRONG_FUND);
        }
        if (threshold == null || threshold.isNaN() || threshold < MIN_THRESHOLD || threshold > MAX_THRESHOLD) {
            throw new ServerException(ServerErrorCode.WRONG_THRESHOLD);
        }
    }

    private static final class RankedApplication {
        private final GrantApplication application;
        private final double averageRating;

        private RankedApplication(GrantApplication application, double averageRating) {
            this.application = application;
            this.averageRating = averageRating;
        }
    }
}
