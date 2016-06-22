package systems.rcd.bm.json;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import systems.rcd.bm.model.BmModelService;
import systems.rcd.fwk.core.ctx.RcdContext;
import systems.rcd.fwk.core.format.json.RcdJsonService;
import systems.rcd.fwk.core.format.json.data.RcdJsonArray;
import systems.rcd.fwk.jetty.impl.data.RcdJettyHandler;

public class BmAccountsJsonInterfaceHandler
    implements RcdJettyHandler
{

    @Override
    public void handle( final String target, final HttpServletRequest request, final HttpServletResponse response )
        throws IOException
    {

        final RcdJsonArray jsonArray = RcdJsonService.createJsonArray();
        RcdContext.getService( BmModelService.class ).findAccountNames().stream().sorted().forEach( jsonArray::add );

        if ( jsonArray != null )
        {
            response.setContentType( "application/json; charset=utf-8" );
            response.getWriter().println( RcdJsonService.toJson( jsonArray ) );
        }
    }
}
