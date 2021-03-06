package by.tarasiuk.ct.model.service.impl;

import by.tarasiuk.ct.exception.DaoException;
import by.tarasiuk.ct.exception.ServiceException;
import by.tarasiuk.ct.model.dao.DaoProvider;
import by.tarasiuk.ct.model.dao.builder.CompanyDaoBuilder;
import by.tarasiuk.ct.model.dao.impl.CompanyDaoImpl;
import by.tarasiuk.ct.model.entity.impl.Company;
import by.tarasiuk.ct.model.service.CompanyService;
import by.tarasiuk.ct.validator.CompanyValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Optional;

public class CompanyServiceImpl implements CompanyService {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final CompanyServiceImpl companyService = new CompanyServiceImpl();
    private final CompanyDaoImpl companyDao = DaoProvider.getCompanyDao();

    private CompanyServiceImpl() {
    }

    public static CompanyServiceImpl getInstance() {
        return companyService;
    }

    @Override
    public boolean validateCompany(Map<String, String> companyData) throws ServiceException {
        return CompanyValidator.isValidCompany(companyData);
    }

    public boolean validateCompanyData(String address, String phoneNumber) throws ServiceException {
        return CompanyValidator.isValidCompanyData(address, phoneNumber);
    }

    @Override
    public boolean createCompany(Map<String, String> companyData) throws ServiceException {
        Company company = CompanyDaoBuilder.buildCompany(companyData);

        try {
            return companyDao.createEntity(company);
        } catch (DaoException e) {
            LOGGER.error("Error while creating: '{}'.", company, e);
            throw new ServiceException("Error while creating: '" + company + "'.", e);
        }
    }

    @Override
    public Optional<Company> findCompanyByName(String name) throws ServiceException {
        Optional<Company> findCompany;

        try {
            findCompany = companyDao.findCompanyByName(name);

            LOGGER.info(findCompany.isPresent()
                    ? "Successfully was find company by name '{}'."
                    : "Company with name '{}' not found in the database.", name);
        } catch (DaoException e) {
            LOGGER.error("Error when searching for an company by name '{}'.", name, e);
            throw new ServiceException("Error when searching for an company by name '" + name + "'.", e);
        }

        return findCompany;
    }

    @Override
    public Optional<Company> findCompanyById(long companyId) throws ServiceException {
        Optional<Company> findCompany;

        try {
            findCompany = companyDao.findEntityById(companyId);

            LOGGER.info(findCompany.isPresent()
                    ? "Successfully was find company by id '{}'."
                    : "Company with id '{}' not found in the database.", companyId);
        } catch (DaoException e) {
            LOGGER.error("Error when searching for an company by id '{}'.", companyId, e);
            throw new ServiceException("Error when searching for an company by id '" + companyId + "'.", e);
        }

        return findCompany;
    }

    public boolean updateCompanyDataById(long companyId, String address, String phoneNumber) throws ServiceException {
        boolean result = false;

        try {
            Optional<Company> findCompany = companyDao.findEntityById(companyId);

            if(findCompany.isPresent()) {
                Company company = findCompany.get();
                company.setAddress(address);
                company.setPhoneNumber(phoneNumber);

                result = companyDao.updateEntity(company);
            }

        } catch (DaoException e) {
            LOGGER.error("Error when updating company data by company id '{}'.", companyId, e);
            throw new ServiceException("Error when updating company data by company id '" + companyId + "'.", e);
        }

        LOGGER.info(result
                ? "Successfully updated company data for company with ID '{}'."
                : "Company data for company with ID '{}' not updated.", companyId);

        return result;
    }
}
