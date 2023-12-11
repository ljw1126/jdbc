package hello.jdbc.exception.tranlatior;

import hello.jdbc.connection.ConnectionConst;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;

@Slf4j
public class SpringExceptionTranslatorTest {
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    }

    @DisplayName("")
    @Test
    void sqlExceptionErrorCode() {
        String sql = "select bad grammer";

        try {
            Connection con = dataSource.getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.executeQuery();
        } catch(SQLException e) {
            assertThat(e.getErrorCode()).isEqualTo(42122); // h2 code

            log.info("errorCode = {}", e.getErrorCode());
            log.info("error", e);
        }
    }

    @DisplayName("")
    @Test
    void exceptionTranslator() {
        String sql = "select bad grammer";

        try {
            Connection con = dataSource.getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.executeQuery();
        } catch(SQLException e) {
            assertThat(e.getErrorCode()).isEqualTo(42122); // h2 code

            SQLErrorCodeSQLExceptionTranslator translator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
            DataAccessException exception = translator.translate("select", sql, e);
            log.info("exception", exception);

            assertThat(exception.getClass()).isEqualTo(BadSqlGrammarException.class);
        }
    }
}
