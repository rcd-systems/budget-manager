package systems.rcd.bm;

import systems.rcd.bm.model.BmModelService;
import systems.rcd.fwk.core.ctx.RcdContext;
import systems.rcd.fwk.core.log.RcdLogService;

public class Main {

    public static void main(final String[] args) throws Exception {
        RcdLogService.info("Budget Manager 8");
        load();
    }

    private static void load() throws Exception {
        final BmModelService bmModelService = new BmModelService();
        RcdContext.setGlobalServiceSupplier(BmModelService.class, () -> bmModelService);
    }
}
