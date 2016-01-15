package systems.rcd.bm.model;

import java.util.List;
import java.util.Map;

import systems.rcd.bm.exc.BmException;
import systems.rcd.bm.model.convert.BmModelXlsAccountConverter;
import systems.rcd.bm.model.convert.BmModelXlsTransferConverter;
import systems.rcd.bm.model.convert.BmModelXlsTypeConverter;
import systems.rcd.bm.model.data.Account;
import systems.rcd.bm.model.data.Transfer;
import systems.rcd.bm.model.data.Type;
import systems.rcd.fwk.core.ctx.RcdService;
import systems.rcd.fwk.core.format.xls.data.RcdXlsWorkbook;
import systems.rcd.fwk.core.log.RcdLogService;

public class BmModelService implements RcdService, BmModelConstants {

    private Map<String, Account> accountMap;
    private Map<String, Type> typeMap;
    private List<Transfer> transfers;

    public BmModelService() throws Exception {
        final RcdXlsWorkbook workbook = parseInputFile();
        validateFormat(workbook);
        load(workbook);
        System.out.println(transfers);
    }

    private RcdXlsWorkbook parseInputFile() throws Exception {
        return new BmModelInputParser().parseInputFile();
    }

    private void validateFormat(final RcdXlsWorkbook workbook) throws BmException {
        final List<String> errors = new BmModelValidator().validate(workbook);
        if (!errors.isEmpty()) {
            for (final String error : errors) {
                RcdLogService.error(error);
            }
            throw new BmException("Incorrect format in the input file");
        }
    }

    private void load(final RcdXlsWorkbook workbook) throws BmException {
        accountMap = new BmModelXlsAccountConverter().convert(workbook.get(ACCOUNTS_SHEET_NAME));
        typeMap = new BmModelXlsTypeConverter().convert(workbook.get(TYPES_SHEET_NAME));
        final BmModelXlsTransferConverter transferConverter = new BmModelXlsTransferConverter().accountMap(accountMap)
                .typeMap(typeMap);
        transfers = transferConverter.convert(workbook.get(TRANSFERS_SHEET_NAME));

        final List<String> errors = transferConverter.getErrors();
        if (!errors.isEmpty()) {
            for (final String error : errors) {
                RcdLogService.error(error);
            }
            throw new BmException("Incorrect data in the input file");
        }
    }
}
