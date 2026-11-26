package com.mangmuganae.eolmago.domain.user.docs;

import com.mangmuganae.eolmago.domain.user.dto.SignupRequest;
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
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "사용자 관련 API", description = "사용자를 조회/수정/삭제 합니다.")
public interface UserControllerSpecification {


  @Operation(
      summary = "회원가입",
      description = """
          소셜 로그인 후 닉네임과 프로필 이미지를 등록해 회원가입을 마칩니다.
          multipart/form-data 로 호출합니다.
          - request : 닉네임 JSON 파트입니다. 필수입니다. (Content-Type: application/json)
          - file : 프로필 이미지 파일 파트입니다. 선택이며, 보내지 않으면 프로필 이미지 없이 가입됩니다.

          nickname 은 비어 있을 수 없습니다. 공백만 보내도 400 으로 응답됩니다.

          소셜 로그인 성공 시 리다이렉트 쿼리로 받은 accessToken 을 Authorization 헤더에 넣어 호출합니다.
          isNewUser 가 true 인 경우에 호출하면 됩니다.

          이미 닉네임이 등록된 사용자가 다시 호출하면 아무것도 변경하지 않고 성공 응답이 내려갑니다.
          응답 본문에 data 는 없습니다.
          """,
      parameters = {
          @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer 액세스 토큰",
              required = true, example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
      })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "회원가입 성공",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = @ExampleObject(
                  name = "SuccessExample",
                  value = """
                      {
                        "localDateTime": "2026-11-26T15:30:00.123",
                        "responseCode": 200,
                        "statusCode": "SUCCESS",
                        "message": "회원가입에 성공하였습니다.",
                        "data": null
                      }
                      """
              ))),
      @ApiResponse(responseCode = "400", description = "닉네임이 비어 있음",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = @ExampleObject(
                  name = "NicknameRequiredExample",
                  value = """
                      {
                        "localDateTime": "2026-11-26T15:30:00.123",
                        "responseCode": 400,
                        "statusCode": "NICKNAME_REQUIRED",
                        "message": "닉네임을 입력해주세요.",
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
                        "localDateTime": "2026-11-26T15:30:00.123",
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
                        "localDateTime": "2026-11-26T15:30:00.123",
                        "responseCode": 404,
                        "statusCode": "USER_NOT_FOUND",
                        "message": "해당 사용자를 찾을 수 없습니다.",
                        "data": null
                      }
                      """
              ))),
      @ApiResponse(responseCode = "500", description = "프로필 이미지 저장 실패 또는 서버 내부 오류 (이미지를 보낸 경우에만 발생)",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = {
                  @ExampleObject(
                      name = "FileUploadFailedExample",
                      summary = "프로필 이미지 저장에 실패한 경우 - 재시도 가능",
                      value = """
                          {
                            "localDateTime": "2026-11-26T15:30:00.123",
                            "responseCode": 500,
                            "statusCode": "FILE_UPLOAD_FAILED",
                            "message": "파일 저장에 실패했습니다. 잠시 후 다시 시도해주세요.",
                            "data": null
                          }
                          """
                  ),
                  @ExampleObject(
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
                  )
              }))
  })
  ResponseDTO<?> signup(SignupRequest signupRequest, MultipartFile file,
      CustomUserDetails userDetails);

}
