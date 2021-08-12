package by.tarasiuk.ct.model.dao.impl;

import by.tarasiuk.ct.exception.DaoException;
import by.tarasiuk.ct.model.dao.BaseDao;
import by.tarasiuk.ct.model.dao.OfferDao;
import by.tarasiuk.ct.model.dao.builder.TradingDaoBuilder;
import by.tarasiuk.ct.model.entity.impl.Trading;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TradingDaoImpl extends BaseDao<Trading> implements OfferDao {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final TradingDaoImpl instance = new TradingDaoImpl();

    private static final String SQL_PROCEDURE_CREATE_TRADING = "{CALL create_trading (?, ?, ?, ?)}";
    private static final String SQL_PROCEDURE_FIND_TRADING_BY_ID = "{CALL find_trading_by_id (?)}";
    private static final String SQL_PROCEDURE_FIND_TRADINGS_BY_OFFER_ID = "{CALL find_tradings_by_offer_id (?)}";
    private static final String SQL_PROCEDURE_FIND_TRADINGS_BY_EMPLOYEE_ID = "{CALL find_tradings_by_employee_id (?)}";
    private static final String SQL_PROCEDURE_UPDATE_TRADING_STATUS_BY_ID = "{CALL update_trading_status_by_id (?, ?)}";

    private static final class IndexCreate {
        private static final int OFFER_ID = 1;
        private static final int EMPLOYEE_ID = 2;
        private static final int TRADING_FREIGHT = 3;
        private static final int TRADING_STATUS = 4;
    }

    private static final class IndexFind {
        private static final int TRADING_ID = 1;
        private static final int OFFER_ID = 1;
        private static final int EMPLOYEE_ID = 1;
    }

    private static final class IndexUpdate {
        private static final int TRADING_ID = 1;
        private static final int TRADING_STATUS = 2;
    }

    private TradingDaoImpl() {
    }

    public static TradingDaoImpl getInstance() {
        return instance;
    }

    public boolean createEntity(Trading trading) throws DaoException {
        Connection connection = connectionPool.getConnection();

        long offerId = trading.getOfferId();
        long employeeId = trading.getEmployeeId();
        float freight = trading.getFreight();
        Trading.Status status = trading.getStatus();

        try (PreparedStatement statement = connection.prepareStatement(SQL_PROCEDURE_CREATE_TRADING)) {
            statement.setLong(IndexCreate.OFFER_ID, offerId);
            statement.setLong(IndexCreate.EMPLOYEE_ID, employeeId);
            statement.setFloat(IndexCreate.TRADING_FREIGHT, freight);
            statement.setString(IndexCreate.TRADING_STATUS, status.name());

            statement.executeUpdate();

            LOGGER.info("Trading was successfully created in the database: {}.", trading);
            return true;    //fixme -> statement.executeUpdate(); (см. выше).
        } catch (SQLException e) {
            LOGGER.error("Failed to create trading in the database: {}.", trading, e);
            throw new DaoException("Failed to create trading in the database: " + trading + ".", e);
        } finally {
            closeConnection(connection);
        }
    }

    public boolean updateTradingStatusById(long tradingId, Trading.Status status) throws DaoException {
        Connection connection = connectionPool.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(SQL_PROCEDURE_UPDATE_TRADING_STATUS_BY_ID)) {
            statement.setLong(IndexUpdate.TRADING_ID, tradingId);
            statement.setString(IndexUpdate.TRADING_STATUS, status.name());

            statement.executeUpdate();

            LOGGER.info("Trading with ID '{}' has been successfully status to '{}' in the database.", tradingId, status);
            return true;    //fixme -> statement.executeUpdate(); (см. выше).
        } catch (SQLException e) {
            LOGGER.error("Failed updating trading with ID '{}' to status '{}' in the database.", tradingId, status, e);
            throw new DaoException("Failed updating trading with ID '" + tradingId + "' to status '" + status + "' in the database.", e);
        } finally {
            closeConnection(connection);
        }
    }

    public List<Trading> findListTradingsByOfferId(long offerId) throws DaoException {
        Connection connection = connectionPool.getConnection();
        List<Trading> tradings = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(SQL_PROCEDURE_FIND_TRADINGS_BY_OFFER_ID)) {
            statement.setLong(IndexFind.OFFER_ID, offerId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Trading trading = TradingDaoBuilder.build(result);
                    tradings.add(trading);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error when performing tradings search by offer id '{}'.", offerId, e);
            throw new DaoException("Error when performing tradings search by offer id '" + offerId + "'.", e);
        } finally {
            closeConnection(connection);
        }

        return tradings;
    }

    public List<Trading> findListTradingsByEmployeeId(long employeeId) throws DaoException {
        Connection connection = connectionPool.getConnection();
        List<Trading> tradings = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(SQL_PROCEDURE_FIND_TRADINGS_BY_EMPLOYEE_ID)) {
            statement.setLong(IndexFind.EMPLOYEE_ID, employeeId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Trading trading = TradingDaoBuilder.build(result);
                    tradings.add(trading);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error when performing tradings search by employee id '{}'.", employeeId, e);
            throw new DaoException("Error when performing tradings search by employee id '" + employeeId + "'.", e);
        } finally {
            closeConnection(connection);
        }

        return tradings;
    }

    @Override
    public List<Trading> findAll() throws DaoException {
        return null;
    }

    @Override
    public boolean updateEntity(Trading entity) throws DaoException {
        return true;
    }

    @Override
    public Optional<Trading> findEntityById(long id) throws DaoException {
        Connection connection = connectionPool.getConnection();
        Optional<Trading> findTrading;

        try (PreparedStatement statement = connection.prepareStatement(SQL_PROCEDURE_FIND_TRADING_BY_ID)) {
            statement.setLong(IndexFind.TRADING_ID, id);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    Trading trading = TradingDaoBuilder.build(result);
                    findTrading = Optional.of(trading);
                    LOGGER.debug("Trading with ID '{}' was fount successfully in the database.", id);
                } else {
                    findTrading = Optional.empty();
                }

            }
        } catch (SQLException e) {
            LOGGER.error("Error when performing tradings search by id '{}'.", id, e);
            throw new DaoException("Error when performing tradings search by id '" + id + "'.", e);
        } finally {
            closeConnection(connection);
        }

        return findTrading;
    }
}