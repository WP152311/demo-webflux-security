package me.vvsos1.demowebfluxsecurity.user.handler;

import lombok.extern.slf4j.Slf4j;
import me.vvsos1.demowebfluxsecurity.exception.DuplicateIdException;
import me.vvsos1.demowebfluxsecurity.exception.PermissionDeniedException;
import me.vvsos1.demowebfluxsecurity.exception.UnauthenticatedException;
import me.vvsos1.demowebfluxsecurity.user.service.UserService;
import me.vvsos1.demowebfluxsecurity.user.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.security.Principal;
import java.util.NoSuchElementException;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Component
@Slf4j
public class UserHandler {
    private UserService service;

    @Autowired
    public UserHandler(UserService service) {
        this.service = service;
    }

    @Bean
    public RouterFunction<ServerResponse> userRouter() {
        return RouterFunctions
                .route()
                .nest(path("/user").and(accept(APPLICATION_JSON)), b1 -> b1
                        .GET("", this::findAllUser)
                        .POST("", this::saveUser)
                        .onError(DuplicateIdException.class, (e, req) ->
                                status(HttpStatus.CONFLICT).build())
                        .nest(path("/{id}"), b2 -> b2
                                .DELETE("", this::deleteUser)
                                .onError(NoSuchElementException.class, (e, req) ->
                                        notFound().build())
                                .onError(UnauthenticatedException.class, (e, req) ->
                                        status(HttpStatus.UNAUTHORIZED)
                                                .build())
                                .onError(PermissionDeniedException.class, (e, req) ->
                                        status(HttpStatus.FORBIDDEN)
                                                .build())))
                .route(all(), req -> notFound().build())
                .build();
    }


    public Mono<ServerResponse> findAllUser(ServerRequest req) {
        return ok().contentType(APPLICATION_JSON).body(service.findAll(), UserVo.class);
    }

    public Mono<ServerResponse> saveUser(ServerRequest req) {
        var newUser = req.bodyToMono(UserVo.class);

        return newUser
                .flatMap(service::save)
                .flatMap(savedUser ->
                        created(URI.create(req.path() + "/" + savedUser.getId()))
                                .contentType(APPLICATION_JSON)
                                .body(Mono.just(savedUser), UserVo.class));
    }


    public Mono<ServerResponse> deleteUser(ServerRequest req) {
        String targetId = req.pathVariable("id");
        Mono<UserVo> reqUserMono = req.principal()
                .map(Principal::getName)
                .flatMap(service::findById);


        return
                reqUserMono
                        .filter(reqUser -> reqUser.getId().equals(targetId))
                        .switchIfEmpty(Mono.error(new PermissionDeniedException("Permission Denied")))
                        .flatMap(requester -> service.deleteById(targetId))
                        .flatMap(vo ->
                                ok()
                                        .contentType(APPLICATION_JSON)
                                        .body(Mono.just(vo), UserVo.class));

    }
}
