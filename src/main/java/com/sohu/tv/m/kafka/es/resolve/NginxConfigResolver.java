package com.sohu.tv.m.kafka.es.resolve;

import com.sohu.tv.m.kafka.es.utils.DateFormatUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NginxConfigResolver implements Serializable {

	private static final long serialVersionUID = -2417237041725931920L;

	private static final Logger log = Logger
			.getLogger(NginxConfigResolver.class);

	private static String nginxPattern = ".*?(\\$[a-zA-Z_]+).*?";

	private static Pattern pattern = Pattern.compile(nginxPattern);

	private static String httpRequestReg = "(.*)\\s(.*)\\?(.*)\\s(HTTP\\/.*)";

	private static Pattern requestPattern = Pattern.compile(httpRequestReg);

	private static String noParamReuest = "(.*)\\s(.*)\\s(HTTP\\/.*)";

	private static Pattern noParamPattern = Pattern.compile(noParamReuest);

	private static final String REQUEST = "request";

	private static final String REQUEST_METHOD = "request_method";

	private static final String REQUEST_ADDRESS = "request_address";

	private static final String REQUEST_PREFIX = "r_";

	private static final String HTTP_REFERER = "http_referer";
	
	private static final String HTTP_REFERER_URL = "http_referer_url";

	private static final String TIME = "time_local";

	private String nginxconfig;

	private String newreg;

	private Pattern logpattern;

	private List<String> list;

	public void setNginxconfig(String nginxconfig) {
		this.nginxconfig = nginxconfig;
	}

	public SerializSparkLog resolve(String log) {
		SerializSparkLog sparkLog = new SerializSparkLog();
		Map<String, String> logFields = resolveField(log, nginxconfig);
		sparkLog.setFields(logFields);
		return sparkLog;
	}

	public NginxConfigResolver(String nginxconfig) {
		this.nginxconfig = nginxconfig;
		list = new ArrayList<String>();
		newreg = replace(nginxconfig, list);
		logpattern = Pattern.compile(newreg);
	}

	public Map<String, String> resolveField(String log, String nginxFormat) {
		Map<String, String> resultMap = new HashMap<String, String>();
		Matcher m = logpattern.matcher(log);
		if (m.matches()) {
			for (int i = 1; i <= m.groupCount(); i++) {
				if (StringUtils.equals(list.get(i - 1), REQUEST)) {
					resultMap.putAll(requestSplit(m.group(i)));
				} else if (StringUtils.equals(list.get(i - 1), HTTP_REFERER)) {
					resultMap.putAll(refererSplit(m.group(i)));
				} else if (StringUtils.equals(list.get(i - 1), TIME)) {
					resultMap.putAll(tansTime(m.group(i)));
				}
				resultMap.put(list.get(i - 1), m.group(i));
			}
		}
		return resultMap;
	}

	private Map<String, String> tansTime(String time) {
		Map<String, String> times = new HashMap<String, String>();
		Date date = DateFormatUtils.parseFromOrigin(time);
		times.put("create_time", DateFormatUtils.parseToCreateTime(date));
		times.put("create_time_id", DateFormatUtils.parseToCreateTimeId(date));
        times.put("timestamp", DateFormatUtils.parseFromOrigin(time).getTime() + "");
		return times;
	}

	private String replace(String origin, List<String> list) {
		Matcher m = pattern.matcher(origin);
		if (m.matches()) {
			String match = m.group(1);
			String toRep = origin.replace(match, "(.*)");
			list.add(match.substring(1));
			return replace(toRep, list);
		} else {
			return origin.replace("[", "\\[").replace("]", "\\]")
					.replace("{", "\\{").replace("}", "\\}");
		}
	}

	private Map<String, String> requestSplit(String request) {
		Map<String, String> params = new HashMap<String, String>();
		boolean hasParam = request.contains("?");
		if (StringUtils.isNotBlank(request)) {
			try {
				Matcher matcher;
				if (hasParam) {
					matcher = requestPattern.matcher(request);
				} else {
					matcher = noParamPattern.matcher(request);
				}
				matcher.find();
				params.put(REQUEST_METHOD, matcher.group(1));
				params.put(REQUEST_ADDRESS, matcher.group(2));
				if (hasParam) {
					params.putAll(paramsSplit(matcher.group(3)));
				}
			} catch (Exception e) {
				log.error("reg error. request is " + request, e);
			}
		}
		return params;
	}

	private Map<String, String> refererSplit(String referer) {
		Map<String, String> params = new HashMap<String, String>();
		if (StringUtils.isNotBlank(referer)) {
			params.put(HTTP_REFERER_URL,referer.split("\\?")[0]);
		}
		return params;
	}

	private Map<String, String> paramsSplit(String params) {
		Map<String, String> paramMap = new HashMap<String, String>();
		if (StringUtils.isNotBlank(params)) {
			String[] keyAndValues = params.split("&");
			for (int i = 0; i < keyAndValues.length; i++) {
				if (StringUtils.isNotBlank(keyAndValues[i])) {
					String keyAndValue[] = keyAndValues[i].split("=");
					if (keyAndValue.length > 1) {
						paramMap.put(REQUEST_PREFIX + keyAndValue[0],
								StringUtils.trim(keyAndValue[1]));
					}
				}
			}
		}
		return paramMap;
	}
}
