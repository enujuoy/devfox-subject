package domain.dto;

import lombok.Data;

@Data
public class UserLoginRequest {

    private String loginId;
    private String password;


}
