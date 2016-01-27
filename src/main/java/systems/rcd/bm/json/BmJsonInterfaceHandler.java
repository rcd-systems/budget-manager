package systems.rcd.bm.json;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import systems.rcd.bm.model.BmModelService;
import systems.rcd.bm.model.convert.BmTransferJsonConverter;
import systems.rcd.bm.model.data.Transfer;
import systems.rcd.fwk.core.ctx.RcdContext;
import systems.rcd.fwk.core.format.json.RcdJsonService;
import systems.rcd.fwk.core.format.json.data.RcdJsonArray;
import systems.rcd.fwk.core.format.json.data.RcdJsonValue;
import systems.rcd.fwk.jetty.impl.data.RcdJettyHandler;

public class BmJsonInterfaceHandler implements RcdJettyHandler {

    private static final Pattern TRANSFERS_PATTERN = Pattern.compile("/transfers(?:/([0-9]{4})(?:/([0-9]{1,2}))?)?");

    @Override
    public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {

        RcdJsonValue jsonResponse = null;
        if (target.startsWith("/transfers")) {
            final Matcher matcher = TRANSFERS_PATTERN.matcher(target);
            if (matcher.matches()) {
                final Integer year = matcher.group(1) == null ? null : Integer.parseInt(matcher.group(1));
                final Integer month = matcher.group(2) == null ? null : Integer.parseInt(matcher.group(2));
                final List<Transfer> transfers = RcdContext.getService(BmModelService.class)
                        .findTransfers(year, month);
                jsonResponse = new BmTransferJsonConverter().convert(transfers);
            }
        } else if (target.startsWith("/years")) {
            final RcdJsonArray jsonArray = RcdJsonService.createJsonArray();
            RcdContext.getService(BmModelService.class)
                    .findYears()
                    .forEach(jsonArray::add);
            jsonResponse = jsonArray;
        }

        if (jsonResponse != null) {
            response.setContentType("application/json; charset=utf-8");
            response.getWriter()
            .println(RcdJsonService.toJson(jsonResponse));
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }
}
