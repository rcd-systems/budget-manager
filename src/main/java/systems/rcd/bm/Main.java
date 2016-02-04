package systems.rcd.bm;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Paths;

import systems.rcd.bm.json.BmAccountsJsonInterfaceHandler;
import systems.rcd.bm.json.BmDeltasJsonInterfaceHandler;
import systems.rcd.bm.json.BmIncomingTransfersJsonInterfaceHandler;
import systems.rcd.bm.json.BmOutgoingTransfersJsonInterfaceHandler;
import systems.rcd.bm.json.BmSubAccountsJsonInterfaceHandler;
import systems.rcd.bm.json.BmTransfersJsonInterfaceHandler;
import systems.rcd.bm.json.BmTypesJsonInterfaceHandler;
import systems.rcd.bm.json.BmYearsJsonInterfaceHandler;
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
                .addResourceHandler("/res", Paths.get("src/main/resources/systems/rcd/bm/res")
                        .toUri()
                        .toURL())
                // .addResourceHandler("/res", Main.class.ggetResource("res"))
                .addHandler("/json/transfers", new BmTransfersJsonInterfaceHandler())
                .addHandler("/json/sub-accounts", new BmSubAccountsJsonInterfaceHandler())
                .addHandler("/json/incoming-transfers", new BmIncomingTransfersJsonInterfaceHandler())
                .addHandler("/json/outgoing-transfers", new BmOutgoingTransfersJsonInterfaceHandler())
                .addHandler("/json/years", new BmYearsJsonInterfaceHandler())
                .addHandler("/json/types", new BmTypesJsonInterfaceHandler())
                .addHandler("/json/accounts", new BmAccountsJsonInterfaceHandler())
                .addHandler("/json/deltas", new BmDeltasJsonInterfaceHandler())
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
