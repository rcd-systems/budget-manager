package systems.rcd.bm.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private static List<Account> rootAccounts;
    private static List<Type> rootTypes;
    private static Map<Integer, Map<Integer, List<Transfer>>> transfersByDate;
    private static Map<Integer, Map<Integer, List<Transfer>>> transfersBySourceDate;
    private static Map<Integer, Map<Integer, List<Transfer>>> transfersByTargetDate;

    public BmModelService() throws Exception {
        final RcdXlsWorkbook workbook = parseInputFile();
        validateFormat(workbook);
        load(workbook);
        index();
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

    private void index() {
        rootAccounts = accountMap.values()
                .stream()
                .filter(account -> account.getParent() == null)
                .collect(Collectors.toList());

        rootTypes = typeMap.values()
                .stream()
                .filter(type -> type.getParent() == null)
                .collect(Collectors.toList());

        transfersByDate = transfers.stream()
                .collect(Collectors.groupingBy(transfer -> transfer.getDate()
                        .getYear(), Collectors.groupingBy(transfer -> ((Transfer) transfer).getDate()
                                .getMonthValue())));

        transfersBySourceDate = transfers.stream()
                .filter(transfer -> transfer.getSourceDate() != null)
                .collect(Collectors.groupingBy(transfer -> transfer.getSourceDate()
                        .getYear(), Collectors.groupingBy(transfer -> ((Transfer) transfer).getSourceDate()
                                .getMonthValue())));

        transfersByTargetDate = transfers.stream()
                .filter(transfer -> transfer.getTargetDate() != null)
                .collect(Collectors.groupingBy(transfer -> transfer.getTargetDate()
                        .getYear(), Collectors.groupingBy(transfer -> ((Transfer) transfer).getTargetDate()
                                .getMonthValue())));

    }

    // TODO Make BmModelService thread safe
    public List<Transfer> findTransfers(final Integer year, final Integer month) {
        if (year == null) {
            return transfers;
        }

        final Map<Integer, List<Transfer>> map = transfersByDate.get(year);
        if (month == null) {
            return map.values()
                    .stream()
                    .flatMap(s -> s.stream())
                    .collect(Collectors.toList());
        }

        return map.get(month);
    }
}
