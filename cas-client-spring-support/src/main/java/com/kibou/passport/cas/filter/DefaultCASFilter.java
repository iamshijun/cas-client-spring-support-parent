package com.kibou.passport.cas.filter;

import org.springframework.core.Ordered;

public class DefaultCASFilter extends DelegateFilterWrapper implements CASFilter, Ordered {

	private int order;

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return order;
	}
}
