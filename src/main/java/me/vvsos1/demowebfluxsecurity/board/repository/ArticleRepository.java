package me.vvsos1.demowebfluxsecurity.board.repository;

import me.vvsos1.demowebfluxsecurity.board.vo.Article;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ArticleRepository extends ReactiveCrudRepository<Article, Integer> {
}
