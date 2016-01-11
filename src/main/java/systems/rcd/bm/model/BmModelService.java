package systems.rcd.bm.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import systems.rcd.bm.exc.BmException;
import systems.rcd.bm.model.data.Account;
import systems.rcd.bm.model.data.Transfer;
import systems.rcd.bm.model.data.Type;
import systems.rcd.fwk.core.ctx.RcdService;
import systems.rcd.fwk.core.format.xls.data.RcdXlsWorkbook;

public class BmModelService implements RcdService, BmModelConstants {

    private static Map<String, Account> accountMap = Collections.synchronizedMap(new HashMap<>());
    private static Map<String, Type> typeMap = Collections.synchronizedMap(new HashMap<>());
    private static List<Transfer> transfers = Collections.synchronizedList(new LinkedList<>());

    public BmModelService() throws Exception {
        final RcdXlsWorkbook workbook = parseInputFile();
        if (!validateFormat(workbook)) {
            throw new BmException("Incorrect format in the input file");
        }
        load(workbook);
    }

    private RcdXlsWorkbook parseInputFile() throws Exception {
        return new BmModelInputParser().parseInputFile();
    }

    private boolean validateFormat(final RcdXlsWorkbook workbook) {
        return new BmModelValidator().validate(workbook);
    }

    private void load(final RcdXlsWorkbook workbook) {

    }
}
