package me.vvsos1.demowebfluxsecurity.user.repository;

import me.vvsos1.demowebfluxsecurity.user.vo.UserVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<UserVo, String> {

    @Query("select * from user_vo where id = :id and pwd = :pwd")
    public Mono<UserVo> findByIdAndPwd(String id, String pwd);
}
