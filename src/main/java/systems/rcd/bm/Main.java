package systems.rcd.bm;

import java.awt.Desktop;
import java.io.IOException;

import systems.rcd.bm.json.BmJsonInterfaceHandler;
import systems.rcd.bm.model.BmModelService;
import systems.rcd.fwk.core.ctx.RcdContext;
import systems.rcd.fwk.core.log.RcdLogService;
import systems.rcd.fwk.jetty.impl.RcdJettyService;
import systems.rcd.fwk.jetty.impl.data.RcdJettyServer;
import systems.rcd.fwk.poi.xls.impl.RcdPoiXlsService;

public class Main {

    public static void main(final String[] args) throws Exception {
        RcdLogService.info("Budget Manager 8");
        configure();
        loadData();
        final RcdJettyServer server = launchServer();
        launchBrowser(server);
        server.join();
    }

    private static void configure() {
        RcdPoiXlsService.init();
    }

    private static void loadData() throws Exception {
        final BmModelService bmModelService = new BmModelService();
        RcdContext.setGlobalServiceSupplier(BmModelService.class, () -> bmModelService);
    }

    private static RcdJettyServer launchServer() throws Exception {
        return RcdJettyService.createServer("localhost", 0)
                .addResourceHandler("/res", Main.class.getResource("res"))
                .addHandler("/json", new BmJsonInterfaceHandler())
                .start();
    }

    private static void launchBrowser(final RcdJettyServer server) throws IOException {
        if (Desktop.isDesktopSupported())
        {
            Desktop.getDesktop()
                    .browse(server.getUri());
        }
    }
}
