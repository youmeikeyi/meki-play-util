package com.meki.play.util;

/**
 * Created by xujinchao on 2015/6/9.
 */
public class Student {

    /**
     * id
     */
    private Integer id;
    /**
     * 学号
     */
    private String no;
    /**
     * 姓名
     */
    private String name;
    /**
     * 学院
     */
    private String age;
    /**
     * 成绩
     */
    private float score;

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder(getClass().getName());
        builder.append(" ").append(no).append(" ").append(name).append(" ").append(age).append(" ").append(score);
        return builder.toString();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public static void main(String[] args) {
        Student student = new Student();
        student.setName("2131");
        student.setId(1);
        student.setAge("12");
        System.out.println(student);
    }
}
