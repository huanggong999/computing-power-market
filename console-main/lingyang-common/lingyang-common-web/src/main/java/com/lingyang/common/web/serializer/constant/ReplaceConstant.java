package com.lingyang.common.web.serializer.constant;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/4/15 16:55
 */
public interface ReplaceConstant {
   String ID_CARD_REPLACE = "(\\d{4})\\d{10}(\\w{4})";
   String EMAIL_REPLACE = "(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)";
   String PHONE_REPLACE = "(\\d{3})\\d{4}(\\d{4})";
}
