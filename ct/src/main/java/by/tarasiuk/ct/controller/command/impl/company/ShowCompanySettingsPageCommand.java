package by.tarasiuk.ct.controller.command.impl.company;

import by.tarasiuk.ct.controller.RequestContent;
import by.tarasiuk.ct.controller.command.AttributeName;
import by.tarasiuk.ct.controller.command.Command;
import by.tarasiuk.ct.controller.command.CommandType;
import by.tarasiuk.ct.controller.command.PagePath;
import by.tarasiuk.ct.exception.ServiceException;
import by.tarasiuk.ct.model.entity.impl.Company;
import by.tarasiuk.ct.model.entity.impl.Employee;
import by.tarasiuk.ct.model.service.ServiceProvider;
import by.tarasiuk.ct.model.service.impl.CompanyServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Optional;

/**
 * Show company settings page command.
 */
public class ShowCompanySettingsPageCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger();
    private final CompanyServiceImpl companyService = ServiceProvider.getCompanyService();

    /**
     * Search for a company by employee ID in the database.
     * If successful, getting the company object and transferring it to the page.
     *
     * @param content   Request data content.
     * @return          Page path.
     * @see             by.tarasiuk.ct.controller.RequestContent
     */
    @Override
    public String execute(RequestContent content) {
        String page = PagePath.COMPANY_SETTINGS;

        try {
            Optional<Object> findEmployee = content.findSessionAttribute(AttributeName.EMPLOYEE);
            if(findEmployee.isPresent()) {
                Employee employee = (Employee) findEmployee.get();
                long companyId = employee.getCompanyId();

                Optional<Company> findCompany = companyService.findCompanyById(companyId);
                if(findCompany.isPresent()) {
                    Company company = findCompany.get();
                    content.putRequestAttribute(AttributeName.COMPANY, company);
                    LOGGER.debug("Company with ID '{}' find and send to the '{}'.", companyId, PagePath.COMPANY_SETTINGS);
                }
            }
        } catch (ServiceException e) {
            LOGGER.error("Failed to process the command '{}'.", CommandType.SHOW_COMPANY_SETTINGS_PAGE, e);
            page = PagePath.ACCOUNT_OFFERS;
        }

        LOGGER.info("Command '{}' return path '{}'", CommandType.SHOW_COMPANY_SETTINGS_PAGE, PagePath.COMPANY_SETTINGS);
        return page;
    }
}
