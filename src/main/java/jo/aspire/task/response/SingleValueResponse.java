package jo.aspire.task.response;

public class SingleValueResponse {
    private String code;
    private String message;
    private String value;

    public SingleValueResponse() {
    }

    public SingleValueResponse(String code, String message, String value) {
        this.code = code;
        this.message = message;
        this.value = value;
    }

    public SingleValueResponse(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
