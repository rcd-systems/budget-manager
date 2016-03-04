package systems.rcd.bm.model.convert;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import systems.rcd.bm.model.data.Type;
import systems.rcd.fwk.core.format.xls.data.RcdXlsRow;
import systems.rcd.fwk.core.format.xls.data.RcdXlsSheet;

public class BmXlsTypeConverter {
	Map<String, Type> typeMap = new HashMap<>();

	public Map<String, Type> convert(final RcdXlsSheet rcdXlsSheet) {

		rcdXlsSheet.stream()
		        .skip(1)
		        .forEach(this::convert);

		return Collections.synchronizedMap(typeMap);
	}

	private void convert(final RcdXlsRow xlsRow) {
		final String typeName = xlsRow.get(0)
		        .getStringValue();
		final Type type = getType(typeName);

		xlsRow.stream()
		        .skip(1)
		        .filter(Objects::nonNull)
		        .map(cell -> getType(cell.getStringValue()))
		        .forEach(type::addSubType);

	}

	private Type getType(final String name) {
		return typeMap.computeIfAbsent(name, newName -> new Type(newName));
	}
}
