package by.tarasiuk.ct.controller.command.impl.offer;

import by.tarasiuk.ct.controller.RequestContent;
import by.tarasiuk.ct.controller.command.Command;
import by.tarasiuk.ct.controller.command.CommandType;
import by.tarasiuk.ct.controller.command.PagePath;
import by.tarasiuk.ct.exception.ServiceException;
import by.tarasiuk.ct.model.entity.impl.Account;
import by.tarasiuk.ct.model.entity.impl.Employee;
import by.tarasiuk.ct.model.service.ServiceProvider;
import by.tarasiuk.ct.model.service.impl.EmployeeServiceImpl;
import by.tarasiuk.ct.model.service.impl.OfferServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Optional;

import static by.tarasiuk.ct.controller.command.AttributeName.ACCOUNT;
import static by.tarasiuk.ct.controller.command.AttributeName.MESSAGE_INCORRECT_OFFER_DATA;

/**
 * Create a new offer command.
 */
public class CreateOfferCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger();
    private final OfferServiceImpl offerService = ServiceProvider.getOfferService();
    private final EmployeeServiceImpl employeeService = ServiceProvider.getEmployeeService();

    /**
     * The validity of the offer data is checked.
     * If successful, create an offer in the database.
     * Otherwise, it displays the corresponding message and returns to the interrupting page.
     *
     * @param content   Request data content.
     * @return          Page path.
     * @see             by.tarasiuk.ct.controller.RequestContent
     */
    @Override
    public String execute(RequestContent content) {
        String page = PagePath.CREATE_OFFER;
        Map<String, String> offerData = content.getRequestParameters();
        Map<String, Object> sessionAttributes = content.getSessionAttributes();

        try {
            if(!offerService.isValidOfferData(offerData)) {
                content.putRequestAttributes(offerData);
                content.putRequestAttribute(MESSAGE_INCORRECT_OFFER_DATA, true);
            } else {
                Account account = (Account) sessionAttributes.get(ACCOUNT);
                long accountId = account.getId();
                Optional<Employee> findEmployee = employeeService.findEmployeeByAccountId(accountId);
                if(findEmployee.isPresent()) {
                    Employee employee = findEmployee.get();
                    long employeeId = employee.getId();
                    offerService.createOffer(employeeId, offerData);

                    page = PagePath.OFFER_CREATED_INFO;
                }
            }
        } catch (ServiceException e) {
            LOGGER.error("Failed to process the command '{}'.", CommandType.CREATE_OFFER, e);
        }

        return page;
    }
}
