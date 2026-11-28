package com.mangmuganae.eolmago.domain.comment.docs;

import com.mangmuganae.eolmago.domain.comment.dto.CommentCreateRequestDto;
import com.mangmuganae.eolmago.domain.comment.dto.CommentCreateResponseDto;
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

@Tag(name = "댓글 관련 API", description = "핫딜 글에 댓글과 대댓글을 작성합니다.")
public interface CommentControllerSpecification {

  @Operation(
      summary = "댓글 / 대댓글 작성",
      description = """
          핫딜 글에 댓글을 작성합니다. 댓글과 대댓글을 같은 API 로 처리합니다.

          parentCommentId 를 보내지 않거나 null 이면 댓글로 작성되고,
          기존 댓글의 ID 를 보내면 그 댓글의 대댓글로 작성됩니다.

          대댓글에는 다시 답글을 달 수 없습니다. 대댓글의 ID 를 parentCommentId 로 보내면 400 으로 응답됩니다.
          다른 글에 달린 댓글의 ID 를 보낸 경우는 존재하지 않는 댓글과 동일하게 404 로 응답됩니다.

          작성에 성공하면 글의 댓글 수가 1 증가합니다.
          HTTP 상태는 200 이고, 본문의 responseCode 가 201, statusCode 가 CREATED 입니다.
          """,
      parameters = {
          @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer 액세스 토큰",
              required = true, example = "Bearer eyJhbGciOiJIUzI1NiJ9..."),
          @Parameter(in = ParameterIn.PATH, name = "dealPostId", description = "댓글을 달 핫딜 글 ID",
              required = true, example = "12")
      },
      requestBody = @RequestBody(required = true,
          content = @Content(schema = @Schema(implementation = CommentCreateRequestDto.class),
              examples = {
                  @ExampleObject(
                      name = "CommentExample",
                      summary = "댓글 작성",
                      value = """
                          {
                            "content": "이 가격이면 바로 사야죠"
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "ReplyExample",
                      summary = "대댓글 작성",
                      value = """
                          {
                            "content": "저도 방금 주문했어요",
                            "parentCommentId": 48
                          }
                          """
                  )
              }))
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "댓글 작성 성공 (본문 responseCode 는 201)",
          useReturnTypeSchema = true,
          content = @Content(
              examples = {
                  @ExampleObject(
                      name = "CommentCreatedExample",
                      summary = "댓글로 작성된 경우",
                      value = """
                          {
                            "localDateTime": "2026-11-28T15:30:00.123",
                            "responseCode": 201,
                            "statusCode": "CREATED",
                            "message": "댓글 작성에 성공하였습니다.",
                            "data": {
                              "commentId": 51,
                              "parentCommentId": null
                            }
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "ReplyCreatedExample",
                      summary = "대댓글로 작성된 경우",
                      value = """
                          {
                            "localDateTime": "2026-11-28T15:30:00.123",
                            "responseCode": 201,
                            "statusCode": "CREATED",
                            "message": "댓글 작성에 성공하였습니다.",
                            "data": {
                              "commentId": 52,
                              "parentCommentId": 48
                            }
                          }
                          """
                  )
              })),
      @ApiResponse(responseCode = "400", description = "내용이 비어 있거나 대댓글에 답글을 단 경우",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = {
                  @ExampleObject(
                      name = "CommentContentRequiredExample",
                      value = """
                          {
                            "localDateTime": "2026-11-28T15:30:00.123",
                            "responseCode": 400,
                            "statusCode": "COMMENT_CONTENT_REQUIRED",
                            "message": "댓글 내용을 입력해주세요.",
                            "data": null
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "CommentReplyDepthExceededExample",
                      value = """
                          {
                            "localDateTime": "2026-11-28T15:30:00.123",
                            "responseCode": 400,
                            "statusCode": "COMMENT_REPLY_DEPTH_EXCEEDED",
                            "message": "대댓글에는 답글을 달 수 없습니다.",
                            "data": null
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
                        "localDateTime": "2026-11-28T15:30:00.123",
                        "responseCode": 1001,
                        "statusCode": "ACCESS_TOKEN_INVALID",
                        "message": "엑세스 토큰이 유효하지 않습니다.",
                        "data": null
                      }
                      """
              ))),
      @ApiResponse(responseCode = "404", description = "핫딜 글 / 부모 댓글 / 사용자를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = {
                  @ExampleObject(
                      name = "PostNotFoundExample",
                      value = """
                          {
                            "localDateTime": "2026-11-28T15:30:00.123",
                            "responseCode": 404,
                            "statusCode": "POST_NOT_FOUND",
                            "message": "해당 게시글을 찾을 수 없습니다.",
                            "data": null
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "CommentNotFoundExample",
                      summary = "없는 댓글이거나 다른 글의 댓글을 부모로 보낸 경우",
                      value = """
                          {
                            "localDateTime": "2026-11-28T15:30:00.123",
                            "responseCode": 404,
                            "statusCode": "COMMENT_NOT_FOUND",
                            "message": "해당 댓글을 찾을 수 없습니다.",
                            "data": null
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "UserNotFoundExample",
                      value = """
                          {
                            "localDateTime": "2026-11-28T15:30:00.123",
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
                        "localDateTime": "2026-11-28T15:30:00.123",
                        "responseCode": 500,
                        "statusCode": "INTERNAL_SERVER_ERROR",
                        "message": "서버 내부 오류가 발생했습니다.",
                        "data": null
                      }
                      """
              )))
  })
  ResponseDTO<CommentCreateResponseDto> createComment(Long dealPostId,
      CommentCreateRequestDto commentCreateRequestDto, CustomUserDetails userDetails);

  @Operation(
      summary = "댓글 / 대댓글 삭제",
      description = """
          본인이 작성한 댓글을 삭제합니다. 댓글과 대댓글 모두 같은 API 로 삭제합니다.

          댓글을 삭제하면 그 댓글에 달린 대댓글도 함께 삭제되고, 글의 댓글 수가 삭제된 개수만큼 줄어듭니다.
          다른 사람의 댓글은 삭제할 수 없습니다.

          다른 글에 달린 댓글의 ID 를 보낸 경우는 존재하지 않는 댓글과 동일하게 404 로 응답됩니다.
          응답 본문에 data 는 없습니다.
          """,
      parameters = {
          @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer 액세스 토큰",
              required = true, example = "Bearer eyJhbGciOiJIUzI1NiJ9..."),
          @Parameter(in = ParameterIn.PATH, name = "dealPostId", description = "댓글이 달린 핫딜 글 ID",
              required = true, example = "12"),
          @Parameter(in = ParameterIn.PATH, name = "commentId", description = "삭제할 댓글 ID",
              required = true, example = "51")
      })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "댓글 삭제 성공",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = @ExampleObject(
                  name = "SuccessExample",
                  value = """
                      {
                        "localDateTime": "2026-11-28T15:30:00.123",
                        "responseCode": 200,
                        "statusCode": "SUCCESS",
                        "message": "댓글 삭제에 성공하였습니다.",
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
                        "localDateTime": "2026-11-28T15:30:00.123",
                        "responseCode": 1001,
                        "statusCode": "ACCESS_TOKEN_INVALID",
                        "message": "엑세스 토큰이 유효하지 않습니다.",
                        "data": null
                      }
                      """
              ))),
      @ApiResponse(responseCode = "403", description = "본인이 작성한 댓글이 아님",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = @ExampleObject(
                  name = "AccessDeniedExample",
                  value = """
                      {
                        "localDateTime": "2026-11-28T15:30:00.123",
                        "responseCode": 403,
                        "statusCode": "ACCESS_DENIED",
                        "message": "이 기능에 접근할 수 없습니다.",
                        "data": null
                      }
                      """
              ))),
      @ApiResponse(responseCode = "404", description = "핫딜 글 / 댓글 / 사용자를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = {
                  @ExampleObject(
                      name = "CommentNotFoundExample",
                      value = """
                          {
                            "localDateTime": "2026-11-28T15:30:00.123",
                            "responseCode": 404,
                            "statusCode": "COMMENT_NOT_FOUND",
                            "message": "해당 댓글을 찾을 수 없습니다.",
                            "data": null
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "PostNotFoundExample",
                      value = """
                          {
                            "localDateTime": "2026-11-28T15:30:00.123",
                            "responseCode": 404,
                            "statusCode": "POST_NOT_FOUND",
                            "message": "해당 게시글을 찾을 수 없습니다.",
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
                        "localDateTime": "2026-11-28T15:30:00.123",
                        "responseCode": 500,
                        "statusCode": "INTERNAL_SERVER_ERROR",
                        "message": "서버 내부 오류가 발생했습니다.",
                        "data": null
                      }
                      """
              )))
  })
  ResponseDTO<?> deleteComment(Long dealPostId, Long commentId, CustomUserDetails userDetails);

}
