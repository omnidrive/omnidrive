package omnidrive.api.managers;

import omnidrive.api.base.BaseAccount;
import omnidrive.api.base.AccountType;
import omnidrive.stub.Account;
import org.junit.Test;

import static org.junit.Assert.*;

public class AccountsManagerTest {

    private AccountsManager accountsManager = new AccountsManager();

    private BaseAccount account1 = new Account();

    private BaseAccount account2 = new Account();

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