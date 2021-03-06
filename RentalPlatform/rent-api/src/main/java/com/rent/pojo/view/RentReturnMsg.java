package com.rent.pojo.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rent.pojo.base.Trade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author w
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RentReturnMsg {
    private Trade order;
    private String returnTime;
}
