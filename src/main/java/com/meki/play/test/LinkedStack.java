package com.meki.play.test;

/**
 * 栈的实现：利用泛型
 * 将内部类类型参数去掉，测试非静态内部类可使用外部类的类型参数
 * Created by xujinchao on 2015/5/22.
 */
public class LinkedStack<T> {

    private static class Node<U>{
        U item;
        Node<U> next;
        Node(){
            this.item = null;
            this.next = null;
        }
        Node(U item, Node<U> next){
            this.item = item;
            this.next = next;
        }

        boolean end(){
            return item == null && next == null;
        }
    }

    private Node<T> top = new Node<T>();//结束哨兵
    public void push(T item) {
        top = new Node<T>(item, top);
    }

    public T pop(){
        T result = top.item;
        if (!top.end()) {
            top = top.next;
        }
        return result;
    }

    public static void main(String[] args) {
        LinkedStack<String> linkedStack = new LinkedStack<String>();
        linkedStack.push("a");
        linkedStack.push("b");

        System.out.println(linkedStack.pop());
        linkedStack.push("c");
        System.out.println(linkedStack.pop() + ", next pop:" + linkedStack.pop());

        for (String s : "I have hello world".split(" ")) {
            linkedStack.push(s);
        }

        String temp;
        while ((temp = linkedStack.pop())!= null) {
            System.out.println(temp);
        }
    }
}
