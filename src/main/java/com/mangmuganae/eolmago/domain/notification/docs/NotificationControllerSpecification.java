package com.mangmuganae.eolmago.domain.notification.docs;

import com.mangmuganae.eolmago.domain.notification.dto.NotificationDto;
import com.mangmuganae.eolmago.global.common.response.ResponseDTO;
import com.mangmuganae.eolmago.global.security.core.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@Tag(name = "알림 관련 API", description = "로그인한 사용자의 알림을 조회합니다.")
public interface NotificationControllerSpecification {

  @Operation(
      summary = "내 알림 전체 조회",
      description = """
          액세스 토큰의 사용자에게 발송된 알림을 전부 조회합니다. 최근에 발송된 알림이 먼저 내려갑니다.
          읽음 여부와 무관하게 모두 조회되며, read 가 false 인 알림이 읽지 않은 알림입니다.

          notificationType 에 따라 함께 내려가는 ID 가 다릅니다.
          댓글 관련 알림은 commentId 로 해당 댓글까지 이동할 수 있고,
          핫딜 글 알림은 dealPostId 로 글 상세로 이동할 수 있습니다.
          해당하지 않는 ID 는 null 입니다.

          pushedAt 은 알림이 발송된 시각입니다.
          알림이 없으면 404 가 아니라 빈 배열로 응답됩니다.
          """,
      parameters = {
          @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer 액세스 토큰",
              required = true, example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
      })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "알림 조회 성공",
          useReturnTypeSchema = true,
          content = @Content(
              examples = {
                  @ExampleObject(
                      name = "SuccessExample",
                      value = """
                          {
                            "localDateTime": "2026-11-26T15:30:00.123",
                            "responseCode": 200,
                            "statusCode": "SUCCESS",
                            "message": "알림 조회에 성공하였습니다.",
                            "data": [
                              {
                                "notificationId": 31,
                                "content": "작성한 핫딜에 새 댓글이 달렸습니다.",
                                "notificationType": "COMMENT",
                                "read": false,
                                "dealPostId": 12,
                                "commentId": 48,
                                "pushedAt": "2026-11-26T15:12:40.221"
                              },
                              {
                                "notificationId": 27,
                                "content": "등록한 키워드 '에어팟' 핫딜이 올라왔습니다.",
                                "notificationType": "KEYWORD",
                                "read": true,
                                "dealPostId": 9,
                                "commentId": null,
                                "pushedAt": "2026-11-25T09:31:55.120"
                              }
                            ]
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "EmptyResultExample",
                      summary = "받은 알림이 없는 경우",
                      value = """
                          {
                            "localDateTime": "2026-11-26T15:30:00.123",
                            "responseCode": 200,
                            "statusCode": "SUCCESS",
                            "message": "알림 조회에 성공하였습니다.",
                            "data": []
                          }
                          """
                  )
              })),
      @ApiResponse(responseCode = "401", description = "액세스 토큰이 없거나 유효하지 않음 (본문 responseCode 는 커스텀 코드 1001)",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = @ExampleObject(
                  name = "AccessTokenInvalidExample",
                  value = """
                      {
                        "localDateTime": "2026-11-26T15:30:00.123",
                        "responseCode": 1001,
                        "statusCode": "ACCESS_TOKEN_INVALID",
                        "message": "엑세스 토큰이 유효하지 않습니다.",
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
                        "localDateTime": "2026-11-26T15:30:00.123",
                        "responseCode": 500,
                        "statusCode": "INTERNAL_SERVER_ERROR",
                        "message": "서버 내부 오류가 발생했습니다.",
                        "data": null
                      }
                      """
              )))
  })
  ResponseDTO<List<NotificationDto>> findAllByUserId(CustomUserDetails customUserDetails);

}
