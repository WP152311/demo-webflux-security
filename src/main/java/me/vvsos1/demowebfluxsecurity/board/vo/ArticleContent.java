package me.vvsos1.demowebfluxsecurity.board.vo;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;


/**
 * <pre>
 * org.dimigo.vo |_ FreeArticleContent
 *
 * 1. 개요 : 2. 작성일 : 2017. 11. 16.
 * 
 * <pre>
 *
 * @author : 박명규(로컬계정)
 * @version : 1.0
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ArticleContent {
	@Id
	private int number;

	private String content;

}
