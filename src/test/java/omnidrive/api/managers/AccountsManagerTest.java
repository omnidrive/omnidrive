package omnidrive.api.managers;

import omnidrive.api.account.Account;
import omnidrive.api.account.AccountType;
import org.junit.Test;

import static org.junit.Assert.*;

public class AccountsManagerTest {

    private AccountsManager accountsManager = new AccountsManager();

    private Account account1 = new omnidrive.stub.Account();

    private Account account2 = new omnidrive.stub.Account();

    @Test
    public void testGetAccountTypeForUnregisteredAccountReturnsNull() throws Exception {
        // Given no accounts have been registered

        // When you try to get an account
        AccountType result = accountsManager.toType(account1);

        // Then null is returned
        assertNull(result);
    }

    @Test
    public void testGetAccountForRegisteredAccount() throws Exception {
        // Given an two accounts are registered
        accountsManager.setAccount(AccountType.GoogleDrive, account1);
        accountsManager.setAccount(AccountType.Dropbox, account2);

        // When you try to get an account
        AccountType result = accountsManager.toType(account2);

        // Then you get the
        assertEquals(AccountType.Dropbox, result);
    }

}