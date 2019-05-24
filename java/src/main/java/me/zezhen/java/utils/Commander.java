package me.zezhen.java.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;

public class Commander {

    public final static String ORDER_BY = "orderby";
    public final static String GROUP_BY = "groupby";
    public final static String DESC = "desc";
    public final static String LIMIT = "limit";
    public final static String SUM = "sum";
    public final static String SELECT = "select";
    public final static String MERGE = "merge";
    public final static String FILTER = "filter";
    public final static Set<String> POST_ACTION = Sets.newHashSet();
    static {
        POST_ACTION.add(ORDER_BY);
        POST_ACTION.add(GROUP_BY);
        POST_ACTION.add(DESC); // default is asc
        POST_ACTION.add(LIMIT);
        POST_ACTION.add(SUM);
        POST_ACTION.add(FILTER);
        POST_ACTION.add(SELECT);
    }

    private final static Set<String> filterOperations = Sets.newHashSet();
    static {
        filterOperations.add("eq");
        filterOperations.add("ne");
        filterOperations.add("lt");
        filterOperations.add("le");
        filterOperations.add("gt");
        filterOperations.add("ge");
    }

    private static Logger LOG = LoggerFactory.getLogger(Commander.class);

    public final static String ERROR_PREFIX = "[ERROR]";
    public final static String UNIQ_PREFIX = "uniq";
    public final static String COMPONENT_PREFIX = "comp";
    private static Gson gson = new Gson();

    public static Object query(Object object, String component, int index, String args) {
        Pair<String, String> query = parseArgument(args);
        if(query == null) {
            LOG.error("wrong query: {}", args);
            return null;
        }
        String field = query.getLeft();
        MultiMap<String> params = parseParameter(query.getRight());

        MultiMap<String> holder = new MultiMap<String>();
        if (params != null) {
            if ("true".equals(params.getString(UNIQ_PREFIX)) && index != 0
                    || params.containsKey(COMPONENT_PREFIX)
                    && !component.endsWith(params.getString(COMPONENT_PREFIX))) {
                // error(LOG, "not execute because uniq=true and not the task 0 or not the choosen component");
                return null;
            }
            params.remove(UNIQ_PREFIX);
            params.remove(COMPONENT_PREFIX);
            for (String action : POST_ACTION) {
                if (params.containsKey(action)) {
                    holder.put(action, params.get(action));
                    params.remove(action);
                }
            }
        }
        Object result = null;

        boolean isField = false;
        try {
            if (params == null || params.isEmpty() && object.getClass().getDeclaredField(field) != null) {
                isField = true;
            }
        } catch (Exception e) {
        }

        if (isField) { // field
            Object value = queryField(object, field);
            Map<String, Object> ret = Maps.newHashMap();
            ret.put("result", value);
            ret.put("component", component);
            result = ret;
        } else { // method w/ params
            params.addAllValues(holder);
            result = executeMethod(object, field, params);
            result = Commander.process(result, params, false);
            if (result instanceof Double) {
                List<Object> l = Lists.newArrayList();
                Map<String, Object> m = Maps.newHashMap();
                String key = params.containsKey(SUM) ? key = params.getString(SUM) : "sum";
                m.put(key, result);
                l.add(m);
                result = l;
            }
        }
        return result;
    }

    /**
     * Parses input string, which represents parameter lists, to separated strings where each one pertains a name-value
     * pair. The input should comply with the url parameter query format.
     * 
     * @param parameters
     *            Input string representing a parameter list.
     * @return Separated parameters, will be <code>null</code> if input is invalid or <code>null</code>.
     */
    public static MultiMap<String> parseParameter(String parameters) {
        if (parameters == null) {
            return null;
        }

        MultiMap<String> params = new MultiMap<String>();
        UrlEncoded.decodeTo(parameters, params, "UTF-8", 1000);
        return params;
    }

    /**
     * Parses input string into field/method name and, if applicable, parameter list. Valid input string format can be
     * one of the belows
     * 
     * <pre>
     * field-name
     * method-name:
     * method-name:parameter-list
     * </pre>
     * 
     * While, valid parameter list is valid if only complies with the following format
     * 
     * <pre>
     * param_0=value_0&amp;param_1=value_1 ...
     * </pre>
     * 
     * @param arguments
     *            Input string representing a query.
     * @return String pair comprising of field/method name and parameter list. The paramter list is <code>null</code> if
     *         the input string represents a field; empty string if the input is a method without parameter. Return
     *         value will be <code>null</code> if input is ill-formatted.
     */
    public static Pair<String, String> parseArgument(String arguments) {
        if (arguments == null) {
            return null;
        }

        // Adds one space for method-without-parameter type recognition.
        String[] args = (arguments + " ").split("\\:");

        if (args.length == 1) {

            // Field.
            return Pair.of(args[0].trim(), null);
        } else if (args.length == 2) {

            // Method. Before trimming, the second string contains one space if the actual parameter list is empty.
            return Pair.of(args[0].trim(), args[1].trim());
        }
        return null;
    }

    public static String toJsonString(Object object) {
        return gson.toJson(object);
    }

    private static Object executeMethod(Object object, String methodName, MultiMap<String> map) {
        try {
            Object result = null;
            Method method = object.getClass().getDeclaredMethod(methodName, MultiMap.class);
            if (method != null) {
                method.setAccessible(true);
                result = method.invoke(object, map);
                if (result == null && method.getReturnType().getName().equals("void")) {
                    result = "successfully execute void method " + methodName;
                }
            }
            if (result != null) {
                return result;
            } else {
                return warn(LOG, "can not query result from method {} w/o params {}", methodName,
                        map.toString());
            }
        } catch(NoSuchMethodException e) {
            return warn(LOG, "NoSuchMethodException {}.", methodName);
        } catch (Exception e) {
            return warn(LOG, "execute method {} error.", methodName, e);
        }
    }

    private static String queryField(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object o = field.get(object);
            if (o != null) {
                return o.toString();
            }
        } catch (Exception e) {
        }
        return warn(LOG, "query field {} in object {}.", fieldName, object.getClass());
    }

    public static Object process(Object draft, String args, boolean global) {
        LOG.info("process its class is {}, args is {}", draft.getClass().getName(), args);
        MultiMap<String> actions = Commander.parseParameter(Commander.parseArgument(args).getRight());
        return process(draft, actions, global);
    }

    public static Object process(Object draft, MultiMap<String> actions, boolean global) {
        if (actions == null || (draft instanceof String) && draft.toString().startsWith(ERROR_PREFIX)) {
            return draft;
        }

        if (actions.containsKey(FILTER)) {
            LOG.info("filter by {}.", actions.getString(FILTER));
            draft = filter((List<Object>) draft, actions.get(FILTER));
        }

        if (actions.containsKey(GROUP_BY)) {

            Set<String> mergeKeys = Sets.newHashSet();
            if (actions.containsKey(MERGE)) {
                mergeKeys.addAll(actions.get(MERGE));
            }
            if (checkGroupMerge(draft, mergeKeys)) {
                LOG.info("group by {} and merge {}", actions.get(GROUP_BY), actions.get(MERGE));
                List<String> groupKeys = actions.get(GROUP_BY);
                List<List<Map<String, Object>>> groupList = Lists.newArrayList();
                groupList.add((List<Map<String, Object>>) draft);
                for (String groupKey : groupKeys) {
                    List<List<Map<String, Object>>> hold = Lists.newArrayList();
                    for (List<Map<String, Object>> ls : groupList) {
                        List<List<Map<String, Object>>> lst = groupBy(ls, groupKey);
                        hold.addAll(lst);
                    }
                    groupList.clear();
                    groupList.addAll(hold);
                    hold.clear();
                }
                draft = groupList;

                if (actions.containsKey(MERGE)) {
                    mergeKeys.addAll(actions.get(MERGE));
                    List<Map<String, Object>> result = Lists.newArrayList();
                    for (List<Map<String, Object>> ls : groupList) {
                        Map<String, Object> r = merge(ls, mergeKeys);
                        result.add(r);
                    }
                    draft = result;
                }
            }
        }

        if (global && actions.containsKey(SELECT)) {
            LOG.info("select {}.", actions.getString(SELECT));
            Set<String> selectKeys = Sets.newHashSet(actions.get(SELECT));
            draft = select((List<Object>) draft, selectKeys);
        }

        if (actions.containsKey(ORDER_BY) && checkOrderBy(draft, actions.getString(ORDER_BY))) {
            LOG.info("order by {}.", actions.getString(ORDER_BY));
            Collections.sort((List) draft, new MapCompatator(actions.getString(ORDER_BY), actions.getString(DESC)));
        }

        if (actions.containsKey(LIMIT)) {
            String limit = actions.getString(LIMIT);
            LOG.info("limit is {}", limit);
            try {
                List<Object> tmp = limit((List) draft, Integer.parseInt(limit));
                draft = tmp;
            } catch (Exception e) {
                LOG.error("limit must be a integer. {}", e);
            }
        }

        if (actions.containsKey(SUM)) {
            List<String> sumKeys = actions.get(SUM);
            LOG.info("execute sum action, key is {}.", sumKeys);

            List<Map<String, Object>> ret = Lists.newArrayList();
            for (String key : sumKeys) {
                Map<String, Object> map = Maps.newHashMap();
                double value = sum((List<Map<String, Object>>) draft, key);
                map.put(key, value);
                ret.add(map);
            }
            return ret;
        }

        return draft;
    }

    private static List<Object> filter(List<Object> list, List<String> filters) {
        Iterator<Object> iter = list.iterator();
        while (iter.hasNext()) {
            Object e = iter.next();
            if (!(e instanceof Map && processFilter((Map<String, Object>) e, filters))) {
                iter.remove();
            }
        }
        return list;
    }

    private static boolean processFilter(Map<String, Object> map, List<String> filters) {
        for (String filter : filters) {
            String[] arr = filter.split("-"); // key-op-threshold
            if (arr.length != 3 || !filterOperations.contains(arr[1])) {
                continue;
            }
            String key = arr[0];
            String op = arr[1];
            String threshold = arr[2];
            if (!map.containsKey(key) || !(map.get(key) instanceof Comparable)) {
                return false;
            }

            Object value = map.get(key);
            Class<?> clz = value.getClass();
            boolean pass = false;
            if (clz == Boolean.class || clz == boolean.class) {
                pass = check((Boolean) value, Boolean.parseBoolean(threshold), op);
            } else if (clz == Double.class || clz == double.class) {
                pass = check((Double) value, Double.parseDouble(threshold), op);
            } else if (clz == Float.class || clz == float.class) {
                pass = check((Float) value, Float.parseFloat(threshold), op);
            } else if (clz == Long.class || clz == long.class) {
                pass = check((Long) value, Long.parseLong(threshold), op);
            } else if (clz == Integer.class || clz == int.class) {
                pass = check((Integer) value, Integer.parseInt(threshold), op);
            } else if (clz == String.class) {
                pass = check((String) value, threshold, op);
            }
            if (!pass) {
                return false;
            }
        }
        return true;
    }

    private static boolean check(Comparable value, Comparable threshold, String op) {
        int r = value.compareTo(threshold);
        return r == 0 && Arrays.asList(new String[] { "ge", "le", "eq" }).contains(op) || r == 1
                && Arrays.asList(new String[] { "gt", "ge", "ne" }).contains(op) || r == -1
                && Arrays.asList(new String[] { "lt", "le", "ne" }).contains(op);
    }

    private static double sum(List<Map<String, Object>> list, String key) {
        double sum = 0d;
        for (Map<String, Object> map : list) {
            Object value = map.get(key);
            if (value != null && value instanceof Number) {
                sum += ((Number) value).doubleValue();
            }
        }
        return sum;
    }

    private static List<Object> limit(List<Object> list, int limit) {
        return new ArrayList<Object>(list.subList(0, limit));
    }

    private static List<List<Map<String, Object>>> groupBy(List<Map<String, Object>> list, String groupKey) {
        Map<Object, List<Map<String, Object>>> retMap = Maps.newHashMap();
        for (Map<String, Object> ele : list) {
            Object key = ele.get(groupKey);
            if (key == null) {
                key = "null";
            }
            if (!retMap.containsKey(key)) {
                List<Map<String, Object>> l = Lists.newArrayList();
                retMap.put(key, l);
            }
            List<Map<String, Object>> lst = retMap.get(key);
            lst.add(ele);
        }
        return Lists.newArrayList(retMap.values());
    }

    private static Map<String, Object> merge(List<Map<String, Object>> list, Set<String> mergeKeys) {
        Map<String, Object> retMap = list.get(0);
        for (Map<String, Object> ele : list.subList(1, list.size())) {
            for (Entry<String, Object> entry : ele.entrySet()) {
                String k = entry.getKey();
                if (mergeKeys.contains(k)) {
                    double sum = ((Number) entry.getValue()).doubleValue() + ((Number) retMap.get(k)).doubleValue();
                    retMap.put(k, sum);
                }
            }
        }
        return retMap;
    }

    private static Object select(List<Object> list, Set<String> selectKeys) {
        for (Iterator<Object> iter = list.iterator(); iter.hasNext();) {
            Object o = iter.next();
            if (!(o instanceof Map)) {
                iter.remove();
                continue;
            }
            Map<String, Object> map = (Map<String, Object>) o;
            for (Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, Object> entry = it.next();
                if (!selectKeys.contains(entry.getKey())) {
                    it.remove();
                }
            }
            if (map.isEmpty()) {
                iter.remove();
            }
        }
        return list;
    }

    private static boolean checkOrderBy(Object draft, String orderKey) {
        if (draft instanceof List) {
            for (Object o : (List) draft) {
                if (!(o instanceof Map && ((Map<String, Object>) o).get(orderKey) instanceof Comparable)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private static boolean checkGroupMerge(Object draft, Set<String> mergeKeys) {
        if (draft instanceof List) {
            for (Object o : (List) draft) {
                if (o instanceof Map) {
                    for (Entry<String, Object> entry : ((Map<String, Object>) o).entrySet()) {
                        if (mergeKeys.contains(entry.getKey()) && !(entry.getValue() instanceof Number)) {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    static class MapCompatator implements Comparator<Object> {
        String orderKey;
        boolean desc;

        public MapCompatator(String orderKey, String desc) {
            this.orderKey = orderKey;
            this.desc = desc != null && "true".equals(desc);
        }

        @Override
        public int compare(Object o1, Object o2) {

            Comparable v1 = (Comparable) ((Map<String, Object>) o1).get(orderKey);
            Comparable v2 = (Comparable) ((Map<String, Object>) o2).get(orderKey);

            int factor = desc ? -1 : 1;
            if (v1 == null && v2 == null) {
                return 0;
            }
            if (v1 == null) {
                return v2.compareTo(v1) * factor * -1;
            } else {
                return v1.compareTo(v2) * factor;
            }
        }
    }

    public static String warn(Logger LOG, String error, Object... parameters) {
        String result = String.format((ERROR_PREFIX + error).replace("{}", "%s"), parameters);
        LOG.warn(error, parameters);
        return result;
    }

}
