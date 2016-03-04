package systems.rcd.bm.model;

import java.nio.file.Path;
import java.nio.file.Paths;

import systems.rcd.fwk.core.format.xls.RcdXlsService;
import systems.rcd.fwk.core.format.xls.data.RcdXlsWorkbook;
import systems.rcd.fwk.core.log.RcdLogService;

public class BmModelInputParser {

	private final Path INPUT_PATH = Paths.get("input.xls");
	private final Path DEFAULT_INPUT_PATH = Paths.get("src/main/resources/input.xls");

	public RcdXlsWorkbook parseInputFile() throws Exception {
		if (INPUT_PATH.toFile()
		        .exists()) {
			parseInputFile(INPUT_PATH);
		}
		return parseInputFile(DEFAULT_INPUT_PATH);
	}

	private RcdXlsWorkbook parseInputFile(final Path inputFilePath) throws Exception {
		RcdLogService.info("Parsing input file '" + inputFilePath + "'...");
		final RcdXlsWorkbook workbook = RcdXlsService.read(inputFilePath);
		RcdLogService.info("Input file parsed!");
		return workbook;
	}
}
