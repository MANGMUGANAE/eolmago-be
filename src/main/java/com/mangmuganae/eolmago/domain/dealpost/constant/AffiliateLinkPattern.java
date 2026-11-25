package com.mangmuganae.eolmago.domain.dealpost.constant;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * 제휴(파트너스) / 추천인 / 리워드 링크를 판별하기 위한 패턴 모음입니다.
 * DB 를 사용하지 않고 상수로 관리합니다.
 */
public final class AffiliateLinkPattern {

  /**
   * 제휴 링크 전용으로 운영되는 호스트입니다.
   * 서브도메인까지 포함해 비교하므로 일반 상품 링크(www.coupang.com/vp/products/...)는 걸러지지 않습니다.
   */
  private static final Set<String> AFFILIATE_HOSTS = Set.of(
      //쿠팡 파트너스
      "link.coupang.com",
      "coupa.ng",
      //알리익스프레스 제휴
      "s.click.aliexpress.com",
      "star.aliexpress.com",
      //아마존 어소시에이트
      "amzn.to",
      //테무 초대/제휴
      "temu.to",
      //토스쇼핑 쉐어링크 (상품 공유 수익 링크)
      "sharelink.toss.im",
      //국내외 CPS 제휴 중계사
      "click.linkprice.com",
      "linkprice.com",
      "click.linksynergy.com",
      "shareasale.com",
      "go.redirectingat.com",
      "prf.hn",
      "awin1.com",
      "dpbolvw.net",
      "anrdoezrs.net",
      "tkqlhce.com",
      "jdoqocy.com",
      "kqzyfj.com"
  );

  /**
   * 제휴 코드 / 추천인 코드 / 리워드 코드를 실어 보내는 쿼리 파라미터 키입니다.
   */
  private static final Set<String> AFFILIATE_QUERY_PARAMETERS = Set.of(
      //쿠팡 파트너스
      "lptag",
      "subid",
      "subparam",
      //알리익스프레스
      "aff_short_key",
      "aff_trace_key",
      "aff_platform",
      "aff_fcid",
      "aff_fsk",
      "aff_request_id",
      //아마존
      "ascsubtag",
      //범용 제휴
      "aff",
      "aff_id",
      "affid",
      "affiliate",
      "affiliate_id",
      "affiliateid",
      "partner",
      "partner_id",
      "partnerid",
      "clickid",
      "click_id",
      "irclickid",
      "ranmid",
      //추천인 / 초대 / 리워드
      "ref",
      "referrer",
      "referral",
      "referralcode",
      "referral_code",
      "invite",
      "invitecode",
      "invite_code",
      "recommender",
      "reward",
      "rewardcode"
  );

  /**
   * utm_medium 은 일반 마케팅에도 쓰이므로 키가 아니라 값으로 판별합니다.
   */
  private static final Set<String> AFFILIATE_UTM_MEDIUMS = Set.of(
      "affiliate",
      "affiliates",
      "referral",
      "partner",
      "cps"
  );

  private AffiliateLinkPattern() {
  }

  /**
   * 리다이렉트를 모두 따라간 최종 URL 을 받아 제휴 / 추천인 / 리워드 링크인지 판별합니다.
   */
  public static boolean isAffiliateLink(String url) {

    if (url == null || url.isBlank()) {
      return false;
    }

    URI uri;
    try {
      uri = URI.create(url.trim());
    } catch (IllegalArgumentException e) {
      return false;
    }

    return isAffiliateHost(uri.getHost()) || hasAffiliateQueryParameter(uri.getRawQuery());
  }

  private static boolean isAffiliateHost(String host) {

    if (host == null) {
      return false;
    }

    String normalizedHost = host.toLowerCase();

    return AFFILIATE_HOSTS.stream()
        .anyMatch(affiliateHost -> normalizedHost.equals(affiliateHost)
            || normalizedHost.endsWith("." + affiliateHost));
  }

  private static boolean hasAffiliateQueryParameter(String rawQuery) {

    if (rawQuery == null || rawQuery.isBlank()) {
      return false;
    }

    for (String parameter : rawQuery.split("&")) {

      int separatorIndex = parameter.indexOf('=');
      String key = (separatorIndex == -1 ? parameter : parameter.substring(0, separatorIndex))
          .toLowerCase();

      if (AFFILIATE_QUERY_PARAMETERS.contains(key)) {
        return true;
      }

      if ("utm_medium".equals(key) && separatorIndex != -1) {
        String value = URLDecoder
            .decode(parameter.substring(separatorIndex + 1), StandardCharsets.UTF_8)
            .toLowerCase();

        if (AFFILIATE_UTM_MEDIUMS.contains(value)) {
          return true;
        }
      }
    }

    return false;
  }

}
