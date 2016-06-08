package com.pelletier.valuelist.paging;

import com.pelletier.valuelist.PagingInfo;

/**
 * 
 * 	Provides pagination support for Oracle Databases
 * 
 *  Pre and post SQL can be injected, note that post SQL must have place-holders pageNumber and numberPerPage
 * 
 *  Note: This hasn't been tested.
 * 
 * 
 * @author Ryan Pelletier
 *
 */


public class OraclePagingSupport implements PagingSupport {

	@Override
	public String getCountQuery(String query) {
		return "SELECT count(*) FROM (" + query + ")";
	}

		
	//I think this query string can be simplified, but I would like to double check with Matt.
	@Override
	public String getPagedQuery(String query, PagingInfo pagingInfo) {
    	return "SELECT * FROM (SELECT INNER.*, ROWNUM as RECORDNUM FROM (" + 
				query + 
				String.format(") INNER ) WRAPPED WHERE WRAPPED.RECORDNUM BETWEEN ((%1$s-1)*%2$s+1) AND (%1$s)*%2$s", new Integer(pagingInfo.getPage()).toString(),new Integer(pagingInfo.getNumberPerPage()).toString());
	}

}
