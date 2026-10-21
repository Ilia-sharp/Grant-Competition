package academy.tochkavhoda.competition.application.services;

import academy.tochkavhoda.competition.domain.Database;
import academy.tochkavhoda.competition.domain.exception.ServerErrorCode;
import academy.tochkavhoda.competition.domain.exception.ServerException;
import academy.tochkavhoda.competition.domain.models.Expert;
import academy.tochkavhoda.competition.domain.models.Participant;
import academy.tochkavhoda.competition.domain.models.User;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

final class ServiceUtils {
    private ServiceUtils() {
    }

    static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    static Set<String> normalizeDirections(List<String> directions) throws ServerException {
        if (directions == null || directions.isEmpty()) {
            throw new ServerException(ServerErrorCode.WRONG_DIRECTIONS);
        }
        Set<String> result = new LinkedHashSet<>();
        for (String direction : directions) {
            if (isBlank(direction)) {
                throw new ServerException(ServerErrorCode.WRONG_DIRECTIONS);
            }
            result.add(direction.trim().toLowerCase(Locale.ROOT));
        }
        return result;
    }

    static User getUserByToken(Database database, String token) throws ServerException {
        if (isBlank(token)) {
            throw new ServerException(ServerErrorCode.WRONG_TOKEN);
        }
        User user = database.getUserByToken(token);
        if (user == null) {
            throw new ServerException(ServerErrorCode.WRONG_TOKEN);
        }
        return user;
    }

    static Participant getParticipantByToken(Database database, String token) throws ServerException {
        User user = getUserByToken(database, token);
        if (!(user instanceof Participant)) {
            throw new ServerException(ServerErrorCode.NOT_PARTICIPANT);
        }
        return (Participant) user;
    }

    static Expert getExpertByToken(Database database, String token) throws ServerException {
        User user = getUserByToken(database, token);
        if (!(user instanceof Expert)) {
            throw new ServerException(ServerErrorCode.NOT_EXPERT);
        }
        return (Expert) user;
    }
}
