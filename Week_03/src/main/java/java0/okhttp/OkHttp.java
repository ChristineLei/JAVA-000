package java0.okhttp;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class OkHttp {
    OkHttpClient client = new OkHttpClient();
    // code request code here
    public String doGetRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static void main(String[] args) throws Exception {
        // issue the Get request
        OkHttp oksrv = new OkHttp();
        String getResponse = oksrv.doGetRequest("http://localhost:8801/");
        System.out.println(getResponse);
    }
}
