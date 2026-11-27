package com.baseballrefree.api;

import com.baseballrefree.api.dto.GameStateResponse;
import com.baseballrefree.api.dto.JudgmentRequest;
import com.baseballrefree.api.dto.PitchResponse;
import com.baseballrefree.game.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/* HTTP Request/Response Mapping만 담당한다.
   로직은 GameSerivce
   응답 변환은 GameResponseMapping 에 위임한다
*/
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GameController {

  private final GameService gameService;
  private final GameResponseMapper responseMapper;

  // 새 게임을 시작하고 초기 상태를 반환
  @PostMapping("/game/start")
  public GameStateResponse start() {  return responseMapper.toStateResponse(gameService.startGame());  }

  // 공 한 개를 던지고 결과를 반환한다
  @PostMapping("/pitch")
  public PitchResponse pitch() {  return responseMapper.toPitchResponse(gameService.throwPitch());  }

  // 심판 판정 (STRIKE/BALL)을 제출하고 갱신된 상태를 반환한다
  @PostMapping("/judgment")
  public GameStateResponse judgment(@Valid @RequestBody JudgmentRequest request) {
    return responseMapper.toStateResponse(gameService.submitJudgment(request.judgment()));
  }

  // var 기회를 사용하고 갱신된 상태를 반환한다
  @PostMapping("/var")
  public GameStateResponse useVar() {  return responseMapper.toStateResponse(gameService.useVar());  }

  // 현재 경기 상태를 반환한다
  @GetMapping("/game/state")
  public GameStateResponse state() {  return responseMapper.toStateResponse(gameService.getState());  }
}
