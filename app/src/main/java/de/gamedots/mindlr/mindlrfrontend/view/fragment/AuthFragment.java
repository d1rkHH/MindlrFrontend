package de.gamedots.mindlr.mindlrfrontend.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import de.gamedots.mindlr.mindlrfrontend.MindlrApplication;
import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.auth.IdentityProvider;
import de.gamedots.mindlr.mindlrfrontend.auth.IdpResponse;
import de.gamedots.mindlr.mindlrfrontend.jobs.SignInTask;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.util.DebugUtil;
import de.gamedots.mindlr.mindlrfrontend.util.Utility;
import de.gamedots.mindlr.mindlrfrontend.view.activity.MainActivity;

/**
 * Non-UI-Fragment encapsulating the process for starting the authentication workflow
 * and catching the result of it. After creation the client must set an
 * appropriate {@link IdentityProvider} to be able to start the workflow.
 * It also must be added to an Activity to delegate the onActivityResult event.
 */

public class AuthFragment extends Fragment implements IdentityProvider.IdpCallback,
        SignInTask.AuthRequestCallback {

    /* Fragment tag to identify it later via FragmentManager */
    public static final String TAG = "auth_fragment";

    /* Identity Provider to use in authentication flow */
    private IdentityProvider _idpProvider;


    public static AuthFragment getInstance() {
        return new AuthFragment();
    }

    /* Set Identity Provider. This should be call before startLogin() */
    public void setIdentityProvider(IdentityProvider provider) {
        _idpProvider = provider;
    }

    /* Start provider specific SignIn-Flow.
     * And make this class listen for the process outcome */
    public void startLogin() {
        _idpProvider.setAuthenticationCallback(this);
        _idpProvider.startLogin(getActivity());
    }

    /* Delegate activity result to provider implementation */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG.AUTH, "auth callback");
        _idpProvider.onActivityResult(requestCode, resultCode, data);
    }

    /* Provider authentication was successful. Authenticate against mindlr backend server */
    @Override
    public void onSuccess(IdpResponse idpResponse) {
        if (Utility.isNetworkAvailable(getActivity())) {
            // set the global provider instance for the application
            MindlrApplication.User.setIdentityProvider(_idpProvider);

            // send auth credentials to server for verification
            // TODO: fix backend json always null
            //new SignInTask(getActivity(),
              //      new JSONObject(), idpResponse.getProviderType(), null, idpResponse).execute();
            onSendingSuccess(idpResponse);
        } else {
            DebugUtil.toast(getActivity(), getString(R.string.auth_error_network_unavailable));
        }
    }

    /* Provider authentications fails. We cannot do something here despite
     * providing some hints for the user.
     */
    @Override
    public void onFailure(Bundle extra) {
        DebugUtil.toast(getActivity(), getString(R.string.auth_error_unknown));
    }

    /* Successful verified user on backend. Mark user as authenticated and create DB entry */
    @Override
    public void onSendingSuccess(IdpResponse idpResponse) {
        // user was verified on backend so set authstate accordingly
        Utility.addAuthStateToPreference(getActivity(), true);

        // create new user if not already exists
        Utility.createUserEntryIfNotExists(getActivity(),
                idpResponse.getEmail(), idpResponse.getProviderType());

        // user was successfully verified and account was created
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
