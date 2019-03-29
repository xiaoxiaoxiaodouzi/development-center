package com.chinacreator.c2.appraisals.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.chinacreator.c2.dao.mybatis.enhance.Order;
import com.chinacreator.c2.dao.mybatis.enhance.Sortable;

/**
 * 排序工具类
 * 
 * @author 彭盛
 * 
 */
public class SortableUtil {

	private static Pattern pattern = Pattern.compile("[\\+|\\-]{1}\\w+");

	/**
	 * 通过排序字符串，生成排序对象
	 * 
	 * @param sort
	 *            排序字符串 
	 * @return 排序对象
	 */
	public static Sortable buildSortable(String sort) {

		Sortable sortable = null;
		if (StringUtils.isBlank(sort)) {
			return sortable;
		}
		sortable = SortableUtil.originBuiderSortable(sort);
		return sortable;
	}

	/**
	 * 
	 * @param order
	 * @return
	 */
	private static Sortable originBuiderSortable(String order) {
		Sortable sortable = null;
		if (StringUtils.isNotBlank(order)) {
			try {
				List<Order> orderList = new ArrayList<Order>();
				Matcher matcher = pattern.matcher(order);
				while (matcher.find()) {
					String group = matcher.group();
					if (StringUtils.isNotBlank(group)) {
						if (group.startsWith("+")) {
							Order o = new Order(group.substring(1), "asc");
							orderList.add(o);
						} else if (group.startsWith("-")) {
							Order o = new Order(group.substring(1), "desc");
							orderList.add(o);
						}
					}
				}
				if( orderList != null && !orderList.isEmpty() ){
					sortable = new Sortable( orderList.toArray( new Order[orderList.size()]) );
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sortable;
	}
}
