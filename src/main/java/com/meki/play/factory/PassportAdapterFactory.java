package com.meki.play.factory;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: jinchao.xu
 * Date: 15-1-7
 * Time: 下午5:49
 */
public class PassportAdapterFactory {

    public static Login getMobileClientLogin() {
        return LoginMobileClientImpl.getInstance();
    }

    public static Login getWebLogin() {
        return LoginWebImpl.getInstance();
    }

    /*public static Login getWapLogin() {
        return LoginWapImpl.getInstance();
    }



    public static Ticket getWebTicket() {
        return TicketWebImpl.getInstance();
    }

    public static Transfer getTransfer() {
        return TransferImpl.getInstance();
    }*/

    public static void main(String[] args) throws NoSuchAlgorithmException {
        try{
            List<Login.Type> types=new ArrayList<Login.Type>();
            types.add(Login.Type.Account);
            List<Account> as=PassportAdapterFactory.getWebLogin().get("cnm", types);
            System.out.println(as.size());
            for(Account a:as){
                System.out.println(a.getAccount()+" "+a.getId());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        System.exit(0);
    }
}
