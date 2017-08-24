package com.kibou.passport.cas.filter;

import javax.servlet.Filter;

/**
 * 标识接口,为SecurityFilterChain注入 提供特定类型
 */
public interface CASFilter extends Filter{//extends Ordered?

}
