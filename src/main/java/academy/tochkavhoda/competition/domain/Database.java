package academy.tochkavhoda.competition.domain;

import academy.tochkavhoda.competition.domain.exception.ServerErrorCode;
import academy.tochkavhoda.competition.domain.exception.ServerException;
import academy.tochkavhoda.competition.domain.models.CompetitionResult;
import academy.tochkavhoda.competition.domain.models.Expert;
import academy.tochkavhoda.competition.domain.models.GrantApplication;
import academy.tochkavhoda.competition.domain.models.Participant;
import academy.tochkavhoda.competition.domain.models.User;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

public final class Database {
    private static final Database INSTANCE = new Database();

    private final Map<String, User> usersByLogin = new HashMap<>();
    private final Set<String> companyNames = new HashSet<>();
    private final BidiMap<String, User> sessions = new DualHashBidiMap<>();
    private final Map<Integer, GrantApplication> applications = new TreeMap<>();
    private final MultiValuedMap<String, GrantApplication> applicationsByDirection = new HashSetValuedHashMap<>();
    private final Map<GrantApplication, Map<Expert, Integer>> ratings = new HashMap<>();

    private int nextApplicationId;
    private CompetitionResult competitionResult;

    private Database() {
        clear();
    }

    public static Database getInstance() {
        return INSTANCE;
    }

    public synchronized void clear() {
        usersByLogin.clear();
        companyNames.clear();
        sessions.clear();
        applications.clear();
        applicationsByDirection.clear();
        ratings.clear();
        nextApplicationId = 1;
        competitionResult = null;
    }

    public synchronized void insertUser(User user) throws ServerException {
        if (usersByLogin.containsKey(user.getLogin())) {
            throw new ServerException(ServerErrorCode.LOGIN_ALREADY_EXISTS, user.getLogin());
        }
        if (user instanceof Participant) {
            String companyName = ((Participant) user).getCompanyName();
            if (!companyNames.add(companyName.toLowerCase())) {
                throw new ServerException(ServerErrorCode.COMPANY_ALREADY_REGISTERED, companyName);
            }
        }
        usersByLogin.put(user.getLogin(), user);
    }

    public synchronized User getUserByLogin(String login) {
        return usersByLogin.get(login);
    }

    public synchronized String login(User user) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, user);
        return token;
    }

    public synchronized void logout(String token) {
        sessions.remove(token);
    }

    public synchronized User getUserByToken(String token) {
        return sessions.get(token);
    }

    public synchronized void deleteUser(User user) {
        sessions.removeValue(user);
        usersByLogin.remove(user.getLogin());
        if (user instanceof Participant) {
            companyNames.remove(((Participant) user).getCompanyName().toLowerCase());
            for (GrantApplication application : getApplicationsByParticipant((Participant) user)) {
                removeApplication(application);
            }
        }
        if (user instanceof Expert) {
            for (Map<Expert, Integer> applicationRatings : ratings.values()) {
                applicationRatings.remove(user);
            }
        }
    }

    public synchronized List<Participant> getParticipants() {
        List<Participant> result = new ArrayList<>();
        for (User user : usersByLogin.values()) {
            if (user instanceof Participant) {
                result.add((Participant) user);
            }
        }
        return result;
    }

    public synchronized List<Integer> insertApplications(List<GrantApplication> newApplications) throws ServerException {
        checkCompetitionNotFinished();
        List<Integer> ids = new ArrayList<>();
        for (GrantApplication application : newApplications) {
            application.setId(nextApplicationId++);
            applications.put(application.getId(), application);
            for (String direction : application.getDirections()) {
                applicationsByDirection.put(direction, application);
            }
            ids.add(application.getId());
        }
        return ids;
    }

    public synchronized GrantApplication getApplicationById(int id) {
        return applications.get(id);
    }

    public synchronized List<GrantApplication> getApplicationsByParticipant(Participant participant) {
        List<GrantApplication> result = new ArrayList<>();
        for (GrantApplication application : applications.values()) {
            if (application.getParticipant().equals(participant)) {
                result.add(application);
            }
        }
        return result;
    }

    public synchronized List<GrantApplication> getApplicationsByDirections(Collection<String> directions) {
        Map<Integer, GrantApplication> result = new TreeMap<>();
        for (String direction : directions) {
            for (GrantApplication application : applicationsByDirection.get(direction)) {
                result.put(application.getId(), application);
            }
        }
        return new ArrayList<>(result.values());
    }

    public synchronized void deleteApplication(GrantApplication application) throws ServerException {
        checkCompetitionNotFinished();
        removeApplication(application);
    }

    private void removeApplication(GrantApplication application) {
        applications.remove(application.getId());
        for (String direction : application.getDirections()) {
            applicationsByDirection.removeMapping(direction, application);
        }
        ratings.remove(application);
    }

    public synchronized Integer getRating(Expert expert, GrantApplication application) {
        Map<Expert, Integer> applicationRatings = ratings.get(application);
        return applicationRatings == null ? null : applicationRatings.get(expert);
    }

    public synchronized void setRating(Expert expert, GrantApplication application, int rating) throws ServerException {
        checkCompetitionNotFinished();
        ratings.computeIfAbsent(application, key -> new HashMap<>()).put(expert, rating);
    }

    public synchronized void deleteRating(Expert expert, GrantApplication application) throws ServerException {
        checkCompetitionNotFinished();
        Map<Expert, Integer> applicationRatings = ratings.get(application);
        if (applicationRatings != null) {
            applicationRatings.remove(expert);
        }
    }

    public synchronized Map<GrantApplication, Integer> getRatingsByExpert(Expert expert) {
        Map<GrantApplication, Integer> result = new LinkedHashMap<>();
        for (GrantApplication application : applications.values()) {
            Map<Expert, Integer> applicationRatings = ratings.get(application);
            if (applicationRatings != null && applicationRatings.containsKey(expert)) {
                result.put(application, applicationRatings.get(expert));
            }
        }
        return result;
    }

    public synchronized Map<GrantApplication, List<Integer>> getAllApplicationsWithRatings() {
        Map<GrantApplication, List<Integer>> result = new LinkedHashMap<>();
        for (GrantApplication application : applications.values()) {
            Map<Expert, Integer> applicationRatings = ratings.get(application);
            result.put(application, applicationRatings == null
                    ? new ArrayList<>() : new ArrayList<>(applicationRatings.values()));
        }
        return result;
    }

    public synchronized boolean isCompetitionFinished() {
        return competitionResult != null;
    }

    public synchronized void saveCompetitionResult(CompetitionResult result) throws ServerException {
        checkCompetitionNotFinished();
        competitionResult = result;
    }

    public synchronized CompetitionResult getCompetitionResult() {
        return competitionResult;
    }

    private void checkCompetitionNotFinished() throws ServerException {
        if (competitionResult != null) {
            throw new ServerException(ServerErrorCode.COMPETITION_FINISHED);
        }
    }
}
