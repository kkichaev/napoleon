package google_plus;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;
 
public class GAuthHelper {
 
 private AccountManager accountManager;
 private static final String ACC_TYPE = "com.google";
 private static final String SCOPE = "oauth2:https://docs.google.com/feeds/";
 private Activity act;
 
 public GAuthHelper(Activity activity) {
  accountManager = AccountManager.get(activity);
  act = activity;
 }
 
 public Account[] getAccounts() {
  Account[] accounts = accountManager.getAccountsByType(ACC_TYPE);
  return accounts;
 }
 
 public String[] getAccNames() {
  Account[] accounts = getAccounts();
  String[] rez = new String[accounts.length];
  for (int i = 0; i < accounts.length; i++) {
   rez[i] = accounts[i].name;
  }
  return rez;
 }
 
 private Account getAccountByName(String name) {
  Account[] accounts = getAccounts();
  for (int i = 0; i < accounts.length; i++) {
   if (name.equals(accounts[i].name)) return accounts[i];
  }
  return null;
 }
 
 public void invalidateToken(String token) {
  accountManager.invalidateAuthToken(ACC_TYPE, token);
 }
 
 public void getAuthToken(String accname, OAuthCallbackListener authCallbackListener) {
  getAuthToken(getAccountByName(accname), authCallbackListener);
 }
 
 public void getAuthToken(Account account, final OAuthCallbackListener authCallbackListener) {
  accountManager.getAuthToken(account, SCOPE, null, act,
    new AccountManagerCallback<Bundle>() {
     public void run(AccountManagerFuture<Bundle> future) {
      try {
       String token = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
       authCallbackListener.callback(token);
      } catch (OperationCanceledException e) {
       authCallbackListener.callback(null);
      } catch (Exception e) {
       e.printStackTrace();
      }
     }
    }, null);
 
 }
 
 public static interface OAuthCallbackListener {
  public void callback(String authToken);
 }
}
