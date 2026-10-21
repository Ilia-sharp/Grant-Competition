package academy.tochkavhoda.competition.api.dto.responses;

import academy.tochkavhoda.competition.application.results.ParticipantResult;
import academy.tochkavhoda.competition.application.results.ParticipantsResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParticipantsResponse {
    public List<ParticipantResponse> participants;

    public ParticipantsResponse() {
    }

    public ParticipantsResponse(List<ParticipantResponse> participants) {
        this.participants = participants;
    }

    public static ParticipantsResponse from(ParticipantsResult result) {
        List<ParticipantResponse> items = new ArrayList<>();
        for (ParticipantResult participant : result.participants) {
            items.add(ParticipantResponse.from(participant));
        }
        return new ParticipantsResponse(items);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(participants, ((ParticipantsResponse) o).participants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(participants);
    }
}
