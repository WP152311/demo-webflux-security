package me.vvsos1.demowebfluxsecurity.user.service;

import me.vvsos1.demowebfluxsecurity.exception.DuplicateIdException;
import me.vvsos1.demowebfluxsecurity.exception.PermissionDeniedException;
import me.vvsos1.demowebfluxsecurity.user.repository.UserRepository;
import me.vvsos1.demowebfluxsecurity.user.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Service
@Slf4j
public class UserService implements ReactiveUserDetailsService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public Mono<UserVo> save(UserVo newUser) {
        String rawPassword = newUser.getPassword();
        return repo.save(newUser.setPassword(encoder.encode(rawPassword)))
                .onErrorMap(DataIntegrityViolationException.class, e -> new DuplicateIdException());
    }

    @Transactional
    public Mono<UserVo> deleteById(String targetId) {
        return Mono.justOrEmpty(targetId)
                .flatMap(repo::findById)
                .switchIfEmpty(Mono.error(new NoSuchElementException(targetId)))
                .flatMap(target -> repo.deleteById(targetId)
                        .thenReturn(target)
                );
    }

    public Mono<UserVo> login(UserVo user) {
        return repo.findByIdAndPwd(user.getId(), user.getPassword())
                .switchIfEmpty(Mono.error(new NoSuchElementException()));
    }

    public Mono<UserVo> findById(String id) {
        return repo.findById(id)
                .switchIfEmpty(Mono.error(new NoSuchElementException()));
    }

    public Flux<UserVo> findAll() {
        return repo.findAll();
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return findById(username)
                .map(vo ->
                        User
                                .withUsername(vo.getId())
                                .password(vo.getPassword())
                                .authorities("USER")
                                .build()
                );
    }
}
