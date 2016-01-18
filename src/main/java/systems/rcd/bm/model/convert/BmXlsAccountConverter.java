package systems.rcd.bm.model.convert;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import systems.rcd.bm.model.data.Account;
import systems.rcd.fwk.core.format.xls.data.RcdXlsRow;
import systems.rcd.fwk.core.format.xls.data.RcdXlsSheet;

public class BmXlsAccountConverter {
    Map<String, Account> accountMap = new HashMap<>();

    public Map<String, Account> convert(final RcdXlsSheet rcdXlsSheet) {

        rcdXlsSheet.stream()
                .skip(1)
                .forEach(this::convert);

        return Collections.synchronizedMap(accountMap);
    }

    private void convert(final RcdXlsRow xlsRow) {
        final String accountName = xlsRow.get(0)
                .getStringValue();
        final Account account = getAccount(accountName);

        xlsRow.stream()
                .skip(1)
                .filter(Objects::nonNull)
                .map(cell -> getAccount(cell.getStringValue()))
                .forEach(account::addSubAccount);

    }

    private Account getAccount(final String name) {
        return accountMap.computeIfAbsent(name, newName -> new Account(newName));
    }
}
