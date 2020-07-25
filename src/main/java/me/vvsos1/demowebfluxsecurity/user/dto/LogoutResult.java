package me.vvsos1.demowebfluxsecurity.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.vvsos1.demowebfluxsecurity.user.vo.UserVo;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogoutResult {
    private String id;

    private String name;

    private String authority;

    public static LogoutResult from(UserVo user) {
        return new LogoutResult(user.getId(),user.getName(),user.getAuthority());
    }
}
