package com.kibou.passport.cas.filter;

import org.springframework.core.Ordered;

/**
 * @author aimysaber@gmail.com
 *
 */
public class DefaultCasFilter extends FilterWrapperBean implements CasFilter, Ordered {

	private int order;

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return order;
	}
}
