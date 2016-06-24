package systems.rcd.bm.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import systems.rcd.fwk.core.format.xls.RcdXlsService;
import systems.rcd.fwk.core.format.xls.data.RcdXlsWorkbook;
import systems.rcd.fwk.core.io.file.RcdFileService;
import systems.rcd.fwk.core.log.RcdLogService;

public class BmModelInputParser
{

    private final Path INPUT_PATH = Paths.get( "input.xls" );

    private final Path IMPORT_FOLDER_PATH = Paths.get( "src/main/resources/import/dnb" );

    private final Path DEFAULT_INPUT_PATH = Paths.get( "src/main/resources/input.xls" );

    public RcdXlsWorkbook parseInputFile()
        throws Exception
    {
        if ( INPUT_PATH.toFile().exists() )
        {
            parseInputFile( INPUT_PATH );
        }
        return parseInputFile( DEFAULT_INPUT_PATH );
    }

    private RcdXlsWorkbook parseInputFile( final Path inputFilePath )
        throws Exception
    {
        RcdLogService.info( "Parsing input file '" + inputFilePath + "'..." );
        final RcdXlsWorkbook workbook = RcdXlsService.read( inputFilePath );
        RcdLogService.info( "Input file parsed!" );
        return workbook;
    }

    public List<RcdXlsWorkbook> parseImports()
    {
        LinkedList<RcdXlsWorkbook> xlsWorkbookLinkedList = new LinkedList<>();

        if ( IMPORT_FOLDER_PATH.toFile().exists() )
        {
            //For each DNB account
            RcdFileService.listSubPaths( IMPORT_FOLDER_PATH, dnbAccountFolderPath -> {
                //For each month report
                RcdFileService.listSubPaths( dnbAccountFolderPath, dnbAccountMonthReportPath -> {

                    //Parses the report
                    final RcdXlsWorkbook xlsWorkbook = RcdXlsService.read( dnbAccountMonthReportPath );
                    xlsWorkbookLinkedList.add( xlsWorkbook );
                } );
            } );
        }

        return xlsWorkbookLinkedList;
    }

}
