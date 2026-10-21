package academy.tochkavhoda.competition.application.services;

import academy.tochkavhoda.competition.application.results.EmptyResult;
import academy.tochkavhoda.competition.domain.Database;

public class DebugService {
    private final Database database = Database.getInstance();

    public EmptyResult clearDatabase() {
        database.clear();
        return new EmptyResult();
    }
}
