package by.tarasiuk.ct.model.dao.impl;

import by.tarasiuk.ct.entity.Account;
import by.tarasiuk.ct.entity.AccountRole;
import by.tarasiuk.ct.entity.AccountStatus;
import by.tarasiuk.ct.entity.Entity;
import by.tarasiuk.ct.exception.DaoException;
import by.tarasiuk.ct.model.dao.AccountDaoAction;
import by.tarasiuk.ct.model.dao.BaseDao;
import by.tarasiuk.ct.manager.DatabaseTableName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class AccountDao extends BaseDao<Account> implements AccountDaoAction {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final AccountDao instance = new AccountDao();

    private static final AccountRoleDao accountRoleDao = daoProvider.getAccountRoleDao();
    private static final AccountStatusDao accountStatusDao = daoProvider.getAccountStatusDao();
    
    private final String SQL_PROCEDURE_CREATE_ACCOUNT = "{call create_account (?, ?, ?, ?, ?, ?, ?, ?)}";   // todo: посмотреть, может тут нужна не процедура, а функция
    private final String SQL_PROCEDURE_GET_ACCOUNT_BY_LOGIN = "{call get_account_by_login (?)}";

    private AccountDao() {
    }

    public static AccountDao getInstance() {
        return instance;
    }

    @Override
    public Optional<Account> getEntityById(int id) throws DaoException {
        return Optional.empty(); // todo
    }

    @Override
    public List<Entity> findAll() throws DaoException {
        return null;
    }

    @Override
    public Account update(Account account) {
        return null;
    }

    @Override
    public Optional<Account> getAccountByLogin(String login) throws DaoException {
        Connection connection = connectionPool.getConnection();
        CallableStatement statement = null;
        Account account = null;

        try {
            statement = connection.prepareCall(SQL_PROCEDURE_GET_ACCOUNT_BY_LOGIN);
            statement.setString(ParameterIndex.LOGIN, login);
            statement.registerOutParameter(DatabaseTableName.ACCOUNT_LOGIN.getColumnName(), Types.VARCHAR);
            statement.registerOutParameter(DatabaseTableName.ACCOUNT_EMAIL.getColumnName(), Types.DATE);
            statement.registerOutParameter(DatabaseTableName.ACCOUNT_REGISTRATION_DATE.getColumnName(), Types.VARCHAR);
            statement.registerOutParameter(DatabaseTableName.ACCOUNT_PHONE_NUMBER.getColumnName(), Types.VARCHAR);
            statement.registerOutParameter(DatabaseTableName.ACCOUNT_ADDRESS.getColumnName(), Types.SMALLINT);
            statement.registerOutParameter(DatabaseTableName.ACCOUNT_ROLE_ID.getColumnName(), Types.SMALLINT);
            statement.registerOutParameter(DatabaseTableName.ACCOUNT_STATUS_ID.getColumnName(), Types.VARCHAR);

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                String loginFromDb = result.getString(DatabaseTableName.ACCOUNT_LOGIN.getColumnName());
                String email = result.getString(DatabaseTableName.ACCOUNT_PASSWORD.getColumnName());
                String registrationDateString = result.getString(DatabaseTableName.ACCOUNT_PASSWORD.getColumnName());
                LocalDate registrationDate = LocalDate.parse(registrationDateString);
                String phoneNumber = result.getString(DatabaseTableName.ACCOUNT_PASSWORD.getColumnName());
                String address = result.getString(DatabaseTableName.ACCOUNT_PASSWORD.getColumnName());

                // правильно ли тут реализовано получения AccountRole
                int accountRoleId = Integer.parseInt(result.getString(DatabaseTableName.ACCOUNT_PASSWORD.getColumnName()));
                Optional<AccountRole> optionalAccountRole = accountRoleDao.getEntityById(accountRoleId);
                AccountRole accountRole = null;
                if(optionalAccountRole.isPresent()) {
                    accountRole = optionalAccountRole.get();
                } else {
                    // todo может ли быть такая ситуация?
                    return Optional.empty();
                }

                int accountStatusId = Integer.parseInt(result.getString(DatabaseTableName.ACCOUNT_PASSWORD.getColumnName()));
                Optional<AccountStatus> optionalAccountStatus = accountStatusDao.getEntityById(accountStatusId);
                AccountStatus accountStatus = null;
                if(optionalAccountStatus.isPresent()) {
                    accountStatus = optionalAccountStatus.get();
                } else {
                    // todo может ли быть такая ситуация?
                    return Optional.empty();
                }

                account = new Account();
                account.setLogin(loginFromDb);
                account.setEmail(email);
                account.setRegistrationDate(registrationDate);
                account.setPhoneNumber(phoneNumber);
                account.setAddress(address);
                account.setAccountRole(accountRole);
                account.setAccountStatus(accountStatus);
            }
        } catch (SQLException e) {
            LOGGER.error("Database access error occurs or this method is called on a closed connection: {}.", connection, e);
            throw new DaoException("Database access error occurs or this method is called on a closed connection: " + connection, e);
        } finally {
            closeStatement(statement);
            closeConnection(connection);
        }

        return Optional.of(account);
    }

    /**
     * It is generally good practice to release resources as soon as you are finished with them to avoid tying up
     * database resources.
     * @param account
     * @return
     * @throws DaoException
     * @throws SQLException Database access error occurs when trying to close <code>Statement</code> object.
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/sql/Statement.html#close--">About release statement.</a>
     */
    @Override
    public boolean createAccount(Account account, String encryptPassword) throws DaoException {
        Connection connection = connectionPool.getConnection();
        CallableStatement statement;
        boolean operationResult;

        try {
            statement = connection.prepareCall(SQL_PROCEDURE_CREATE_ACCOUNT);

            statement.setString(ParameterIndex.LOGIN, account.getLogin());
            statement.setString(ParameterIndex.PASSWORD, encryptPassword);
            statement.setString(ParameterIndex.EMAIL, account.getEmail());
            DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;
            LocalDate localDate = account.getRegistrationDate();
            String registrationDate = localDate.format(dateFormatter);
            statement.setString(ParameterIndex.REGISTRATION_DATE, registrationDate);
            statement.setString(ParameterIndex.PHONE_NUMBER, account.getPhoneNumber());
            statement.setString(ParameterIndex.ADDRESS, account.getAddress());
            statement.setString(ParameterIndex.ACCOUNT_ROLE_NAME, account.getAccountRole().toString());
            statement.setString(ParameterIndex.ACCOUNT_STATUS_NAME, account.getAccountStatus().toString());
        } catch (SQLException e) {
            LOGGER.error("Failed to create CallableStatement object.");
            throw new DaoException("Failed to create CallableStatement object.");
        }

        try {
            operationResult = statement.execute();
        } catch (SQLException e) {
            LOGGER.error("Failed to execute SQL statement: {}.", SQL_PROCEDURE_CREATE_ACCOUNT);
            throw new DaoException("Failed to execute SQL statement: " + SQL_PROCEDURE_CREATE_ACCOUNT, e);
        } finally {
            closeStatement(statement);
            closeConnection(connection);
        }

        LOGGER.info("Account was successfully created into database: {}.", account);
        return operationResult;
    }

    private static class ParameterIndex {
        private static final int LOGIN = 1;
        private static final int PASSWORD = 2;
        private static final int EMAIL = 3;
        private static final int REGISTRATION_DATE = 4;
        private static final int PHONE_NUMBER = 5;
        private static final int ADDRESS = 6;
        private static final int ACCOUNT_ROLE_NAME = 7;
        private static final int ACCOUNT_STATUS_NAME = 8;
    }
}
