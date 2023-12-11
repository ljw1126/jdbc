package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.exception.MyDbException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

/**
 * SQLExceptionTranslate 추가
 */
@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryV4_2 implements MemberRepository{

    private final DataSource dataSource;
    private final SQLExceptionTranslator sqlExceptionTranslator;

    public MemberRepositoryV4_2(DataSource dataSource) {
        this.dataSource = dataSource;
        this.sqlExceptionTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
    }

    @Override
    public Member save(Member member) {
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
            throw sqlExceptionTranslator.translate("task", sql, e);
        } finally {
            JdbcUtils.closeStatement(ps);
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    @Override
    public Member findById(String memberId) {
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
            throw sqlExceptionTranslator.translate("task", sql, e);
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(ps);
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    @Override
    public void update(String memberId, int money) {
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
            throw sqlExceptionTranslator.translate("task", sql, e);
        } finally {
            JdbcUtils.closeStatement(ps);
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    @Override
    public void delete(String memberId) {
        String sql = "delete from member where member_id = ?";

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = getConnection();
            ps = con.prepareStatement(sql);

            ps.setString(1, memberId);
            ps.executeUpdate(); // 쿼리 결과 row 수 반환
        } catch (SQLException e) {
            throw sqlExceptionTranslator.translate("task", sql, e);
        } finally {
            JdbcUtils.closeStatement(ps);
            //주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    private Connection getConnection() {
        //주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("get connection = {}, class = {}", con, con.getClass());
        return con;
    }
}
