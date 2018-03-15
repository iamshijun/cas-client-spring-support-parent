package com.github.iamshijun.passport.cas.filter;

import com.github.iamshijun.passport.cas.filter.support.AbstractServerBasedCompositeCasFilter;
import com.github.iamshijun.passport.cas.handler.TicketValidationHandler;
import org.jasig.cas.client.validation.AbstractTicketValidationFilter;
import org.jasig.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.jasig.cas.client.validation.DelegatingTicketValidationFilter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * @author aimysaber@gmail.com
 */
@Deprecated
public class CompositeTicketValidationFilter2
        extends AbstractServerBasedCompositeCasFilter<AbstractTicketValidationFilter> {

    private TicketValidationHandler ticketValidationHandler;

    //@NotNull
    private Class<? extends AbstractTicketValidationFilter> ticketValidationFilterType =
            Cas30ProxyReceivingTicketValidationFilter.class;

    public void setTicketValidationFilterType(
            Class<? extends AbstractTicketValidationFilter> ticketValidationFilterType) {
        this.ticketValidationFilterType = ticketValidationFilterType;
    }


    public void setTicketValidationHandler(TicketValidationHandler ticketValidationHandler) {
        this.ticketValidationHandler = ticketValidationHandler;
    }

    @Override
    protected AbstractTicketValidationFilter createInnerFilter(
            String serverName, FilterConfig config, HttpServletRequest request) throws ServletException {

        DelegatingTicketValidationFilter ticketValidationFilter =
                new DelegatingTicketValidationFilter(ticketValidationFilterType);

        ticketValidationFilter.setInnerServerName(serverName);
        ticketValidationFilter.setTicketValidationHandler(ticketValidationHandler);

        ticketValidationFilter.init(config);

        return ticketValidationFilter;
    }


}

 