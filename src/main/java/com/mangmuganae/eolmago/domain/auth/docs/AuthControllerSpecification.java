package com.mangmuganae.eolmago.domain.auth.docs;

import com.mangmuganae.eolmago.domain.auth.dto.RefreshResponseDto;
import com.mangmuganae.eolmago.global.common.response.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "인증 관련 API", description = "토큰을 재발급합니다.")
public interface AuthControllerSpecification {

  @Operation(
      summary = "액세스 토큰 재발급",
      description = """
          리프레시 토큰으로 액세스 토큰과 리프레시 토큰을 새로 발급받습니다.
          이 API 는 Authorization 헤더에 액세스 토큰이 아니라 리프레시 토큰을 넣어 호출합니다.
          요청 본문은 없습니다.

          재발급에 성공하면 리프레시 토큰도 함께 새 값으로 교체되므로 둘 다 저장해야 합니다.
          이전 리프레시 토큰은 더 이상 사용할 수 없습니다.

          리프레시 토큰이 유효하지 않은 경우의 HTTP 상태는 403 이고 본문 responseCode 는 1002 입니다.
          서버에 저장된 리프레시 토큰이 만료된 경우에는 저장된 토큰이 삭제되므로 다시 소셜 로그인해야 합니다.
          """,
      parameters = {
          @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer 리프레시 토큰",
              required = true, example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
      })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "재발급 성공",
          useReturnTypeSchema = true,
          content = @Content(
              examples = @ExampleObject(
                  name = "SuccessExample",
                  value = """
                      {
                        "localDateTime": "2026-11-27T15:30:00.123",
                        "responseCode": 200,
                        "statusCode": "SUCCESS",
                        "message": "발급에 성공하였습니다.",
                        "data": {
                          "accessToken": "eyJhbGciOiJIUzI1NiJ9.new-access-token",
                          "refreshToken": "eyJhbGciOiJIUzI1NiJ9.new-refresh-token"
                        }
                      }
                      """
              ))),
      @ApiResponse(responseCode = "401", description = "서버에 저장된 리프레시 토큰이 없음 (본문 responseCode 는 커스텀 코드 1002)",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = @ExampleObject(
                  name = "RefreshTokenNotStoredExample",
                  summary = "이미 로그아웃되었거나 저장된 토큰이 삭제된 경우",
                  value = """
                      {
                        "localDateTime": "2026-11-27T15:30:00.123",
                        "responseCode": 1002,
                        "statusCode": "REFRESH_TOKEN_INVALID",
                        "message": "리프레시 토큰이 유효하지 않습니다.",
                        "data": null
                      }
                      """
              ))),
      @ApiResponse(responseCode = "403", description = "리프레시 토큰이 없거나 유효하지 않거나 저장된 값과 다름",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = {
                  @ExampleObject(
                      name = "RefreshTokenInvalidExample",
                      summary = "토큰이 만료되었거나 저장된 값과 일치하지 않는 경우",
                      value = """
                          {
                            "localDateTime": "2026-11-27T15:30:00.123",
                            "responseCode": 1002,
                            "statusCode": "REFRESH_TOKEN_INVALID",
                            "message": "리프레시 토큰이 유효하지 않습니다.",
                            "data": null
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "TokenExtractionFailedExample",
                      summary = "Authorization 헤더가 없거나 Bearer 로 시작하지 않는 경우",
                      value = """
                          {
                            "localDateTime": "2026-11-27T15:30:00.123",
                            "responseCode": 401,
                            "statusCode": "TOKEN_EXTRACTION_FAILED",
                            "message": "토큰 추출에 실패했습니다.",
                            "data": null
                          }
                          """
                  )
              })),
      @ApiResponse(responseCode = "404", description = "토큰의 사용자를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = @ExampleObject(
                  name = "UserNotFoundExample",
                  value = """
                      {
                        "localDateTime": "2026-11-27T15:30:00.123",
                        "responseCode": 404,
                        "statusCode": "USER_NOT_FOUND",
                        "message": "해당 사용자를 찾을 수 없습니다.",
                        "data": null
                      }
                      """
              ))),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = @ExampleObject(
                  name = "InternalServerErrorExample",
                  value = """
                      {
                        "localDateTime": "2026-11-27T15:30:00.123",
                        "responseCode": 500,
                        "statusCode": "INTERNAL_SERVER_ERROR",
                        "message": "서버 내부 오류가 발생했습니다.",
                        "data": null
                      }
                      """
              )))
  })
  ResponseDTO<RefreshResponseDto> authRefresh(HttpServletRequest request);

}
