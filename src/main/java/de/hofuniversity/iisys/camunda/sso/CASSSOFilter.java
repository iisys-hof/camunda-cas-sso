package de.hofuniversity.iisys.camunda.sso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.webapp.impl.security.SecurityActions;
import org.camunda.bpm.webapp.impl.security.SecurityActions.SecurityAction;
import org.camunda.bpm.webapp.impl.security.auth.Authentication;
import org.camunda.bpm.webapp.impl.security.auth.Authentications;
import org.camunda.bpm.webapp.impl.security.auth.UserAuthentication;

public class CASSSOFilter implements Filter
{
    @Override
    public void init(FilterConfig arg0) throws ServletException
    {
        //nothing to do
    }
    
    @Override
    public void destroy()
    {
        //nothing to do
    }

    @Override
    public void doFilter(final ServletRequest request,
        final ServletResponse response, final FilterChain filterChain)
            throws IOException, ServletException
    {
        if(request instanceof HttpServletRequest)
        {
            HttpServletRequest req = (HttpServletRequest)request;
            
            //read and insert preauthenticated user
            String user = req.getRemoteUser();
            
            //for which process engine?
            //TODO: make configurable
            String engineName = "default";
            
            Authentications authentications = Authentications.getFromSession(
                req.getSession());
            Authentications.setCurrent(authentications);
            
            
            //create and add authentication
            //TODO: read from register?
//          processEngine.getIdentityService().createGroupQuery().groupMember(username).list();
            List<String> groupIds = new ArrayList<String>();
            groupIds.add("camunda-admin");
            groupIds.add("camunda-user");
            
            Set<String> authorizedApps = new HashSet<String>();
            authorizedApps.add("cockpit");
            authorizedApps.add("tasklist");
            authorizedApps.add("admin");
            
            Authentication auth = new UserAuthentication(user, groupIds,
                engineName, authorizedApps);
            authentications.addAuthentication(auth);
            
            
            Authentications.updateSession(req.getSession(), authentications);
            //continue filter chain
            try
            {
                SecurityActions.runWithAuthentications(new SecurityAction<Void>()
                {
                    public Void execute()
                    {
                        try
                        {
                            filterChain.doFilter(request, response);
                        }
                        catch(Exception e)
                        {
                            throw new RuntimeException(e);
                        }
                        return null;
                    }
                }, authentications);
            }
            finally
            {
                Authentications.clearCurrent();
                Authentications.updateSession(req.getSession(), authentications);
            }
            
            
            //clean up
            Authentications.clearCurrent();
            Authentications.updateSession(req.getSession(), authentications);
        }
    }
    
//    protected ProcessEngine lookupProcessEngine(String engineName)
//    {
//        ServiceLoader<ProcessEngineProvider> serviceLoader =
//            ServiceLoader.load(ProcessEngineProvider.class);
//        Iterator<ProcessEngineProvider> iterator = serviceLoader.iterator();
//
//        if(iterator.hasNext())
//        {
//          ProcessEngineProvider provider = iterator.next();
//          return provider.getProcessEngine(engineName);
//
//        }
//        else
//        {
//          throw new RestException(Status.INTERNAL_SERVER_ERROR,
//              "Could not find an implementation of the "+ProcessEngineProvider.class+"- SPI");
//        }
//
//    }
}
