package systems.rcd.bm.model.data;

import java.time.LocalDate;

public class Transfer {
    private Type type;
    private LocalDate date;
    private double amount;
    private Currency currency;
    private String comments;
    private Account sourceAccount;
    private LocalDate sourceDate;
    private Account targetAccount;
    private LocalDate targetDate;

    public Transfer(final Type type, final LocalDate date, final double amount, final Currency currency) {
        this.type = type;
        this.date = date;
        this.amount = amount;
        this.currency = currency;
    }

    public Type getType() {
        return type;
    }

    public Transfer setType(final Type type) {
        this.type = type;
        return this;
    }

    public LocalDate getDate() {
        return date;
    }

    public Transfer setDate(final LocalDate date) {
        this.date = date;
        return this;
    }

    public double getAmount() {
        return amount;
    }

    public Transfer setAmount(final double amount) {
        this.amount = amount;
        return this;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Transfer setCurrency(final Currency currency) {
        this.currency = currency;
        return this;
    }

    public String getComments() {
        return comments;
    }

    public Transfer setComments(final String comments) {
        this.comments = comments;
        return this;
    }

    public Account getSourceAccount() {
        return sourceAccount;
    }

    public Transfer setSourceAccount(final Account sourceAccount) {
        this.sourceAccount = sourceAccount;
        return this;
    }

    public LocalDate getSourceDate() {
        return sourceDate;
    }

    public Transfer setSourceDate(final LocalDate sourceDate) {
        this.sourceDate = sourceDate;
        return this;
    }

    public Account getTargetAccount() {
        return targetAccount;
    }

    public Transfer setTargetAccount(final Account targetAccount) {
        this.targetAccount = targetAccount;
        return this;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public Transfer setTargetDate(final LocalDate targetDate) {
        this.targetDate = targetDate;
        return this;
    }

    public boolean isIncoming(final String account) {
        if (targetAccount == null || targetDate == null) {
            return false;
        }

        if (targetAccount.isOrChildOf(account)) {
            if (sourceAccount == null || sourceDate == null) {
                return true;
            }

            return !sourceAccount.isChildOf(account);
        }

        return false;
    }

    public boolean isOutgoing(final String account) {
        if (sourceAccount == null || sourceDate == null) {
            return false;
        }

        if (sourceAccount.isChildOf(account)) {
            if (targetAccount == null || targetDate == null) {
                return true;
            }
            return !targetAccount.isOrChildOf(account);
        }

        return false;
    }

}
