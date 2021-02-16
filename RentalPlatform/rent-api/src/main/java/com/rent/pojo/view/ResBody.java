package com.rent.pojo.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResBody {
    String logo;
    int status;
    String tel;
    String upgrade_info;
    List<LogisticsMsg> data;
}
