package com.social.preserve.network;

import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.social.preserve.App;


import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

/**
 * Created by hjw on 2018/7/29.
 */

public class MyRequest {


//    private static Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            Toast.makeText(context, "Network error : " + (String) msg.obj, Toast.LENGTH_SHORT).show();
//        }
//    };

    public static <T> void sendPostJsonRequest(String url, Map<String, String> paramsMap, MyResponseCallback<T> callBack, Class<T> clazz, boolean resultIsList) {
        sendJsonRequest(HttpMethod.POST, url, paramsMap, callBack, clazz, resultIsList);
    }

    public static <T> void sendJsonRequest(final HttpMethod method, final String reqUrl, Map<String, String> paramsMap, final MyResponseCallback<T> callBack, final Class<T> clazz, final boolean resultIsList) {


        if (TextUtils.isEmpty(reqUrl)) {
            return;
        }


        RequestParams reParams = new RequestParams(reqUrl);
        reParams.setCharset("utf-8");
//        reParams.addBodyParameter("appVersion", App.appVersionName);
//        reParams.addBodyParameter("version", App.appVersionName);
//        reParams.addBodyParameter("deviceId", App.deviceId + "");
//        reParams.addBodyParameter("deviceModel", App.deviceModel + "");
//        reParams.addBodyParameter("osType", "android");
//        reParams.addBodyParameter("osVersion", App.osVersion + "");
//        reParams.addBodyParameter("channelCode", App.channel);
        reParams.addBodyParameter("osLanguage", App.locale);
//        reParams.addBodyParameter("platForm", "1");
//
//        reParams.addBodyParameter("latitude", App.mLatitude);
//        reParams.addBodyParameter("longitude", App.mLongitude);
//        reParams.addBodyParameter("positionCountry", App.mUnationName);
//        reParams.addBodyParameter("abbr", App.mUnationCode);
//
//        if (null != App.getLoginUser()) {
//
//            reParams.addBodyParameter("uId", App.getLoginUser().getuId());
//            if (null != App.getLoginUser().getToken() /*|| App.getLoginUser().getToken().equals("null")*/) {
//                reParams.addBodyParameter("token", App.getLoginUser().getToken());
//            }
//        }


        //解析封装参数
        if (null != paramsMap && paramsMap.size() > 0) {
            for (String key : paramsMap.keySet()) {
                reParams.addBodyParameter(key, paramsMap.get(key));
            }
        }


        List<KeyValue> ll = reParams.getQueryStringParams();
        final StringBuffer sb = new StringBuffer("");
        for (KeyValue kv : ll) {
            sb.append(" " + kv.key + ":" + kv.value + ", ");
        }
        Log.d("http-info", "sendRequest>> " + reqUrl + " ,请求参数>> " + sb.toString());
        x.http().request(method, reParams, new Callback.CacheCallback<String>() {


            @Override
            public boolean onCache(String result) {

                return false;
            }

            @Override
            public void onSuccess(String result) {
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

                callBack.onFailure(new MyException(-1, "cancelled"));
            }

            @Override
            public void onFinished() {

            }
        });
    }


    /**
     * @param url       请求地址
     * @param paramsMap 请求参数
     * @param callBack  回调
     * @param clazz     bean类
     * @param <T>
     */
    public static <T> void sendPostRequest(String url, Map<String, String> paramsMap, MyResponseCallback<T> callBack, Class<T> clazz, boolean resultIsList) {
        sendRequest(HttpMethod.POST, url, paramsMap, callBack, clazz, resultIsList);
    }


    /**
     * @param url       请求地址
     * @param paramsMap 请求参数
     * @param callBack  回调
     * @param clazz     bean类
     * @param <T>
     */
    public static <T> void sendGetRequest(String url, Map<String, String> paramsMap, MyResponseCallback<T> callBack, Class<T> clazz, boolean resultIsList) {
        sendRequest(HttpMethod.GET, url, paramsMap, callBack, clazz, resultIsList);
    }


    public static String string2Sha1(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
    }


    public static <T> void sendRequest(final HttpMethod method, final String reqUrl, Map<String, String> paramsMap, final MyResponseCallback<T> callBack, final Class<T> clazz, final boolean resultIsList) {




        if (TextUtils.isEmpty(reqUrl)) {
            return;
        }

//        BuglyLogUtil.uploadCustomLogE(Constant.HTTP_TAG, "--------------------------- request-start ----------------------------");
        RequestParams reParams = new RequestParams(reqUrl);
        reParams.setCharset("utf-8");
//        reParams.addBodyParameter("appVersion", App.appVersionName);
//        reParams.addBodyParameter("version", App.appVersionName);
//        reParams.addBodyParameter("deviceId", App.deviceId + "");
//        reParams.addBodyParameter("deviceModel", App.deviceModel + "");
//        reParams.addBodyParameter("osType", "android");
//        reParams.addBodyParameter("osVersion", App.osVersion + "");
//        reParams.addBodyParameter("channelCode", App.channel);
        reParams.addBodyParameter("osLanguage", App.locale);
//        reParams.addBodyParameter("platForm", "1");
//        //
//        reParams.addBodyParameter("latitude", App.mLatitude);
//        reParams.addBodyParameter("longitude", App.mLongitude);
//        reParams.addBodyParameter("positionCountry", App.mUnationName);
//        reParams.addBodyParameter("abbr", App.mUnationCode);
//
//        if (null != App.getLoginUser()) {
//
//            reParams.addBodyParameter("uId", App.getLoginUser().getuId());
//            if (null != App.getLoginUser().getToken() /*|| App.getLoginUser().getToken().equals("null")*/) {
//                reParams.addBodyParameter("token", App.getLoginUser().getToken());
//            }
//        }


        //解析封装参数
        if (null != paramsMap && paramsMap.size() > 0) {
            for (String key : paramsMap.keySet()) {
                reParams.addBodyParameter(key, paramsMap.get(key));
            }
        }


        List<KeyValue> ll = reParams.getQueryStringParams();
        final StringBuffer sb = new StringBuffer("");
        for (KeyValue kv : ll) {
            sb.append(" " + kv.key + ":" + kv.value + ", ");
        }

       // reParams.setCacheMaxAge(1*1000);

        Log.d("http-info", "sendRequest>> " + reqUrl + " ,请求参数>> " + sb.toString());
        x.http().request(method, reParams, new Callback.CacheCallback<String>() {


            @Override
            public boolean onCache(String result) {

               /* Log.d("http-info", "onSuccess>>onCache>> " + reqUrl + " ,相应结果>> " + result);

                MyResponse response = JSON.parseObject(result, MyResponse.class);
                if (null != response) {
                    if (response.getCode() != 200) {
                        if (response.getCode() == 10109) {
                            //账户被禁用

                            //liujianwei 防止内存泄漏 通过发送event事件触发

                            if (App.currActivity instanceof WelcomeActivity || App.currActivity instanceof PhoneLoginActivity) {
                                callBack.onFailure(new MyException(response.getCode(), TextUtils.isEmpty(response.getMsg()) ? App.getRequestErrorString() : response.getMsg()));
                            } else {
                                //非手机登录页 & welcome登录页，直接跳转到登录页
                                if (null != App.currActivity && !App.isExit) {

                                    App.isExit = true;
                                    App.logout();
                                    Intent login = new Intent(App.currActivity, WelcomeActivity.class);
                                    login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    login.putExtra("loginError", response.getMsg());
                                    App.currActivity.startActivity(login);
                                    App.isExit = false;

                                } else {
                                    callBack.onFailure(new MyException(response.getCode(), TextUtils.isEmpty(response.getMsg()) ? App.getRequestErrorString() : response.getMsg()));
                                }
                            }

                        } else if(response.getCode() == 10201){
                            Intent completeInfo = new Intent(App.currActivity, PerfectInfomationActivity.class);
                            completeInfo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            App.currActivity.startActivity(completeInfo);
                        }else {
                            callBack.onFailure(new MyException(response.getCode(), TextUtils.isEmpty(response.getMsg()) ? App.getRequestErrorString() : response.getMsg()));

                            String errorMsg = "onFailure 接口反馈错误，请求地址>> " + reqUrl;
                            Log.d("http-info", errorMsg + " ,相应结果>> " + result);
                            try {
                                BuglyLogUtil.postCatchedException(new Throwable(errorMsg));
                                BuglyLogUtil.uploadCustomLogE(Constant.HTTP_TAG, "请求地址>> " + reqUrl + ",  请求参数>> " + sb.substring(0, sb.length() - 1) + " , 响应结果 >> " + JSON.toJSONString(response));
                            } catch (Exception e) {
                            }
                            ;
                        }

                    } else {
                        if (response.getData() != null) {
                            if (resultIsList) {
                                callBack.onSuccessList(JSON.parseArray(response.getData().toString(), clazz));
                            } else {
                                callBack.onSuccess(JSON.parseObject(response.getData().toString(), clazz));
                            }
                        } else {
                            if (resultIsList) {
                                callBack.onSuccessList(null);
                            } else {
                                callBack.onSuccess(null);
                            }
                        }
                    }
                }*/
                return false;
            }

            @Override
            public void onSuccess(String result) {
                Log.d("http-info", "onSuccess>> " + reqUrl + " ,相应结果>> " + result);
                MyResponse response = JSON.parseObject(result, MyResponse.class);
                if (null != response) {
                    if (response.getCode() != 200) {
//                        if (response.getCode() == 10109) {
//                            //账户被禁用
//
//                            //liujianwei 防止内存泄漏 通过发送event事件触发
//
//                            if (App.currActivity instanceof WelcomeActivity || App.currActivity instanceof PhoneLoginActivity) {
//                                callBack.onFailure(new MyException(response.getCode(), TextUtils.isEmpty(response.getMsg()) ? App.getRequestErrorString() : response.getMsg()));
//                            } else {
//                                //非手机登录页 & welcome登录页，直接跳转到登录页
//                                if (null != App.currActivity && !App.isExit) {
//
//                                    App.isExit = true;
//                                    App.logout();
//                                    Intent login = new Intent(App.currActivity, WelcomeActivity.class);
//                                    login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    login.putExtra("loginError", response.getMsg());
//                                    App.currActivity.startActivity(login);
//                                    App.isExit = false;
//
//                                } else {
//                                    callBack.onFailure(new MyException(response.getCode(), TextUtils.isEmpty(response.getMsg()) ? App.getRequestErrorString() : response.getMsg()));
//                                }
//                            }
//
//                        } else if(response.getCode() == 10201){
////                            Intent perfectInfo = new Intent(App.currActivity, PerfectInfomationActivity.class);
//                            Intent perfectInfo = new Intent(App.currActivity, PerNicknameActivity.class);
//                            perfectInfo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            App.getInstance().startActivity(perfectInfo);
//
//
//                        }else{
                            callBack.onFailure(new MyException(response.getCode(), response.getMsg()));

                            String errorMsg = "onFailure 接口反馈错误，请求地址>> " + reqUrl;
                            Log.d("http-info", errorMsg + " ,相应结果>> " + result);
//                            try {
//                                BuglyLogUtil.postCatchedException(new Throwable(errorMsg));
//                                BuglyLogUtil.uploadCustomLogE(Constant.HTTP_TAG, "请求地址>> " + reqUrl + ",  请求参数>> " + sb.substring(0, sb.length() - 1) + " , 响应结果 >> " + JSON.toJSONString(response));
//                            } catch (Exception e) {
//                            }
                            ;
//                        }

                    } else {
                        if (response.getData() != null) {
                            if (resultIsList) {
                                callBack.onSuccessList(JSON.parseArray(response.getData().toString(), clazz));
                            } else {
                                callBack.onSuccess(JSON.parseObject(response.getData().toString(), clazz));
                            }
                        } else {
                            if (resultIsList) {
                                callBack.onSuccessList(null);
                            } else {
                                callBack.onSuccess(null);
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                String exString = ex.toString();

                Message msg = new Message();

                msg.what = 0;
                msg.obj = exString;

                //   mHandler.sendMessage(msg) ;

                callBack.onFailure(new MyException(-99,ex.toString()));
                String errorMsg = "onError 请求地址>> " + reqUrl;
                Log.d("http-info", errorMsg + " 异常信息>> " + ex.toString());
//                BuglyLogUtil.postCatchedException(new Throwable(errorMsg));
//                BuglyLogUtil.uploadCustomLogE(Constant.HTTP_TAG, "  请求参数>> " + sb.substring(0, sb.length() - 1) + " 异常信息>> " + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

                callBack.onFailure(new MyException(-1, "cancelled"));
            }

            @Override
            public void onFinished() {

            }
        });


    }

}