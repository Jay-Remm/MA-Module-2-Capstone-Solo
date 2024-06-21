package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.MakeTransferDto;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcServicesDao implements ServicesDao{

    private final JdbcTemplate jdbcTemplate;
    private final UserDao userDao;

    public JdbcServicesDao(JdbcTemplate jdbcTemplate, UserDao userDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDao = userDao;
    }

    @Override
    public BigDecimal getBalance(int userId) {
        BigDecimal balance = null;
        String sql = "SELECT balance FROM account WHERE user_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            if (results.next()) {
                balance = results.getBigDecimal("balance");
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Cannot connect to server or database.", e);
        }
        return balance;
    }

    @Override
    public Transfer getTransferById(int id) {
        Transfer transfer = null;
        String sql = "SELECT transfer_id, tt.transfer_type_desc, ts.transfer_status_desc, account_from, account_to, amount " +
                        "FROM transfer " +
                            "JOIN transfer_type tt USING(transfer_type_id) " +
                            "JOIN transfer_status ts USING(transfer_status_id) " +
                        "WHERE transfer_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if (results.next()) {
                transfer = mapRowToTransfer(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Cannot connect to server or database", e);
        }
        return transfer;
    }

    @Override
    public Transfer makeTransfer(MakeTransferDto transfer) {
        Transfer newTransfer = null;
        // Make new transfer
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                            "VALUES (?, ?, ?, ?, ?) " +
                            "RETURNING transfer_id;";
        try {
            int newTransferId = jdbcTemplate.queryForObject(sql, int.class, transfer.getTransferType(), transfer.getTransferStatus(), getAccountIdFromUser(transfer.getUserIdFrom()), getAccountIdFromUser(transfer.getUserIdTo()), transfer.getAmount());
            newTransfer = getTransferById(newTransferId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return newTransfer;
    }

    // Only call this method when the transfer is approved by the accountFrom.
    // @Transactional annotation acts like transactions in SQL, if both calls don't work, it rolls back
    @Transactional
    @Override
    public void pushTransfer(int id) {
        Transfer transfer = getTransferById(id);
        String sqlUpdateFrom = "UPDATE account SET balance = balance - ? WHERE account_id = ?;";
        String sqlUpdateTo = "UPDATE account SET balance = balance + ? WHERE account_id = ?;";

        try {
            jdbcTemplate.update(sqlUpdateFrom, transfer.getAmount(), transfer.getAccountFromId());
            jdbcTemplate.update(sqlUpdateTo, transfer.getAmount(), transfer.getAccountToId());
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Cannot connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    @Override
    public List<Transfer> getTransfers(int userId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, tt.transfer_type_desc, ts.transfer_status_desc, account_from, account_to, amount " +
                        "FROM transfer " +
                            "JOIN transfer_type tt USING(transfer_type_id) " +
                            "JOIN transfer_status ts USING(transfer_status_id) " +
                        "WHERE account_from = ? OR account_to = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
            while (results.next()) {
                Transfer transfer = mapRowToTransfer(results);
                transfers.add(transfer);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Cannot connect to server or database", e);
        }
        return transfers;
    }

    @Override
    public List<Transfer> getPendingTransfers(int userId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, tt.transfer_type_desc, ts.transfer_status_desc, account_from, account_to, amount " +
                        "FROM transfer " +
                            "JOIN transfer_type tt USING(transfer_type_id) " +
                            "JOIN transfer_status ts USING(transfer_status_id) " +
                        "WHERE account_from = ? AND transfer_status_id = 1;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            while (results.next()) {
                Transfer transfer = mapRowToTransfer(results);
                transfers.add(transfer);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Cannot connect to server or database", e);
        }
        return transfers;
    }

    private int getAccountIdFromUser(int userId) {
        int accountId = 0;
        String sql = "SELECT account_id FROM account WHERE user_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            if (results.next()) {
                accountId = results.getInt("account_id");
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return accountId;
    }

    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        User userFrom = null;
        User userTo = null;
        transfer.setId(rs.getInt("transfer_id"));
        transfer.setType(rs.getString("transfer_type_desc"));
        transfer.setStatus(rs.getString("transfer_status_desc"));
        userFrom = userDao.getUserByAccount(rs.getInt("account_from"));
        transfer.setAccountFrom(userFrom.getUsername());
        transfer.setAccountFromId(rs.getInt("account_from"));
        userTo = userDao.getUserByAccount(rs.getInt("account_to"));
        transfer.setAccountTo(userTo.getUsername());
        transfer.setAccountToId(rs.getInt("account_to"));
        transfer.setAmount(rs.getBigDecimal("amount"));
        return transfer;
    }
}
