package com.mangmuganae.eolmago.domain.dealpost.docs;

import com.mangmuganae.eolmago.domain.dealpost.dto.DealPostDetailResponseDto;
import com.mangmuganae.eolmago.domain.dealpost.dto.DealPostReactionResponse;
import com.mangmuganae.eolmago.domain.dealpost.dto.DealPostDto;
import com.mangmuganae.eolmago.domain.dealpost.dto.DealPostSearchRequestDto;
import com.mangmuganae.eolmago.domain.dealpost.dto.DealPostSearchResponseDto;
import com.mangmuganae.eolmago.domain.dealpost.dto.DealPostUploadResponseDto;
import com.mangmuganae.eolmago.domain.dealpost.dto.DealPostVerificationResponseDto;
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
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "핫딜 관련 API", description = "핫딜 글을 업로드하고 3단계 검증을 수행합니다.")
public interface DealPostControllerSpecification {

  @Operation(
      summary = "도움돼요",
      description = """
          핫딜 글에 도움돼요를 남깁니다. 한 사용자는 글 하나에 반응을 하나만 남길 수 있습니다.

          같은 버튼을 다시 누르면 반응이 취소되고 reactionType 이 NONE 으로 내려갑니다.
          별로예요를 누른 상태에서 호출하면 도움돼요로 바뀌며 양쪽 카운트가 함께 조정됩니다.

          reactionType 은 이 호출 이후 사용자의 현재 반응 상태입니다.
          반응 결과로 핫딜 신뢰도 점수가 20 미만이 되면 글이 HOLD 상태로 바뀌어 목록 조회에서 사라집니다.
          """,
      parameters = {
          @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer 액세스 토큰",
              required = true, example = "Bearer eyJhbGciOiJIUzI1NiJ9..."),
          @Parameter(in = ParameterIn.PATH, name = "dealPostId", description = "반응을 남길 핫딜 글 ID",
              required = true, example = "1")
      })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "도움돼요 반영 성공",
          useReturnTypeSchema = true,
          content = @Content(
              examples = {
                  @ExampleObject(
                      name = "AppliedExample",
                      summary = "도움돼요를 남긴 경우",
                      value = """
                          {
                            "localDateTime": "2026-11-28T15:30:00.123",
                            "responseCode": 200,
                            "statusCode": "SUCCESS",
                            "message": "성공적으로 호출되었습니다",
                            "data": {
                              "reactionType": "LIKE",
                              "likeCount": 97,
                              "dislikeCount": 3
                            }
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "CanceledExample",
                      summary = "같은 버튼을 다시 눌러 취소한 경우",
                      value = """
                          {
                            "localDateTime": "2026-11-28T15:30:00.123",
                            "responseCode": 200,
                            "statusCode": "SUCCESS",
                            "message": "성공적으로 호출되었습니다",
                            "data": {
                              "reactionType": "NONE",
                              "likeCount": 96,
                              "dislikeCount": 3
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
                        "localDateTime": "2026-11-28T15:30:00.123",
                        "responseCode": 1001,
                        "statusCode": "ACCESS_TOKEN_INVALID",
                        "message": "엑세스 토큰이 유효하지 않습니다.",
                        "data": null
                      }
                      """
              ))),
      @ApiResponse(responseCode = "404", description = "핫딜 글 또는 사용자를 찾을 수 없음",
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
  ResponseDTO<DealPostReactionResponse> likeDealPost(CustomUserDetails userDetails, Long dealPostId);


  @Operation(
      summary = "별로예요",
      description = """
          핫딜 글에 별로예요를 남깁니다. 한 사용자는 글 하나에 반응을 하나만 남길 수 있습니다.

          같은 버튼을 다시 누르면 반응이 취소되고 reactionType 이 NONE 으로 내려갑니다.
          도움돼요를 누른 상태에서 호출하면 별로예요로 바뀌며 양쪽 카운트가 함께 조정됩니다.

          reactionType 은 이 호출 이후 사용자의 현재 반응 상태입니다.
          반응 결과로 핫딜 신뢰도 점수가 20 미만이 되면 글이 HOLD 상태로 바뀌어 목록 조회에서 사라집니다.
          """,
      parameters = {
          @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer 액세스 토큰",
              required = true, example = "Bearer eyJhbGciOiJIUzI1NiJ9..."),
          @Parameter(in = ParameterIn.PATH, name = "dealPostId", description = "반응을 남길 핫딜 글 ID",
              required = true, example = "1")
      })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "별로예요 반영 성공",
          useReturnTypeSchema = true,
          content = @Content(
              examples = {
                  @ExampleObject(
                      name = "AppliedExample",
                      summary = "별로예요를 남긴 경우",
                      value = """
                          {
                            "localDateTime": "2026-11-28T15:30:00.123",
                            "responseCode": 200,
                            "statusCode": "SUCCESS",
                            "message": "성공적으로 호출되었습니다",
                            "data": {
                              "reactionType": "DISLIKE",
                              "likeCount": 96,
                              "dislikeCount": 4
                            }
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "CanceledExample",
                      summary = "같은 버튼을 다시 눌러 취소한 경우",
                      value = """
                          {
                            "localDateTime": "2026-11-28T15:30:00.123",
                            "responseCode": 200,
                            "statusCode": "SUCCESS",
                            "message": "성공적으로 호출되었습니다",
                            "data": {
                              "reactionType": "NONE",
                              "likeCount": 96,
                              "dislikeCount": 3
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
                        "localDateTime": "2026-11-28T15:30:00.123",
                        "responseCode": 1001,
                        "statusCode": "ACCESS_TOKEN_INVALID",
                        "message": "엑세스 토큰이 유효하지 않습니다.",
                        "data": null
                      }
                      """
              ))),
      @ApiResponse(responseCode = "404", description = "핫딜 글 또는 사용자를 찾을 수 없음",
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
  ResponseDTO<DealPostReactionResponse> dislikeDealPost(CustomUserDetails userDetails, Long dealPostId);


  @Operation(
      summary = "핫딜 상세 조회",
      description = """
          핫딜 글 한 건의 상세 정보와 댓글 목록을 조회합니다.

          imageFileUrlList 는 글에 등록된 이미지 전체이며, 등록하지 않았으면 빈 배열입니다.
          userInfo 는 글쓴이 정보이고 imageFileUrl 은 프로필 이미지를 등록하지 않았으면 null 입니다.
          jjandolRank 의 표시 이름과 등급 순서는 JjandolRank 스키마를 참고하세요.

          userInfo.reactionType 은 글쓴이가 아니라 요청한 사용자가 이 글에 남긴 반응입니다.
          LIKE 면 도움돼요, DISLIKE 면 별로예요를 누른 상태이고, 아무것도 누르지 않았으면 NONE 입니다.
          상세에 들어갔을 때 도움돼요 / 별로예요 버튼의 선택 상태를 이 값으로 표시하면 됩니다.

          dealPostCommentResponseDto 에 댓글 목록과 댓글 수가 함께 내려갑니다.
          댓글이 없으면 comments 는 빈 배열이고 commentCount 는 0 입니다.

          상태와 무관하게 조회되므로 검증 전(HOLD) 이나 종료(ENDED) 된 글도 ID 를 알면 조회됩니다.
          조회해도 viewCount 는 증가하지 않습니다.
          """,
      parameters = {
          @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer 액세스 토큰",
              required = true, example = "Bearer eyJhbGciOiJIUzI1NiJ9..."),
          @Parameter(in = ParameterIn.PATH, name = "dealPostId", description = "조회할 핫딜 글 ID",
              required = true, example = "1")
      })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "핫딜 상세 조회 성공",
          useReturnTypeSchema = true,
          content = @Content(
              examples = @ExampleObject(
                  name = "SuccessExample",
                  value = """
                      {
                        "localDateTime": "2026-11-28T15:30:00.123",
                        "responseCode": 200,
                        "statusCode": "SUCCESS",
                        "message": "핫딜 상세 조회에 성공하였습니다.",
                        "data": {
                          "imageFileUrlList": [
                            "/files/8320405b-7667-4863-91a3-1e9903dc9c5f.jpg"
                          ],
                          "storeName": "쿠팡",
                          "productName": "신라면 멀티팩 40개입",
                          "productPrice": 25400,
                          "userInfo": {
                            "nickname": "짠돌이자취러",
                            "imageFileUrl": "/files/13b2b2ad-cbe8-4849-9724-ed296d82bd7d.png",
                            "jjandolRank": "BEGINNER",
                            "reactionType": "LIKE"
                          },
                          "createdAt": "2026-11-28T14:02:11.482",
                          "content": "로켓배송으로 샀어요. 쿠폰 중복 적용됩니다.",
                          "purchaseLink": "https://www.coupang.com/vp/products/1234567890",
                          "likeCount": 96,
                          "dislikeCount": 3,
                          "viewCount": 1350,
                          "dealPostCommentResponseDto": {
                            "comments": [
                              {
                                "userInfo": {
                                  "nickname": "절약왕",
                                  "imageFileUrl": null,
                                  "jjandolRank": "EXPERT"
                                },
                                "commentInfo": {
                                  "content": "이 가격이면 바로 사야죠",
                                  "createdAt": "2026-11-28T14:30:02.001"
                                }
                              }
                            ],
                            "commentCount": 1
                          }
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
                        "localDateTime": "2026-11-28T15:30:00.123",
                        "responseCode": 1001,
                        "statusCode": "ACCESS_TOKEN_INVALID",
                        "message": "엑세스 토큰이 유효하지 않습니다.",
                        "data": null
                      }
                      """
              ))),
      @ApiResponse(responseCode = "404", description = "핫딜 글을 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = @ExampleObject(
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
              ))),
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
  ResponseDTO<DealPostDetailResponseDto> getDealPostDetailById(Long dealPostId);

  @Operation(
      summary = "핫딜 목록 조회",
      description = """
          조건에 맞는 핫딜 목록을 페이지 단위로 조회합니다.
          검증을 통과하지 않은 글(HOLD)과 운영자가 거절한 글(REJECTED)은 조회되지 않습니다.

          ended 는 종료된 핫딜을 제외할지 여부입니다.
          기본값 false 면 진행중(ACTIVE) 과 종료(ENDED) 를 전부 조회하고, true 면 진행중만 조회합니다.

          hotDeal 을 true 로 주면 핫딜 필터가 적용되어 두 조건을 모두 만족하는 글만 조회됩니다.
          업로드자의 짠돌 등급이 중수(INTERMEDIATE) 이상이고, 핫딜 신뢰도 점수가 9 이하인 글입니다.

          thumbnailImageUrl 은 글에 등록된 이미지 중 가장 먼저 등록된 한 장입니다.
          이미지를 등록하지 않은 글은 null 이며, 판매처를 선택하지 않은 글은 storeName 이 null 입니다.

          uploaderRank 는 업로드자의 짠돌 등급입니다. 표시 이름과 등급 순서는 JjandolRank 스키마를 참고하세요.
          sortType=grade 와 hotDeal 필터의 등급 조건도 그 순서를 기준으로 동작합니다.
          """,
      parameters = {
          @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer 액세스 토큰",
              required = true, example = "Bearer eyJhbGciOiJIUzI1NiJ9..."),
          @Parameter(in = ParameterIn.QUERY, name = "category", description = "카테고리 ID (생략 시 전체)",
              required = false, example = "1"),
          @Parameter(in = ParameterIn.QUERY, name = "keyword", description = "상품명 검색어 (생략 시 전체)",
              required = false, example = "에어팟"),
          @Parameter(in = ParameterIn.QUERY, name = "ended", description = "true 면 종료된 핫딜 제외 (기본 false)",
              required = false, example = "false"),
          @Parameter(in = ParameterIn.QUERY, name = "hotDeal", description = "true 면 핫딜 필터 적용 (기본 false)",
              required = false, example = "false"),
          @Parameter(in = ParameterIn.QUERY, name = "sortType", description = "정렬 조건 (기본 latest)",
              required = false,
              schema = @Schema(type = "string", allowableValues = {"latest", "price", "grade"},
                  defaultValue = "latest")),
          @Parameter(in = ParameterIn.QUERY, name = "page", description = "조회할 페이지 번호 (0 부터 시작, 기본 0)",
              required = false, example = "0"),
          @Parameter(in = ParameterIn.QUERY, name = "size", description = "한 페이지 크기 (기본 20, 최대 50)",
              required = false, example = "20")
      })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "핫딜 목록 조회 성공",
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
                            "message": "핫딜 목록 조회에 성공하였습니다.",
                            "data": {
                              "dealPosts": [
                                {
                                  "dealPostId": 12,
                                  "storeName": "쿠팡",
                                  "productName": "에어팟 프로 2세대",
                                  "productPrice": 289000,
                                  "commentCount": 4,
                                  "likeCount": 31,
                                  "viewCount": 520,
                                  "uploaderRank": "EXPERT",
                                  "dealPostStatus": "ACTIVE",
                                  "thumbnailImageUrl": "/files/8320405b-7667-4863-91a3-1e9903dc9c5f.jpg",
                                  "createdAt": "2026-11-26T14:02:11.482"
                                },
                                {
                                  "dealPostId": 9,
                                  "storeName": null,
                                  "productName": "삼성 모니터 27인치",
                                  "productPrice": 159000,
                                  "commentCount": 0,
                                  "likeCount": 2,
                                  "viewCount": 88,
                                  "uploaderRank": "BEGINNER",
                                  "dealPostStatus": "ENDED",
                                  "thumbnailImageUrl": null,
                                  "createdAt": "2026-11-25T09:31:55.120"
                                }
                              ],
                              "page": 0,
                              "size": 20,
                              "totalElements": 2,
                              "totalPages": 1,
                              "hasNext": false
                            }
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "EmptyResultExample",
                      summary = "조건에 맞는 글이 없는 경우",
                      value = """
                          {
                            "localDateTime": "2026-11-26T15:30:00.123",
                            "responseCode": 200,
                            "statusCode": "SUCCESS",
                            "message": "핫딜 목록 조회에 성공하였습니다.",
                            "data": {
                              "dealPosts": [],
                              "page": 0,
                              "size": 20,
                              "totalElements": 0,
                              "totalPages": 0,
                              "hasNext": false
                            }
                          }
                          """
                  )
              })),
      @ApiResponse(responseCode = "400", description = "정렬 조건이 올바르지 않거나 파라미터 타입이 맞지 않음",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = {
                  @ExampleObject(
                      name = "InvalidDealPostSortTypeExample",
                      summary = "sortType 에 정의되지 않은 값을 보낸 경우",
                      value = """
                          {
                            "localDateTime": "2026-11-26T15:30:00.123",
                            "responseCode": 400,
                            "statusCode": "INVALID_DEAL_POST_SORT_TYPE",
                            "message": "정렬 조건이 올바르지 않습니다. latest, price, grade 중 하나를 사용해주세요.",
                            "data": null
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "InvalidInputValueExample",
                      summary = "category 에 숫자가 아닌 값을 보낸 경우",
                      value = """
                          {
                            "localDateTime": "2026-11-26T15:30:00.123",
                            "responseCode": 400,
                            "statusCode": "INVALID_INPUT_VALUE",
                            "message": "입력값이 유효하지 않습니다.",
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
  ResponseDTO<DealPostSearchResponseDto> search(DealPostSearchRequestDto dealPostSearchRequestDto);


  @Operation(
      summary = "핫딜 글 업로드",
      description = """
          핫딜 글을 업로드합니다. multipart/form-data 로 호출합니다.
          - request : 글 정보 JSON 파트입니다. (Content-Type: application/json)
          - files : 이미지 파일 파트입니다. 최대 10개까지 허용하며 생략할 수 있습니다.

          storeId 는 생략하거나 null 로 보낼 수 있습니다. 판매처를 식별하지 못한 글을 올릴 때 사용합니다.
          categoryId 는 반드시 등록된 카테고리여야 합니다.

          업로드된 글은 아직 검증 전이므로 HOLD 상태로 저장되며 목록 조회에 노출되지 않습니다.
          응답으로 받은 dealPostId 로 1~3단계 검증 API 를 차례로 호출해야 ACTIVE 로 전환됩니다.

          업로드 성공 시 HTTP 상태는 200 이고, 본문의 responseCode 가 201, statusCode 가 CREATED 입니다.

          파일 용량 제한(파일당 10MB, 요청 전체 100MB)을 넘기면 스프링이 처리하는 413 응답이 내려가며,
          이 응답만 공통 포맷을 따르지 않습니다.
          """,
      parameters = {
          @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer 액세스 토큰",
              required = true, example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
      })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "핫딜 글 업로드 성공 (본문 responseCode 는 201)",
          useReturnTypeSchema = true,
          content = @Content(
              examples = @ExampleObject(
                  name = "SuccessExample",
                  value = """
                      {
                        "localDateTime": "2026-11-26T15:30:00.123",
                        "responseCode": 201,
                        "statusCode": "CREATED",
                        "message": "핫딜 글 업로드에 성공하였습니다.",
                        "data": {
                          "dealPostId": 12
                        }
                      }
                      """
              ))),
      @ApiResponse(responseCode = "400", description = "이미지 개수 초과",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = @ExampleObject(
                  name = "ImageFileCountExceededExample",
                  value = """
                      {
                        "localDateTime": "2026-11-26T15:30:00.123",
                        "responseCode": 400,
                        "statusCode": "IMAGE_FILE_COUNT_EXCEEDED",
                        "message": "이미지는 최대 10개까지 업로드 가능합니다.",
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
      @ApiResponse(responseCode = "404", description = "사용자 / 카테고리 / 판매처를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = {
                  @ExampleObject(
                      name = "CategoryNotFoundExample",
                      summary = "등록되지 않은 categoryId 를 보낸 경우",
                      value = """
                          {
                            "localDateTime": "2026-11-26T15:30:00.123",
                            "responseCode": 404,
                            "statusCode": "CATEGORY_NOT_FOUND",
                            "message": "해당 카테고리를 찾을 수 없습니다.",
                            "data": null
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "StoreNotFoundExample",
                      summary = "등록되지 않은 storeId 를 보낸 경우",
                      value = """
                          {
                            "localDateTime": "2026-11-26T15:30:00.123",
                            "responseCode": 404,
                            "statusCode": "STORE_NOT_FOUND",
                            "message": "해당 판매처를 찾을 수 없습니다.",
                            "data": null
                          }
                          """
                  ),
                  @ExampleObject(
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
                  )
              })),
      @ApiResponse(responseCode = "500", description = "이미지 저장 실패 또는 서버 내부 오류",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = {
                  @ExampleObject(
                      name = "FileUploadFailedExample",
                      summary = "이미지 저장에 실패한 경우 - 재시도 가능",
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
  ResponseDTO<DealPostUploadResponseDto> upload(DealPostDto dealPostDto, List<MultipartFile> files,
      CustomUserDetails userDetails);


  @Operation(
      summary = "1단계 검증 - 제휴/추천인/리워드 링크 여부",
      description = """
          구매 링크의 리다이렉트를 끝까지 따라간 최종 URL 이 제휴(파트너스) / 추천인 / 리워드 링크인지 검증합니다.
          제휴 전용 호스트(link.coupang.com, amzn.to, linkprice 등)이거나 제휴·추천인 코드를 실어 보내는
          쿼리 파라미터(ref, aff_id, lptag …), utm_medium=affiliate 계열이면 탈락합니다.
          리다이렉트 추적에 실패하면 원본 링크를 그대로 검증 대상으로 사용합니다.

          탈락은 예외가 아니라 200 응답의 passed=false 로 내려갑니다.
          통과 시에만 linkVerified 가 true 로 바뀌며, 3단계를 모두 통과하면 상태가 HOLD 에서 ACTIVE 로 전환됩니다.
          본인이 업로드한 글만 검증할 수 있습니다.
          """,
      parameters = {
          @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer 액세스 토큰",
              required = true, example = "Bearer eyJhbGciOiJIUzI1NiJ9..."),
          @Parameter(in = ParameterIn.PATH, name = "dealPostId", description = "검증할 핫딜 글 ID",
              required = true, example = "1")
      })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "검증 수행 완료 (통과/탈락은 passed 로 판단)",
          useReturnTypeSchema = true,
          content = @Content(
              examples = {
                  @ExampleObject(
                      name = "PassedExample",
                      summary = "통과 - 제휴 링크가 아님",
                      value = """
                          {
                            "localDateTime": "2026-11-26T15:30:00.123",
                            "responseCode": 200,
                            "statusCode": "SUCCESS",
                            "message": "링크 검증을 완료하였습니다.",
                            "data": {
                              "dealPostId": 1,
                              "passed": true,
                              "dealPostStatus": "HOLD"
                            }
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "FailedExample",
                      summary = "탈락 - 제휴 링크로 판별됨",
                      value = """
                          {
                            "localDateTime": "2026-11-26T15:30:00.123",
                            "responseCode": 200,
                            "statusCode": "SUCCESS",
                            "message": "링크 검증을 완료하였습니다.",
                            "data": {
                              "dealPostId": 1,
                              "passed": false,
                              "dealPostStatus": "HOLD"
                            }
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "AllVerifiedExample",
                      summary = "통과 - 3단계가 모두 끝나 ACTIVE 로 전환됨",
                      value = """
                          {
                            "localDateTime": "2026-11-26T15:30:00.123",
                            "responseCode": 200,
                            "statusCode": "SUCCESS",
                            "message": "링크 검증을 완료하였습니다.",
                            "data": {
                              "dealPostId": 1,
                              "passed": true,
                              "dealPostStatus": "ACTIVE"
                            }
                          }
                          """
                  )
              })),
      @ApiResponse(responseCode = "400", description = "글의 구매 링크가 비어 있어 검증할 수 없음",
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
      @ApiResponse(responseCode = "403", description = "본인이 업로드한 글이 아님",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = @ExampleObject(
                  name = "AccessDeniedExample",
                  value = """
                      {
                        "localDateTime": "2026-11-26T15:30:00.123",
                        "responseCode": 403,
                        "statusCode": "ACCESS_DENIED",
                        "message": "이 기능에 접근할 수 없습니다.",
                        "data": null
                      }
                      """
              ))),
      @ApiResponse(responseCode = "404", description = "핫딜 글 또는 사용자를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = {
                  @ExampleObject(
                      name = "PostNotFoundExample",
                      value = """
                          {
                            "localDateTime": "2026-11-26T15:30:00.123",
                            "responseCode": 404,
                            "statusCode": "POST_NOT_FOUND",
                            "message": "해당 게시글을 찾을 수 없습니다.",
                            "data": null
                          }
                          """
                  ),
                  @ExampleObject(
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
                  )
              })),
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
  ResponseDTO<DealPostVerificationResponseDto> verifyLink(Long dealPostId,
      CustomUserDetails userDetails);


  @Operation(
      summary = "2단계 검증 - 업로드자 신뢰도",
      description = """
          글을 올린 사용자의 신뢰도 점수로 검증합니다.
          누적 업로드 글이 3개 미만이면 표본이 부족하다고 보아 검증에서 제외되어 그대로 통과 처리됩니다.
          3개 이상이면 사용자 신뢰도 점수가 0.4 를 초과해야 통과합니다.

          탈락은 예외가 아니라 200 응답의 passed=false 로 내려갑니다.
          통과 시에만 reliabilityVerified 가 true 로 바뀌며, 3단계를 모두 통과하면 상태가 ACTIVE 로 전환됩니다.
          본인이 업로드한 글만 검증할 수 있습니다.
          """,
      parameters = {
          @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer 액세스 토큰",
              required = true, example = "Bearer eyJhbGciOiJIUzI1NiJ9..."),
          @Parameter(in = ParameterIn.PATH, name = "dealPostId", description = "검증할 핫딜 글 ID",
              required = true, example = "1")
      })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "검증 수행 완료 (통과/탈락은 passed 로 판단)",
          useReturnTypeSchema = true,
          content = @Content(
              examples = {
                  @ExampleObject(
                      name = "PassedExample",
                      summary = "통과 - 신뢰도 충족 또는 글 3개 미만으로 검증 제외",
                      value = """
                          {
                            "localDateTime": "2026-11-26T15:30:00.123",
                            "responseCode": 200,
                            "statusCode": "SUCCESS",
                            "message": "업로드자 신뢰도 검증을 완료하였습니다.",
                            "data": {
                              "dealPostId": 1,
                              "passed": true,
                              "dealPostStatus": "HOLD"
                            }
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "FailedExample",
                      summary = "탈락 - 신뢰도 점수 미달",
                      value = """
                          {
                            "localDateTime": "2026-11-26T15:30:00.123",
                            "responseCode": 200,
                            "statusCode": "SUCCESS",
                            "message": "업로드자 신뢰도 검증을 완료하였습니다.",
                            "data": {
                              "dealPostId": 1,
                              "passed": false,
                              "dealPostStatus": "HOLD"
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
      @ApiResponse(responseCode = "403", description = "본인이 업로드한 글이 아님",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = @ExampleObject(
                  name = "AccessDeniedExample",
                  value = """
                      {
                        "localDateTime": "2026-11-26T15:30:00.123",
                        "responseCode": 403,
                        "statusCode": "ACCESS_DENIED",
                        "message": "이 기능에 접근할 수 없습니다.",
                        "data": null
                      }
                      """
              ))),
      @ApiResponse(responseCode = "404", description = "핫딜 글 또는 사용자를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = {
                  @ExampleObject(
                      name = "PostNotFoundExample",
                      value = """
                          {
                            "localDateTime": "2026-11-26T15:30:00.123",
                            "responseCode": 404,
                            "statusCode": "POST_NOT_FOUND",
                            "message": "해당 게시글을 찾을 수 없습니다.",
                            "data": null
                          }
                          """
                  ),
                  @ExampleObject(
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
                  )
              })),
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
  ResponseDTO<DealPostVerificationResponseDto> verifyReliability(Long dealPostId,
      CustomUserDetails userDetails);


  @Operation(
      summary = "3단계 검증 - 광고글 여부",
      description = """
          상품명과 본문을 합친 텍스트를 광고 분류 모델(AWS Lambda) 로 보내 광고글 여부를 검증합니다.
          광고일 확률이 0.5 이상이면 탈락합니다.

          탈락은 예외가 아니라 200 응답의 passed=false 로 내려갑니다.
          통과 시에만 advertisementVerified 가 true 로 바뀌며, 3단계를 모두 통과하면 상태가 ACTIVE 로 전환됩니다.
          분류 모델 호출 자체가 실패하면 서버 내부 오류와 구분되는 AD_CLASSIFICATION_FAILED 로 500 응답되며,
          이 경우 재시도하면 됩니다.
          본인이 업로드한 글만 검증할 수 있습니다.
          """,
      parameters = {
          @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer 액세스 토큰",
              required = true, example = "Bearer eyJhbGciOiJIUzI1NiJ9..."),
          @Parameter(in = ParameterIn.PATH, name = "dealPostId", description = "검증할 핫딜 글 ID",
              required = true, example = "1")
      })
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "검증 수행 완료 (통과/탈락은 passed 로 판단)",
          useReturnTypeSchema = true,
          content = @Content(
              examples = {
                  @ExampleObject(
                      name = "PassedExample",
                      summary = "통과 - 광고글로 판별되지 않음",
                      value = """
                          {
                            "localDateTime": "2026-11-26T15:30:00.123",
                            "responseCode": 200,
                            "statusCode": "SUCCESS",
                            "message": "광고글 여부 검증을 완료하였습니다.",
                            "data": {
                              "dealPostId": 1,
                              "passed": true,
                              "dealPostStatus": "ACTIVE"
                            }
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "FailedExample",
                      summary = "탈락 - 광고글로 판별됨",
                      value = """
                          {
                            "localDateTime": "2026-11-26T15:30:00.123",
                            "responseCode": 200,
                            "statusCode": "SUCCESS",
                            "message": "광고글 여부 검증을 완료하였습니다.",
                            "data": {
                              "dealPostId": 1,
                              "passed": false,
                              "dealPostStatus": "HOLD"
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
      @ApiResponse(responseCode = "403", description = "본인이 업로드한 글이 아님",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = @ExampleObject(
                  name = "AccessDeniedExample",
                  value = """
                      {
                        "localDateTime": "2026-11-26T15:30:00.123",
                        "responseCode": 403,
                        "statusCode": "ACCESS_DENIED",
                        "message": "이 기능에 접근할 수 없습니다.",
                        "data": null
                      }
                      """
              ))),
      @ApiResponse(responseCode = "404", description = "핫딜 글 또는 사용자를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = {
                  @ExampleObject(
                      name = "PostNotFoundExample",
                      value = """
                          {
                            "localDateTime": "2026-11-26T15:30:00.123",
                            "responseCode": 404,
                            "statusCode": "POST_NOT_FOUND",
                            "message": "해당 게시글을 찾을 수 없습니다.",
                            "data": null
                          }
                          """
                  ),
                  @ExampleObject(
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
                  )
              })),
      @ApiResponse(responseCode = "500", description = "광고 분류 모델 호출 실패 또는 서버 내부 오류",
          content = @Content(schema = @Schema(implementation = ResponseDTO.class),
              examples = {
                  @ExampleObject(
                      name = "AdClassificationFailedExample",
                      summary = "광고 분류 람다 호출 실패 - 재시도 가능",
                      value = """
                          {
                            "localDateTime": "2026-11-26T15:30:00.123",
                            "responseCode": 500,
                            "statusCode": "AD_CLASSIFICATION_FAILED",
                            "message": "광고글 판별에 실패했습니다. 잠시 후 다시 시도해주세요.",
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
  ResponseDTO<DealPostVerificationResponseDto> verifyAdvertisement(Long dealPostId,
      CustomUserDetails userDetails);

}
