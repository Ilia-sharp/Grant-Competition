package academy.tochkavhoda.competition.api.dto.responses;

import java.util.Objects;

public class ServerResponse {
    public static final int CODE_SUCCESS = 200;
    public static final int CODE_ERROR = 400;

    private int responseCode;
    private String responseData;

    public ServerResponse() {
    }

    public ServerResponse(int responseCode, String responseData) {
        this.responseCode = responseCode;
        this.responseData = responseData;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerResponse that = (ServerResponse) o;
        return responseCode == that.responseCode && Objects.equals(responseData, that.responseData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(responseCode, responseData);
    }

    @Override
    public String toString() {
        return "ServerResponse{responseCode=" + responseCode + ", responseData=" + responseData + "}";
    }
}
