package org.fisco.bcos.asset.client;
import java.sql.*;
public class FileDao {
    public boolean regist(file user) throws SQLException {
        Connection conn = DB.getConnection();//获取连接对象
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery(
                "SELECT * FROM filetable WHERE id='" + user.getId() + "'" + "and time='" + user.getTime() + "'");
        //如果搜到有这个该id以及time的信息，就返回注册失败
        if (rs.next()) {
            return false;
        }
        String sql = "insert into filetable values(?,?,?,?,?)";
        // 预编译
        PreparedStatement ptmt = conn.prepareStatement(sql);
        // 传参
        ptmt.setString(1, user.getId());
        ptmt.setString(5, user.getTime());
        ptmt.setString(2, user.getTotal_hash());
        ptmt.setString(3, user.getPart_hash());
        ptmt.setInt(4, user.getResult());
        // 执行
        ptmt.execute();
        return true;
    }
}
    //用户登录的方法
//    public User login(String name, String password) throws SQLException {
//        Connection conn = DB.getConnection();
//        // 操作数据库
//        Statement state = conn.createStatement();
//        ResultSet rs = state.executeQuery(
//                "SELECT nickname,username,password,sex FROM user WHERE username='" +
//                        name+"' AND password='"+password+"'");
//        User user = null;
//        //如果搜到了用户，就给他填充信息，如果搜不到的话就返回null，让login判断是否能成功登录
//        if (rs.next()) {
//            user = new User();
//            user.setnickname(rs.getString("nickname"));
//            user.setusername(rs.getString("username"));
//            user.setPassword(rs.getString("password"));
//            user.setSex(rs.getString("sex"));
//        }
//        return user;
//    }

