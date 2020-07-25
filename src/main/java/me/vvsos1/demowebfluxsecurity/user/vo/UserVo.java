package me.vvsos1.demowebfluxsecurity.user.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserVo implements Persistable<String> {

    @Id
    private String id;

    private String password;

    private String name;

    private String authority = "USER";

    public UserVo(String id) {
        this.id = id;
    }

    public UserVo(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public UserVo(String id, String password,String name){
        this.id = id;
        this.password = password;
        this.name = name;
    }


    @Override @JsonIgnore
    public boolean isNew() {
        return true;
    }


}
