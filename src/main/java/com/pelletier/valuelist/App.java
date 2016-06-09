package com.pelletier.valuelist;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Author: Ryan Pelletier
 *
 * Simple main class example for using ValueListService
 */
public class App {
	public static void main(String[] args) {
		
		final int PAGE_NUMBER = 1;
		final int NUMBER_PER_PAGE = 11;
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");
		ValueListService valueListService = (ValueListService) applicationContext.getBean("valueListService");

		Map<String, Object> queryParams = new HashMap<String, Object>();
//		queryParams.put("regex", ".*\\.xml");
//		queryParams.put("name", "desc");
//		queryParams.put("date", "asc");

		Values<Map<String,Object>> result = null;
		try {
			PagingInfo pagingInfo = new PagingInfo();
			pagingInfo.setNumberPerPage(NUMBER_PER_PAGE);
			pagingInfo.setPage(PAGE_NUMBER);
			
			result = (Values<Map<String,Object>>) valueListService.getValuesList("query", queryParams,pagingInfo);
			System.out.println(result);
			
		} catch (RuntimeException e) {	
			e.printStackTrace();
		}		
	}
}

