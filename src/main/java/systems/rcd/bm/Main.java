package systems.rcd.bm;

import systems.rcd.bm.model.BmModelService;
import systems.rcd.fwk.core.ctx.RcdContext;
import systems.rcd.fwk.core.log.RcdLogService;
import systems.rcd.fwk.poi.xls.impl.RcdPoiXlsService;

public class Main {

    public static void main(final String[] args) throws Exception {
        RcdLogService.info("Budget Manager 8");
        configure();
        load();
    }

    private static void configure() {
        RcdPoiXlsService.init();
    }

    private static void load() throws Exception {
        final BmModelService bmModelService = new BmModelService();
        RcdContext.setGlobalServiceSupplier(BmModelService.class, () -> bmModelService);
    }
}
