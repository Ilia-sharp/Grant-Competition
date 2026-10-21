package academy.tochkavhoda.competition.application.services;

import academy.tochkavhoda.competition.application.results.EmptyResult;
import academy.tochkavhoda.competition.application.results.LoginResult;
import academy.tochkavhoda.competition.application.results.ParticipantResult;
import academy.tochkavhoda.competition.application.results.ParticipantsResult;
import academy.tochkavhoda.competition.domain.Database;
import academy.tochkavhoda.competition.domain.exception.ServerErrorCode;
import academy.tochkavhoda.competition.domain.exception.ServerException;
import academy.tochkavhoda.competition.domain.models.Expert;
import academy.tochkavhoda.competition.domain.models.GrantApplication;
import academy.tochkavhoda.competition.domain.models.Participant;
import academy.tochkavhoda.competition.domain.models.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static academy.tochkavhoda.competition.application.services.ServiceUtils.getParticipantByToken;
import static academy.tochkavhoda.competition.application.services.ServiceUtils.getUserByToken;
import static academy.tochkavhoda.competition.application.services.ServiceUtils.isBlank;
import static academy.tochkavhoda.competition.application.services.ServiceUtils.normalizeDirections;

public class UserService {
    private static final int MAX_NAME_LENGTH = 50;
    private static final int MAX_COMPANY_NAME_LENGTH = 100;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 50;
    private static final Pattern LOGIN_PATTERN = Pattern.compile("[A-Za-z0-9_]{4,30}");

    private final Database database = Database.getInstance();

    public EmptyResult registerParticipant(String firstName, String lastName, String companyName,
                                           String login, String password) throws ServerException {
        validateParticipant(firstName, lastName, companyName, login, password);
        database.insertUser(new Participant(firstName.trim(), lastName.trim(), companyName.trim(), login, password));
        return new EmptyResult();
    }

    public EmptyResult registerExpert(String firstName, String lastName, List<String> directions,
                                      String login, String password) throws ServerException {
        validateExpert(firstName, lastName, directions, login, password);
        database.insertUser(new Expert(firstName.trim(), lastName.trim(),
                normalizeDirections(directions), login, password));
        return new EmptyResult();
    }

    public LoginResult login(String login, String password) throws ServerException {
        if (isBlank(login) || isBlank(password)) {
            throw new ServerException(ServerErrorCode.WRONG_LOGIN_OR_PASSWORD);
        }
        User user = database.getUserByLogin(login);
        if (user == null || !user.getPassword().equals(password)) {
            throw new ServerException(ServerErrorCode.WRONG_LOGIN_OR_PASSWORD);
        }
        return new LoginResult(database.login(user));
    }

    public EmptyResult logout(String token) throws ServerException {
        getUserByToken(database, token);
        database.logout(token);
        return new EmptyResult();
    }

    public EmptyResult leaveServer(String token) throws ServerException {
        User user = getUserByToken(database, token);
        database.deleteUser(user);
        return new EmptyResult();
    }

    public ParticipantsResult getParticipants(String token, List<String> directions) throws ServerException {
        getUserByToken(database, token);
        List<Participant> participants;
        if (directions == null || directions.isEmpty()) {
            participants = database.getParticipants();
        } else {
            Set<Participant> selected = new LinkedHashSet<>();
            for (GrantApplication application : database.getApplicationsByDirections(
                    normalizeDirections(directions))) {
                selected.add(application.getParticipant());
            }
            participants = new ArrayList<>(selected);
        }
        participants.sort(Comparator.comparing(Participant::getCompanyName));
        List<ParticipantResult> items = new ArrayList<>();
        for (Participant participant : participants) {
            items.add(new ParticipantResult(participant.getFirstName(), participant.getLastName(),
                    participant.getCompanyName()));
        }
        return new ParticipantsResult(items);
    }

    private static void validateParticipant(String firstName, String lastName, String companyName,
                                            String login, String password) throws ServerException {
        validateName(firstName, ServerErrorCode.WRONG_FIRST_NAME, MAX_NAME_LENGTH);
        validateName(lastName, ServerErrorCode.WRONG_LAST_NAME, MAX_NAME_LENGTH);
        validateName(companyName, ServerErrorCode.WRONG_COMPANY_NAME, MAX_COMPANY_NAME_LENGTH);
        validateCredentials(login, password);
    }

    private static void validateExpert(String firstName, String lastName, List<String> directions,
                                       String login, String password) throws ServerException {
        validateName(firstName, ServerErrorCode.WRONG_FIRST_NAME, MAX_NAME_LENGTH);
        validateName(lastName, ServerErrorCode.WRONG_LAST_NAME, MAX_NAME_LENGTH);
        normalizeDirections(directions);
        validateCredentials(login, password);
    }

    private static void validateName(String name, ServerErrorCode errorCode, int maxLength) throws ServerException {
        if (isBlank(name) || name.trim().length() > maxLength) {
            throw new ServerException(errorCode);
        }
    }

    private static void validateCredentials(String login, String password) throws ServerException {
        if (login == null || !LOGIN_PATTERN.matcher(login).matches()) {
            throw new ServerException(ServerErrorCode.WRONG_LOGIN);
        }
        if (password == null || password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            throw new ServerException(ServerErrorCode.WRONG_PASSWORD);
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isWhitespace(c)) {
                throw new ServerException(ServerErrorCode.WRONG_PASSWORD);
            } else if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else {
                hasSpecial = true;
            }
        }
        if (!hasLetter || !hasDigit || !hasSpecial) {
            throw new ServerException(ServerErrorCode.WRONG_PASSWORD);
        }
    }
}
