package com.meki.play.test;

import com.alibaba.fastjson.JSONObject;
import com.meki.play.util.HttpClientUtil;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;

/**
 * Created by xujinchao on 2015/5/15.
 */
public class Test {

    public static void main2(String[] args){
        String result = "success\r\n";
        System.out.println("success".equals(result));

        String nowTime = System.currentTimeMillis() / 1000 +"";
        System.out.println(nowTime.length());
        System.out.println((Long) System.currentTimeMillis());

        String data = "transdata={\"code\":\"1001\",\"errmsg\":\"签名验证失败\"}";
        JSONObject json = new JSONObject();

        System.out.println(json);
    }

    public static void main(String[] args) throws Exception {
        String result = "transdata={\"transid\":\"11111\"}&a=1";
//        result = "transdata={\"appid\":\"123\",\"waresid\":1,\"cporderid\":\"22222\",\"price\":1.00,\"currency\":\"RMB\",\"appuserid\":\"test\",\"notifyurl\":\"http://www.iapppay.com/test\"}&sign=xxxxxx&signtype=RSA";

//        result = "transdata=%7B%22code%22%3A1002%2C%22errmsg%22%3A%22%E8%AF%B7%E6%B1%82%E5%8F%82%E6%95%B0%E9%94%99%E8%AF%AF%22%7D";
//        result = "transdata=%7B%22cporderid%22%3A%22208897%22%2C%22price%22%3A1%2C%22notifyurl%22%3A%22http%3A%2F%2F211.100.255.12%2Fkofcopa%22%2C%22appuserid%22%3A%2236865%22%2C%22waresid%22%3A%22101402%22%2C%22appid%22%3A%225000001141%22%2C%22cpprivateinfo%22%3A%224850%22%2C%22currency%22%3A%22RMB%22%7D&sign=Jjv0nHJ7kYlnikiqgQiwn4rmTWb9xm67KzE7LG47TIOrn87UJxoDLJatHS1AxUsFFq%2BR1%2FrLNbLfU798v%2BGwwWDT8vZPTRDqE9dF8uXaQgxhCe5D5tjlXm0VKYBRRbG5Oh30QPBeYmnrORFt6e9tlWCkdflYqC55lrQClqwMYk8%3D&signtype=RSA";
//        result = URLDecoder.decode(result, "UTF-8");
        result = "transdata=%7B%22cporderid%22%3A%22233473%22%2C%22price%22%3A1%2C%22notifyurl%22%3A%22http%3A%2F%2F211.100.255.12%2Fkofcopa%22%2C%22appuserid%22%3A%2236865%22%2C%22waresid%22%3A101402%2C%22appid%22%3A%225000001141%22%2C%22cpprivateinfo%22%3A%224850%22%2C%22currency%22%3A%22RMB%22%7D&sign=UZzprG2WpTyAFe0S1K2%2BKoOszNr7nvIMlQByZN9wu1%2BkcBPa75RdMCRWVuFUINmIB5RId2agb2hz%2F3bcnwMKV5t8ipVqMfd2kH8oS3ZnXDTexeZyXsMoNr%2FkZTyPdHVohrXbJB2qNedROBTlqw%2FhL4m4Eq5xDlNiMRZIFABWIss%3D&signtype=RSA";
//        String decodeResult = URLDecoder.decode(result, "UTF-8");
        System.out.println("before sign allData=" + result);
//        System.out.println("after decode allData:" + decodeResult);
        System.out.println();
//        final Map<String, String> resultMap = HttpIO.argsToStringMap(result);
        String paykey = "MIICXQIBAAKBgQClqJ54ozHmsP07ufD0H+VauixKo3QLE7lQ/sAKALB8t5/RE1bmtNiLjcFv/KXysRuPtLlVFzXUKEIV+qVYgAzxS9Mp7vQdwhLch/NUi7F20Irh/prd0fGfzO6s83hfTR8QqMcEn34XQcjht3dSm4uN2L9rvkk3T9fpW/oBQupOvwIDAQABAoGAMQE3l+JW8bJrxw5TXDRPSc6HKbE+s8Qq7u3da1gW3V4IlfXNoEPhRy8xCOrJQIjp6VIB/Uz3bIJYQl46KXr/2KvIk6XhYW3YO7LWtyyo1BczAUwkBVmcsljbyFInp1Wl8dWbrLpX43bC4US1Wh2BbIKZD0iw3fio5RawnNv78aECQQDPr2G61q4xqbNlxrF5kxJOxvmNgGGvuKNlQUPbHYSwHRT826I1g2/kg+G4lw0HSrcWQHtZmdL9vzSiQuMCWGkRAkEAzDJeGPqY/1/iP+SJ/XQKsCAz015qro/SHbqtzNZgsKp4EOOnvRF8vn3LCXHh84tuo+SakN27UdWM0CQQJ6e6zwJBAIL+81y+9kaa7G3goqa1TlHkDRnkhTkp8931CZDWkI/o+Aj5m9m3Gsd/K8ubcvtLdCoT5/m/HacxiEhkef1LuJECQGaDquYTeZhMBQRaskRCa9M8d4hYUGrO3Yf7XqIFxokrLBgekunCy8O+154oRG3GxIcXj9nG1/IfqhAc6tddx9UCQQCukbdWGTJlzxjcHkKSm/WVNA395a1i26pTW0YDhf3vH54fGEPyqFvuee2xLQ0JxNAC7XVLwguGx5r64l4Z3q3l";
        paykey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKWonnijMeaw/Tu58PQf5Vq6LEqjdAsT\n" +
                "uVD+wAoAsHy3n9ETVua02IuNwW/8pfKxG4+0uVUXNdQoQhX6pViADPFL0ynu9B3CEtyH81SLsXbQiuH+\n" +
                "mt3R8Z/M7qzzeF9NHxCoxwSffhdByOG3d1Kbi43Yv2u+STdP1+lb+gFC6k6/AgMBAAECgYAxATeX4lbx\n" +
                "smvHDlNcNE9JzocpsT6zxCru7d1rWBbdXgiV9c2gQ+FHLzEI6slAiOnpUgH9TPdsglhCXjopev/Yq8iT\n" +
                "peFhbdg7sta3LKjUFzMBTCQFWZyyWNvIUienVaXx1ZusulfjdsLhRLVaHYFsgpkPSLDd+KjlFrCc2/vx\n" +
                "oQJBAM+vYbrWrjGps2XGsXmTEk7G+Y2AYa+4o2VBQ9sdhLAdFPzbojWDb+SD4biXDQdKtxZAe1mZ0v2/\n" +
                "NKJC4wJYaRECQQDMMl4Y+pj/X+I/5In9dAqwIDPTXmquj9Iduq3M1mCwqngQ46e9EXy+fcsJceHzi26j\n" +
                "5JqQ3btR1YzQJBAnp7rPAkEAgv7zXL72RprsbeCiprVOUeQNGeSFOSnz3fUJkNaQj+j4CPmb2bcax38r\n" +
                "y5ty+0t0KhPn+b8dpzGISGR5/Uu4kQJAZoOq5hN5mEwFBFqyREJr0zx3iFhQas7dh/teogXGiSssGB6S\n" +
                "6cLLw77XnihEbcbEhxeP2cbX8h+qEBzq113H1QJBAK6Rt1YZMmXPGNweQpKb9ZU0Df3lrWLbqlNbRgOF\n" +
                "/e8fnh8YQ/KoW+557bEtDQnE0ALtdUvCC4bHmvriXhnereU=";
//        String data = result.substring(result.indexOf('{'), result.lastIndexOf('}') + 1);
//        System.out.println(result.indexOf("transdata"));
//        String sign = RSASignature.MD5withRSA.encodeSignature(new ObjectIdentifier(resultMap.get("transdata")), paykey);
        String transdata = "%7B%22cporderid%22%3A%22208897%22%2C%22price%22%3A1%2C%22notifyurl%22%3A%22http%3A%2F%2F211.100.255.12%2Fkofcopa%22%2C%22appuserid%22%3A%2236865%22%2C%22waresid%22%3A%22101402%22%2C%22appid%22%3A%225000001141%22%2C%22cpprivateinfo%22%3A%224850%22%2C%22currency%22%3A%22RMB%22%7D";
        transdata = "%7B%22cporderid%22%3A%22233473%22%2C%22price%22%3A1%2C%22notifyurl%22%3A%22http%3A%2F%2F211.100.255.12%2Fkofcopa%22%2C%22appuserid%22%3A%2236865%22%2C%22waresid%22%3A101402%2C%22appid%22%3A%225000001141%22%2C%22cpprivateinfo%22%3A%224850%22%2C%22currency%22%3A%22RMB%22%7D";
        String transdata2 = URLDecoder.decode(transdata, "UTF-8");
//        transdata2 = "{\"cporderid\":\"208897\",\"price\":1.00,\"notifyurl\":\"http://211.100.255.12/kofcopa\"," +
//                "\"appuserid\":\"36865\",\"waresid\":101402,\"appid\":\"5000001141\",\"cpprivateinfo\":\"4850\",\"currency\":\"RMB\"}";
        System.out.println("transdata1:" + transdata);
        System.out.println("transdata2:" + transdata2);
//        JSONObject json = new JSONObject();
//        json.put("appid","5000001141");
//        json.put("waresid", 101402);
//        json.put("cporderid", "208897");
//        json.put("price", 1.00);
//        json.put("currency", "RMB");
//        json.put("appuserid", "36865");
//        json.put("notifyurl", "http://211.100.255.12/kofcopa");
//        System.out.println("Json:" + json.toString());
        String sign = SignHelper.sign(transdata, paykey);
//        MD5.md5Digest()
//        System.out.println("transdata:" + resultMap.get("transdata"));
//        System.out.println("nHrBtlm9FBGDynz6kDyBM8cWNaeM7xuOiMyWKFJqBh5LAE50upUIBYwUABLBKPcShHeeBLZnpSijLo8801UP24Ov3RIhxoKtUne/kFumOr+csHhjtSWFAFFqn85gmAGUItEW9A+t5mdu2wExFC1i8t4zXBwSrkhhIyxnPr+IlNg=");
        System.out.println("sign transdata1: " + sign);
//        System.out.println("sign encoded:" + URLEncoder.encode(sign, "UTF-8"));
        System.out.println("sign transdata2 :" + SignHelper.sign(transdata2, paykey));
        String chargeUrl = "http://pay.coolyun.com:6988/payapi/order";
//        HttpConfig httpConfig = new HttpConfig();
//        httpConfig.readtimeout = 5000;
//        httpConfig.connecttimeout = 5000;
        System.out.println();
        String  data = URLDecoder.decode(HttpUtils.sentPost(chargeUrl, result), "UTF-8");
        System.out.println("before decode : " + HttpUtils.sentPost(chargeUrl, result));
        System.out.println("after decode: " + data);

//        long time = 1434470401060L;
//        time = 1434547155942L;
//        time = 1434470401127L;
//        Date date = new Date(time);
//        System.out.println(date);
    }

}
