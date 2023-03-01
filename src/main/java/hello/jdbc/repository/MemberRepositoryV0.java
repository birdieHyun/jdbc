package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException {

        String sql = "insert into member(member_id, money) values(?,?)";

        Connection con = null;

        PreparedStatement pstm = null;

        try {
            con = getConnection();
            pstm = con.prepareStatement(sql);
            pstm.setString(1, member.getMemberId());
            pstm.setInt(2, member.getMoney());
            pstm.executeUpdate();  // 준비된 쿼리가 실행이 되게 된다

            return member;
        } catch (SQLException e) {
            log.info("db.error", e); // 로그를 확인하고
            throw e;  // 밖으로 다시 예외를 던진다.
        } finally {
            close(con, pstm, null);
        }

    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money =? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }finally {
            close(con, pstmt, null);
        }
    }


    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.execute();
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }finally {
            close(con, pstmt, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        // 이렇게 각각 트라이캐치로 묶어주어야 서로 영향 주지 않는다.
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.error("error", e);
            }
        }

//        stmt.close(); // pstm 과 stmt의 차이
        /**
         * statement 는 sql을 바로 넣는 것이고, preparedstatement 는 파라미터를 바인딩 할 수 있는 것이다.
         * pstm 은 statement 를 상속받았다.
         */
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.error("error", e);
            }
        }

        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        // 오픈한 역순으로 클로즈 해준다.
        // close 안해주면 연결된 채로 어딘가를 떠돌 수 있기 때문에 close 꼭 해주어야 한다.
    }


    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

//            pstmt.executeUpdate();             // executeUpdate 는 데이터를 변경할 때 사용
            rs = pstmt.executeQuery();           // executeQuery 를 통해 결과를 담은 통인 ResultSet 을 반환

            if (rs.next()) {      // rs.next를 한번은 호출해주어야 실제 데이터가 있는 부분부터 시작한다.
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }else {
                throw new NoSuchElementException("member not found memberId =" + memberId);  // 이렇게 예외를 던질 땐 무엇때문에 예외가 터진건지 명시해야 한다.
            }

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }finally {
            close(con, pstmt, rs);
        }
    }

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }


}
