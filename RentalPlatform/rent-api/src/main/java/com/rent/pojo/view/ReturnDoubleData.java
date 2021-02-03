package com.rent.pojo.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author w
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReturnDoubleData {
    private String code;
    private boolean error;
    private String errorMessage;
    private Object data1;
    private Object data2;

    public ReturnDoubleData(String code, boolean error) {
        this.code = code;
        this.error = error;
    }

    public ReturnDoubleData(String code, boolean error, String errorMessage) {
        this.code = code;
        this.error = error;
        this.errorMessage = errorMessage;
    }

    public ReturnDoubleData(String code, boolean error, Object data) {
        this.code = code;
        this.error = error;
        this.data1 = data;
    }
}
