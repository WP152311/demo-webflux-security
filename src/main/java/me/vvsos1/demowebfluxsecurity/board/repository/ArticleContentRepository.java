package me.vvsos1.demowebfluxsecurity.board.repository;

import me.vvsos1.demowebfluxsecurity.board.vo.ArticleContent;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ArticleContentRepository extends ReactiveCrudRepository<ArticleContent, Integer> {
}
