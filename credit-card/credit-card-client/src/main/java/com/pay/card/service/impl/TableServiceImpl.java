package com.pay.card.service.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pay.card.service.TableService;

import word.Request;
import word.Response;
import word.Table;

@Service
public class TableServiceImpl implements TableService {
	
	String encoding = "utf-8";
	
    public List<Table> tableList() {
        List<Table> list = new LinkedList();
        try {
            ClassLoader classLoader = TableServiceImpl.class.getClassLoader();
            URL resource = classLoader.getResource("data.json");
            Map map = new ObjectMapper().readValue(resource, Map.class);
            //得到host，用于模拟http请求
            String host = String.valueOf(map.get("host"));
            host = "http://"+host;
            //解析paths
            LinkedHashMap<String, LinkedHashMap> paths = (LinkedHashMap) map.get("paths");
            if (paths != null) {
                Iterator<Map.Entry<String, LinkedHashMap>> iterator = paths.entrySet().iterator();
                while (iterator.hasNext()) {
                    Table table = new Table();
                    List<Request> requestList = new LinkedList<Request>();
                    String requestType = "";

                    Map.Entry<String, LinkedHashMap> next = iterator.next();
                    String url = next.getKey();//得到url
                    LinkedHashMap<String, LinkedHashMap> value = next.getValue();
                    //得到请求方式，输出结果类似为 get/post/delete/put 这样
                    Set<String> requestTypes = value.keySet();
                    for (String str : requestTypes) {
                        requestType += str + "/";
                    }
                    Iterator<Map.Entry<String, LinkedHashMap>> it2 = value.entrySet().iterator();
                    //解析请求
                    Map.Entry<String, LinkedHashMap> get = it2.next();//得到get
                    LinkedHashMap getValue = get.getValue();
                    String title = (String) ((List) getValue.get("tags")).get(0);//得到大标题
                    String tag = String.valueOf(getValue.get("summary"));
                    //请求体
                    ArrayList parameters = (ArrayList) getValue.get("parameters");
                    if (parameters != null && parameters.size() > 0) {
                        for (int i = 0; i < parameters.size(); i++) {
                            Request request = new Request();
                            LinkedHashMap<String, Object> param = (LinkedHashMap) parameters.get(i);
                            request.setDescription(String.valueOf(param.get("description")));
                            request.setName(String.valueOf(param.get("name")));
                            request.setType(String.valueOf(param.get("type")));
                            request.setParamType(String.valueOf(param.get("in")));
                            request.setRequire((Boolean) param.get("required"));
                            requestList.add(request);
                        }
                    }
                    //返回体，比较固定
                    List<Response> responseList = listResponse();
                    //模拟一次HTTP请求,封装请求体和返回体，如果是Restful的文档可以再补充
                    if (requestType.contains("post")) {
                        Map<String, String> stringStringMap = toPostBody(requestList);
                        table.setRequestParam(stringStringMap.toString());
                        CloseableHttpClient httpClient = HttpClients.createDefault();
                        HttpPost httpPost = new HttpPost(host + url);
                        
                        List<NameValuePair> lists = new ArrayList<NameValuePair>();
                        for (Map.Entry<String, String> entry : stringStringMap.entrySet()) {
                        	lists.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
                        }
                        httpPost.setEntity(new UrlEncodedFormEntity( lists,encoding));
                        CloseableHttpResponse response = httpClient.execute(httpPost);
                       // String post = NetUtil.post(host + url, stringStringMap);
                        HttpEntity entity = response.getEntity();
                        table.setResponseParam(EntityUtils.toString(entity,encoding));
                    } else if (requestType.contains("get")) {
                        String s = toGetHeader(requestList);
                        table.setResponseParam(s);
                        CloseableHttpClient httpClient = HttpClients.createDefault();
                        //String getStr = NetUtil.get(host + url + s);
                        HttpGet httpGet = new HttpGet(host + url + s);
                        CloseableHttpResponse response = null;
                        response = httpClient.execute(httpGet);
                        HttpEntity entity = response.getEntity();
                        String getStr = EntityUtils.toString(entity,"utf-8");
                        table.setResponseParam(getStr);
                    }

                    //封装Table
                    table.setTitle(title);
                    table.setUrl(url);
                    table.setTag(tag);
                    table.setResponseForm("application/json");
                    table.setRequestType(StringUtils.removeEnd(requestType, "/"));
                    table.setRequestList(requestList);
                    table.setResponseList(responseList);
                    list.add(table);
                }
            }
            return list;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //封装返回信息，可能需求不一样，可以自定义
    private List<Response> listResponse() {
        List<Response> responseList = new LinkedList<Response>();
        responseList.add(new Response("受影响的行数", "counts", null));
        responseList.add(new Response("结果说明信息", "msg", null));
        responseList.add(new Response("是否成功", "success", null));
        responseList.add(new Response("返回对象", "data", null));
        responseList.add(new Response("错误代码", "errCode", null));
        return responseList;
    }

    //封装post请求体
    private Map<String, String> toPostBody(List<Request> list) {
        Map<String, String> map = new HashMap<>(16);
        if (list != null && list.size() > 0) {
            for (Request request : list) {
                String name = request.getName();
                String type = request.getType();
                switch (type) {
                    case "string":
                        map.put(name, "string");
                        break;
                    case "integer":
                        map.put(name, "0");
                        break;
                    case "double":
                        map.put(name, "0.0");
                        break;
                    default:
                        map.put(name, "null");
                        break;
                }
            }
        }
        return map;
    }

    //封装get请求头
    private String toGetHeader(List<Request> list) {
        StringBuffer stringBuffer = new StringBuffer();
        if (list != null && list.size() > 0) {
            for (Request request : list) {
                String name = request.getName();
                String type = request.getType();
                switch (type) {
                    case "string":
                        stringBuffer.append(name+"&=string");
                        break;
                    case "integer":
                        stringBuffer.append(name+"&=0");
                        break;
                    case "double":
                        stringBuffer.append(name+"&=0.0");
                        break;
                    default:
                        stringBuffer.append(name+"&=null");
                        break;
                }
            }
        }
        String s = stringBuffer.toString();
        if ("".equalsIgnoreCase(s)){
            return "";
        }
        return "?" + StringUtils.removeStart(s, "&");
    }
}
