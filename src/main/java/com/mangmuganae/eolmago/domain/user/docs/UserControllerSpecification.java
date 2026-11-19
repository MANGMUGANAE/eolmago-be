package com.mangmuganae.eolmago.domain.user.docs;

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

@Tag(name = "사용자 관련 API", description = "사용자를 조회/수정/삭제 합니다.")
public interface UserControllerSpecification {


  @Operation(summary = "accountId 조회", description = "accessKey와 secretKey로 accountId를 조회하는 메서드입니다.",
      parameters = {
          @Parameter(in = ParameterIn.HEADER, name = "region", description = "test1", required = true, example = "test1"),
          @Parameter(in = ParameterIn.HEADER, name = "access-key", description = "test2", required = true, example = "test2"),
          @Parameter(in = ParameterIn.HEADER, name = "secret-access-key", description = "test3", required = true, example = "test3")
      })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ResponseDTO.class),
          examples = {@ExampleObject(
              name = "SuccessRequestExample",
              value = "{\"success\": true, \"id\": -\"}"
          ),
              @ExampleObject(
                  name = "BadRequestExample",
                  value = "{\n" +
                      "  \"success\":false,\n" +
                      "  \"id\":\"\",\n" +
                      "  \"message\":\"The security token included in the request is invalid. (Service: Sts, Status Code: 403, Request ID: -)\"\n" +
                      "}"
              )
          })),
      @ApiResponse(responseCode = "400", description = "조회 실패", content = @Content(
          examples = @ExampleObject(
              name = "ClientBadRequestExample",
              value = "{\n" +
                  "  \"timestamp\": \"2024-06-16T06:36:10.860+00:00\",\n" +
                  "  \"status\": 400,\n" +
                  "  \"error\": \"Bad Request\",\n" +
                  "  \"path\": \"test\n" +
                  "}"
          ))),
      @ApiResponse(responseCode = "500", description = "서버 응답 실패", content = @Content(
          examples = @ExampleObject(
              name = "ServerBadResponseExample",
              value = "{\n" +
                  "  \"timestamp\": \"2024-06-04T06:51:14.175+00:00\",\n" +
                  "  \"status\": 500,\n" +
                  "  \"error\": \"Internal Server Error\",\n" +
                  "  \"path\": \"/test\"\n" +
                  "}"
          ))),
  })
  public ResponseDTO<String> healthCheck();

}
