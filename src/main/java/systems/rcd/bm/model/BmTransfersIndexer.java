package systems.rcd.bm.model;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

import systems.rcd.bm.model.data.Transfer;

public class BmTransfersIndexer {
	SortedMap<LocalDate, List<Transfer>> transferMap = new TreeMap<>();

	public void add(final LocalDate date, final Transfer transfer) {
		if (date == null) {
			return;
		}

		transferMap.compute(date, (date2, transfers) -> {
			if (transfers == null) {
				transfers = new LinkedList<>();
			}
			transfers.add(transfer);

			return transfers;
		});
	}

	public Stream<Transfer> findTransfers(final Integer year, final Integer month) {
		final LocalDate start = LocalDate.of(year, month == null ? 1 : month, 1);
		final LocalDate end = month == null ? start.plusYears(1) : start.plusMonths(1);

		return subTransfers(start, end);
	}

	public Stream<Transfer> findTransfers(final LocalDate date) {
		final List<Transfer> transfers = transferMap.get(date);
		return transfers == null ? Stream.empty() : transfers.stream();
	}

	public Stream<Transfer> findTransfersBefore(final Integer year, final Integer month) {
		final LocalDate date = LocalDate.of(year, month == null ? 1 : month, 1);
		final LocalDate firstKey = transferMap.firstKey();
		if (date.isBefore(firstKey)) {
			return Stream.empty();
		}

		return subTransfers(firstKey, date);
	}

	private Stream<Transfer> subTransfers(final LocalDate start, final LocalDate end) {
		final SortedMap<LocalDate, List<Transfer>> subMap = transferMap.subMap(start, end);
		return subMap.values()
		        .stream()
		        .flatMap(c -> c.stream());
	}

	public Stream<Integer> findYears() {
		return transferMap.keySet()
		        .stream()
		        .map(LocalDate::getYear)
		        .distinct();
	}
}
