package com.rent.pojo.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginMsg {
    private String loginAccount;
    private String loginPassword;
}
