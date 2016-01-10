package systems.rcd.bm.model;

import java.nio.file.Path;
import java.nio.file.Paths;

import systems.rcd.bm.exc.BmException;
import systems.rcd.fwk.core.ctx.RcdService;
import systems.rcd.fwk.core.format.xls.RcdXlsService;
import systems.rcd.fwk.core.format.xls.data.RcdXlsWorkbook;
import systems.rcd.fwk.core.log.RcdLogService;

public class BmModelService implements RcdService {

    private final Path INPUT_PATH = Paths.get("input.xls");
    private final Path DEFAULT_INPUT_PATH = Paths.get("input.xls");

    public BmModelService() throws Exception {
        final RcdXlsWorkbook workbook = parseInputFile();
        if (!validateFormat(workbook)) {
            throw new BmException("Incorrect format in the input file");
        }
        load(workbook);
    }

    private RcdXlsWorkbook parseInputFile() throws Exception {
        if (INPUT_PATH.toFile().exists()) {
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

    private void load(final RcdXlsWorkbook workbook) {

    }

    private boolean validateFormat(final RcdXlsWorkbook workbook) {
        return true;
    }
}
