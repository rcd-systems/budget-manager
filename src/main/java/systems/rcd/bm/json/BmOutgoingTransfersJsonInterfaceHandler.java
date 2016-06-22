package systems.rcd.bm.json;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import systems.rcd.bm.model.BmModelService;
import systems.rcd.bm.model.convert.BmTransferJsonConverter;
import systems.rcd.bm.model.data.Transfer;
import systems.rcd.fwk.core.ctx.RcdContext;
import systems.rcd.fwk.core.format.json.RcdJsonService;
import systems.rcd.fwk.core.format.json.data.RcdJsonArray;
import systems.rcd.fwk.jetty.impl.data.RcdJettyHandler;

public class BmOutgoingTransfersJsonInterfaceHandler
    implements RcdJettyHandler
{

    @Override
    public void handle( final String target, final HttpServletRequest request, final HttpServletResponse response )
        throws IOException
    {

        final Integer year = request.getParameter( "year" ) == null ? null : Integer.parseInt( request.getParameter( "year" ) );
        final Integer month = request.getParameter( "month" ) == null ? null : Integer.parseInt( request.getParameter( "month" ) );
        final String account = request.getParameter( "account" );

        final List<Transfer> transfers = RcdContext.getService( BmModelService.class ).findOutgoingTransfers( year, month ).stream().filter(
            transfer -> transfer.isOutgoing( account ) ).collect( Collectors.toList() );
        final RcdJsonArray jsonResponse = new BmTransferJsonConverter().convert( transfers );

        if ( jsonResponse != null )
        {
            response.setContentType( "application/json; charset=utf-8" );
            response.getWriter().println( RcdJsonService.toJson( jsonResponse ) );
        }
        else
        {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
        }

    }
}
