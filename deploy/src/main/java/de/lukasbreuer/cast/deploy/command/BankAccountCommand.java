package de.lukasbreuer.cast.deploy.command;

import com.clearspring.analytics.util.Lists;
import de.lukasbreuer.cast.core.command.Command;
import de.lukasbreuer.cast.core.log.Log;
import de.lukasbreuer.cast.deploy.finance.BankAccount;
import de.lukasbreuer.cast.deploy.finance.BankAccountAccess;
import de.lukasbreuer.cast.deploy.finance.BankAccountCollection;

import java.util.List;
import java.util.UUID;

public final class BankAccountCommand extends Command {
  public static BankAccountCommand create(
    Log log, BankAccountCollection bankAccountCollection
  ) {
    return new BankAccountCommand(log, bankAccountCollection);
  }

  private final BankAccountCollection bankAccountCollection;

  private BankAccountCommand(Log log, BankAccountCollection bankAccountCollection) {
    super(log, "bankAccount", new String[] {"bankAccounts"},
      new String[] {"add <name, money>", "remove <name>",
        "deposit <name> <money>", "debit <name> <money>"});
    this.bankAccountCollection = bankAccountCollection;
  }

  @Override
  public boolean execute(String[] arguments) {
    if (arguments.length == 0) {
      bankAccountCollection.allBankAccounts(this::printBankAccounts);
      return true;
    }
    if (arguments[0].equalsIgnoreCase("add")) {
      return executeAdd(arguments);
    }
    if (arguments[0].equalsIgnoreCase("remove")) {
      return executeRemove(arguments);
    }
    if (
      arguments[0].equalsIgnoreCase("deposit") ||
        arguments[0].equalsIgnoreCase("debit")
    ) {
      return executeMoneyChange(arguments);
    }
    return false;
  }

  private boolean executeAdd(String[] arguments) {
    if (arguments.length != 3) {
      return false;
    }
    var bankAccountName = arguments[1].toUpperCase();
    var bankAccountMoney = Double.parseDouble(arguments[2]);
    bankAccountCollection.addBankAccount(BankAccount.create(UUID.randomUUID(),
      bankAccountName, bankAccountMoney, Lists.newArrayList()),success ->
      log().info("Bank Account " + bankAccountName + " has been successfully added"));
    return true;
  }

  private boolean executeRemove(String[] arguments) {
    if (arguments.length != 2) {
      return false;
    }
    var bankAccountName = arguments[1].toUpperCase();
    bankAccountCollection.removeBankAccount(bankAccountName, success ->
      log().info("Bank Account " + bankAccountName + " has been successfully removed"));
    return true;
  }

  private boolean executeMoneyChange(String[] arguments) {
    if (arguments.length != 3) {
      return false;
    }
    var accessType = arguments[0].equalsIgnoreCase("deposit") ?
      BankAccountAccess.AccessType.DEPOSIT : BankAccountAccess.AccessType.DEBIT;
    var bankAccountName = arguments[1].toUpperCase();
    var money = Double.parseDouble(arguments[2]);
    bankAccountCollection.findBankAccountByName(bankAccountName, bankAccount ->
      executeMoneyChange(bankAccount, accessType, money));
    return true;
  }

  private void executeMoneyChange(
    BankAccount bankAccount, BankAccountAccess.AccessType accessType,
    double money
  ) {
    if (accessType.isDeposit()) {
      bankAccount.deposit(money);
    }
    if (accessType.isDebit()) {
      bankAccount.debit(money);
    }
    bankAccountCollection.updateBankAccount(bankAccount, success ->
      log().info("Successfully update balance of bank account " +
        bankAccount.name().toLowerCase()));
  }

  private void printBankAccounts(List<BankAccount> bankAccounts) {
    log().info("Bank Accounts (" + bankAccounts.size() + "): ");
    if (bankAccounts.size() == 0) {
      log().info(" Empty");
      return;
    }
    for (var bankAccount : bankAccounts) {
      log().info(" - " + bankAccount.name() + ": " + bankAccount.balance());
    }
  }
}
