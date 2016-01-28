package systems.rcd.bm.model.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Account {
    private final String name;
    private final Map<String, Account> subAccounts = new HashMap<>();
    private Account parent;

    public Account(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Collection<Account> getSubAccounts() {
        return subAccounts.values();
    }

    public Account getParent() {
        return parent;
    }

    public Account addSubAccount(final Account subAccount) {
        if (subAccount.parent != null) {
            subAccount.parent.subAccounts.remove(subAccount);
        }
        subAccount.parent = this;
        subAccounts.put(subAccount.name, subAccount);
        return this;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Account) {
            return name.equals(((Account) obj).name);
        }
        return false;
    }

    public boolean isChildOf(final String account) {
        if (this.parent == null) {
            return account == null;
        }
        if (account == null) {
            return false;
        }
        if (this.parent.getName()
                .equals(account)) {
            return true;
        }
        return this.parent.isChildOf(account);
    }

    public boolean isOrChildOf(final String account) {
        if (getName().equals(account)) {
            return true;
        }
        return isChildOf(account);
    }

    @Override
    public String toString() {
        return name;
    }
}
