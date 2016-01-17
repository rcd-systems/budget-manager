package systems.rcd.bm.json;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import systems.rcd.bm.model.BmModelService;
import systems.rcd.bm.model.data.Transfer;
import systems.rcd.fwk.core.ctx.RcdContext;
import systems.rcd.fwk.core.format.json.RcdJsonService;
import systems.rcd.fwk.core.format.json.data.RcdJsonArray;
import systems.rcd.fwk.core.format.json.data.RcdJsonObject;
import systems.rcd.fwk.jetty.impl.data.RcdJettyHandler;

public class BmJsonInterfaceHandler implements RcdJettyHandler {

    private static final Pattern TRANSFERS_PATTERN = Pattern.compile("/transfer(?:/([0-9]{4})(?:/([0-9]{1,2}))?)?");

    @Override
    public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {

        final Matcher matcher = TRANSFERS_PATTERN.matcher(target);
        if (matcher.matches()) {

            final Integer year = matcher.group(1) == null ? null : Integer.parseInt(matcher.group(1));
            final Integer month = matcher.group(2) == null ? null : Integer.parseInt(matcher.group(2));

            final List<Transfer> transfers = RcdContext.getService(BmModelService.class)
                    .findTransfers(year, month);

            final RcdJsonArray transferJsonArray = RcdJsonService.createJsonArray();
            if (transfers != null) {
                transfers.stream()
                        .map(transfer -> {
                            final RcdJsonObject transferJsonObject = RcdJsonService.createJsonObject();
                            transferJsonObject.put("type", transfer.getType()
                                    .toString());
                            transferJsonObject.put("date", transfer.getDate()
                                    .toString());
                            transferJsonObject.put("amount", transfer.getAmount());
                            transferJsonObject.put("currency", transfer.getCurrency()
                                    .toString());
                            if (transfer.getComments() != null) {
                                transferJsonObject.put("comments", transfer.getComments());
                            }
                            if (transfer.getSourceAccount() != null) {
                                transferJsonObject.put("srcAccount", transfer.getSourceAccount()
                                        .toString());
                            }
                            if (transfer.getSourceDate() != null) {
                                transferJsonObject.put("srcDate", transfer.getSourceDate()
                                        .toString());
                            }
                            if (transfer.getTargetAccount() != null) {
                                transferJsonObject.put("tgtAccount", transfer.getTargetAccount()
                                        .toString());
                            }
                            if (transfer.getTargetDate() != null) {
                                transferJsonObject.put("tgtDate", transfer.getTargetDate()
                                        .toString());
                            }

                            return transferJsonObject;
                        })
                        .forEach(transferJsonArray::add);
            }

            response.setContentType("application/json; charset=utf-8");
            response.getWriter()
            .println(RcdJsonService.toJson(transferJsonArray));
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }
}
