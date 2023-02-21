package org.tcollignon.user.utils;

import java.util.Collection;
import java.util.Map;

public class QueryUtils {

    public static String addCriteria(String query, Map<String, Object> params, String criteria, Object value) {
        return addCriteria(query, params, criteria, value, null);
    }

    public static String addCriteria(String query, Map<String, Object> params, String criteria, Object value, String alias) {
        if (value != null) {
            query = addQueryAnd(query);
            if (alias != null) {
                query += alias + ".";
            }
            query += criteria + "=:" + getCriteriaKey(criteria);
            params.put(getCriteriaKey(criteria), value);
        }
        return query;
    }

    public static String addClauseInCriteria(String query, Map<String, Collection> params, String criteria, Collection value, String alias) {
        if (value != null) {
            query = addQueryAnd(query);
            if (alias != null) {
                query += alias + ".";
            }
            query += criteria + " IN (:" + getCriteriaKey(criteria) + ")";
            params.put(getCriteriaKey(criteria), value);
        }
        return query;
    }

    public static String addLikeCriteria(String query, Map<String, Object> params, String criteria, String value) {
        return addLikeCriteria(query, params, criteria, value, null);
    }

    public static String addLikeCriteria(String query, Map<String, Object> params, String criteria, String value, String alias) {
        if (value != null) {
            query = addQueryAnd(query);
            query += " lower(";
            if (alias != null) {
                query += alias + ".";
            }
            query += criteria + ") like :" + getCriteriaKey(criteria);
            value = value.replaceAll("\\*", "");
            value = "%" + value + "%";
            params.put(getCriteriaKey(criteria), value.toLowerCase());
        }
        return query;
    }

    public static String addQueryAnd(String query) {
        if (!query.isEmpty()) {
            if (query.contains("select") && !query.contains("where")) {
                query += " where ";
            } else {
                query += " and ";
            }
        }
        return query;
    }

    private static String getCriteriaKey(String criteria) {
        return criteria.split("\\.")[0];
    }
}
