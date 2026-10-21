package academy.tochkavhoda.competition;

import academy.tochkavhoda.competition.api.dto.requests.DirectionsRequest;
import academy.tochkavhoda.competition.api.dto.responses.ParticipantResponse;
import academy.tochkavhoda.competition.api.dto.responses.ParticipantsResponse;
import academy.tochkavhoda.competition.api.dto.responses.ServerResponse;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestParticipants extends TestBase {

    @Test
    public void testGetParticipants() {
        String first = participant("romashka", "Ромашка");
        String second = participant("vasilek", "Василёк");
        String third = participant("astra", "Астра");
        addApplication(first, "Заявка 1", 100, "физика");
        addApplication(second, "Заявка 2", 100, "химия", "физика");
        String expertToken = expert("expert1", "физика");

        ServerResponse response = server.getParticipants(expertToken, null);
        List<ParticipantResponse> all = getData(response, ParticipantsResponse.class).participants;
        assertEquals(Arrays.asList(
                new ParticipantResponse("Иван", "Иванов", "Астра"),
                new ParticipantResponse("Иван", "Иванов", "Василёк"),
                new ParticipantResponse("Иван", "Иванов", "Ромашка")), all);
        assertFalse(response.getResponseData().contains(PASSWORD));

        List<ParticipantResponse> physics = getData(server.getParticipants(first,
                        toJson(new DirectionsRequest(Collections.singletonList("физика")))),
                ParticipantsResponse.class).participants;
        assertEquals(2, physics.size());

        List<ParticipantResponse> chemistry = getData(server.getParticipants(third,
                        toJson(new DirectionsRequest(Collections.singletonList("Химия")))),
                ParticipantsResponse.class).participants;
        assertEquals(Collections.singletonList(new ParticipantResponse("Иван", "Иванов", "Василёк")), chemistry);
    }

    @Test
    public void testGetParticipantsWrongToken() {
        assertError(server.getParticipants("bad-token", null), "Неверный токен: пользователь не вошёл на сервер");
    }
}
