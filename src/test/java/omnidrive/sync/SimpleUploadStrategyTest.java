package omnidrive.sync;

import omnidrive.api.account.Account;
import omnidrive.api.account.AccountType;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.BaseTest;
import omnidrive.exceptions.NoAccountFoundException;
import omnidrive.sync.upload.SimpleUploadStrategy;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SimpleUploadStrategyTest extends BaseTest {

    private Account emptyAccount = new omnidrive.stub.Account(AccountType.Dropbox);

    private Account fullAccount = new omnidrive.stub.Account(AccountType.Dropbox, 0);

    private File file;

    @Before
    public void setUp() throws Exception {
        file = getResource("hello.txt");
    }

    @Test
    public void testSelectAccountReturnsActiveAccount() throws Exception {
        // Given an empty account exists
        SimpleUploadStrategy uploadStrategy = getUploadStrategy(emptyAccount);

        // When you select an account to upload a file to
        Account result = uploadStrategy.selectAccount(file);

        // Then you get that account
        assertEquals(emptyAccount, result);
    }

    @Test
    public void testSelectAccountOnlyIfItHasCapacity() throws Exception {
        // Given an empty and full accounts exist
        SimpleUploadStrategy uploadStrategy = getUploadStrategy(fullAccount, emptyAccount);

        // When you select an account to upload a file to
        Account result = uploadStrategy.selectAccount(file);

        // Then you get the empty account
        assertEquals(emptyAccount, result);
    }

    @Test(expected = NoAccountFoundException.class)
    public void testExceptionThrownIfNoAccountIsFound() throws Exception {
        // Given all accounts are full
        SimpleUploadStrategy uploadStrategy = getUploadStrategy(fullAccount);

        // When you select an account to upload a file to
        uploadStrategy.selectAccount(file);

        // Then a NoAccountFoundException exception is thrown
    }

    private SimpleUploadStrategy getUploadStrategy(Account... accounts) {
        AccountsManager accountsManager = mock(AccountsManager.class);
        when(accountsManager.getActiveAccounts()).thenReturn(Arrays.asList(accounts));
        return new SimpleUploadStrategy(accountsManager);
    }

}
