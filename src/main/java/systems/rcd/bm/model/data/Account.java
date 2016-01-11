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

    public Account addSubAccount(final Account subAccount) {
        if (subAccount.parent != null) {
            subAccount.parent.subAccounts.remove(subAccount);
        }
        subAccount.parent = this;

        final Account existingSubAccount = subAccounts.put(subAccount.name, subAccount);
        if (existingSubAccount != null) {
            existingSubAccount.parent = null;
        }
        return this;
    }

    public Account getParent() {
        return parent;
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

    public boolean isOrChildOf(final Account account) {
        if (account == null) {
            return false;
        }
        if (equals(account)) {
            return true;
        }
        if (parent != null) {
            return parent.isOrChildOf(account);
        }
        return false;
    }

    public boolean isChildOf(final Account parent) {
        if (this.parent == null) {
            return parent == null;
        }
        if (parent == null) {
            return false;
        }
        if (this.parent.equals(parent)) {
            return true;
        }
        return this.parent.isChildOf(parent);
    }

    public boolean isParentOf(final Account child) {
        for (final Account subAccount : subAccounts.values()) {
            if (subAccount.equals(child)) {
                return true;
            }
        }

        for (final Account subAccount : subAccounts.values()) {
            if (subAccount.isParentOf(child)) {
                return true;
            }
        }
        return false;
    }
}
