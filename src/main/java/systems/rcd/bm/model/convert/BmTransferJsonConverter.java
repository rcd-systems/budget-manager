package systems.rcd.bm.model.convert;

import java.util.Collection;

import systems.rcd.bm.model.data.Transfer;
import systems.rcd.fwk.core.format.json.RcdJsonService;
import systems.rcd.fwk.core.format.json.data.RcdJsonArray;
import systems.rcd.fwk.core.format.json.data.RcdJsonObject;

public class BmTransferJsonConverter {

    public RcdJsonArray convert(final Collection<Transfer> transfers) {
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
        return transferJsonArray;
    }
}
