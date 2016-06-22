package systems.rcd.bm.json;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import systems.rcd.bm.model.BmModelService;
import systems.rcd.bm.model.data.Type;
import systems.rcd.fwk.core.ctx.RcdContext;
import systems.rcd.fwk.core.format.json.RcdJsonService;
import systems.rcd.fwk.core.format.json.data.RcdJsonArray;
import systems.rcd.fwk.core.format.json.data.RcdJsonObject;
import systems.rcd.fwk.jetty.impl.data.RcdJettyHandler;

public class BmBudgetJsonInterfaceHandler
    implements RcdJettyHandler
{

    @Override
    public void handle( final String target, final HttpServletRequest request, final HttpServletResponse response )
        throws IOException
    {

        final Integer year = request.getParameter( "year" ) == null ? null : Integer.parseInt( request.getParameter( "year" ) );
        final Integer month = request.getParameter( "month" ) == null ? null : Integer.parseInt( request.getParameter( "month" ) );
        final String typeName = request.getParameter( "type" );

        final RcdJsonArray jsonResponse = RcdJsonService.createJsonArray();

        final Type type = RcdContext.getService( BmModelService.class ).findType( typeName );

        for ( final Type subType : type.getSubTypes() )
        {
            final RcdJsonObject subTypeJsonObject = toJsonObject( year, month, subType );
            jsonResponse.add( subTypeJsonObject );
        }

        final RcdJsonObject typeJsonObject = toJsonObject( year, month, type );
        jsonResponse.add( typeJsonObject );

        response.setContentType( "application/json; charset=utf-8" );
        response.getWriter().println( RcdJsonService.toString( jsonResponse ) );
    }

    private RcdJsonObject toJsonObject( final Integer year, final Integer month, final Type type )
    {
        final double balance = RcdContext.getService( BmModelService.class ).findTypeBalance( year, month, type.getName() );

        final RcdJsonObject jsonObject = RcdJsonService.createJsonObject();
        jsonObject.put( "name", type.getName() );
        jsonObject.put( "balance", balance );

        return jsonObject;
    }
}
