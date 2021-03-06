package by.tarasiuk.ct.controller.command.impl.manager;

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
 * Only for an account with the <code>MANAGER</code> role.
 * Show the page for creating a new account with the <code>FORWARDER</code> role.
 */
public class ShowForwarderCreatorPageCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger();
    private final CompanyServiceImpl companyService = ServiceProvider.getCompanyService();

    /**
     * Search by company ID and transfer the company name to the page.
     *
     * @param content   Request data content.
     * @return          Page path.
     * @see             by.tarasiuk.ct.controller.RequestContent
     */
    @Override
    public String execute(RequestContent content) {
        String page = PagePath.FORWARDER_CREATOR;

        Optional<Object> findEmployee = content.findSessionAttribute(AttributeName.EMPLOYEE);
        if(findEmployee.isPresent()) {
            Employee employee = (Employee) findEmployee.get();
            long companyId = employee.getCompanyId();

            try {
                Optional<Company> findCompany = companyService.findCompanyById(companyId);
                if(findCompany.isPresent()) {
                    Company company = findCompany.get();
                    String companyName = company.getName();
                    content.putRequestAttribute(AttributeName.COMPANY_NAME, companyName);
                }
            } catch (ServiceException e) {
                LOGGER.error("Failed to process the command '{}'.", CommandType.SHOW_FORWARDER_CREATOR_PAGE, e);
                page = PagePath.OFFER_EDITOR;
            }
        }

        LOGGER.info("Command '{}' return path '{}'", CommandType.SHOW_FORWARDER_CREATOR_PAGE, PagePath.FORWARDER_CREATOR);
        return page;
    }
}
