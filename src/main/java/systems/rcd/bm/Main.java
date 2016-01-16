package systems.rcd.bm;

import systems.rcd.bm.model.BmModelService;
import systems.rcd.fwk.core.ctx.RcdContext;
import systems.rcd.fwk.core.log.RcdLogService;
import systems.rcd.fwk.jetty.impl.RcdJettyService;
import systems.rcd.fwk.poi.xls.impl.RcdPoiXlsService;

public class Main {

    public static void main(final String[] args) throws Exception {
        RcdLogService.info("Budget Manager 8");
        configure();
        loadData();
        launchServer();

    }

    private static void configure() {
        RcdPoiXlsService.init();
    }

    private static void loadData() throws Exception {
        final BmModelService bmModelService = new BmModelService();
        RcdContext.setGlobalServiceSupplier(BmModelService.class, () -> bmModelService);
    }

    private static void launchServer() throws Exception {
        RcdJettyService.createServer(0)
                .addResourceHandler("/res", Main.class.getResource("res"))
        .start()
        .join();
    }
}
