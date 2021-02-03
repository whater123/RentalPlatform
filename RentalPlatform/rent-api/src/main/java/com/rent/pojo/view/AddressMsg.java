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
public class AddressMsg {
    private String lat;
    private String lng;
    private String address;

    public AddressMsg(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public AddressMsg(String address) {
        this.address = address;
    }
}
