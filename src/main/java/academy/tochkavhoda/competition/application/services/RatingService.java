package academy.tochkavhoda.competition.application.services;

import academy.tochkavhoda.competition.application.results.ApplicationResult;
import academy.tochkavhoda.competition.application.results.EmptyResult;
import academy.tochkavhoda.competition.application.results.RatedApplicationResult;
import academy.tochkavhoda.competition.application.results.RatedApplicationsResult;
import academy.tochkavhoda.competition.domain.Database;
import academy.tochkavhoda.competition.domain.exception.ServerErrorCode;
import academy.tochkavhoda.competition.domain.exception.ServerException;
import academy.tochkavhoda.competition.domain.models.Expert;
import academy.tochkavhoda.competition.domain.models.GrantApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RatingService {
    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;

    private final Database database = Database.getInstance();

    public EmptyResult rateApplication(String token, Integer applicationId, Integer rating) throws ServerException {
        Expert expert = ServiceUtils.getExpertByToken(database, token);
        validate(applicationId, rating);
        GrantApplication application = getApplicationForExpert(expert, applicationId);
        if (database.getRating(expert, application) != null) {
            throw new ServerException(ServerErrorCode.ALREADY_RATED, application.getId());
        }
        database.setRating(expert, application, rating);
        return new EmptyResult();
    }

    public EmptyResult changeRating(String token, Integer applicationId, Integer rating) throws ServerException {
        Expert expert = ServiceUtils.getExpertByToken(database, token);
        validate(applicationId, rating);
        GrantApplication application = getApplicationForExpert(expert, applicationId);
        if (database.getRating(expert, application) == null) {
            throw new ServerException(ServerErrorCode.RATING_NOT_FOUND, application.getId());
        }
        database.setRating(expert, application, rating);
        return new EmptyResult();
    }

    public EmptyResult deleteRating(String token, Integer applicationId) throws ServerException {
        Expert expert = ServiceUtils.getExpertByToken(database, token);
        if (applicationId == null) {
            throw new ServerException(ServerErrorCode.WRONG_APPLICATION_ID);
        }
        GrantApplication application = database.getApplicationById(applicationId);
        if (application == null || database.getRating(expert, application) == null) {
            throw new ServerException(ServerErrorCode.RATING_NOT_FOUND, applicationId);
        }
        database.deleteRating(expert, application);
        return new EmptyResult();
    }

    public RatedApplicationsResult getRatedApplications(String token) throws ServerException {
        Expert expert = ServiceUtils.getExpertByToken(database, token);
        List<RatedApplicationResult> items = new ArrayList<>();
        for (Map.Entry<GrantApplication, Integer> entry : database.getRatingsByExpert(expert).entrySet()) {
            items.add(new RatedApplicationResult(toApplicationResult(entry.getKey()), entry.getValue()));
        }
        return new RatedApplicationsResult(items);
    }

    private GrantApplication getApplicationForExpert(Expert expert, int applicationId) throws ServerException {
        GrantApplication application = database.getApplicationById(applicationId);
        if (application == null) {
            throw new ServerException(ServerErrorCode.APPLICATION_NOT_FOUND, applicationId);
        }
        if (Collections.disjoint(expert.getDirections(), application.getDirections())) {
            throw new ServerException(ServerErrorCode.APPLICATION_OUT_OF_EXPERTISE, applicationId);
        }
        return application;
    }

    private static void validate(Integer applicationId, Integer rating) throws ServerException {
        if (applicationId == null) {
            throw new ServerException(ServerErrorCode.WRONG_APPLICATION_ID);
        }
        if (rating == null || rating < MIN_RATING || rating > MAX_RATING) {
            throw new ServerException(ServerErrorCode.WRONG_RATING);
        }
    }

    private static ApplicationResult toApplicationResult(GrantApplication application) {
        return new ApplicationResult(application.getId(), application.getTitle(), application.getDescription(),
                new ArrayList<>(application.getDirections()), application.getRequestedSum(),
                application.getParticipant().getCompanyName());
    }
}
