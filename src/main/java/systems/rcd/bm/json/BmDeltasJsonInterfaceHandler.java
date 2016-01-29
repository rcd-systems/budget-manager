package systems.rcd.bm.json;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import systems.rcd.bm.model.BmModelService;
import systems.rcd.fwk.core.ctx.RcdContext;
import systems.rcd.fwk.core.format.json.RcdJsonService;
import systems.rcd.fwk.core.format.json.data.RcdJsonArray;
import systems.rcd.fwk.core.format.json.data.RcdJsonObject;
import systems.rcd.fwk.jetty.impl.data.RcdJettyHandler;

public class BmDeltasJsonInterfaceHandler implements RcdJettyHandler {

    @Override
    public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {

        final Integer year = request.getParameter("year") == null ? null
                : Integer.parseInt(request.getParameter("year"));
        final Integer month = request.getParameter("month") == null ? null
                : Integer.parseInt(request.getParameter("month"));
        final String account = request.getParameter("account");

        final double initialAmount = RcdContext.getService(BmModelService.class)
                .findInitialAmount(year, month, account);
        final double[] deltas = RcdContext.getService(BmModelService.class)
                .findDeltas(year, month, account);

        final RcdJsonObject jsonResponse = RcdJsonService.createJsonObject();
        jsonResponse.put("initial", initialAmount);
        final RcdJsonArray deltaJsonArray = jsonResponse.createArray("deltas");
        for (final double delta : deltas) {
            deltaJsonArray.add(delta);
        }

        response.setContentType("application/json; charset=utf-8");
        response.getWriter()
        .println(RcdJsonService.toJson(jsonResponse));
    }
}
