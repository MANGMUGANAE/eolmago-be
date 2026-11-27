package com.mangmuganae.eolmago.domain.alert.docs;

import com.mangmuganae.eolmago.domain.alert.dto.AlertDto;
import com.mangmuganae.eolmago.domain.alert.dto.AlertResponseDto;
import com.mangmuganae.eolmago.global.common.response.ResponseDTO;
import com.mangmuganae.eolmago.global.security.core.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "알림 설정 관련 API", description = "키워드 / 카테고리 알림을 등록합니다.")
public interface AlertControllerSpecification {

  @Operation(
      summary = "내 알림 설정 조회",
      description = """
          등록한 알림 설정을 카테고리 알림과 키워드 알림으로 나눠서 조회합니다.

          categories 는 현재 존재하는 카테고리 전체가 내려가고, enabled 로 알림을 켰는지 확인합니다.
          켠 카테고리는 enabled 가 true 이고 alertId 가 함께 내려가므로 그 값으로 삭제할 수 있습니다.
          끈 카테고리는 enabled 가 false 이고 alertId 는 null 입니다.

          keywords 는 등록한 키워드만 내려가며 전부 alertId 를 가집니다. 등록한 키워드가 없으면 빈 배열입니다.
          """,
      parameters = {
          @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer 액세스 토큰",
              required = true, example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
      })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "알림 설정 조회 성공",
          useReturnTypeSchema = true,
          content = @Content(
              examples = @ExampleObject(
                  name = "SuccessExample",
                  value = """
                      {
                        "localDateTime": "2026-11-27T15:30:00.123",
                        "responseCode": 200,
                        "statusCode": "SUCCESS",
                        "message": "알림 설정 조회에 성공하였습니다.",
                        "data": {
                          "categories": [
                            {
                              "alertId": 7,
                              "categoryId": 1,
                              "categoryName": "전자기기",
                              "enabled": true
                            },
                            {
                              "alertId": null,
                              "categoryId": 2,
                              "categoryName": "식품",
                              "enabled": false
                            }
                          ],
                          "keywords": [
                            {
                              "alertId": 12,
                              "keyword": "에어팟"
                            },
                            {
                              "alertId": 9,
                              "keyword": "모니터"
                            }
                          ]
                        }
                      }
                      """
              ))),
      @ApiResponse(responseCode = "401", description = "액세스 토큰이 없거나 유효하지 않음 (본문 responseCode 는 커스텀 코드 1001)",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = @ExampleObject(
                  name = "AccessTokenInvalidExample",
                  value = """
                      {
                        "localDateTime": "2026-11-27T15:30:00.123",
                        "responseCode": 1001,
                        "statusCode": "ACCESS_TOKEN_INVALID",
                        "message": "엑세스 토큰이 유효하지 않습니다.",
                        "data": null
                      }
                      """
              ))),
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
  ResponseDTO<AlertResponseDto> findMyAlerts(CustomUserDetails userDetails);

  @Operation(
      summary = "키워드 / 카테고리 알림 등록",
      description = """
          원하는 키워드나 카테고리의 핫딜이 올라올 때 알림을 받도록 등록합니다.

          keyword 와 categoryId 중 하나는 반드시 보내야 하며, 둘 다 비어 있으면 400 으로 응답됩니다.
          - keyword 만 보내면 키워드 알림으로 등록됩니다.
          - categoryId 만 보내면 카테고리 알림으로 등록됩니다. 등록된 카테고리여야 합니다.
          - 둘 다 보내면 keyword 가 우선 적용되고 categoryId 는 무시됩니다.

          같은 키워드나 카테고리를 다시 등록해도 막지 않으므로 중복 등록될 수 있습니다.
          응답 본문에 data 는 없습니다.

          keyword 와 categoryId 를 모두 생략한 경우의 400 응답은 요청 값 검증 단계에서 발생해
          공통 응답 포맷이 아닌 스프링 기본 포맷(type / title / status / detail)으로 내려갑니다.
          """,
      parameters = {
          @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer 액세스 토큰",
              required = true, example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
      },
      requestBody = @RequestBody(required = true,
          content = @Content(schema = @Schema(implementation = AlertDto.class),
              examples = {
                  @ExampleObject(
                      name = "KeywordAlertExample",
                      summary = "키워드 알림 등록",
                      value = """
                          {
                            "keyword": "에어팟"
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "CategoryAlertExample",
                      summary = "카테고리 알림 등록",
                      value = """
                          {
                            "categoryId": 1
                          }
                          """
                  )
              }))
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "알림 등록 성공",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = @ExampleObject(
                  name = "SuccessExample",
                  value = """
                      {
                        "localDateTime": "2026-11-27T15:30:00.123",
                        "responseCode": 200,
                        "statusCode": "SUCCESS",
                        "message": "알림 등록에 성공하였습니다.",
                        "data": null
                      }
                      """
              ))),
      @ApiResponse(responseCode = "400", description = "keyword 와 categoryId 를 모두 보내지 않음 (공통 포맷 아님)",
          content = @Content(
              examples = @ExampleObject(
                  name = "AlertTargetRequiredExample",
                  value = """
                      {
                        "type": "about:blank",
                        "title": "Bad Request",
                        "status": 400,
                        "detail": "Invalid request content.",
                        "instance": "/api/v1/alerts"
                      }
                      """
              ))),
      @ApiResponse(responseCode = "401", description = "액세스 토큰이 없거나 유효하지 않음 (본문 responseCode 는 커스텀 코드 1001)",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = @ExampleObject(
                  name = "AccessTokenInvalidExample",
                  value = """
                      {
                        "localDateTime": "2026-11-27T15:30:00.123",
                        "responseCode": 1001,
                        "statusCode": "ACCESS_TOKEN_INVALID",
                        "message": "엑세스 토큰이 유효하지 않습니다.",
                        "data": null
                      }
                      """
              ))),
      @ApiResponse(responseCode = "404", description = "사용자 또는 카테고리를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = {
                  @ExampleObject(
                      name = "CategoryNotFoundExample",
                      summary = "등록되지 않은 categoryId 를 보낸 경우",
                      value = """
                          {
                            "localDateTime": "2026-11-27T15:30:00.123",
                            "responseCode": 404,
                            "statusCode": "CATEGORY_NOT_FOUND",
                            "message": "해당 카테고리를 찾을 수 없습니다.",
                            "data": null
                          }
                          """
                  ),
                  @ExampleObject(
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
                  )
              })),
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
  ResponseDTO<?> registerAlert(AlertDto alertDto, CustomUserDetails userDetails);

  @Operation(
      summary = "알림 설정 삭제",
      description = """
          등록한 키워드 / 카테고리 알림을 삭제합니다.

          본인이 등록한 알림만 삭제할 수 있습니다.
          존재하지 않는 alertId 와 다른 사용자의 alertId 는 모두 같은 404 로 응답됩니다.
          남의 알림이 존재하는지 알 수 없게 하기 위한 동작입니다.

          응답 본문에 data 는 없습니다.
          alertId 에 숫자가 아닌 값을 넣으면 공통 응답 포맷이 아닌 스프링 기본 400 이 내려갑니다.
          """,
      parameters = {
          @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer 액세스 토큰",
              required = true, example = "Bearer eyJhbGciOiJIUzI1NiJ9..."),
          @Parameter(in = ParameterIn.PATH, name = "alertId", description = "삭제할 알림 설정 ID",
              required = true, example = "1")
      })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "알림 삭제 성공",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = @ExampleObject(
                  name = "SuccessExample",
                  value = """
                      {
                        "localDateTime": "2026-11-27T15:30:00.123",
                        "responseCode": 200,
                        "statusCode": "SUCCESS",
                        "message": "알림 삭제에 성공하였습니다.",
                        "data": null
                      }
                      """
              ))),
      @ApiResponse(responseCode = "401", description = "액세스 토큰이 없거나 유효하지 않음 (본문 responseCode 는 커스텀 코드 1001)",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = @ExampleObject(
                  name = "AccessTokenInvalidExample",
                  value = """
                      {
                        "localDateTime": "2026-11-27T15:30:00.123",
                        "responseCode": 1001,
                        "statusCode": "ACCESS_TOKEN_INVALID",
                        "message": "엑세스 토큰이 유효하지 않습니다.",
                        "data": null
                      }
                      """
              ))),
      @ApiResponse(responseCode = "404", description = "알림 설정 또는 사용자를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = {
                  @ExampleObject(
                      name = "AlertNotFoundExample",
                      summary = "없는 알림이거나 본인이 등록한 알림이 아닌 경우",
                      value = """
                          {
                            "localDateTime": "2026-11-27T15:30:00.123",
                            "responseCode": 404,
                            "statusCode": "ALERT_NOT_FOUND",
                            "message": "해당 알림 설정을 찾을 수 없습니다.",
                            "data": null
                          }
                          """
                  ),
                  @ExampleObject(
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
                  )
              })),
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
  ResponseDTO<?> deleteAlert(Long alertId, CustomUserDetails userDetails);

}
