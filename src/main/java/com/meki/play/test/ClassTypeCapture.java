package com.meki.play.test;

import java.util.Map;

class Building {
}

class House extends Building {
}

public class ClassTypeCapture<T> {
    Class<T> kind;

    public ClassTypeCapture(Class<T> kind) {
        this.kind = kind;
    }

    public boolean f(Object arg) {
        return kind.isInstance(arg);
    }

    public static void main(String[] args) {
        ClassTypeCapture<Building> ctt1 =
                new ClassTypeCapture<Building>(Building.class);
        System.out.println(ctt1.f(new Building()));
        System.out.println(ctt1.f(new House()));
        ClassTypeCapture<House> ctt2 =
                new ClassTypeCapture<House>(House.class);
        System.out.println(ctt2.f(new Building()));
        System.out.println(ctt2.f(new House()));
    }

    Map<String, Class<?>> map;

    public boolean addType(String typename, Class<?> kind) {
        map.put(typename, kind);
        return true;
    }

//    public T createNew(String typename) {
//        map.put(typename, kind);
//        return T;
//    }
//
//    public T createNew(){
//        return T;
//    }
}