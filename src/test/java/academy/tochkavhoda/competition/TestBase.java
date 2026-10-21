package academy.tochkavhoda.competition;

import academy.tochkavhoda.competition.api.Server;
import academy.tochkavhoda.competition.api.dto.requests.AddApplicationRequest;
import academy.tochkavhoda.competition.api.dto.requests.LoginRequest;
import academy.tochkavhoda.competition.api.dto.requests.RateApplicationRequest;
import academy.tochkavhoda.competition.api.dto.requests.RegisterExpertRequest;
import academy.tochkavhoda.competition.api.dto.requests.RegisterParticipantRequest;
import academy.tochkavhoda.competition.api.dto.requests.SummarizeRequest;
import academy.tochkavhoda.competition.api.dto.responses.ApplicationIdResponse;
import academy.tochkavhoda.competition.api.dto.responses.ApplicationResponse;
import academy.tochkavhoda.competition.api.dto.responses.ApplicationsResponse;
import academy.tochkavhoda.competition.api.dto.responses.CompetitionResultResponse;
import academy.tochkavhoda.competition.api.dto.responses.ErrorResponse;
import academy.tochkavhoda.competition.api.dto.responses.LoginResponse;
import academy.tochkavhoda.competition.api.dto.responses.ServerResponse;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class TestBase {
    protected static final Gson GSON = new Gson();
    protected static final String PASSWORD = "Secret#123";

    protected Server server;

    @BeforeEach
    public void setUp() {
        server = new Server();
        assertSuccess(server.clearDatabase());
    }

    protected static String toJson(Object dto) {
        return GSON.toJson(dto);
    }

    protected static void assertSuccess(ServerResponse response) {
        assertEquals(200, response.getResponseCode(), response.getResponseData());
    }

    protected static void assertError(ServerResponse response, String expectedMessage) {
        assertEquals(400, response.getResponseCode(), response.getResponseData());
        ErrorResponse error = GSON.fromJson(response.getResponseData(), ErrorResponse.class);
        assertNotNull(error.error);
        assertEquals(expectedMessage, error.error);
    }

    protected static <T> T getData(ServerResponse response, Class<T> dtoClass) {
        assertSuccess(response);
        return GSON.fromJson(response.getResponseData(), dtoClass);
    }

    protected ServerResponse registerParticipant(String login, String companyName) {
        return server.registerParticipant(toJson(
                new RegisterParticipantRequest("Иван", "Иванов", companyName, login, PASSWORD)));
    }

    protected ServerResponse registerExpert(String login, String... directions) {
        return server.registerExpert(toJson(
                new RegisterExpertRequest("Пётр", "Петров", Arrays.asList(directions), login, PASSWORD)));
    }

    protected ServerResponse login(String login, String password) {
        return server.login(toJson(new LoginRequest(login, password)));
    }

    protected String loginAndGetToken(String login, String password) {
        String token = getData(login(login, password), LoginResponse.class).token;
        assertNotNull(token);
        return token;
    }

    protected String participant(String login, String companyName) {
        assertSuccess(registerParticipant(login, companyName));
        return loginAndGetToken(login, PASSWORD);
    }

    protected String expert(String login, String... directions) {
        assertSuccess(registerExpert(login, directions));
        return loginAndGetToken(login, PASSWORD);
    }

    protected ServerResponse tryAddApplication(String token, String title, Long sum, String... directions) {
        return server.addApplication(token, toJson(
                new AddApplicationRequest(title, "Описание: " + title, Arrays.asList(directions), sum)));
    }

    protected int addApplication(String token, String title, long sum, String... directions) {
        return getData(tryAddApplication(token, title, sum, directions), ApplicationIdResponse.class).id;
    }

    protected ServerResponse rate(String token, Integer applicationId, Integer rating) {
        return server.rateApplication(token, toJson(new RateApplicationRequest(applicationId, rating)));
    }

    protected ServerResponse trySummarize(String token, Long fund, Double threshold) {
        return server.summarize(token, toJson(new SummarizeRequest(fund, threshold)));
    }

    protected CompetitionResultResponse summarize(String token, long fund, double threshold) {
        return getData(trySummarize(token, fund, threshold), CompetitionResultResponse.class);
    }

    protected static List<Integer> ids(ApplicationsResponse response) {
        List<Integer> ids = new ArrayList<>();
        for (ApplicationResponse application : response.applications) {
            ids.add(application.id);
        }
        return ids;
    }
}
