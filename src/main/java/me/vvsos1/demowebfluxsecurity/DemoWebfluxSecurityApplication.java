package me.vvsos1.demowebfluxsecurity;

import lombok.extern.slf4j.Slf4j;
import me.vvsos1.demowebfluxsecurity.user.repository.UserRepository;
import me.vvsos1.demowebfluxsecurity.user.service.UserService;
import me.vvsos1.demowebfluxsecurity.user.vo.UserVo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@Slf4j
public class DemoWebfluxSecurityApplication {

    public static void main(String[] args) throws InterruptedException {

        ApplicationContext context = SpringApplication.run(DemoWebfluxSecurityApplication.class, args);

        UserService service = context.getBean(UserService.class);

        UserRepository repo = context.getBean(UserRepository.class);


        service.save(new UserVo("vvsos1","1234","park"))
                .subscribe();

//        service.save(new UserVo("vvsos1","1234","park"))
//                .subscribe();


    }

}
