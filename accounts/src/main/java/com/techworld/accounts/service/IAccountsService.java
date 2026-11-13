package com.techworld.accounts.service;

import com.techworld.accounts.dto.CustomerDto;

public interface IAccountsService {
    /**
     * This method is responsible for creating a new account based on the provided customer information.
     *
     * @param customerDto The data transfer object containing customer details required for account creation.
     */
    void createAccount(CustomerDto customerDto);

    /**
     * This method retrieves the account details of a customer based on their mobile number.
     *
     * @param mobileNumber The mobile number of the customer whose account details are to be fetched.
     * @return A CustomerDto containing the account details of the customer.
     */
    CustomerDto fetchAccount(String mobileNumber);

    boolean updateAccount(CustomerDto customerDto);

    boolean deleteAccount(String mobileNumber);
}
