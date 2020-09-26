package Glushkovich.Server.Auth;

import Glushkovich.Server.User;

public interface AuthService {
    boolean isAuthorized(User userCandidate);
    boolean addNewUser(User userCandidate);
}