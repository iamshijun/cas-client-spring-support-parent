package com.kibou.passport.cas.filter;

import org.springframework.core.Ordered;

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