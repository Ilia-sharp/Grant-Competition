package academy.tochkavhoda.competition.api;

import academy.tochkavhoda.competition.api.dto.requests.AddApplicationRequest;
import academy.tochkavhoda.competition.api.dto.requests.AddApplicationsRequest;
import academy.tochkavhoda.competition.api.dto.requests.ApplicationIdRequest;
import academy.tochkavhoda.competition.api.dto.requests.DirectionsRequest;
import academy.tochkavhoda.competition.api.dto.requests.LoginRequest;
import academy.tochkavhoda.competition.api.dto.requests.RateApplicationRequest;
import academy.tochkavhoda.competition.api.dto.requests.RegisterExpertRequest;
import academy.tochkavhoda.competition.api.dto.requests.RegisterParticipantRequest;
import academy.tochkavhoda.competition.api.dto.requests.SummarizeRequest;
import academy.tochkavhoda.competition.api.dto.responses.ApplicationIdResponse;
import academy.tochkavhoda.competition.api.dto.responses.ApplicationIdsResponse;
import academy.tochkavhoda.competition.api.dto.responses.ApplicationsResponse;
import academy.tochkavhoda.competition.api.dto.responses.CompetitionResultResponse;
import academy.tochkavhoda.competition.api.dto.responses.EmptyResponse;
import academy.tochkavhoda.competition.api.dto.responses.ErrorResponse;
import academy.tochkavhoda.competition.api.dto.responses.LoginResponse;
import academy.tochkavhoda.competition.api.dto.responses.ParticipantsResponse;
import academy.tochkavhoda.competition.api.dto.responses.RatedApplicationsResponse;
import academy.tochkavhoda.competition.api.dto.responses.ServerResponse;
import academy.tochkavhoda.competition.application.results.ApplicationIdResult;
import academy.tochkavhoda.competition.application.results.ApplicationIdsResult;
import academy.tochkavhoda.competition.application.results.ApplicationsResult;
import academy.tochkavhoda.competition.application.results.CompetitionSummaryResult;
import academy.tochkavhoda.competition.application.results.EmptyResult;
import academy.tochkavhoda.competition.application.results.LoginResult;
import academy.tochkavhoda.competition.application.results.ParticipantsResult;
import academy.tochkavhoda.competition.application.results.RatedApplicationsResult;
import academy.tochkavhoda.competition.application.services.AddApplicationData;
import academy.tochkavhoda.competition.application.services.ApplicationService;
import academy.tochkavhoda.competition.application.services.CompetitionService;
import academy.tochkavhoda.competition.application.services.DebugService;
import academy.tochkavhoda.competition.application.services.RatingService;
import academy.tochkavhoda.competition.application.services.UserService;
import academy.tochkavhoda.competition.domain.exception.ServerErrorCode;
import academy.tochkavhoda.competition.domain.exception.ServerException;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final Gson GSON = new Gson();

    private final UserService userService = new UserService();
    private final ApplicationService applicationService = new ApplicationService();
    private final RatingService ratingService = new RatingService();
    private final CompetitionService competitionService = new CompetitionService();
    private final DebugService debugService = new DebugService();

    public ServerResponse clearDatabase() {
        EmptyResult result = debugService.clearDatabase();
        return success(EmptyResponse.from(result));
    }

    public ServerResponse registerParticipant(String requestJsonString) {
        try {
            RegisterParticipantRequest request = parse(requestJsonString, RegisterParticipantRequest.class);
            EmptyResult result = userService.registerParticipant(request.firstName, request.lastName,
                    request.companyName, request.login, request.password);
            return success(EmptyResponse.from(result));
        } catch (ServerException e) {
            return error(e);
        }
    }

    public ServerResponse registerExpert(String requestJsonString) {
        try {
            RegisterExpertRequest request = parse(requestJsonString, RegisterExpertRequest.class);
            EmptyResult result = userService.registerExpert(request.firstName, request.lastName,
                    request.directions, request.login, request.password);
            return success(EmptyResponse.from(result));
        } catch (ServerException e) {
            return error(e);
        }
    }

    public ServerResponse login(String requestJsonString) {
        try {
            LoginRequest request = parse(requestJsonString, LoginRequest.class);
            LoginResult result = userService.login(request.login, request.password);
            return success(LoginResponse.from(result));
        } catch (ServerException e) {
            return error(e);
        }
    }

    public ServerResponse logout(String token) {
        try {
            EmptyResult result = userService.logout(token);
            return success(EmptyResponse.from(result));
        } catch (ServerException e) {
            return error(e);
        }
    }

    public ServerResponse leaveServer(String token) {
        try {
            EmptyResult result = userService.leaveServer(token);
            return success(EmptyResponse.from(result));
        } catch (ServerException e) {
            return error(e);
        }
    }

    public ServerResponse getParticipants(String token, String requestJsonString) {
        try {
            DirectionsRequest request = parseDirections(requestJsonString);
            ParticipantsResult result = userService.getParticipants(token, request.directions);
            return success(ParticipantsResponse.from(result));
        } catch (ServerException e) {
            return error(e);
        }
    }

    public ServerResponse addApplication(String token, String requestJsonString) {
        try {
            AddApplicationRequest request = parse(requestJsonString, AddApplicationRequest.class);
            ApplicationIdResult result = applicationService.addApplication(token, request.title,
                    request.description, request.directions, request.requestedSum);
            return success(ApplicationIdResponse.from(result));
        } catch (ServerException e) {
            return error(e);
        }
    }

    public ServerResponse addApplications(String token, String requestJsonString) {
        try {
            AddApplicationsRequest request = parse(requestJsonString, AddApplicationsRequest.class);
            ApplicationIdsResult result = applicationService.addApplications(token, toData(request));
            return success(ApplicationIdsResponse.from(result));
        } catch (ServerException e) {
            return error(e);
        }
    }

    public ServerResponse removeApplication(String token, String requestJsonString) {
        try {
            ApplicationIdRequest request = parse(requestJsonString, ApplicationIdRequest.class);
            EmptyResult result = applicationService.removeApplication(token, request.applicationId);
            return success(EmptyResponse.from(result));
        } catch (ServerException e) {
            return error(e);
        }
    }

    public ServerResponse getMyApplications(String token) {
        try {
            ApplicationsResult result = applicationService.getMyApplications(token);
            return success(ApplicationsResponse.from(result));
        } catch (ServerException e) {
            return error(e);
        }
    }

    public ServerResponse getApplicationsByDirections(String token, String requestJsonString) {
        try {
            DirectionsRequest request = parseDirections(requestJsonString);
            ApplicationsResult result = applicationService.getApplicationsByDirections(token, request.directions);
            return success(ApplicationsResponse.from(result));
        } catch (ServerException e) {
            return error(e);
        }
    }

    public ServerResponse rateApplication(String token, String requestJsonString) {
        try {
            RateApplicationRequest request = parse(requestJsonString, RateApplicationRequest.class);
            EmptyResult result = ratingService.rateApplication(token, request.applicationId, request.rating);
            return success(EmptyResponse.from(result));
        } catch (ServerException e) {
            return error(e);
        }
    }

    public ServerResponse changeRating(String token, String requestJsonString) {
        try {
            RateApplicationRequest request = parse(requestJsonString, RateApplicationRequest.class);
            EmptyResult result = ratingService.changeRating(token, request.applicationId, request.rating);
            return success(EmptyResponse.from(result));
        } catch (ServerException e) {
            return error(e);
        }
    }

    public ServerResponse deleteRating(String token, String requestJsonString) {
        try {
            ApplicationIdRequest request = parse(requestJsonString, ApplicationIdRequest.class);
            EmptyResult result = ratingService.deleteRating(token, request.applicationId);
            return success(EmptyResponse.from(result));
        } catch (ServerException e) {
            return error(e);
        }
    }

    public ServerResponse getRatedApplications(String token) {
        try {
            RatedApplicationsResult result = ratingService.getRatedApplications(token);
            return success(RatedApplicationsResponse.from(result));
        } catch (ServerException e) {
            return error(e);
        }
    }

    public ServerResponse summarize(String token, String requestJsonString) {
        try {
            SummarizeRequest request = parse(requestJsonString, SummarizeRequest.class);
            CompetitionSummaryResult result = competitionService.summarize(token, request.fund, request.threshold);
            return success(CompetitionResultResponse.from(result));
        } catch (ServerException e) {
            return error(e);
        }
    }

    public ServerResponse getResults(String token) {
        try {
            CompetitionSummaryResult result = competitionService.getResults(token);
            return success(CompetitionResultResponse.from(result));
        } catch (ServerException e) {
            return error(e);
        }
    }

    private static List<AddApplicationData> toData(AddApplicationsRequest request) {
        if (request.applications == null) {
            return null;
        }
        List<AddApplicationData> applications = new ArrayList<>();
        for (AddApplicationRequest item : request.applications) {
            applications.add(item == null
                    ? null
                    : new AddApplicationData(item.title, item.description, item.directions, item.requestedSum));
        }
        return applications;
    }

    private static DirectionsRequest parseDirections(String requestJsonString) throws ServerException {
        if (isBlank(requestJsonString)) {
            return new DirectionsRequest();
        }
        return parse(requestJsonString, DirectionsRequest.class);
    }

    private static <T> T parse(String requestJsonString, Class<T> requestClass) throws ServerException {
        if (isBlank(requestJsonString)) {
            throw new ServerException(ServerErrorCode.WRONG_JSON);
        }
        T request;
        try {
            request = GSON.fromJson(requestJsonString, requestClass);
        } catch (JsonParseException e) {
            throw new ServerException(ServerErrorCode.WRONG_JSON);
        }
        if (request == null) {
            throw new ServerException(ServerErrorCode.WRONG_JSON);
        }
        return request;
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static ServerResponse success(Object response) {
        return new ServerResponse(ServerResponse.CODE_SUCCESS, GSON.toJson(response));
    }

    private static ServerResponse error(ServerException e) {
        return new ServerResponse(ServerResponse.CODE_ERROR, GSON.toJson(new ErrorResponse(e.getMessage())));
    }
}
