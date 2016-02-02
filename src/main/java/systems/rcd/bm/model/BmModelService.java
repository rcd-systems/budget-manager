package systems.rcd.bm.model;

import java.time.LocalDate;
import java.util.AbstractMap;
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
    private static Map<Integer, Map<Integer, Map<Integer, List<Transfer>>>> transfersByDate;
    private static Map<Integer, Map<Integer, Map<Integer, List<Transfer>>>> transfersBySourceDate;
    private static Map<Integer, Map<Integer, Map<Integer, List<Transfer>>>> transfersByTargetDate;

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

        transfersByDate = transfers.stream()
                .collect(
                        Collectors.groupingBy(transfer -> transfer.getDate()
                                .getYear(),
                                Collectors.groupingBy(transfer -> transfer.getDate()
                                        .getMonthValue(), Collectors.groupingBy(transfer -> transfer.getDate()
                                        .getDayOfMonth()))));

        transfersBySourceDate = transfers.stream()
                .filter(transfer -> transfer.getSourceDate() != null)
                .collect(
                        Collectors.groupingBy(transfer -> transfer.getSourceDate()
                                .getYear(),
                                Collectors.groupingBy(transfer -> transfer.getSourceDate()
                                        .getMonthValue(), Collectors.groupingBy(transfer -> transfer.getSourceDate()
                                        .getDayOfMonth()))));

        transfersByTargetDate = transfers.stream()
                .filter(transfer -> transfer.getTargetDate() != null)
                .collect(
                        Collectors.groupingBy(transfer -> transfer.getTargetDate()
                                .getYear(),
                                Collectors.groupingBy(transfer -> transfer.getTargetDate()
                                        .getMonthValue(), Collectors.groupingBy(transfer -> transfer.getTargetDate()
                                        .getDayOfMonth()))));

    }

    public List<Transfer> findTransfers(final Integer year, final Integer month) {
        return findTransfers(year, month, transfersByDate);
    }

    public List<Transfer> findIncomingTransfers(final Integer year, final Integer month) {
        return findTransfers(year, month, transfersByTargetDate);
    }

    public List<Transfer> findOutgoingTransfers(final Integer year, final Integer month) {
        return findTransfers(year, month, transfersBySourceDate);
    }

    private List<Transfer> findTransfers(final Integer year, final Integer month,
            final Map<Integer, Map<Integer, Map<Integer, List<Transfer>>>> index) {

        final Map<Integer, Map<Integer, List<Transfer>>> map = index.get(year);
        if (month == null) {
            return map.values()
                    .stream()
                    .flatMap(s -> s.values()
                            .stream())
                            .flatMap(s -> s.stream())
                            .collect(Collectors.toList());
        }

        return map.get(month)
                .values()
                .stream()
                .flatMap(s -> s.stream())
                .collect(Collectors.toList());
    }

    public Set<Integer> findYears() {
        return transfersByDate.keySet();
    }

    public Set<String> findTypeNames() {
        return typeMap.keySet();
    }

    public Set<String> findAccountNames() {
        return accountMap.keySet();
    }

    public double findInitialAmount(final Integer year, final Integer month, final String account) {
        double initialAmount = 0d;

        for (final Entry<Integer, Map<Integer, Map<Integer, List<Transfer>>>> transfersByYearEntry : transfersBySourceDate.entrySet()) {
            if (transfersByYearEntry.getKey() <= year) {
                initialAmount -= transfersByYearEntry.getValue()
                        .entrySet()
                        .stream()
                        .filter(
                                transfersByMonthEntry -> transfersByYearEntry.getKey() < year
                                || month != null && transfersByMonthEntry.getKey() < month)
                                .flatMap(
                                        transfersByMonthEntry -> transfersByMonthEntry.getValue()
                                        .values()
                                        .stream())
                        .flatMap(s -> s.stream())
                                        .filter(transfer -> transfer.isOutgoing(account))
                                        .mapToDouble(Transfer::getAmount)
                                        .sum();
            }
        }

        for (final Entry<Integer, Map<Integer, Map<Integer, List<Transfer>>>> transfersByYearEntry : transfersByTargetDate.entrySet()) {
            if (transfersByYearEntry.getKey() <= year) {
                initialAmount += transfersByYearEntry.getValue()
                        .entrySet()
                        .stream()
                        .filter(
                                transfersByMonthEntry -> transfersByYearEntry.getKey() < year
                                || month != null && transfersByMonthEntry.getKey() < month)
                                .flatMap(
                                        transfersByMonthEntry -> transfersByMonthEntry.getValue()
                                        .values()
                                        .stream())
                                        .flatMap(s -> s.stream())
                                        .filter(transfer -> transfer.isIncoming(account))
                                        .mapToDouble(Transfer::getAmount)
                                        .sum();
            }
        }

        return initialAmount;
    }

    public List<Entry<String, Integer>> findDeltas(final Integer year, final Integer month, final String account) {

        final List<Map.Entry<String, Integer>> deltas = new LinkedList<>();
        LocalDate date = LocalDate.of(year, month == null ? 1 : month, 1);

        while (date.getYear() == year && (month == null || date.getMonthValue() == month)) {

            double delta = 0;
            Map<Integer, Map<Integer, List<Transfer>>> transfersByMonth = transfersBySourceDate.get(date.getYear());
            if (transfersByMonth != null) {
                final Map<Integer, List<Transfer>> transfersByDay = transfersByMonth.get(date.getMonthValue());
                if (transfersByDay != null) {
                    final List<Transfer> transfers = transfersByDay.get(date.getDayOfMonth());

                    if (transfers != null) {
                        delta -= transfers.stream()
                                .filter(transfer -> transfer.isOutgoing(account))
                                .mapToDouble(Transfer::getAmount)
                                .sum();
                    }
                }
            }

            transfersByMonth = transfersByTargetDate.get(date.getYear());
            if (transfersByMonth != null) {
                final Map<Integer, List<Transfer>> transfersByDay = transfersByMonth.get(date.getMonthValue());
                if (transfersByDay != null) {
                    final List<Transfer> transfers = transfersByDay.get(date.getDayOfMonth());

                    if (transfers != null) {
                        delta += transfers.stream()
                                .filter(transfer -> transfer.isIncoming(account))
                                .mapToDouble(Transfer::getAmount)
                                .sum();
                    }
                }
            }

            deltas.add(new AbstractMap.SimpleEntry(date.getDayOfMonth() + "/" + date.getMonthValue(), delta));

            date = date.plusDays(1);
        }
        return deltas;
    }
}
