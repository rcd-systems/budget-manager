package systems.rcd.bm.model.convert;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import systems.rcd.bm.model.BmModelConstants;
import systems.rcd.bm.model.data.Account;
import systems.rcd.bm.model.data.Currency;
import systems.rcd.bm.model.data.Transfer;
import systems.rcd.bm.model.data.Type;
import systems.rcd.fwk.core.format.xls.data.RcdXlsRow;
import systems.rcd.fwk.core.format.xls.data.RcdXlsSheet;
import systems.rcd.fwk.core.log.RcdLogService;

public class BmXlsTransferConverter implements BmModelConstants {
    private Map<String, Type> typeMap;
    private Map<String, Account> accountMap;

    private final List<String> errors = new LinkedList<>();

    public List<String> getErrors() {
        return errors;
    }

    public BmXlsTransferConverter typeMap(final Map<String, Type> typeMap) {
        this.typeMap = typeMap;
        return this;
    }

    public BmXlsTransferConverter accountMap(final Map<String, Account> accountMap) {
        this.accountMap = accountMap;
        return this;
    }

    public List<Transfer> convert(final RcdXlsSheet xlsSheet) {
        return xlsSheet.stream()
                .skip(1)
                .map(this::convert)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Transfer convert(final RcdXlsRow xlsRow) {
        final String typeString = xlsRow.getString(TRANSFERS_TYPE_INDEX);
        final Instant dateInstant = xlsRow.getInstant(TRANSFERS_DATE_INDEX);
        final Number amount = xlsRow.getNumber(TRANSFERS_AMOUNT_INDEX);
        final String currencyString = xlsRow.getString(TRANSFERS_CURRENCY_INDEX);
        final String comments = xlsRow.getString(TRANSFERS_COMMENTS_INDEX);
        final String srcAccountString = xlsRow.getString(TRANSFERS_SRC_ACC_INDEX);
        final Instant srcDateInstant = xlsRow.getInstant(TRANSFERS_SRC_DATE_INDEX);
        final String tgtAccountString = xlsRow.getString(TRANSFERS_TGT_ACC_INDEX);
        final Instant tgtDateInstant = xlsRow.getInstant(TRANSFERS_TGT_DATE_INDEX);

        boolean correctData = true;

        final Type type = typeMap.get(typeString);
        if (type == null) {
            correctData = false;
            errors.add("Type '" + typeString + "' does not exist");
        }

        final Currency currency = Currency.valueOf(currencyString);
        if (currency == null) {
            correctData = false;
            errors.add("Currency '" + currencyString + "' does not exist");
        }

        Account srcAccount = null;
        if (srcAccountString != null) {
            srcAccount = accountMap.get(srcAccountString);
            if (srcAccount == null) {
                correctData = false;
                errors.add("Currency '" + srcAccountString + "' does not exist");
            }
        }

        Account tgtAccount = null;
        if (tgtAccountString != null) {
            tgtAccount = accountMap.get(tgtAccountString);
            if (tgtAccount == null) {
                correctData = false;
                errors.add("Currency '" + tgtAccountString + "' does not exist");
            }
        }

        for (final String error : errors) {
            RcdLogService.error(error);
        }

        if (!correctData) {
            return null;
        }

        final Transfer transfer = new Transfer(type, toLocalDate(dateInstant), amount.doubleValue(), currency);
        transfer.setSourceAccount(srcAccount)
        .setSourceDate(toLocalDate(srcDateInstant))
        .setTargetAccount(tgtAccount)
        .setTargetDate(toLocalDate(tgtDateInstant));

        return transfer;
    }

    private LocalDate toLocalDate(final Instant instant) {
        return instant == null ? null : instant.atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
