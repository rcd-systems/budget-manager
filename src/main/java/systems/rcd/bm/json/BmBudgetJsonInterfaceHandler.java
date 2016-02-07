package systems.rcd.bm.json;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import systems.rcd.bm.model.BmModelService;
import systems.rcd.bm.model.data.Account;
import systems.rcd.bm.model.data.Type;
import systems.rcd.fwk.core.ctx.RcdContext;
import systems.rcd.fwk.core.format.json.RcdJsonService;
import systems.rcd.fwk.core.format.json.data.RcdJsonArray;
import systems.rcd.fwk.core.format.json.data.RcdJsonObject;
import systems.rcd.fwk.jetty.impl.data.RcdJettyHandler;

public class BmBudgetJsonInterfaceHandler implements RcdJettyHandler {

    @Override
    public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {

        final Integer year = request.getParameter("year") == null ? null
                : Integer.parseInt(request.getParameter("year"));
        final Integer month = request.getParameter("month") == null ? null
                : Integer.parseInt(request.getParameter("month"));
        final String typeName = request.getParameter("type");

        final Integer endYear = year + (month == null ? 1 : 0);
        final Integer endMonth = month == null ? null : (month + 1) % 12;

        final RcdJsonArray jsonResponse = RcdJsonService.createJsonArray();

        for (final Type subType : RcdContext.getService(BmModelService.class)
                .findTypes()) {
            // jsonResponse.add(toJsonObject(subType, year, month));
        }

        response.setContentType("application/json; charset=utf-8");
        response.getWriter()
                .println(RcdJsonService.toJson(jsonResponse));
    }

    private RcdJsonObject toJsonObject(final Account account, final Integer year, final Integer month,
            final Integer endYear, final Integer endMonth) {
        final double start = RcdContext.getService(BmModelService.class)
                .findInitialAmount(year, month, account.getName());
        final double end = RcdContext.getService(BmModelService.class)
                .findInitialAmount(endYear, endMonth, account.getName());

        final RcdJsonObject subAccountJsonObject = RcdJsonService.createJsonObject();
        subAccountJsonObject.put("name", account.getName());
        subAccountJsonObject.put("start", start);
        subAccountJsonObject.put("end", end);
        subAccountJsonObject.put("delta", end - start);

        return subAccountJsonObject;
    }
}
