package com.meki.play.factory;

import java.util.ArrayList;
import java.util.List;

/**
 * User: jinchao.xu
 * Date: 15-1-7
 * Time: 下午6:57
 */
public class LoginWebImpl implements Login {
    private static List<Type> defaultTypes = new ArrayList<Type>();
    static {
        defaultTypes.add(Type.Account);
        defaultTypes.add(Type.Phone);
        defaultTypes.add(Type.Id);
    }

    private static LoginWebImpl _instance = new LoginWebImpl();

    public static LoginWebImpl getInstance() {
        return _instance;
    }

    @Override
    public Account login(List<Type> types, String user, String password, String site, int expirePeriod) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Account login(List<Type> types, String user, String password, String site, int expirePeriod, boolean reuseTicket) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Account loginVirtual(List<Type> types, String user, String password, String site, int expirePeriod, boolean reuseTicket) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Account> loginBatchAccount(List<Type> types, String user, String password, String site) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Account login(String user, String password, String site, int expirePeriod) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String createTicket(int id, int expirePeriod, boolean reuseTicket) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean destroyByTicket(String ticket) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean destroyById(int userId) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int ticket2Id(String ticket) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Account id2Account(int id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String id2Ticket(int userId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasAccount(String account) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Account> getAccount(String account) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean getStatus(int userId) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setStatus(int userId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Account> get(String account, Type type) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Account> get(String account, List<Type> types) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Account> getById(int id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean has(String account, Type type) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean has(String account, List<Type> types) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Account switchAccount(int id, String pwd, boolean reuse_ticket) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Account switchAccountIncVirtualId(int sourceId, int desId, String pwd, boolean reuse_ticket) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
