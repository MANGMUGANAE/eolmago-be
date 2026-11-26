package com.mangmuganae.eolmago.domain.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
    짠돌 등급입니다. 화면에 표시할 이름은 아래와 같이 매핑되어 있습니다.
    - STARTER : 짠돌 입문
    - BEGINNER : 짠돌 초수
    - INTERMEDIATE : 짠돌 중수
    - EXPERT : 짠돌 고수
    - MASTER : 짠돌 9단

    등급은 입문 < 초수 < 중수 < 고수 < 9단 순으로 높아집니다.
    """)
public enum JjandolRank {

  STARTER(1), //짠돌 입문
  BEGINNER(2), //짠돌 초수
  INTERMEDIATE(3), //짠돌 중수
  EXPERT(4), //짠돌 고수
  MASTER(5); //짠돌 9단

  //등급이 높을수록 큰 값입니다. User.jjandolRankLevel 의 CASE 식과 값을 동일하게 유지해야 합니다.
  private final int level;

  JjandolRank(int level) {
    this.level = level;
  }

  public int getLevel() {
    return level;
  }

}
