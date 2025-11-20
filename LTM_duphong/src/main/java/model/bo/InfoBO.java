package model.bo;

import model.bean.Account;
import model.dao.InfoDAO;

public class InfoBO {
    public static Account getAccount(String user, String pass) {
        return InfoDAO.getAccount(user, pass);
    }
}