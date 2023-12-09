package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

/**
 * JDBC - Connection 파라미터 사용
 */
@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryV2 {

    private final DataSource dataSource;

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values(?, ?)";

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = getConnection();
            ps = con.prepareStatement(sql);

            ps.setString(1, member.getMemberId());
            ps.setInt(2, member.getMoney());

            int count = ps.executeUpdate();

            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            JdbcUtils.closeStatement(ps);
            JdbcUtils.closeConnection(con);
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, memberId);

            rs = ps.executeQuery();
            if(rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found member_id : " + memberId);
            }
        } catch (SQLException e) {
            log.error("error", e);
            throw e;
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(ps);
            JdbcUtils.closeConnection(con);
        }
    }

    public Member findById(Connection con, String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, memberId);

            rs = ps.executeQuery();
            if(rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found member_id : " + memberId);
            }
        } catch (SQLException e) {
            log.error("error", e);
            throw e;
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(ps);
            // connection은 서비스 계층에서 닫는다
            //JdbcUtils.closeConnection(con);
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money = ? where member_id = ?";

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = getConnection();
            ps = con.prepareStatement(sql);

            ps.setInt(1, money);
            ps.setString(2, memberId);

            int count = ps.executeUpdate(); // 쿼리 결과 row 수 반환
        } catch (SQLException e) {
            log.error("error", e);
            throw e;
        } finally {
            JdbcUtils.closeStatement(ps);
            JdbcUtils.closeConnection(con);
        }
    }

    public void update(Connection con, String memberId, int money) throws SQLException {
        String sql = "update member set money = ? where member_id = ?";

        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(sql);

            ps.setInt(1, money);
            ps.setString(2, memberId);

            int count = ps.executeUpdate(); // 쿼리 결과 row 수 반환
        } catch (SQLException e) {
            log.error("error", e);
            throw e;
        } finally {
            JdbcUtils.closeStatement(ps);
            // connection은 서비스 계층에서 닫는다
            //JdbcUtils.closeConnection(con);
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id = ?";

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = getConnection();
            ps = con.prepareStatement(sql);

            ps.setString(1, memberId);
            ps.executeUpdate(); // 쿼리 결과 row 수 반환
        } catch (SQLException e) {
            log.error("error", e);
            throw e;
        } finally {
            JdbcUtils.closeStatement(ps);
            JdbcUtils.closeConnection(con);
        }
    }

    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection = {}, class = {}", con, con.getClass());
        return con;
    }
}
