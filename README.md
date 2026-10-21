# Конкурс грантов — сервер (данные в оперативной памяти)

Пакет: `academy.tochkavhoda.competition`. Клиентом сервера выступают JUnit-тесты.

Проект построен по чистой архитектуре из трёх слоёв. Обращаться можно только
сверху вниз: api → application → domain.

| Слой | Пакет | Содержимое |
|---|---|---|
| api | `api` | `Server` — входная точка: принимает json от клиента, парсит его в Request, вызывает сервис, конвертирует Result в Response и возвращает `ServerResponse` |
| api | `api.dto.requests` | структуры запросов (`XxxRequest`) для общения с внешним миром |
| api | `api.dto.responses` | структуры ответов (`XxxResponse`), каждая создаётся статическим методом `from(...)` из соответствующего Result |
| application | `application.services` | `UserService`, `ApplicationService`, `RatingService`, `CompetitionService`, `DebugService` — валидация и бизнес-логика |
| application | `application.results` | структуры-результаты (`XxxResult`), которые возвращают методы сервисов |
| domain | `domain` | `Database` — синглетон с коллекциями (используются `BidiMap` и `MultiValuedMap` из Apache Commons Collections) |
| domain | `domain.models` | `User` → `Participant`, `Expert`; `GrantApplication`, `Grant`, `CompetitionResult` |
| domain | `domain.exception` | `ServerException` с кодом `ServerErrorCode` |

Поток данных для любого запроса:

```
клиент -> Server (json -> XxxRequest) -> сервис (бизнес-логика) -> XxxResult
        -> Server (XxxResponse.from(result) -> json -> ServerResponse) -> клиент
```

Сервисы никогда не возвращают `ServerResponse` и не знают про json: они принимают
простые значения, возвращают структуры-результаты (Results) или бросают
`ServerException`, которое перехватывает `Server`.

Ответ любого метода — `ServerResponse`: код `200` и json с результатом либо код
`400` и json `{"error": "описание"}`.

## Методы Server

| Метод | Кто вызывает | Запрос | Ответ |
|---|---|---|---|
| `registerParticipant(json)` | — | `firstName, lastName, companyName, login, password` | `{}` |
| `registerExpert(json)` | — | `firstName, lastName, directions[], login, password` | `{}` |
| `login(json)` | — | `login, password` | `{"token"}` |
| `logout(token)` | любой | — | `{}` |
| `leaveServer(token)` | участник, эксперт | — | `{}` |
| `getParticipants(token, json)` | любой | `directions[]` (необязательно) | `{"participants": [...]}` |
| `addApplication(token, json)` | участник | `title, description, directions[], requestedSum` | `{"id"}` |
| `addApplications(token, json)` | участник | `applications[]` | `{"ids": [...]}` |
| `removeApplication(token, json)` | участник | `applicationId` | `{}` |
| `getMyApplications(token)` | участник | — | `{"applications": [...]}` |
| `getApplicationsByDirections(token, json)` | эксперт | `directions[]` (необязательно) | `{"applications": [...]}` |
| `rateApplication(token, json)` | эксперт | `applicationId, rating` | `{}` |
| `changeRating(token, json)` | эксперт | `applicationId, rating` | `{}` |
| `deleteRating(token, json)` | эксперт | `applicationId` | `{}` |
| `getRatedApplications(token)` | эксперт | — | `{"applications": [{"application", "rating"}]}` |
| `summarize(token, json)` | любой вошедший | `fund, threshold` | итоги конкурса |
| `getResults(token)` | любой | — | итоги конкурса |
| `clearDatabase()` | тесты | — | `{}` |

## Принятые требования (уточнения к ТЗ)

- Логин: 4–30 символов, латинские буквы, цифры, `_`; уникален среди всех пользователей.
- Пароль: 8–50 символов без пробелов, минимум одна буква, одна цифра и один спецсимвол.
- Имя, фамилия — непустые, до 50 символов; название фирмы — непустое, до 100 символов.
- Фирма не может зарегистрироваться дважды: название фирмы уникально (без учёта регистра).
- Направления хранятся без пробелов по краям и в нижнем регистре, дубли убираются.
- Повторный вход выдаёт новый токен, прежний токен становится недействительным.
- Участник, покинувший сервер, теряет все свои заявки (вместе с оценками к ним); эксперт — все свои оценки.
- Эксперт может оценивать только заявки, у которых есть хотя бы одно из его направлений. Оценка — целое 1..5.
  Поставить оценку повторно нельзя — для этого есть `changeRating`; удалить — `deleteRating`.
- Роли «администратор фонда» в ТЗ нет, поэтому отдельного пользователя-администратора в системе нет:
  итоги подводит любой вошедший пользователь (момент X).
- Заявка без оценок имеет среднюю оценку 0.
- После подведения итогов подача, отмена и оценка заявок запрещены, повторно итоги не подводятся.

## Алгоритм подведения итогов

1. Заявки сортируются по убыванию средней оценки, при равенстве — по возрастанию суммы, при равных суммах — случайно
   (список перемешивается, затем сортируется устойчивой сортировкой).
2. Заявки просматриваются по порядку: если сумма не превышает остатка фонда — грант выделяется и сумма вычитается, иначе заявка пропускается.
3. Как только встречается заявка со средней оценкой не больше порога — распределение прекращается, остаток не распределяется.

## Сборка и запуск тестов

Проект собирается Maven-ом (`mvn test`) либо напрямую:

```
javac --release 17 -cp "gson.jar:commons-collections4.jar" -d target/classes $(find src/main/java -name "*.java")
java -jar junit-platform-console-standalone.jar --class-path "target/classes:target/test-classes:gson.jar" --scan-class-path
```
