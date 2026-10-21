package academy.tochkavhoda.competition.application.services;

import academy.tochkavhoda.competition.application.results.ApplicationIdResult;
import academy.tochkavhoda.competition.application.results.ApplicationIdsResult;
import academy.tochkavhoda.competition.application.results.ApplicationResult;
import academy.tochkavhoda.competition.application.results.ApplicationsResult;
import academy.tochkavhoda.competition.application.results.EmptyResult;
import academy.tochkavhoda.competition.domain.Database;
import academy.tochkavhoda.competition.domain.exception.ServerErrorCode;
import academy.tochkavhoda.competition.domain.exception.ServerException;
import academy.tochkavhoda.competition.domain.models.Expert;
import academy.tochkavhoda.competition.domain.models.GrantApplication;
import academy.tochkavhoda.competition.domain.models.Participant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static academy.tochkavhoda.competition.application.services.ServiceUtils.isBlank;
import static academy.tochkavhoda.competition.application.services.ServiceUtils.normalizeDirections;

public class ApplicationService {
    private static final int MAX_TITLE_LENGTH = 200;

    private final Database database = Database.getInstance();

    public ApplicationIdResult addApplication(String token, String title, String description,
                                              List<String> directions, Long requestedSum) throws ServerException {
        Participant participant = ServiceUtils.getParticipantByToken(database, token);
        GrantApplication application = createApplication(participant,
                new AddApplicationData(title, description, directions, requestedSum));
        List<Integer> ids = database.insertApplications(Collections.singletonList(application));
        return new ApplicationIdResult(ids.get(0));
    }

    public ApplicationIdsResult addApplications(String token, List<AddApplicationData> applications)
            throws ServerException {
        Participant participant = ServiceUtils.getParticipantByToken(database, token);
        if (applications == null || applications.isEmpty()) {
            throw new ServerException(ServerErrorCode.EMPTY_APPLICATION_LIST);
        }
        List<GrantApplication> created = new ArrayList<>();
        for (AddApplicationData data : applications) {
            if (data == null) {
                throw new ServerException(ServerErrorCode.WRONG_JSON);
            }
            created.add(createApplication(participant, data));
        }
        return new ApplicationIdsResult(database.insertApplications(created));
    }

    public EmptyResult removeApplication(String token, Integer applicationId) throws ServerException {
        Participant participant = ServiceUtils.getParticipantByToken(database, token);
        if (applicationId == null) {
            throw new ServerException(ServerErrorCode.WRONG_APPLICATION_ID);
        }
        GrantApplication application = database.getApplicationById(applicationId);
        if (application == null) {
            throw new ServerException(ServerErrorCode.APPLICATION_NOT_FOUND, applicationId);
        }
        if (!application.getParticipant().equals(participant)) {
            throw new ServerException(ServerErrorCode.NOT_APPLICATION_OWNER, applicationId);
        }
        database.deleteApplication(application);
        return new EmptyResult();
    }

    public ApplicationsResult getMyApplications(String token) throws ServerException {
        Participant participant = ServiceUtils.getParticipantByToken(database, token);
        return toApplicationsResult(database.getApplicationsByParticipant(participant));
    }

    public ApplicationsResult getApplicationsByDirections(String token, List<String> directions)
            throws ServerException {
        Expert expert = ServiceUtils.getExpertByToken(database, token);
        Set<String> selected;
        if (directions == null || directions.isEmpty()) {
            selected = expert.getDirections();
        } else {
            selected = normalizeDirections(directions);
            for (String direction : selected) {
                if (!expert.getDirections().contains(direction)) {
                    throw new ServerException(ServerErrorCode.DIRECTION_NOT_EXPERT, direction);
                }
            }
        }
        return toApplicationsResult(database.getApplicationsByDirections(selected));
    }

    private static GrantApplication createApplication(Participant participant, AddApplicationData data)
            throws ServerException {
        validate(data);
        return new GrantApplication(participant, data.title.trim(), data.description.trim(),
                normalizeDirections(data.directions), data.requestedSum);
    }

    private static void validate(AddApplicationData data) throws ServerException {
        if (isBlank(data.title) || data.title.trim().length() > MAX_TITLE_LENGTH) {
            throw new ServerException(ServerErrorCode.WRONG_TITLE);
        }
        if (isBlank(data.description)) {
            throw new ServerException(ServerErrorCode.WRONG_DESCRIPTION);
        }
        normalizeDirections(data.directions);
        if (data.requestedSum == null || data.requestedSum <= 0) {
            throw new ServerException(ServerErrorCode.WRONG_REQUESTED_SUM);
        }
    }

    private static ApplicationResult toApplicationResult(GrantApplication application) {
        return new ApplicationResult(application.getId(), application.getTitle(), application.getDescription(),
                new ArrayList<>(application.getDirections()), application.getRequestedSum(),
                application.getParticipant().getCompanyName());
    }

    private static ApplicationsResult toApplicationsResult(List<GrantApplication> applications) {
        List<ApplicationResult> items = new ArrayList<>();
        for (GrantApplication application : applications) {
            items.add(toApplicationResult(application));
        }
        return new ApplicationsResult(items);
    }
}
