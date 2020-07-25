package me.vvsos1.demowebfluxsecurity.user.handler;


import lombok.extern.slf4j.Slf4j;
import me.vvsos1.demowebfluxsecurity.user.dto.LoginResult;
import me.vvsos1.demowebfluxsecurity.user.dto.LogoutResult;
import me.vvsos1.demowebfluxsecurity.user.service.UserService;
import me.vvsos1.demowebfluxsecurity.user.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Component
@Slf4j
public class AuthHandler {
    @Autowired
    private UserService service;

    @Autowired
    private ReactiveAuthenticationManager manager;


    @Bean
    public RouterFunction<ServerResponse> authRouter() {
        return RouterFunctions
                .route()
                .nest(path("/auth").and(accept(APPLICATION_JSON)), b -> b
                        .GET("",this::find)
                        .POST("", this::login)
                        .DELETE("", this::logout))
                .build();
    }

    public Mono<ServerResponse> login(ServerRequest req) {
        Mono<UserVo> userMono = req.bodyToMono(UserVo.class).cache();
        Mono<WebSession> sessionMono = req.session();

        Mono<Authentication> authMono = userMono.map(user ->
                new UsernamePasswordAuthenticationToken(user.getId(), user.getPassword(), List.of(new SimpleGrantedAuthority(user.getAuthority()))))
                .flatMap(manager::authenticate);

        Mono<SecurityContextImpl> ctxMono = authMono.map(SecurityContextImpl::new);

        return sessionMono
                .doOnNext(WebSession::start)
                .zipWith(ctxMono)
                .doOnNext(t -> t.getT1().getAttributes().put(DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME,t.getT2()))
                .map(Tuple2::getT1)
                .zipWith(userMono.flatMap(user->service.findById(user.getId()))
                        ,(session,user) -> LoginResult.from(user,session.getId()))
                .flatMap(loginResult ->
                        ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .body(Mono.just(loginResult),LoginResult.class)
                );
    }

AuthenticationFilter
    public Mono<ServerResponse> logout(ServerRequest req) {
        Mono<SecurityContext> contextMono = ReactiveSecurityContextHolder.getContext();

        return req.session().map(WebSession::getAttributes)
                .doOnNext(map -> map.remove(DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME))
                .then(
                        contextMono.map(ctx -> {
                            Authentication authentication = ctx.getAuthentication();

                            ctx.setAuthentication(null);

                            return authentication.getName();
                        })
                                .flatMap(service::findById)
                                .map(LogoutResult::from)
                                .flatMap(logoutResult ->
                                        ServerResponse
                                                .ok()
                                                .contentType(APPLICATION_JSON)
                                                .body(Mono.just(logoutResult), LogoutResult.class)
                                ));
    }

    public Mono<ServerResponse> find(ServerRequest req) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(UserDetails.class)
                .map(UserDetails::getUsername)
                .flatMap(name->
                        ServerResponse
                .ok()
                .contentType(TEXT_PLAIN)
                .body(Mono.just(name),String.class));
    }

}
