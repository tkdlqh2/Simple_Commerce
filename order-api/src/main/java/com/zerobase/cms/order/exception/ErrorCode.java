package com.zerobase.cms.order.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    NOT_FOUND_ITEM(HttpStatus.BAD_REQUEST,"아이템을 찾을 수 없습니다."),
    SAME_ITEM_NAME(HttpStatus.BAD_REQUEST,"아이템 명 중복입니다."),
    NOT_FOUND_PRODUCT(HttpStatus.BAD_REQUEST,"상품을 찾을 수 없습니다."),
    CART_CHANGE_FAIL(HttpStatus.BAD_REQUEST,"장바구니에 추가할 수 없습니다."),
    ITEM_COUNT_NOT_ENOUGH(HttpStatus.BAD_REQUEST,"상품의 수량이 부족합니다."),
    ORDER_FAIL_CHECK_CART(HttpStatus.BAD_REQUEST,"주문 불가! 장바구니를 확인해주세요."),
    ORDER_FAIL_NO_MONEY(HttpStatus.BAD_REQUEST,"주문 불가! 잔액 부족입니다.");

    private final HttpStatus httpStatus;
    private final String detail;
}
