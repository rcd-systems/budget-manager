package systems.rcd.bm.model;

import java.util.List;

import systems.rcd.fwk.core.format.xls.data.RcdXlsCellType;
import systems.rcd.fwk.core.format.xls.data.RcdXlsWorkbook;
import systems.rcd.fwk.core.format.xls.util.RcdXlsSheetValidator;
import systems.rcd.fwk.core.format.xls.util.RcdXlsWorkbookValidator;

public class BmModelValidator implements BmModelConstants {

	public List<String> validate(final RcdXlsWorkbook workbook) {
		final RcdXlsSheetValidator transfersSheetValidator = new RcdXlsSheetValidator()
		        .setColumnMandatoriness(TRANSFERS_TYPE_INDEX, TRANSFERS_DATE_INDEX, TRANSFERS_DATE_INDEX,
		                TRANSFERS_AMOUNT_INDEX, TRANSFERS_CURRENCY_INDEX)
		        .setColumnType(TRANSFERS_TYPE_INDEX, RcdXlsCellType.STRING)
		        .setColumnType(TRANSFERS_DATE_INDEX, RcdXlsCellType.INSTANT)
		        .setColumnType(TRANSFERS_AMOUNT_INDEX, RcdXlsCellType.NUMBER)
		        .setColumnType(TRANSFERS_CURRENCY_INDEX, RcdXlsCellType.STRING)
		        .setColumnType(TRANSFERS_COMMENTS_INDEX, RcdXlsCellType.STRING)
		        .setColumnType(TRANSFERS_SRC_ACC_INDEX, RcdXlsCellType.STRING)
		        .setColumnType(TRANSFERS_SRC_DATE_INDEX, RcdXlsCellType.INSTANT)
		        .setColumnType(TRANSFERS_TGT_ACC_INDEX, RcdXlsCellType.STRING)
		        .setColumnType(TRANSFERS_TGT_DATE_INDEX, RcdXlsCellType.INSTANT);

		new RcdXlsSheetValidator().setColumnMandatoriness(0);

		final List<String> errors = new RcdXlsWorkbookValidator()
		        .addSheetValidator(TRANSFERS_SHEET_NAME, transfersSheetValidator)
		        .addSheetValidator(TYPES_SHEET_NAME, new RcdXlsSheetValidator().setColumnMandatoriness(0))
		        .addSheetValidator(ACCOUNTS_SHEET_NAME, new RcdXlsSheetValidator().setColumnMandatoriness(0))
		        .validate(workbook);

		return errors;
	}

}
