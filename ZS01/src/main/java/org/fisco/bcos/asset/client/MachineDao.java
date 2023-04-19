package org.fisco.bcos.asset.client;
import java.sql.*;
public class MachineDao {
    public boolean regist(machine ma) throws SQLException {
        Connection conn = DB.getConnection();//获取连接对象
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery(
                "SELECT * FROM machine WHERE id='" + ma.getId()+"'");
        //如果搜到有这个该id以及time的信息，就返回注册失败
        if (rs.next()) {
            return false;
        }
        String sql = "insert into machine values(?,?,?,?,?,?)";
        // 预编译
        PreparedStatement ptmt = conn.prepareStatement(sql);

        // 传参
        ptmt.setString(1, ma.getId());
        ptmt.setString(2, ma.getName());
        ptmt.setString(3, ma.getLateststate());
        ptmt.setString(4, ma.getSensortype());
        ptmt.setString(5, ma.getIntro());
        ptmt.setString(6, ma.getImageurl());
        // 执行
        ptmt.execute();
        return true;
    }
}