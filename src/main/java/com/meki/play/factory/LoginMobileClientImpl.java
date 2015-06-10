package com.meki.play.factory;

import java.util.List;

/**
 * User: jinchao.xu
 * Date: 15-1-7
 * Time: 下午6:51
 */
public class LoginMobileClientImpl implements Login {

    private static LoginMobileClientImpl _instance = new LoginMobileClientImpl();

    public static LoginMobileClientImpl getInstance() {
        return _instance;
    }

    @Override
    public Account login(List<Type> types, String user, String password, String site, int expirePeriod) {
        return null;  
    }

    @Override
    public Account login(List<Type> types, String user, String password, String site, int expirePeriod, boolean reuseTicket) {
        return null;  
    }

    @Override
    public Account loginVirtual(List<Type> types, String user, String password, String site, int expirePeriod, boolean reuseTicket) {
        return null;  
    }

    @Override
    public List<Account> loginBatchAccount(List<Type> types, String user, String password, String site) {
        return null;  
    }

    @Override
    public Account login(String user, String password, String site, int expirePeriod) {
        return null;  
    }

    @Override
    public String createTicket(int id, int expirePeriod, boolean reuseTicket) {
        return null;  
    }

    @Override
    public boolean destroyByTicket(String ticket) {
        return false;  
    }

    @Override
    public boolean destroyById(int userId) {
        return false;  
    }

    @Override
    public int ticket2Id(String ticket) {
        return 0;  
    }

    @Override
    public Account id2Account(int id) {
        return null;  
    }

    @Override
    public String id2Ticket(int userId) {
        return null;  
    }

    @Override
    public boolean hasAccount(String account) {
        return false;  
    }

    @Override
    public List<Account> getAccount(String account) {
        return null;  
    }

    @Override
    public boolean getStatus(int userId) {
        return false;  
    }

    @Override
    public void setStatus(int userId) {
        
    }

    @Override
    public List<Account> get(String account, Type type) {
        return null;  
    }

    @Override
    public List<Account> get(String account, List<Type> types) {
        return null;  
    }

    @Override
    public List<Account> getById(int id) {
        return null;  
    }

    @Override
    public boolean has(String account, Type type) {
        return false;  
    }

    @Override
    public boolean has(String account, List<Type> types) {
        return false;  
    }

    @Override
    public Account switchAccount(int id, String pwd, boolean reuse_ticket) {
        return null;  
    }

    @Override
    public Account switchAccountIncVirtualId(int sourceId, int desId, String pwd, boolean reuse_ticket) {
        return null;  
    }
}
