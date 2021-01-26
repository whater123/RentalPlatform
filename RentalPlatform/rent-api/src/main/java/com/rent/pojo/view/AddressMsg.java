package com.rent.pojo.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author w
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressMsg {
    private String lat;
    private String lng;
}
