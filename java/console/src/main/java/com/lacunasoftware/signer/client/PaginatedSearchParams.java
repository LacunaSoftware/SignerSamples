package com.lacunasoftware.signer.client;


public class PaginatedSearchParams {
	private static final int DEFAULT_LIMIT = 10;

	private String q;
	private int limit = DEFAULT_LIMIT;
	private int offset;
	private PaginationOrders order;

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public PaginationOrders getOrder() {
		return order;
	}

	public void setOrder(PaginationOrders order) {
		this.order = order;
	}
}
