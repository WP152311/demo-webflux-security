package me.vvsos1.demowebfluxsecurity.board.vo;

import me.vvsos1.demowebfluxsecurity.user.vo.UserVo;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * <pre>
 * org.dimigo.vo |_ FreeArticle
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
public class Article {
    @Id
    private int number;

//    @ManyToOne
    private UserVo writer;

    private String title;

    private Date regDate = new Date();

    private int readCount = 0;


}
