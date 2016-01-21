package com.weijie.firerunning.net;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;

/**
 * 服务端单向签名https请求的支持
 * @author guodong
 * @datetime 2014-12-11 下午4:16:21
 */
public class HttpsStack implements HttpStack {

	private HttpStack stack;
	
	public HttpsStack(Context context){
		if (Build.VERSION.SDK_INT >= 9) {
            stack = new HurlStack(null,getSocketFactory());
        } else {
        	 String userAgent = "volley/0";
             try {
                 String packageName = context.getPackageName();
                 PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
                 userAgent = packageName + "/" + info.versionCode;
             } catch (NameNotFoundException e) {
             }
            // Prior to Gingerbread, HttpUrlConnection was unreliable.
            // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
            stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
        }
	}
	
	@Override
	public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException, AuthFailureError {
		return stack.performRequest(request, additionalHeaders);
	}
	

	private SSLSocketFactory getSocketFactory(){
		try {
		    // Create a trust manager that does not validate certificate chains
		    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
		        public X509Certificate[] getAcceptedIssuers(){return null;}
		        public void checkClientTrusted(X509Certificate[] certs, String authType){}
		        public void checkServerTrusted(X509Certificate[] certs, String authType){}
		    }};
		 
		    // Install the all-trusting trust manager
		    SSLContext sc = SSLContext.getInstance("TLS");
		    sc.init(null, trustAllCerts, new SecureRandom());
		    return sc.getSocketFactory();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		return null;
	}
}
