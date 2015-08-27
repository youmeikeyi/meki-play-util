package com.meki.play.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 进程控制
 * Created by xujinchao on 2015/8/17.
 */
public class OSExecute {

    public static void command(String command){
        boolean err = false;
        try {
            Process process = new ProcessBuilder(command.split(" ")).start();
            BufferedReader results = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String s;
            while ((s = results.readLine()) != null) {
                System.out.println(s);
            }
            BufferedReader errors = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            //report errors and return nonzero value to calling process if there are problems.
            while ((s = errors.readLine()) != null) {
                System.out.println(s);
                err = true;
            }

        } catch (Exception e) {
            //compensate for windows 2000,..
            if (!command.startsWith("CMD /C")) {
                command("CMD /C " + command);
            } else
                throw new RuntimeException(e);
        }
        if (err) {
            //user define exception, always special os exception
//            throw new RuntimeException("");
            System.out.println("throw new RuntimeException(\"\")");
        }
    }

    public static void main(String[] args){
        OSExecute.command("javap OSExceute");
    }

}
