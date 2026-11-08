package com.baseballrefree.game;

/*
* OUT -> 뜬공 혹은 선행 주자 아웃
* GOUND_OUT -> 땅볼 아웃(포스 아웃의 경우 야수가 송구 베이스를 선택)*/
public enum HitOutcome {
  FOUL, OUT, GROUND_OUT, SINGLE, DOUBLE, TRIPLE, HOMERUN
}
