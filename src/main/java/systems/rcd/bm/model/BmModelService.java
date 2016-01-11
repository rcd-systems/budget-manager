package systems.rcd.bm.model;

import java.util.List;
import java.util.Map;

import systems.rcd.bm.exc.BmException;
import systems.rcd.bm.model.convert.BmModelXlsAccountConverter;
import systems.rcd.bm.model.convert.BmModelXlsTypeConverter;
import systems.rcd.bm.model.data.Account;
import systems.rcd.bm.model.data.Transfer;
import systems.rcd.bm.model.data.Type;
import systems.rcd.fwk.core.ctx.RcdService;
import systems.rcd.fwk.core.format.xls.data.RcdXlsWorkbook;

public class BmModelService implements RcdService, BmModelConstants {

    private Map<String, Account> accountMap;
    private Map<String, Type> typeMap;
    private List<Transfer> transfers;

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
        accountMap = new BmModelXlsAccountConverter().convert(workbook.get(ACCOUNTS_SHEET_NAME));
        typeMap = new BmModelXlsTypeConverter().convert(workbook.get(TYPES_SHEET_NAME));
    }
}
