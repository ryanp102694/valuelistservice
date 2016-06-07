package com.pelletier.valuelist.adapter.jdbc;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pelletier.valuelist.DataAdapter;
import com.pelletier.valuelist.DefaultPagingInfo;
import com.pelletier.valuelist.DefaultValues;
import com.pelletier.valuelist.PagingInfo;
import com.pelletier.valuelist.Values;
import com.pelletier.valuelist.paging.PagingSupport;
import com.pelletier.valuelist.transformer.QueryParameterMapper;

/**
 * Default Jdbc implementation, which does have a dependency to Spring. 
 * 
 * Depends on sql, which is a SQL query to run, 
 * a NamedParameterJdbcTemplate, which allows Spring to map params with parameter names in the query,
 * a QueryParameterMapper, which is a way of pre-parameter mapping before Spring. It is suggested to use the
 * VelocityQueryParameterMapper, 
 * and optionally PagingSupport.
 * 
 * Note: If pagingSupport is null, everything is returned from the query, and the returned
 * pagingInfo is null.
 * 
 * @author Ryan Pelletier
 *
 */
public class DefaultJdbcDataAdapter implements DataAdapter<Map<String, Object>> {

	private String sql;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private QueryParameterMapper queryParameterMapper;
	private PagingSupport pagingSupport;

	@Override
	public Values<Map<String, Object>> query(Map<String, Object> params, PagingInfo pagingInfo) {
		
		//results to be returned to client
		List<Map<String, Object>> results = null;

		/*
		 * SQL after transformation
		 * 
		 * This is used to include sql based on the existence of a parameter, and also to 
		 * inject the values of parameters directly into the SQL without Spring.
		 */
		String sqlWithParams = queryParameterMapper.transform(sql, params);
		
		//need both paging support and pagingInfo to run paging
		if (pagingSupport != null && pagingInfo != null) {
			
			//create PagingInfo object to be returned to client
			DefaultPagingInfo defaultPagingInfo = new DefaultPagingInfo();
			defaultPagingInfo.setNumberPerPage(pagingInfo.getNumberPerPage());
			defaultPagingInfo.setPage(pagingInfo.getPage());
			defaultPagingInfo.setTotalCount(namedParameterJdbcTemplate.queryForObject(pagingSupport.getCountQuery(sqlWithParams), params, Integer.class));
			
			//get query necessary for paging
			String pagedQuery = pagingSupport.getPagedQuery(sqlWithParams, pagingInfo);
			
			//run paging query with query parameters
			results = namedParameterJdbcTemplate.queryForList(pagedQuery, params);
			
			return new DefaultValues(results, defaultPagingInfo);
		} else {
			results = namedParameterJdbcTemplate.queryForList(sqlWithParams, params);			
			//if they didn't do pagination, we don't return info about pagination
			return new DefaultValues(results, null);
		}

	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	public void setQueryParameterMapper(QueryParameterMapper queryParameterMapper) {
		this.queryParameterMapper = queryParameterMapper;
	}

	public void setPagingSupport(PagingSupport pagingSupport) {
		this.pagingSupport = pagingSupport;
	}

}
