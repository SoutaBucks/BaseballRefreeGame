package com.baseballrefree.api;

import com.baseballrefree.game.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/* HTTP Request/Response Mapping만 담당한다.
   로직은 GameSerivce
   응답 변환은 GameResponseMapping 에 위임한다
*/
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GameController {

  private final GameService gameService;
}
