package by.tarasiuk.ct.model.dao.impl;

import by.tarasiuk.ct.exception.DaoException;
import by.tarasiuk.ct.model.dao.BaseDao;
import by.tarasiuk.ct.model.dao.OfferDao;
import by.tarasiuk.ct.model.dao.builder.OfferDaoBuilder;
import by.tarasiuk.ct.model.entity.impl.Offer;
import by.tarasiuk.ct.model.entity.impl.Offer.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OfferDaoImpl extends BaseDao<Offer> implements OfferDao {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final OfferDaoImpl instance = new OfferDaoImpl();

    private static final String SQL_PROCEDURE_CREATE_OFFER = "{CALL create_offer (?, ?, ?, ?, ?, ?, ?, ?, ?)}";
    private static final String SQL_PROCEDURE_FIND_ALL_OFFERS = "{CALL find_all_offers ()}";
    private static final String SQL_PROCEDURE_FIND_OPEN_OFFERS = "{CALL find_open_offers ()}";
    private static final String SQL_PROCEDURE_FIND_OFFER_BY_ID = "{CALL find_offer_by_id (?)}";
    private static final String SQL_PROCEDURE_FIND_ALL_OFFERS_BY_EMPLOYEE_ID = "{CALL find_all_offers_by_employee_id (?)}";
    private static final String SQL_PROCEDURE_UPDATE_OFFER_STATUS_BY_ID = "{CALL update_offer_status_by_id (?, ?)}";
    private static final String SQL_PROCEDURE_UPDATE_OFFER_BY_ID = "{CALL update_offer_by_id (?, ?, ?, ?, ?, ?, ?)}";

    private OfferDaoImpl() {
    }

    public static OfferDaoImpl getInstance() {
        return instance;
    }

    private static final class IndexCreate {
        private static final int EMPLOYEE_ID = 1;
        private static final int PRODUCT_NAME = 2;
        private static final int PRODUCT_WEIGHT = 3;
        private static final int PRODUCT_VOLUME = 4;
        private static final int ADDRESS_FROM = 5;
        private static final int ADDRESS_TO = 6;
        private static final int FREIGHT = 7;
        private static final int CREATION_DATE = 8;
        private static final int STATUS = 9;
    }

    private static final class IndexUpdate {
        private static final int OFFER_ID = 1;
        private static final int PRODUCT_NAME = 2;
        private static final int PRODUCT_WEIGHT = 3;
        private static final int PRODUCT_VOLUME = 4;
        private static final int ADDRESS_FROM = 5;
        private static final int ADDRESS_TO = 6;
        private static final int FREIGHT = 7;
    }

    private static final class IndexFind {
        private static final int OFFER_ID = 1;
        private static final int EMPLOYEE_ID = 1;
    }

    private static final class IndexStatusUpdate {
        private static final int OFFER_ID = 1;
        private static final int OFFER_STATUS = 2;
    }

    @Override
    public boolean createEntity(Offer offer) throws DaoException {
        boolean result;

        long employeeId = offer.getEmployeeId();
        String productName = offer.getProductName();
        float productWeight = offer.getProductWeight();
        float productVolume = offer.getProductVolume();
        String addressFrom = offer.getAddressFrom();
        String addressTo = offer.getAddressTo();
        float freight = offer.getFreight();
        LocalDate creationDate = offer.getCreationDate();
        Status status = offer.getStatus();

        try (Connection connection = connectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_PROCEDURE_CREATE_OFFER)) {
            statement.setLong(IndexCreate.EMPLOYEE_ID, employeeId);
            statement.setString(IndexCreate.PRODUCT_NAME, productName);
            statement.setFloat(IndexCreate.PRODUCT_WEIGHT, productWeight);
            statement.setFloat(IndexCreate.PRODUCT_VOLUME, productVolume);
            statement.setString(IndexCreate.ADDRESS_FROM, addressFrom);
            statement.setString(IndexCreate.ADDRESS_TO, addressTo);
            statement.setFloat(IndexCreate.FREIGHT, freight);
            statement.setDate(IndexCreate.CREATION_DATE, Date.valueOf(creationDate));
            statement.setString(IndexCreate.STATUS, status.name());

            int rowCount = statement.executeUpdate();
            result = rowCount == 1;

            LOGGER.info(result ? "Offer was successfully created in the database: {}."
                    : "Failed to create offer '{}'.", offer);
        } catch (SQLException e) {
            LOGGER.error("Failed to create offer in the database: {}.", offer, e);
            throw new DaoException("Failed to create offer in the database: " + offer + ".", e);
        }

        return result;
    }

    @Override
    public boolean updateEntity(Offer entity) throws DaoException {
        boolean result;

        long offerId = entity.getId();
        String productName = entity.getProductName();
        float productWeight = entity.getProductWeight();
        float productVolume = entity.getProductVolume();
        String addressFrom = entity.getAddressFrom();
        String addressTo = entity.getAddressTo();
        float offerFreight = entity.getFreight();

        try (Connection connection = connectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_PROCEDURE_UPDATE_OFFER_BY_ID)) {
            statement.setLong(IndexUpdate.OFFER_ID, offerId);
            statement.setString(IndexUpdate.PRODUCT_NAME, productName);
            statement.setFloat(IndexUpdate.PRODUCT_WEIGHT, productWeight);
            statement.setFloat(IndexUpdate.PRODUCT_VOLUME, productVolume);
            statement.setString(IndexUpdate.ADDRESS_FROM, addressFrom);
            statement.setString(IndexUpdate.ADDRESS_TO, addressTo);
            statement.setFloat(IndexUpdate.FREIGHT, offerFreight);

            int rowCount = statement.executeUpdate();
            result = rowCount == 1;

            LOGGER.info(result ? "Offer '{}' has been successfully updated in the database."
                    : "Failed to update offer '{}'.", entity);
        } catch (SQLException e) {
            LOGGER.error("Failed updating offer '{}' in the database.", entity, e);
            throw new DaoException("Failed updating offer '" + entity + "' in the database.", e);
        }

        return result;
    }

    @Override
    public boolean updateOfferStatusById(long id, Offer.Status status) throws DaoException {
        boolean result;

        try (Connection connection = connectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_PROCEDURE_UPDATE_OFFER_STATUS_BY_ID)) {
            statement.setLong(IndexStatusUpdate.OFFER_ID, id);
            statement.setString(IndexStatusUpdate.OFFER_STATUS, status.toString());

            int rowCount = statement.executeUpdate();
            result = rowCount == 1;

            LOGGER.info(result ? "Offer with id '{}' has successfully updated status to '{}' in the database."
                    : "Failed to update offer status by offer ID '{}'", id, status);
        } catch (SQLException e) {
            LOGGER.error("Failed updating offer with ID '{}' to status '{}' in the database.", id, status, e);
            throw new DaoException("Failed updating offer with ID '" + id + "' to status '" + status + "' in the database.", e);
        }

        return result;
    }

    @Override
    public List<Offer> findAll() throws DaoException {
        List<Offer> offers = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SQL_PROCEDURE_FIND_ALL_OFFERS)) {
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        Offer offer = OfferDaoBuilder.build(result);
                        offers.add(offer);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error when find all offers.", e);
            throw new DaoException("Error when find all offers.", e);
        }

        return offers;
    }

    @Override
    public List<Offer> findOpenOffers() throws DaoException {
        List<Offer> offers = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SQL_PROCEDURE_FIND_OPEN_OFFERS)) {
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        Offer offer = OfferDaoBuilder.build(result);
                        offers.add(offer);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error when find all offers.", e);
            throw new DaoException("Error when find all offers.", e);
        }

        return offers;
    }

    @Override
    public List<Offer> findOfferListByEmployeeId(long employeeId) throws DaoException {
        List<Offer> offers = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SQL_PROCEDURE_FIND_ALL_OFFERS_BY_EMPLOYEE_ID)) {
                statement.setLong(IndexFind.EMPLOYEE_ID, employeeId);

                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        Offer offer = OfferDaoBuilder.build(result);
                        offers.add(offer);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error when performing offers search by employee id '{}'.", employeeId, e);
            throw new DaoException("Error when performing offers search by employee id '" + employeeId + "'.", e);
        }

        return offers;
    }

    @Override
    public Optional<Offer> findEntityById(long id) throws DaoException {
        Optional<Offer> findOffer = Optional.empty();

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SQL_PROCEDURE_FIND_OFFER_BY_ID)) {
                statement.setLong(IndexFind.OFFER_ID, id);

                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        Offer offer = OfferDaoBuilder.build(result);
                        findOffer = Optional.of(offer);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error when performing offer search by id '{}'.", id, e);
            throw new DaoException("Error when performing offer search by id '" + id + "'.", e);
        }

        return findOffer;
    }
}
