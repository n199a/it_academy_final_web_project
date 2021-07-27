package by.tarasiuk.ct.util;

import by.tarasiuk.ct.entity.Company;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static by.tarasiuk.ct.manager.RequestAttribute.*;

public class CompanyBuilder {
    private static final Logger LOGGER = LogManager.getLogger();

    private CompanyBuilder() {
    }

    public static Company buildCompany(Map<String, String> companyData) {
        String name = companyData.get(COMPANY_NAME);
        String address = companyData.get(COMPANY_ADDRESS);
        String phoneNumber = companyData.get(COMPANY_PHONE_NUMBER);

        Company company = new Company();
        company.setName(name);
        company.setAddress(address);
        company.setPhoneNumber(phoneNumber);

        LOGGER.info("Company successfully build: {}.", company);
        return company;
    }

    public static Company buildCompany(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong(COMPANY_ID);
        String name = resultSet.getString(COMPANY_NAME);
        String address = resultSet.getString(COMPANY_ADDRESS);
        String phoneNumber = resultSet.getString(COMPANY_PHONE_NUMBER);

        Company company = new Company();
        company.setId(id);
        company.setName(name);
        company.setAddress(address);
        company.setPhoneNumber(phoneNumber);

        LOGGER.info("Company successfully build: {}.", company);
        return company;
    }
}
