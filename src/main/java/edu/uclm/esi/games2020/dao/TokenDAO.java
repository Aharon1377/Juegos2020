package edu.uclm.esi.games2020.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.uclm.esi.games2020.model.Token;
import edu.uclm.esi.games2020.model.User;

public class TokenDAO {
	public static Token getToken(String token) throws Exception {
        try (WrapperConnection bd = Broker.get().getBd()) {
            String sql = "SELECT id, email, token, fecha " + 
            		"FROM user_token " + 
            		"WHERE token = ?";
            try (PreparedStatement ps = bd.prepareStatement(sql)) {
                ps.setString(1, token);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Token reqToken = new Token();
                        reqToken.setId(rs.getLong(1));
                        reqToken.setEmail(rs.getString(2));
                        reqToken.setToken(rs.getString(3));
                        reqToken.setFecha(rs.getLong(4));
                        System.out.println("El correo del token es: " +reqToken.getEmail());
                        System.out.println("El token es: " +reqToken.getToken());
                        System.out.println("La fecha en milisseconds del token es: " +reqToken.getFecha());
                        return reqToken;
                    } else throw new SQLException();
                }
            }
        }
    }
}