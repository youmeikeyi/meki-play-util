package com.meki.play.util.pattern;

public class RegexBox {

    //一代身份证
    public static final String ID_CARD_VER1 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";

    //二代身份证
    public static final String ID_CARD_VER2 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|x|X)$";

    //邮箱  changed by aiguoxin 加上A-Z
    public static final String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$";

    //电话
    public static final String PHONE = "1([\\d]{10})|((\\+[0-9]{2,4})?\\(?[0-9]+\\)?-?)?[0-9]{7,8}";

    //手机号
    public static final String TELEPHONE = "^1[3-8]\\d{9}$";

    //邮编
    public static final String POSTCODE = "^[1-9]\\d{5}";

    // IM
    public static final String IM = "[0-9a-zA-z_\\-\\.\\@]{1,40}";

    public static final String NAME = "[\\p{InCJKUnifiedIdeographs}]+";


    public static final String URL = "(http|https)://([\\w-_]+\\.)+[\\w-_]+(/[\\w-_ ./?%&=]*)?";

    private RegexBox() {
    }

    public static void main(String[] args) {
        String certNo = "42112519860514331x";
        if (!certNo.matches(RegexBox.ID_CARD_VER2)
                && !certNo.matches(RegexBox.ID_CARD_VER1))
            throw new IllegalArgumentException("身份证号码填写有误，请重新输入");
        System.out.println("42112519860514331x".matches(ID_CARD_VER2));
    }
}
