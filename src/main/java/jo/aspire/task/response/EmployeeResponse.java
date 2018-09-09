package jo.aspire.task.response;

import jo.aspire.task.dto.EmployeeDTO;

import java.util.List;

public class EmployeeResponse{
    private String code;
    private String message;
    private List<EmployeeDTO> data;

    public EmployeeResponse(){

    }
    public EmployeeResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }


    public EmployeeResponse(String code, String message, List<EmployeeDTO> data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public EmployeeResponse(String code, List<EmployeeDTO> data) {
        this.code = code;
        this.data = data;
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

    public List<EmployeeDTO> getData() {
        return data;
    }

    public void setData(List<EmployeeDTO> data) {
        this.data = data;
    }
}
