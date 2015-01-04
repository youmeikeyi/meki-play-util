package com.meki.play.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * User: jinchao.xu
 * Date: 14-12-26
 * Time: 下午5:00
 */
public class JsoupUtil {
    public static void main(String[] args) {

        try {
            getMobileLocation();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final String TAOBAO_API = "http://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=%s";

    public static void getMobileLocation() throws IOException {
        String mobileLocationUrl = String.format(TAOBAO_API, "1881060");

        Document doc = null;
        try {
            doc = Jsoup.connect(mobileLocationUrl).timeout(3000).get();
        } catch (Exception e) {
//            System.out.println(e.getMessage());
            e.printStackTrace();
        }

//        System.out.println(doc.toString());
//        Element element = doc.select("body").first();
//        Elements content = doc.select("body ol:eq(2) ul:eq(2) li:eq(1)");
//        System.out.println("content:" + content.text());
    }

    public static void test() throws IOException {
        String mobileLocationUrl = "http://www.67cha.com/mobile/1511022.html";

        Document doc = Jsoup.connect(mobileLocationUrl).get();
        Element element = doc.select("body").first();

        Elements result = element.getElementsByClass("result");

//        System.out.println(doc.toString());
        System.out.println(element.toString());
        Element location = result.select("ul").get(2);
        System.out.println("## location:" + location);
        String value = location.text();
//        System.out.println("## result:"+result);
        System.out.println("## location:" + value);

        Elements city = location.getElementsByClass("value");
        System.out.println("## city :" + city);
        String cityStr = city.text();
        System.out.println("## cityStr :" + cityStr);

    }

    public static void demo(){
        String html = "<p>An <a href='http://www.jb51.net/'><b>www.jb51.net</b></a> link.</p>";
        Document doc = Jsoup.parse(html);//解析HTML字符串返回一个Document实现
        Element link = doc.select("a").first();//查找第一个a元素
        String text = doc.body().text(); // "An www.jb51.net link"//取得字符串中的文本
        String linkHref = link.attr("href"); // "http://www.jb51.net/"//取得链接地址
        String linkText = link.text(); // "www.jb51.net""//取得链接地址中的文本
        String linkOuterH = link.outerHtml();
        // "<a href="http://www.jb51.net"><b>www.jb51.net</b></a>"
        String linkInnerH = link.html(); // "<b>www.jb51.net</b>"//取得链接内的html内容
    }
}
