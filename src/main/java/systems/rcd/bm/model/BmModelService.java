package systems.rcd.bm.model;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import systems.rcd.bm.exc.BmException;
import systems.rcd.bm.model.convert.BmXlsAccountConverter;
import systems.rcd.bm.model.convert.BmXlsTransferConverter;
import systems.rcd.bm.model.convert.BmXlsTypeConverter;
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
    private static BmTransfersIndexer transfersByDate = new BmTransfersIndexer();
    private static BmTransfersIndexer transfersBySourceDate = new BmTransfersIndexer();
    private static BmTransfersIndexer transfersByTargetDate = new BmTransfersIndexer();

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
        accountMap = new BmXlsAccountConverter().convert(workbook.get(ACCOUNTS_SHEET_NAME));
        typeMap = new BmXlsTypeConverter().convert(workbook.get(TYPES_SHEET_NAME));
        final BmXlsTransferConverter transferConverter = new BmXlsTransferConverter().accountMap(accountMap)
                .typeMap(
                        typeMap);
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
                .collect(
                        Collectors.toList());

        rootTypes = typeMap.values()
                .stream()
                .filter(type -> type.getParent() == null)
                .collect(Collectors.toList());

        transfers.stream()
        .forEach(transfer -> {
            transfersByDate.add(transfer.getDate(), transfer);
            transfersBySourceDate.add(transfer.getSourceDate(), transfer);
            transfersByTargetDate.add(transfer.getTargetDate(), transfer);
        });

    }

    public List<Transfer> findTransfers(final Integer year, final Integer month) {
        return transfersByDate.findTransfers(year, month)
                .collect(Collectors.toList());
    }

    public List<Transfer> findIncomingTransfers(final Integer year, final Integer month) {
        return transfersByTargetDate.findTransfers(year, month)
                .collect(Collectors.toList());
    }

    public List<Transfer> findOutgoingTransfers(final Integer year, final Integer month) {
        return transfersBySourceDate.findTransfers(year, month)
                .collect(Collectors.toList());
    }

    public Set<Integer> findYears() {
        return transfersByDate.findYears()
                .collect(Collectors.toSet());
    }

    public Set<String> findTypeNames() {
        return typeMap.keySet();
    }

    public Type findType(final String key) {
        return typeMap.get(key);
    }

    public Account findAccount(final String key) {
        return accountMap.get(key);
    }

    public Collection<Account> findAccounts() {
        return accountMap.values();
    }

    public Collection<Type> findTypes() {
        return typeMap.values();
    }

    public Set<String> findAccountNames() {
        return accountMap.keySet();
    }

    public double findInitialAmount(final Integer year, final Integer month, final String account) {
        double initialAmount = 0d;

        initialAmount -= transfersBySourceDate.findTransfersBefore(year, month)
                .filter(transfer -> transfer.isOutgoing(account))
                .mapToDouble(Transfer::getAmount)
                .sum();

        initialAmount += transfersByTargetDate.findTransfersBefore(year, month)
                .filter(transfer -> transfer.isIncoming(account))
                .mapToDouble(Transfer::getAmount)
                .sum();

        return initialAmount;
    }

    public List<Entry<String, Integer>> findDeltas(final Integer year, final Integer month, final String account) {

        final List<Map.Entry<String, Integer>> deltas = new LinkedList<>();
        LocalDate date = LocalDate.of(year, month == null ? 1 : month, 1);

        while (date.getYear() == year && (month == null || date.getMonthValue() == month)) {
            double delta = 0d;
            delta -= transfersBySourceDate.findTransfers(date)
                    .filter(transfer -> transfer.isOutgoing(account))
                    .mapToDouble(Transfer::getAmount)
                    .sum();
            delta += transfersByTargetDate.findTransfers(date)
                    .filter(transfer -> transfer.isIncoming(account))
                    .mapToDouble(Transfer::getAmount)
                    .sum();

            deltas.add(new AbstractMap.SimpleEntry(date.getDayOfMonth() + "/" + date.getMonthValue(), delta));
            date = date.plusDays(1);
        }
        return deltas;
    }

    public double findTypeBalance(final Integer year, final Integer month, final String type) {
        typeMap.get("Mission")
        .isOrChildOf("Income");

        return transfersByDate.findTransfers(year, month)
                .filter(transfer -> transfer.getType()
                        .isOrChildOf(type))
                .mapToDouble(transfer -> {
                    if (transfer.isIncoming("Assets")) {
                        return transfer.getAmount();
                    }
                    if (transfer.isOutgoing("Assets")) {
                        return -1 * transfer.getAmount();
                    }
                    return 0;
                })
                .sum();
    }
}
