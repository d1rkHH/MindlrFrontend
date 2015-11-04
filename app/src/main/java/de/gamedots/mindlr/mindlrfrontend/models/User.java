package de.gamedots.mindlr.mindlrfrontend.models;

/**
 * This class represents the App´s user. A User has a final ID used for
 * unique representation.
 * Created by Dirk on 01.11.15.
 */
public class User {

    private static User _user = new User();

    private long _id;

    /* is this flag set, the user id cannot be changed anymore */
    private boolean _idSet;

    private User() {
    }

    /**
     * Get the current unique app user
     * @return the app user
     */
    public static User getInstance() {
        return _user;
    }

    /**
     * Set the id for the user that cannot be changed once the value has been set
     *
     * @param id the final id for the user
     * @throws IdAlreadySetException: if setFinalValueForId was already called successfully in the past
     */
    public void setFinalValueForID(long id) throws IdAlreadySetException {
        if (!_idSet) {
            _id = id;
            _idSet = true;
        } else throw new IdAlreadySetException();
    }

    // Exception which indicates that the setFinalValueForId
    // method was already called (successful) earlier
    private class IdAlreadySetException extends RuntimeException {

        public IdAlreadySetException() {
            super("ID is already set for this user");
        }
    }
}
