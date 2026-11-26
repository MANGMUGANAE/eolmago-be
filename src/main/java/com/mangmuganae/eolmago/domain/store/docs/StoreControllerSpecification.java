package com.mangmuganae.eolmago.domain.store.docs;

import com.mangmuganae.eolmago.domain.store.dto.StoreDto;
import com.mangmuganae.eolmago.domain.store.dto.StoreResponseDto;
import com.mangmuganae.eolmago.domain.store.dto.StoreSearchRequestDto;
import com.mangmuganae.eolmago.global.common.response.ResponseDTO;
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

@Tag(name = "판매처 관련 API", description = "상품 링크 또는 판매처명으로 판매처를 조회합니다.")
public interface StoreControllerSpecification {

  @Operation(
      summary = "상품 링크로 판매처 조회",
      description = """
          상품 링크의 호스트로 판매처를 식별해 storeId 와 storeName 을 반환합니다.
          외부 요청 없이 서버에 올려둔 도메인 매핑으로만 판별하므로 단축 URL 은 식별되지 않습니다.
          등록되지 않은 판매처라면 404 로 응답되며, 이때는 판매처 검색 API 로 직접 선택하게 하면 됩니다.
          purchaseLink 를 보내지 않았거나 공백이면 404 와 구분되는 400 으로 응답됩니다.
          """,
      parameters = {
          @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer 액세스 토큰",
              required = true, example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
      },
      requestBody = @RequestBody(required = true,
          content = @Content(schema = @Schema(implementation = StoreSearchRequestDto.class),
              examples = @ExampleObject(
                  name = "SearchRequestExample",
                  value = """
                      {
                        "purchaseLink": "https://www.coupang.com/vp/products/1234567890"
                      }
                      """
              )))
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "판매처 조회 성공",
          useReturnTypeSchema = true,
          content = @Content(
              examples = @ExampleObject(
                  name = "SuccessExample",
                  value = """
                      {
                        "localDateTime": "2026-11-26T15:30:00.123",
                        "responseCode": 200,
                        "statusCode": "SUCCESS",
                        "message": "판매처 조회에 성공하였습니다.",
                        "data": {
                          "storeId": 1,
                          "storeName": "쿠팡"
                        }
                      }
                      """
              ))),
      @ApiResponse(responseCode = "400", description = "purchaseLink 가 없거나 공백",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = @ExampleObject(
                  name = "PurchaseLinkMissingExample",
                  value = """
                      {
                        "localDateTime": "2026-11-26T15:30:00.123",
                        "responseCode": 400,
                        "statusCode": "PURCHASE_LINK_MISSING",
                        "message": "상품 링크를 입력해주세요.",
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
      @ApiResponse(responseCode = "404", description = "매핑에 등록되지 않은 판매처",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = @ExampleObject(
                  name = "StoreNotFoundExample",
                  value = """
                      {
                        "localDateTime": "2026-11-26T15:30:00.123",
                        "responseCode": 404,
                        "statusCode": "STORE_NOT_FOUND",
                        "message": "해당 판매처를 찾을 수 없습니다.",
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
  ResponseDTO<StoreDto> searchByPurchaseLink(StoreSearchRequestDto storeSearchRequestDto);


  @Operation(
      summary = "판매처명으로 판매처 검색",
      description = """
          판매처명에 검색어가 포함된 판매처를 모두 조회합니다.
          상품 링크로 판매처를 찾지 못했을 때 사용자가 직접 선택할 수 있도록 사용합니다.
          keyword 를 생략하거나 공백만 넘기면 등록된 판매처 전체가 반환되고,
          일치하는 판매처가 없으면 404 가 아니라 빈 배열로 응답됩니다.
          """,
      parameters = {
          @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer 액세스 토큰",
              required = true, example = "Bearer eyJhbGciOiJIUzI1NiJ9..."),
          @Parameter(in = ParameterIn.QUERY, name = "keyword", description = "판매처명 검색어 (생략 시 전체 조회)",
              required = false, example = "네이버")
      })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "판매처 검색 성공",
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
                            "message": "판매처 검색에 성공하였습니다.",
                            "data": {
                              "stores": [
                                {
                                  "storeId": 5,
                                  "storeName": "네이버 스마트스토어"
                                },
                                {
                                  "storeId": 6,
                                  "storeName": "네이버쇼핑"
                                }
                              ]
                            }
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "EmptyResultExample",
                      value = """
                          {
                            "localDateTime": "2026-11-26T15:30:00.123",
                            "responseCode": 200,
                            "statusCode": "SUCCESS",
                            "message": "판매처 검색에 성공하였습니다.",
                            "data": {
                              "stores": []
                            }
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
  ResponseDTO<StoreResponseDto> searchByStoreName(String keyword);

}
